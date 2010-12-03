//////////////////////////////////////////////////////////////////////////////
//
// CTypeBuilder
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.util.List;
import java.util.ArrayList;

import com.pag.sym.Env;

import static com.smwatt.comp.CType.*;

public class CTypeBuilder {
	
	Env env;
	
	public CTypeBuilder(Env senv) { env = senv; }
	
	/**
	 * Form a type given a specifier list and a declarator.
	 * E.g. arg1: volatile double, arg2: *f(char **) gives
	 * Function(Pointer(double{volatile}), [Pointer(Pointer(char))])
	 */
	public CType formType(
			List<C.CodeSpecifier> specifiers, 
			C.CodeDeclarator declarator) 
	{
		return formType(formType(specifiers), declarator);
	}
	public CType formReturnType(
			List<C.CodeSpecifier> specifiers,
			C.CodeDeclarator declarator)
	{
		return formReturnType(formType(specifiers), declarator);
	}
	
	
	public CType formType(List<C.CodeSpecifier> specifiers) {
		return formTypeFromSpecifiers(specifiers);
	}
	public CType formType(CType base, C.CodeDeclarator declarator) {
		return formTypeFromDeclarator(base, declarator);
	}
	public CType formReturnType(CType base, C.CodeDeclarator declarator) {
		return formTypeFromDeclarator(base, declarator, true);
	}
	
	
	
	private CType formTypeFromDeclaration(C.CodeDeclaration d) {
		List<C.CodeSpecifier>  lspec = d._lspec;
		List<C.CodeDeclarator> ldtor = d._ldtor;
		
		CType base = formTypeFromSpecifiers(lspec);
		
		switch (ldtor.size()) {
		case 0: 
			return base;
		case 1: 
			return formTypeFromDeclarator(base, ldtor.get(0));
		default: 
			// TODO: Error handling... this should never happen.
			System.out.println("Case 8"); 
			return new CTypeInvalid();
		}
	}
	private CType formTypeFromDeclarator(CType base, C.CodeDeclarator dtor) {
		return formTypeFromDeclarator(base, dtor, false);
	}
	
