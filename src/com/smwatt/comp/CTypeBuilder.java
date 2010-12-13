//////////////////////////////////////////////////////////////////////////////
//
// CTypeBuilder
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.util.List;
import java.util.ArrayList;

import com.pag.comp.ParseTreeNodePhase;
import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import com.pag.val.CompileTimeInteger;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.C.CodeSpecifierStorage;

import static com.pag.diag.Message.*;

import static com.smwatt.comp.CType.*;

public class CTypeBuilder {
    
    private Env env;
    public final CType INVALID_TYPE = new CTypeInvalid();
    
    public CTypeBuilder(Env senv) { 
        env = senv;
    }
    
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
            return INVALID_TYPE;
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
        
        if (dtor instanceof C.CodeDeclaratorId) {
            if (wantReturnTypeOnly) {
                System.out.println("Case 1");
                return INVALID_TYPE;
            }
            return base;
        
        // init declarator
        } else if (dtor instanceof C.CodeDeclaratorInit) {
            if (wantReturnTypeOnly) {
                // TODO error handling
                System.out.println("Case 2");
                return INVALID_TYPE;
            }
            
            return formTypeFromDeclarator(
                base, ((C.CodeDeclaratorInit) dtor)._dtor
            );
        
        // function declaration
        } else if (dtor instanceof C.CodeDeclaratorFunction) {
            // E.g. int (**f)(char,...) => base=int, dtor=(**f)(char,...)
            C.CodeDeclaratorFunction fdtor = (C.CodeDeclaratorFunction) dtor;
            
            CTypeFunction func_type = new CTypeFunction();
            CType yield_type = func_type;            
            CType retType = base;
            
            if(null != fdtor._optFn) {
                if(fdtor._optFn instanceof C.CodeDeclaratorParen) {
                    
                    CType internal = formTypeFromDeclarator(
                        new CTypeVoid(), fdtor._optFn
                    );
                    
                    C.CodeDeclaratorParen dd = (C.CodeDeclaratorParen) fdtor._optFn;
                    
                    if(dd._decl instanceof C.CodeDeclaratorWidth) {
                        env.diag.report(E_FIELD_WIDTH_INT_ONLY, dd);
                        return INVALID_TYPE;
                    
                    } else if(dd._decl instanceof C.CodeDeclaratorId) {
                        retType = formTypeFromDeclarator(base, fdtor._optFn);
                        
                    } else {
                        yield_type = formTypeFromDeclarator(
                            func_type, 
                            fdtor._optFn
                        );
                    }
                    
                } else if(fdtor._optFn instanceof C.CodeDeclaratorWidth) {
                    env.diag.report(E_FIELD_WIDTH_INT_ONLY, fdtor._optFn);
                    return INVALID_TYPE;
                    
                } else {
                    retType = formTypeFromDeclarator(base, fdtor._optFn);
                }
            }
            
            // functions aren't allowed to return arrays
            if(null != retType && retType instanceof CTypeArray) {
                env.diag.report(E_FUNC_RETURN_ARRAY, dtor);
                return INVALID_TYPE;
            }
            
            if (wantReturnTypeOnly) {
                return retType;
            }
            
            ArrayList<CType> argTypes = new ArrayList<CType>();
            
            boolean hadDotDotDot = false;
            boolean hadId        = false;
            
            for (C.Code cc: fdtor._argl) {

                if (hadDotDotDot) {
                    env.diag.report(
                        B_BUG, cc, "Multiple '...'s in function declarator list"
                    );
                    return INVALID_TYPE;
                }
                
                if (cc instanceof C.CodeId) {
                    // TODO: Handle old style function declarations.
                    // Should have a different entry point with declaration list.
                    // handled by ExprTypeVisitor :D
                    hadId        = true;
                    //hadDotDotDot = true; // Pretend.
                
                } else if (cc instanceof C.CodeDotDotDot) {
                    hadDotDotDot = true;
                
                } else if (cc instanceof C.CodeDeclaration) {                    
                    argTypes.add(formTypeFromDeclaration((C.CodeDeclaration) cc));
                    
                    // handle f(void) by clearing it out
                    if(1 == argTypes.size() && argTypes.get(0) instanceof CTypeVoid) {
                        argTypes.clear();
                    }
                
                } else {
                    env.diag.report(
                        B_BUG, cc, "Invalid code in function declarator list"
                    );
                    return INVALID_TYPE;
                }
            }
            
            if (hadId) {
                argTypes = null;
            }
            
            func_type.init(retType, argTypes, hadDotDotDot);
            
            return yield_type;
        
        // array declarator
        } else if (dtor instanceof C.CodeDeclaratorArray) {
            
            if (wantReturnTypeOnly) {
                // TODO error handling
                System.out.println("Case 4");
                return INVALID_TYPE;
            }
            
            C.CodeDeclaratorArray adtor = (C.CodeDeclaratorArray) dtor;
            
            // go and check the dimensions after the types have been
            // computed. this will ensure that no array has a zero/negative
            // size.
            if(null != adtor._optIndex) {
                env.addPhase(new ParseTreeNodePhase(adtor) {
                    public boolean apply(Env env, Code code) {
                        C.CodeDeclaratorArray arr = (C.CodeDeclaratorArray) node;
                        CompileTimeInteger obj_result = (CompileTimeInteger) env.interpret(arr._optIndex);
                        
                        if(null == obj_result) {
                            return false;
                        }
                        
                        int result = (obj_result).value;
                        
                        if(result <= 0) {
                            env.diag.report(
                                E_ARRAY_SIZE_NOT_POS, 
                                arr._optIndex, 
                                new Integer(obj_result.value)
                            );
                            return false;
                        }
                        
                        CTypeArray type = (CTypeArray) arr._type;
                        
                        // TODO calculate width
                        
                        return true;
                    }
                });
            }
            
            if(base instanceof CTypeArray) {
                CTypeArray sub = (CTypeArray) base;
                
                // need to have a dimension in multi-dimensional array
                if(null == sub._optSize) {
                    env.diag.report(E_INCOMPLETE_MULTI_ARRAY, dtor);
                    return INVALID_TYPE;
                }
            }
            
            // fixed to create the right type
            CTypeConstExpr optSize = CTypeConstExpr.optNew(adtor._optIndex);
            adtor._type = new CTypeArray(base, optSize);
            return formTypeFromDeclarator(
                adtor._type,
                adtor._optAr
            );
        
        } else if (dtor instanceof C.CodeDeclaratorPointer) {
            C.CodeDeclaratorPointer pdtor = (C.CodeDeclaratorPointer) dtor;
            C.CodePointerStar star = pdtor._star;
            
            while (star != null) {
                base = CTypePointer.optNew(base);
                
                int constCount = 0;
                int volatileCount = 0;
                int otherCount = 0;
                
                for (C.CodeSpecifier spec : star._lspec) {
                    if (spec instanceof C.CodeSpecifierQualifier) {
                        
                        C.CodeSpecifierQualifier qspec = (
                            (C.CodeSpecifierQualifier) spec
                        );
                        
                        switch (qspec._spec._type) {
                            case CTokenType.CONST:
                                constCount++; break;
                            case CTokenType.VOLATILE: 
                                volatileCount++; break;
                            default:
                                otherCount++;
                        }
                    }
                }
                
                if (otherCount > 0) {
                    env.diag.report(
                        B_BUG, 
                        star, 
                        "Non-type-qualifiers were found between two pointer declarators"
                    );
                    return INVALID_TYPE;
                }
                
                if (constCount + volatileCount > 1) {
                    env.diag.report(E_POINTER_MULTI_QUALIF, star);
                    return INVALID_TYPE;
                }
                
                if (constCount > 0) {
                    base._isConst = true;
                }
                
                if (volatileCount > 0) {
                    base._isVolatile = true;
                }
                
                star = star._optStar;
            }
            return formTypeFromDeclarator(base, pdtor._optPointee);
        
        } else if (dtor instanceof C.CodeDeclaratorWidth){
            if (wantReturnTypeOnly) {
                // TODO error handling
                System.out.println("Case 5");
                return INVALID_TYPE;
            }
                        
            return handleFieldWidth(base, (C.CodeDeclaratorWidth) dtor);
        
        } else if(dtor instanceof C.CodeDeclaratorParen) {
            
            return this.formTypeFromDeclarator(
                base, ((C.CodeDeclaratorParen) dtor)._decl
            );
        
        } else {
            // TODO: type inference error.
            System.out.println("Case 7");
            return INVALID_TYPE;
        }
    }
    
    private CType handleFieldWidth(CType base, C.CodeDeclaratorWidth width) {
        
        CType existing = width.getOptId()._type;
        
        if(null != existing) {
            return existing;
        }
        
        base = this.formTypeFromDeclarator(base, width._dtor);
        
        // check that the type with the width declarator is integral
        if(!(base instanceof CTypeIntegral)) {
            env.diag.report(E_FIELD_WIDTH_INT_ONLY, width._dtor);
            return INVALID_TYPE;
        }
        
        // even though we aren't actually using widths, we need to check
        // that they are positive, non-zero integers.
        env.addPhase(new ParseTreeNodePhase(width) {

            public boolean apply(Env env, Code code) {
                C.CodeDeclaratorWidth w = (C.CodeDeclaratorWidth) node;
                int width = ((CompileTimeInteger) env.interpret(w._width)).value; 
                
                if(width <= 0) {
                    env.diag.report(E_FIELD_WIDTH_POS, w);
                    
                } else if(width >= (8 * w.getOptId()._type.sizeOf(env))) {
                    env.diag.report(E_FIELD_WIDTH_TOO_WIDE, w);
                }
                
                // always keep going because this is a minor error, but
                // big enough to stop a major phase
                return true;
            }
            
        });
        
        return base;
    }
    
    public CType formTypeFromSpecifiers(List<C.CodeSpecifier> specifiers) {
        CType base = null;
        
        int constCount      = 0;
        int volatileCount   = 0;
        
        int signedCount     = 0;
        int unsignedCount   = 0;
        
        int longCount       = 0;
        int shortCount      = 0;
        boolean has_error   = false;
        
        for (C.CodeSpecifier spec: specifiers) {
                        
            if (spec instanceof C.CodeSpecifierType) {
                C.CodeSpecifierType tspec = (C.CodeSpecifierType) spec;
                
                switch (tspec._spec._type) {
                case CTokenType.VOID:
                    if (base != null) {
                        has_error = true;
                        env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                    }
                    base = new CTypeVoid();
                    break;
                    
                case CTokenType.FLOAT:
                    if (base != null) {
                        has_error = true;
                        env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                    }
                    base = new CTypeFloat();
                    break;
                case CTokenType.DOUBLE:
                    if (base != null) {
                        has_error = true;
                        env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                    }
                    base = new CTypeDouble();
                    break;
                
                case CTokenType.CHAR:
                    if (base != null) {
                        has_error = true;
                        env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                    }
                    base = new CTypeChar();
                    break;
                case CTokenType.INT:
                    if (base != null) {
                        has_error = true;
                        env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
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
                    env.diag.report(B_BUG, tspec, "Unknown type specifier");
                    return INVALID_TYPE;
                }
            
            } else if (spec instanceof C.CodeSpecifierQualifier) {
                
                C.CodeSpecifierQualifier qspec = (C.CodeSpecifierQualifier) spec;
                
                switch (qspec._spec._type) {
                case CTokenType.CONST:
                    constCount++;
                    break;
                case CTokenType.VOLATILE:
                    volatileCount++;
                    break;
                default:
                    env.diag.report(B_BUG, spec, "Unknown type qualifier");
                    return INVALID_TYPE;
                }
            
            // struct
            } else if (spec instanceof C.CodeSpecifierStruct) {
                
                if (base != null) {
                    has_error = true;
                    env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                }
                
                C.CodeSpecifierStruct sspec = (C.CodeSpecifierStruct) spec;
                if (sspec._optParts != null) {
                    
                    List<? extends C.Code> ldecl = sspec._optParts;
                    
                    ArrayList<CTypeField> lfield = new ArrayList<CTypeField>();
                    
                    // note: putting before we expand out the inside so that
                    //       we can self-reference a struct.
                    base = new CTypeStruct(sspec._optId, lfield);
                    sspec._type = base;
                    
                    for (C.Code d: ldecl) {
                        if (d instanceof C.CodeDeclaration) {
                            fillFields(lfield, (C.CodeDeclaration) d, true, base);
                        } else {
                            env.diag.report(
                                B_BUG, d, "Invalid code in struct body"
                            );
                            return INVALID_TYPE;
                        }
                    }
                    
                    
                } else if (sspec._optId != null) {
                    //base = new CTypeNamedStruct(sspec._optId);
                    base = sspec._scope.get(
                        sspec._optId._s, Type.STRUCT_NAME
                    ).code._type;
                
                } else {
                    env.diag.report(
                        B_BUG, spec, "Struct missing name and body"
                    );
                    return INVALID_TYPE;
                }
            
            // union
            } else if (spec instanceof C.CodeSpecifierUnion) {

                if (base != null) {
                    has_error = true;
                    env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                }
                
                C.CodeSpecifierUnion uspec = (C.CodeSpecifierUnion) spec;
                
                if (uspec._optParts != null) {
                    
                    List<? extends C.Code> ldecl = uspec._optParts;
                    ArrayList<CTypeField> lfield = new ArrayList<CTypeField>();
                    
                    base = new CTypeUnion(uspec._optId, lfield);
                    uspec._type = base;
                    
                    for (C.Code d: ldecl) {
                        
                        if (d instanceof C.CodeDeclaration) {
                            fillFields(lfield, (C.CodeDeclaration) d, false, base);
                        } else {
                            env.diag.report(B_BUG, d, "Invalid code in union body");
                            return INVALID_TYPE;
                        }
                    }
                    
                }
                else if (uspec._optId != null) {
                    //base = new CTypeNamedUnion(uspec._optId);
                    base = uspec._scope.get(
                        uspec._optId._s, Type.UNION_NAME
                    ).code._type;
                }
                else {
                    env.diag.report(B_BUG, spec, "Union missing name and body");
                    return INVALID_TYPE;
                }
            
            // enumeration
            } else if (spec instanceof C.CodeSpecifierEnum) {
                
                if (base != null) {
                    has_error = true;
                    env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                }
                
                C.CodeSpecifierEnum espec = (C.CodeSpecifierEnum) spec;
                
                if (espec._optParts != null) {
                    
                    List<? extends C.Code> lcc= espec._optParts;
                    ArrayList<CTypeEnumerator> letor = 
                        new ArrayList<CTypeEnumerator>();
                    
                    base = new CTypeEnum(espec._optId, letor);
                    espec._type = base;
                    
                    for (C.Code cc: lcc) {
                        if (cc instanceof C.CodeEnumerator) {
                            C.CodeEnumerator cetor = (C.CodeEnumerator) cc;
                            
                            // add in the type of the enumerator constant
                            // for later :D
                            cetor._type = base;
                            cetor._id._type = base;
                            
                            letor.add(new CTypeEnumerator(
                                cetor._id,
                                CTypeConstExpr.optNew(cetor._optValue)
                            ));                            
                        }
                        else {
                            env.diag.report(
                                B_BUG, spec, "Invalid code in enum body"
                            );
                            return INVALID_TYPE;
                        }
                    }
                    
                    // add in a phase to build up the values of the
                    // enumeration constants
                    env.addPhase(new ParseTreeNodePhase(espec) {

                        public boolean apply(Env env, Code code) {
                            
                            C.CodeSpecifierEnum cc = (C.CodeSpecifierEnum) node;
                            int val = 0;
                            
                            for(C.Code part : cc._optParts) {
                                C.CodeEnumerator en = (C.CodeEnumerator) part;
                                
                                // assign this enumeration constant a value
                                if(null == en._optValue) {
                                    en._optValue = new C.CodeIntegerConstant(Integer.toString(val));
                                    en._optValue._const_val = new CompileTimeInteger(val++);
                                
                                // take this enumeration constant's value
                                // as the new starting point
                                } else {
                                    CompileTimeInteger obj_val = (CompileTimeInteger) env.interpret(en._optValue);
                                    en._optValue._const_val = obj_val;
                                    val = obj_val.value + 1;
                                }
                                
                                if(Env.DEBUG) {
                                    System.out.println(
                                        "DEBUG: enumerator " + en._id._s + 
                                        " given value " + 
                                        en._optValue._const_val.toString()
                                    );
                                }
                            }
                            
                            return true;
                        }
                        
                    });
                }
                else if (espec._optId != null) {
                    //base = new CTypeNamedEnum(espec._optId);
                    base = espec._scope.get(
                        espec._optId._s, Type.ENUM_NAME
                    ).code._type;
                }
                else {
                    env.diag.report(B_BUG, spec, "Enum missing name and body");
                    return INVALID_TYPE;
                }
            
            // typedef name, substitute it with the actual type
            } else if (spec instanceof C.CodeSpecifierTypedefName) {
                C.CodeSpecifierTypedefName tspec = 
                    (C.CodeSpecifierTypedefName) spec;
                
                if (base != null) {
                    has_error = true;
                    env.diag.report(E_TOO_MANY_TYPE_SPECS, spec);
                }
                //base = new CTypeNamedTypedef(tspec._id);
                
                // direct substitution for the typedef
                if(null == spec._scope) {
                    spec._scope = env.getScope();
                }
                CSymbol sym = spec._scope.get(
                    tspec._id._s, 
                    Type.TYPEDEF_NAME
                );
                base = sym.code._type;
                
            // storage specifier
            } else {
                
                // not allowed to have auto or register storage specifiers
                // outside of a function scope
                CodeSpecifierStorage stor = (CodeSpecifierStorage) spec;
                if(CTokenType.AUTO == stor._spec._type
                || CTokenType.REGISTER == stor._spec._type) {
                    if(0 == stor._scope.depth) {
                        env.diag.report(E_AUTO_REG_OUTSIDE_FUNC, spec);
                    }
                }
            }
            
            spec._type = base;
        }
        
        if (base == null) {
            base = new CTypeInt();
        }

        if (constCount + volatileCount > 0) {
            if (constCount + volatileCount > 1) {
                env.diag.report(E_MULTI_QUALIFIER, specifiers.get(0));
                has_error = true;
            }
            base._isConst    = constCount > 0;
            base._isVolatile = volatileCount > 0;
        }
        
        if (signedCount + unsignedCount > 0) {
            if (signedCount + unsignedCount > 1) {
                env.diag.report(E_MULTI_SIGNED, specifiers.get(0));
                has_error = true;
            }
            if (base instanceof CTypeIntegral) {
                ((CTypeIntegral) base)._signed = signedCount - unsignedCount;
            
            } else {
                env.diag.report(E_SIGNED_NON_INTEGRAL_T, specifiers.get(0));
                has_error = true;
            }
        }
        
        if (shortCount + longCount > 0) {
            if (shortCount + longCount > 1) {
                env.diag.report(E_SHORT_LONG_T, specifiers.get(0));
                has_error = true;
            }
            if (base instanceof CTypeInt || base instanceof CTypeDouble) {
                ((CTypeArithmetic) base)._length = longCount - shortCount;
            } else {
                env.diag.report(E_SHORT_LONG_NON_INTEGRAL_T, specifiers.get(0));
                has_error = true;
            }
        }
        
        if(has_error) {
            base = INVALID_TYPE;
        }
        
        return base;
    }
    
    private void fillFields(List<CTypeField> lfield, C.CodeDeclaration dcl, boolean in_struct, CType parent_type) {
        List<C.CodeSpecifier>  lspec = dcl._lspec;
        List<C.CodeDeclarator> ldtor = dcl._ldtor;
                
        CType base = formTypeFromSpecifiers(lspec);
        
        for (C.CodeDeclarator dtor: ldtor) {
            
            CType t;
            
            // go look for field widths inside of a union, if found, report
            // the error, but proceed assuming
            if(dtor instanceof C.CodeDeclaratorWidth) {
                if(!in_struct) {
                    env.diag.report(R_FIELD_WIDTH_STRUCT_ONLY, dtor);
                }
                
                t = handleFieldWidth(base, (C.CodeDeclaratorWidth) dtor);
                
            } else {
                t = formTypeFromDeclarator(base, dtor);
            }
            
            C.CodeId optId = dtor.getOptId();
            
            if(t == parent_type) {
                env.diag.report(E_COMPOUND_CONTAIN_SELF, dtor);
                lfield.add(new CTypeField(optId, INVALID_TYPE));
                
            } else {
                lfield.add(new CTypeField(optId, t));
            }
        }
    }
}