	private CType formTypeFromDeclarator(
			CType base,
			C.CodeDeclarator dtor,
			boolean wantReturnTypeOnly)
	{
		if (dtor == null) {
			return base;
		}
		else if (dtor instanceof C.CodeDeclaratorId) {
			if (wantReturnTypeOnly) {
				System.out.println("Case 1");
				return new CTypeInvalid();
			}
			return base;
		}
		else if (dtor instanceof C.CodeDeclaratorInit) {
			if (wantReturnTypeOnly) {
				// TODO error handling
				System.out.println("Case 2");
				return new CTypeInvalid();
			}
			return formTypeFromDeclarator(
						base,((C.CodeDeclaratorInit) dtor)._dtor );
		}
		else if (dtor instanceof C.CodeDeclaratorFunction) {
			// E.g. int (**f)(char,...) => base=int, dtor=(**f)(char,...)
			C.CodeDeclaratorFunction fdtor = (C.CodeDeclaratorFunction) dtor;
			
			CType retType = formTypeFromDeclarator(base, fdtor._optFn);
			
			if (wantReturnTypeOnly) return retType;
			
			ArrayList<CType> argTypes = new ArrayList<CType>();
			
			boolean hadDotDotDot = false;
			boolean hadId        = false;
			
			for (C.Code cc: fdtor._argl) {
				if (hadDotDotDot) {
					// TODO: Handle too many dot dot dots
				    //System.out.println("too many ...'s.");
				    // parse error :D
				}
				if (cc instanceof C.CodeId) {
					// TODO: Handle old style function declarations.
					// Should have a different entry point with declaration list.
					hadId        = true;
					hadDotDotDot = true; // Pretend.
				}
				else if (cc instanceof C.CodeDotDotDot) {
					hadDotDotDot = true;
				}
				else if (cc instanceof C.CodeDeclaration) {				    
					argTypes.add(formTypeFromDeclaration((C.CodeDeclaration) cc));
					
					// handle f(void) by clearing it out
					if(1 == argTypes.size() && argTypes.get(0) instanceof CTypeVoid) {
					    argTypes.clear();
					}
				}
				else {
					System.out.println("Case 3");
					argTypes.add(new CTypeInvalid());
				}
			}
			if (hadId) argTypes = null;
			return new CTypeFunction(retType, argTypes, hadDotDotDot);
		}
		else if (dtor instanceof C.CodeDeclaratorArray) {
			if (wantReturnTypeOnly) {
				// TODO error handling
				System.out.println("Case 4");
				return new CTypeInvalid();
			}
			
			System.out.println("check dimensions");
			
			// TODO Check whether dimensions are given.
			// This is needed in all instances except:
			//    use of array as pointer
			//    use of array as last element of struct (depending)
			// TODO
			// Sometimes the dimension can come from the initializer.
			// E.g. int  (**a)[7] => base=int, dtor=(**a)[7]
			C.CodeDeclaratorArray adtor = (C.CodeDeclaratorArray) dtor;
			
			CType          eltType = formTypeFromDeclarator(base, adtor._optAr);
			CTypeConstExpr optSize = CTypeConstExpr.optNew(adtor._optIndex);
			return new CTypeArray(eltType, optSize);
		}
		else if (dtor instanceof C.CodeDeclaratorPointer) {
			C.CodeDeclaratorPointer pdtor = (C.CodeDeclaratorPointer) dtor;
			C.CodePointerStar star = pdtor._star;
			
			while (star != null) {
				base = new CTypePointer(base);
				
				int constCount = 0;
				int volatileCount = 0;
				int otherCount = 0;
				
				for (C.CodeSpecifier spec : star._lspec) {
					if (spec instanceof C.CodeSpecifierQualifier) {
						C.CodeSpecifierQualifier qspec =
							(C.CodeSpecifierQualifier) spec;
						switch (qspec._spec._type) {
						case CTokenType.CONST: constCount++; break;
						case CTokenType.VOLATILE: volatileCount++; break;
						default: otherCount++;
						}
					}
					if (otherCount > 0) {
						// TODO error
					    System.out.println("otherCount > 0");
					}
					if (constCount + volatileCount > 1) {
						// TODO error
					    System.out.println("constCount + volatileCount > 1");
					}
					if (constCount > 0) base._isConst = true;
					if (volatileCount > 0) base._isVolatile = true;
				}
				star = star._optStar;
			}
			return formTypeFromDeclarator(base, pdtor._optPointee);
		}
		else if (dtor instanceof C.CodeDeclaratorWidth){
			if (wantReturnTypeOnly) {
				// TODO error handling
				System.out.println("Case 5");
				return new CTypeInvalid();
			}
			// TODO: type inference error.  This should only be in structs.
			System.out.println("Case 6");
			return new CTypeInvalid();
		}
		else {
			// TODO: type inference error.
			System.out.println("Case 7");
			return new CTypeInvalid();
		}
	}
	private CType formTypeFromSpecifiers(List<C.CodeSpecifier> specifiers) {
		CType base = null;
		
		int constCount 		= 0;
		int volatileCount 	= 0;
		
		int signedCount 	= 0;
		int unsignedCount 	= 0;
		
		int longCount   	= 0;
		int shortCount  	= 0;
		
		for (C.CodeSpecifier spec: specifiers) {
			
			if (spec instanceof C.CodeSpecifierType) {
				C.CodeSpecifierType tspec = (C.CodeSpecifierType) spec;
				
				switch (tspec._spec._type) {
				case CTokenType.VOID:
					if (base != null) {
						// TODO: error handling
					    System.out.println("VOID: base != null");
					}
					base = new CTypeVoid();
					break;
					
				case CTokenType.FLOAT:
					if (base != null) {
						// TODO: error handling
					    System.out.println("FLOAT: base != null");
					}
					base = new CTypeFloat();
					break;
				case CTokenType.DOUBLE:
					if (base != null) {
						// TODO: error handling
					    System.out.println("DOUBLE: base != null");
					}
					base = new CTypeDouble();
					break;
				
				case CTokenType.CHAR:
					if (base != null) {
						// TODO: error handling.
					    System.out.println("CHAR: base != null");
					}
					base = new CTypeChar();
					break;
				case CTokenType.INT:
					if (base != null) {
						// TODO: error handling
					    System.out.println("INT: base != null");
					}
					base = new CTypeInt();
					break;
					
				case CTokenType.LONG:
					longCount++;
					break;
				case CTokenType.SHORT:
					shortCount++;
					break;
					
				case CTokenType.SIGNED:
					signedCount++;
					break;
				case CTokenType.UNSIGNED:
					unsignedCount++;
					break;
                default:
                    System.out.println("default: unhandled type specifier");
                	// TODO error handling
                	;
				}
			}
			else if (spec instanceof C.CodeSpecifierQualifier) {
				C.CodeSpecifierQualifier qspec = 
					(C.CodeSpecifierQualifier) spec;
				switch (qspec._spec._type) {
				case CTokenType.CONST:
					constCount++;
					break;
				case CTokenType.VOLATILE:
					volatileCount++;
					break;
                default:
                    System.out.println("default: unhandled type qualifier");
                	// TODO error handling
                	;
				}
			}
			else if (spec instanceof C.CodeSpecifierStruct) {
				if (base != null) {
					// TODO error handling
				    System.out.println("STRUCT: base != null");
				}
				C.CodeSpecifierStruct sspec = (C.CodeSpecifierStruct) spec;
				if (sspec._optParts != null) {
					List<? extends C.Code> ldecl = sspec._optParts;
					ArrayList<CTypeField> lfield = 
						new ArrayList<CTypeField>();
					for (C.Code d: ldecl) {
						if (d instanceof C.CodeDeclaration) {
							fillFields(lfield, (C.CodeDeclaration) d);
						}
						else {
							// TODO error handling
						    System.out.println("invalid code in struct body");
						}
					}
					base = new CTypeStruct(sspec._optId, lfield);
				}
				else if (sspec._optId != null) {
					base = new CTypeNamedStruct(sspec._optId);
				}
				else {
					// TODO error handling
				    System.out.println("STRUCT parts and name missing");
				}
			}
			else if (spec instanceof C.CodeSpecifierUnion) {
				if (base != null) {
					// TODO error handling
				    System.out.println("UNION: base != null");
				}
				C.CodeSpecifierUnion uspec = (C.CodeSpecifierUnion) spec;
				if (uspec._optParts != null) {
					List<? extends C.Code> ldecl = uspec._optParts;
					ArrayList<CTypeField> lfield = 
						new ArrayList<CTypeField>();
					for (C.Code d: ldecl) {
						if (d instanceof C.CodeDeclaration) {
							fillFields(lfield, (C.CodeDeclaration) d);
						}
						else {
							// TODO error handling
						    System.out.println("invalid code in union body");
						}
					}
					base = new CTypeUnion(uspec._optId, lfield);
				}
				else if (uspec._optId != null) {
					base = new CTypeNamedUnion(uspec._optId);
				}
				else {
					// TODO error handling
				    System.out.println("STRUCT parts and name missing.");
				}
			}
			else if (spec instanceof C.CodeSpecifierEnum) {
				if (base != null) {
					// TODO error handling
				}
				C.CodeSpecifierEnum espec = (C.CodeSpecifierEnum) spec;
				if (espec._optParts != null) {
					List<? extends C.Code> lcc= espec._optParts;
					ArrayList<CTypeEnumerator> letor = 
						new ArrayList<CTypeEnumerator>();
					for (C.Code cc: lcc) {
						if (cc instanceof C.CodeEnumerator) {
							C.CodeEnumerator cetor = (C.CodeEnumerator) cc;
							letor.add(new CTypeEnumerator(cetor._id,
									CTypeConstExpr.optNew(cetor._optValue)));
						}
						else {
							// TODO error handling
						    System.out.println("invalid code in enum.");
						}
					}
					base = new CTypeEnum(espec._optId, letor);
				}
				else if (espec._optId != null) {
					base = new CTypeNamedEnum(espec._optId);
				}
				else {
					// TODO error handling
				    System.out.println("ENUM witout name or parts.");
				}
			}
			else if (spec instanceof C.CodeSpecifierTypedefName) {
				C.CodeSpecifierTypedefName tspec = 
					(C.CodeSpecifierTypedefName) spec;
				if (base != null) {
					// TODO error handling
				    System.out.println("TYPEDEF name: base != null");
				}
				base = new CTypeNamedTypedef(tspec._id);
				
				// TODO fill in the "really" field.
				System.out.println("really field?");
				
			}
			else {
				// TODO error handling
			    System.out.println("unknown type specifier");
			}
		}
		if (base == null) {
			base = new CTypeInt();
		}
		if (constCount + volatileCount > 0) {
			if (constCount + volatileCount > 0) {
				// TODO error handling
			    System.out.println("constCount + volatileCount > 0");
			}
			base._isConst    = constCount > 0;
			base._isVolatile = volatileCount > 0;
		}
		if (signedCount + unsignedCount > 0) {
			if (signedCount + unsignedCount > 1) {
				// TODO error handling
			    System.out.println("signed and unsigned");
			}
			if (base instanceof CTypeIntegral) {
				((CTypeIntegral) base)._signed = signedCount - unsignedCount;
			}
			else {
				// TODO error only integral types can be signed or unsigned.
			    System.out.println("signed non-integral type");
			}
		}
		if (shortCount + longCount > 0) {
			if (shortCount + longCount > 1) {
				// TODO error handling
			    System.out.println("short long type :/");
			}
			if (base instanceof CTypeInt || base instanceof CTypeDouble) {
				((CTypeArithmetic) base)._length = longCount - shortCount;
			}
			else {
				// TODO error only ints and doubles can be long or short.
			    System.out.println("short/long non-int/double");
			}
		}
		return base;
	}
	private void fillFields(List<CTypeField> lfield, C.CodeDeclaration dcl) {
		List<C.CodeSpecifier>  lspec = dcl._lspec;
		List<C.CodeDeclarator> ldtor = dcl._ldtor;
		
		CType base = formTypeFromSpecifiers(lspec);
		//TODO widths
		
		System.out.println("TODO: widths of fields.");
		
		for (C.CodeDeclarator dtor: ldtor) {
			CType t = formTypeFromDeclarator(base, dtor);
			C.CodeId optId = dtor.getOptId();
			lfield.add(new CTypeField(optId, t));
		}
	}
}
