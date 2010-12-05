package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import com.smwatt.comp.CTokenOperator;
import com.smwatt.comp.CTokenType;
import com.smwatt.comp.CTypeBuilder;
import com.smwatt.comp.CTypePrinter;
import com.smwatt.comp.CType;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.C.CodeDeclaration;
import com.smwatt.comp.CType.CTypePointing;

import static com.smwatt.comp.CType.*;

/**
 * Perform type deduction and type inference on the parse tree. This will
 * implicitly convert the parse tree into an acyclic directed graph by 
 * adding in extra code here and there to convert types, etc.
 * 
 * @author petergoodman
 *
 */
public class TypeInferenceVisitor implements CodeVisitor {
    
    private Env env;
    private CTypeBuilder builder;
    private CTypePrinter printer;
    
    private CTypeArithmetic CHAR_TYPE;
    private CTypeArithmetic INT_TYPE;
    private CTypeArithmetic LONG_INT_TYPE;
    private CTypeArithmetic DOUBLE_TYPE;
    private CTypePointing VOID_POINTER_TYPE;
    
    private CodeExpr NULL_POINTER;
    private CodeExpr ZERO_INT;
    private CodeExpr ZERO_FLOAT;
    
    // are we inside a compound type?
    private int compound_count = 0;
    
    public TypeInferenceVisitor(Env ee) {
        env = ee;
        builder = new CTypeBuilder(ee);
        printer = new CTypePrinter(System.out);
        
        // basic types used in substitution expressions
        CHAR_TYPE = new CTypeChar();
        INT_TYPE = new CTypeInt();
        LONG_INT_TYPE = new CTypeInt();
        DOUBLE_TYPE = new CTypeDouble();
        VOID_POINTER_TYPE = new CTypePointer(new CTypeVoid());
        
        // basic constructions of substitution expressions
        ZERO_INT = new CodeIntegerConstant(0);
        ZERO_FLOAT = new CodeFloatingConstant(0.0);
        NULL_POINTER = castTo(ZERO_INT, VOID_POINTER_TYPE);
        
        // type assignment of substitution expressions
        ZERO_INT._type = LONG_INT_TYPE;
        ZERO_FLOAT._type = DOUBLE_TYPE;
        NULL_POINTER._type = VOID_POINTER_TYPE;
        
        LONG_INT_TYPE.makeLong();
    }
    
    /**
     * Create code to be substituted into the parse tree that will perform
     * the needed conversion to go pointer -> boolean context (int).
     * @param expr
     * @return
     */
    private CodeExpr pointerToBool(CodeExpr expr) {
        CodeExpr ret = new CodeExprInfix(
            new CTokenOperator(CTokenType.EQUALS),
            expr,
            NULL_POINTER 
        );
        ret._type = INT_TYPE;
        return ret;
    }
    
    /**
     * Create code to be substituted into the parse tree that will perform
     * the needed conversion to go float -> boolean context (int).
     * @param expr
     * @return
     */
    private CodeExpr floatToBool(CodeExpr expr) {
        CodeExpr ret = new CodeExprInfix(
            new CTokenOperator(CTokenType.EQUALS),
            expr,
            ZERO_FLOAT 
        );
        ret._type = INT_TYPE;
        return ret;
    }
    
    /**
     * Create code to be substituted into the parse tree that will perform
     * a type cast.
     * 
     * @param expr
     * @param type
     * @return
     */
    private CodeExpr castTo(CodeExpr expr, CType type) {
        CodeExpr ret = new CodeExprCast(type, expr);
        ret._type = type;
        return ret;
    }
    
    public void visit(Code cc) {
        cc.acceptVisitor(this);
    }

    public void visit(CodeUnit cc) {
        for(Code code : cc._l) {
            code.acceptVisitor(this);
        }
    }

    public void visit(CodeFunction cc) {
        cc._type = builder.formType(cc._lspec, cc._head);
        cc._type._isConst = true;
        cc._type._isAddressable = true;
        
        CTypeFunction type = (CTypeFunction) cc._type;
        
        cc._head.acceptVisitor(this);
        
        // handle old-style types
        for(CodeDeclaration decl : cc._ldecl) {
            for(CodeDeclarator dtor : decl._ldtor) {
                
                if(null == dtor) {
                    continue;
                }
                
                CType arg_type = builder.formType(decl._lspec, dtor);
                dtor._type = arg_type;
                
                CodeId id = dtor.getOptId();
                
                if(null != id) {
                    id._type = dtor._type;
                    id._type._isAddressable = true;
                }
                
                if(null != arg_type) {
                    type._argTypes.add(arg_type);
                }
                
                dtor.acceptVisitor(this);
            }
        }
        
        cc._body.acceptVisitor(this);
    }

    public void visit(CodeDeclaration cc) {
        for(CodeDeclarator dtor : cc._ldtor) {
            
            if(null == dtor) {
                continue;
            }
            
            CType arg_type = builder.formType(cc._lspec, dtor);
            dtor._type = arg_type;
            
            // set the type of the id
            CodeId id = dtor.getOptId();
            
            if(null != id) {
                
                id._type = dtor._type;
                id._type._isAddressable = true;
                
                // makes sure we don't try to use function types unless they
                // are function pointers
                if(!dtor._is_typedef) {
                    
                    if(id._type instanceof CTypeFunction
                    && 0 != compound_count) {
                        env.diag.report(E_SYMBOL_FUNC_TYPE, id);
                        id._type = builder.INVALID_TYPE;
                    }
                }
            }
            
            // we need to check the type of constant expressions
            dtor.acceptVisitor(this);
            
            printer.print(dtor._type);
        }
        
        // we need to check the type of constant expressions (e.g. in
        // enums, array dimensions, etc.
        for(CodeSpecifier spec : cc._lspec) {
            spec.acceptVisitor(this);
        }
        
        // make sure we can typecheck specifiers even in the absence of
        // a declarator
        if(cc._ldtor.isEmpty()) {
            cc._type = builder.formTypeFromSpecifiers(cc._lspec);
        }
    }

    public void visit(CodeId cc) { }

    public void visit(CodeTypeName cc) {
        for(CodeSpecifier spec : cc._lspec) {
            spec.acceptVisitor(this);
        }
        if(null != cc._dtor) {
            cc._dtor.acceptVisitor(this);
        }
        cc._type = builder.formType(cc._lspec, cc._dtor);
    }

    public void visit(CodeString cc) {
        
        // add in the null-terminating character.
        cc._s = cc._s + '\0';
        
        // this ensures that the string will have the right dimension
        // for the sizeof operator.
        cc._type = new CTypeArray(
            CHAR_TYPE, new CTypeConstExpr(
                new CodeIntegerConstant(cc._s.length())
            )
        );
        
        // set up the behaviour of this constant
        ((CTypeArray) cc._type)._pointeeType._isConst = true;
        cc._type._isAddressable = true;
        
        cc._const_val = cc._s;
    }

    public void visit(CodeCharacterConstant cc) {
        cc._type = CHAR_TYPE;
        cc._type._isConst = true;
        char[] as_array = cc._s.toCharArray();
        cc._const_val = new Integer(as_array[0]);
    }

    public void visit(CodeIntegerConstant cc) {
        cc._type = new CTypeInt();
        cc._type._isConst = true;
        cc._const_val = Integer.valueOf(cc._s);
    }

    public void visit(CodeFloatingConstant cc) {
        cc._type = new CTypeDouble();
        cc._type._isConst = true;
        cc._const_val = Double.valueOf(cc._s);
    }

    public void visit(CodeEnumerationConstant cc) {
        cc._type = cc._scope.get(cc._s, Type.ENUMERATOR).code._type;
        cc._type._isConst = true;
    }

    public void visit(CodeDotDotDot cc) { }

    public void visit(CodeSpecifierStorage cc) { }

    public void visit(CodeSpecifierQualifier cc) { }

    public void visit(CodeSpecifierType cc) { }

    public void visit(CodeSpecifierTypedefName cc) { }

    public void visit(CodeSpecifierStruct cc) {
        ++compound_count;
        if(null != cc._optParts) {
            for(Code part : cc._optParts) {
                part.acceptVisitor(this);
            }
        }
        --compound_count;
    }

    public void visit(CodeSpecifierUnion cc) {
        ++compound_count;
        if(null != cc._optParts) {
            for(Code part : cc._optParts) {
                part.acceptVisitor(this);
            }
        }
        --compound_count;
    }

    public void visit(CodeSpecifierEnum cc) {
        if(null != cc._optParts) {
            for(Code part : cc._optParts) {
                part.acceptVisitor(this);
            }
        }
    }

    public void visit(CodeEnumerator cc) {
        if(null != cc._optValue) {
            cc._optValue.acceptVisitor(this);
            
            if(!(cc._optValue._type instanceof CTypeIntegral)) {
                env.diag.report(E_ENUMERATOR_INTEGRAL, cc);
            }
        }
    }
    
    public void visit(CodeDeclaratorParen cc) {
        cc._decl.acceptVisitor(this);
    }

    public void visit(CodeDeclaratorArray cc) {
        if(null != cc._optIndex) {
            cc._optIndex.acceptVisitor(this);
            // TODO check that type is integer
        }
        if(null != cc._optAr) {
            cc._optAr.acceptVisitor(this);
        }
    }

    public void visit(CodeDeclaratorFunction cc) {
        
        cc._optFn.acceptVisitor(this);
        
        if(null != cc._argl) {
            for(Code arg : cc._argl) {
                if(null != arg) {
                    arg.acceptVisitor(this);
                }
            }
        }
    }

    public void visit(CodeDeclaratorInit cc) {
        cc._initializer.acceptVisitor(this);
        cc._dtor.acceptVisitor(this);
        
        // don't let us initialize a function type
        CodeId id = cc._dtor.getOptId();
        if(id._type instanceof CTypeFunction) {
            env.diag.report(E_SYMBOL_FUNC_TYPE, cc);
            id._type = builder.INVALID_TYPE;
        }
    }

    public void visit(CodeDeclaratorPointer cc) {
        if(null != cc._optPointee) {
            cc._optPointee.acceptVisitor(this);
        }
        cc._star.acceptVisitor(this);
    }

    public void visit(CodeDeclaratorWidth cc) {
        // TODO check that width type is integer
        // TODO check that type of var with with is integral
        if(null != cc._dtor) {
            cc._dtor.acceptVisitor(this);
        }
        cc._width.acceptVisitor(this);
    }

    public void visit(CodeDeclaratorId cc) {
        //cc._id._type._isAddressable = true;
    }

    public void visit(CodePointerStar cc) {
        if(null != cc._optStar) {
            cc._optStar.acceptVisitor(this);
        }
        for(CodeSpecifier spec : cc._lspec) {
            spec.acceptVisitor(this);
        }
    }

    public void visit(CodeInitializerValue cc) {
        cc._value.acceptVisitor(this);
        cc._type = cc._value._type;
    }

    public void visit(CodeInitializerList cc) {
        CodeInitializer last = null;
        for(CodeInitializer init : cc._list) {
            init.acceptVisitor(this);
            last = init;
        }
        cc._type = last._type;
    }

    public void visit(CodeStatBreak cc) { }

    public void visit(CodeStatCase cc) {
        cc._value.acceptVisitor(this);
        
        // TODO check type of value
        
        env.addPhase(new ParseTreeNodePhase(cc) {

            public boolean apply(Env env, Code code) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatCompound cc) {
        for(CodeDeclaration decl : cc._ldecl) {
            decl.acceptVisitor(this);
        }
        for(CodeStat stat : cc._lstat) {
            stat.acceptVisitor(this);
        }
    }

    public void visit(CodeStatContinue cc) { }

    public void visit(CodeStatDefault cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatDo cc) {
        cc._stat.acceptVisitor(this);
        cc._test.acceptVisitor(this);
        // TODO check type of test
    }

    public void visit(CodeStatExpression cc) {
        if(null != cc._optExpr) {
            cc._optExpr.acceptVisitor(this);
            cc._type = cc._optExpr._type;
        } else {
            cc._type = new CTypeVoid();
        }
    }

    public void visit(CodeStatFor cc) {
        
        if(null != cc._optInit) {
            cc._optInit.acceptVisitor(this);
        }
        
        if(null != cc._optTest) {
            cc._optTest.acceptVisitor(this);
            // TODO check types of test
        }
        
        if(null != cc._optStep) {
            cc._optStep.acceptVisitor(this);
        }
        
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatGoto cc) { }

    public void visit(CodeStatIf cc) {
        // TODO check types of test
        cc._test.acceptVisitor(this);
        cc._thstat.acceptVisitor(this);
        if(null != cc._optElstat) {
            cc._optElstat.acceptVisitor(this);
        }
    }

    public void visit(CodeStatLabeled cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatReturn cc) {
        
        if(null != cc._optExpr) {
            cc._optExpr.acceptVisitor(this);
            cc._type = cc._optExpr._type;
        } else {
            cc._type = new CTypeVoid();
        }
        
        // TODO check return type against function return type
    }

    public void visit(CodeStatSwitch cc) {
        cc._expr.acceptVisitor(this);
        // TODO check test type
        
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatWhile cc) {
        // TODO check test type
        cc._test.acceptVisitor(this);
        
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeExprAssignment cc) {
        // TODO check type of RHS against expected type of LHS
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        cc._type = cc._a._type;
    }

    public void visit(CodeExprCast cc) {
        cc._expr.acceptVisitor(this);
        cc._typename.acceptVisitor(this);
        
        // TODO look for illegal cast

        cc._type = cc._typename._type;
    }

    public void visit(CodeExprConditional cc) {
        // TODO check test type
        // TODO check that both sides have same type
        cc._test.acceptVisitor(this);
        cc._thexpr.acceptVisitor(this);
        cc._elexpr.acceptVisitor(this);
        // TODO yield type, LUB of cc._thexpr and cc.elexpr
    }
    
    private void binaryOpMeetTypes(CodeExprInfix cc) {
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
        int size_of_a = ta.sizeOf(env);
        int size_of_b = tb.sizeOf(env);
        
        if(size_of_a != size_of_b) {
            if(size_of_a < size_of_b) {
                cc._a = castTo(cc._a, tb);
            } else {
                cc._b = castTo(cc._b, ta);
            }
        }
    }

    public void visit(CodeExprInfix cc) {
        // TODO check types of both sides with operator
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        cc._type = builder.INVALID_TYPE;
        
        // TODO yield type
        
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
        // step 1: fail fast on relational operators
        switch(cc._op._type) {
            case CTokenType.EQUALS:
            case CTokenType.NOT_EQUALS:
            case CTokenType.LT:
            case CTokenType.GT:
            case CTokenType.LT_EQ:
            case CTokenType.GT_EQ:
                
                if(!(ta instanceof CTypeComparable)
                || !(tb instanceof CTypeComparable)) {
                    env.diag.report(E_TYPES_NOT_COMPARABLE, cc);
                    return;
                }
                
                // the types are comparable, let's make sure that one
                // can be brought to the other.
                if(!ta.canBeCastTo(tb) && !tb.canBeCastTo(ta)) {
                    env.diag.report(E_INFIX_CANT_UNIFY, cc);
                    return;
                }
                
                binaryOpMeetTypes(cc);
                
        }
        
        // re-compute
        ta = cc._a._type;
        tb = cc._b._type;
        
        boolean a_is_integral = ta instanceof CTypeIntegral;
        boolean b_is_integral = tb instanceof CTypeIntegral;
        
        int size_of_a = ta.sizeOf(env);
        int size_of_b = tb.sizeOf(env);
        
        // step 2: fail fast on arithmetic
        switch(cc._op._type) {
            
            case CTokenType.STAR:
            case CTokenType.SLASH:
            case CTokenType.MOD:
                
                if(!(ta instanceof CTypeMultiplicative)
                || !(tb instanceof CTypeMultiplicative)) {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "infix");
                }
                // fall-through
                
            case CTokenType.PLUS:
            case CTokenType.MINUS:
                
                if(!(ta instanceof CTypeAdditive)
                || !(tb instanceof CTypeAdditive)) {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "infix");
                    return;
                }
                // fall-through
                
            case CTokenType.EQUALS:
            case CTokenType.NOT_EQUALS:
            case CTokenType.LT:
            case CTokenType.GT:
            case CTokenType.LT_EQ:
            case CTokenType.GT_EQ:
                
                boolean a_is_float = ta instanceof CTypeFloating;
                boolean b_is_float = tb instanceof CTypeFloating;
                
                // both arguments are float, but of different widths
                if(a_is_float && b_is_float) {
                    
                    binaryOpMeetTypes(cc);
                
                // one of the arguments is float; convert the other to float
                } if(a_is_float && !b_is_float || !a_is_float && b_is_float) {
                    
                    if(!ta.canBeCastTo(tb) && !tb.canBeCastTo(ta)) {
                        env.diag.report(E_INFIX_CANT_UNIFY, cc);
                        return;
                    }
                    
                    if(a_is_float) {
                        cc._b = castTo(cc._b, ta);
                    }
                    
                    if(b_is_float) {
                        cc._a = castTo(cc._a, tb);
                    }
                
                // neither argument is float
                } else {
                    
                }
                
                
                break;
                
            default:
        }
        
        switch(cc._op._type) {
            case CTokenType.COMMA:
            
            case CTokenType.VBAR:
            case CTokenType.XOR:
            case CTokenType.AMP:
            
            
            case CTokenType.LSH:
            case CTokenType.RSH:
            case CTokenType.PLUS:
            case CTokenType.MINUS:
            case CTokenType.STAR:
            case CTokenType.SLASH:
            case CTokenType.MOD:
                
            // relational + boolean contexts
            case CTokenType.VBAR_VBAR:
            case CTokenType.AMP_AMP:
                
                // fall-through
            
            // relational
            case CTokenType.EQUALS:
            case CTokenType.NOT_EQUALS:
            case CTokenType.LT:
            case CTokenType.GT:
            case CTokenType.LT_EQ:
            case CTokenType.GT_EQ:
                
        }
    }

    public void visit(CodeExprParen cc) {
        cc._expr.acceptVisitor(this);
        cc._type = cc._expr._type;
    }

    public void visit(CodeExprPostfix cc) {
        cc._a.acceptVisitor(this);
        cc._type = builder.INVALID_TYPE;
        
        CType tt = cc._a._type;
        
        switch(cc._op._type) {
            case CTokenType.PLUS_PLUS:
            case CTokenType.MINUS_MINUS:
                if(tt instanceof CTypeArithmetic
                || tt instanceof CTypePointer) {
                    
                    if(tt._isConst) {
                        env.diag.report(E_EXPR_HAS_CONST_TYPE, cc);
                        
                    } else if(!tt._isAddressable) {
                        env.diag.report(E_EXPR_NOT_ADDRESSABLE, cc);
                        
                    } else {
                        cc._type = tt.copy();
                        cc._type._isAddressable = false;
                    }
                } else {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "postfix");
                }
                break;
        }
    }

    public void visit(CodeExprPrefix cc) {
        
        cc._a.acceptVisitor(this);
        cc._type = builder.INVALID_TYPE;
        
        CType tt = cc._a._type;
        
        switch(cc._op._type) {
            case CTokenType.PLUS_PLUS:
            case CTokenType.MINUS_MINUS:
                if(tt instanceof CTypeArithmetic
                || tt instanceof CTypePointer) {
                    
                    if(tt._isConst) {
                        env.diag.report(E_EXPR_HAS_CONST_TYPE, cc);
                    } else if(!tt._isAddressable) {
                        env.diag.report(E_EXPR_NOT_ADDRESSABLE, cc);
                    } else {
                        cc._type = tt;
                    }
                } else {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "prefix");
                }
                break;
                
            case CTokenType.AMP:
                if(!tt._isAddressable) {
                    env.diag.report(E_EXPR_NOT_ADDRESSABLE, cc);
                } else {
                    cc._type = CTypePointer.optNew(tt);
                }
                
                break;
                
            case CTokenType.STAR:
                if(tt instanceof CTypePointing) {
                    if(tt instanceof CTypeFunctionPointer) {
                        env.diag.report(E_DEREF_FUNC_POINTER, cc);
                    } else {
                        cc._type = ((CTypePointing) tt)._pointeeType;
                    }
                } else {
                    env.diag.report(E_DEREF_NON_POINTER, cc);
                }
                break;
                
            case CTokenType.PLUS:
            case CTokenType.MINUS:
            case CTokenType.NOT:
                if(tt instanceof CTypeArithmetic
                || tt instanceof CTypePointer) {
                    
                    // compare the pointer to zero
                    if(tt instanceof CTypePointer) {
                        cc._type = new CTypeInt();
                        cc._a = pointerToBool(cc._a);
                    
                    // arithmetic check
                    } else {
                        cc._type = INT_TYPE;
                        
                        // add in an integer cast
                        if(tt instanceof CTypeFloat) {
                            cc._a = floatToBool(cc._a);
                        }
                    }
                    
                    cc._type._isAddressable = false;
                } else {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "unary");
                }
                break;
                
            case CTokenType.TILDE:
                if(tt instanceof CTypeArithmetic) {
                    cc._type = tt.copy();
                    cc._type._isAddressable = false;
                } else {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "unary");
                }
                break;
        }
    }

    public void visit(CodeExprId cc) {
        CSymbol sym = cc._scope.get(cc._id._s);
        cc._type = sym.code._type;
                
        // handle function pointers
        if(cc._type instanceof CTypeFunction) {
            cc._type = CTypePointer.optNew(cc._type);
        }
    }

    public void visit(CodeExprSizeofValue cc) {
        env.addPhase(new ParseTreeNodePhase(cc) {
            public boolean apply(Env env, Code code) {
                CodeExprSizeofValue val = (CodeExprSizeofValue) node;
                if(null == val._const_val) {
                    val._const_val = new Integer(val._expr._type.sizeOf(env));                
                    if(Env.DEBUG) {
                        System.out.println("DEBUG: sizeof(expr) " + val + " = " + val._const_val);
                    }
                }
                return true;
            }
        });
        
        cc._expr.acceptVisitor(this);
        cc._type = new CSizeT();
    }

    public void visit(CodeExprSizeofType cc) {

        env.addPhase(new ParseTreeNodePhase(cc) {
            public boolean apply(Env env, Code code) {
                CodeExprSizeofType ty = (CodeExprSizeofType) node;
                if(null == ty._const_val) {
                    ty._const_val = new Integer(ty._tname._type.sizeOf(env));                
                    if(Env.DEBUG) {
                        System.out.println("DEBUG sizeof(type) " + ty + " = " + ty._const_val);
                    }
                }
                return true;
            }
        });
        
        cc._tname.acceptVisitor(this);
        cc._type = new CSizeT();
    }

    public void visit(CodeExprCall cc) {
        
        cc._fun.acceptVisitor(this);
        for(CodeExpr expr : cc._argl) {
            expr.acceptVisitor(this);
        }
        
        // default for now, assume worst-case
        cc._type = builder.INVALID_TYPE;
        
        // could be a function name
        if(!(cc._fun._type instanceof CTypeFunctionPointer)) {
            env.diag.report(E_CALL_FUNC_PTR, cc);
            return;
        }
        
        // check that the argument types and numbers are acceptable
        CTypeFunction funt = (CTypeFunction) (
            ((CTypeFunctionPointer) cc._fun._type)._pointeeType
        );
        
        // too few args
        if(cc._argl.size() < funt._argTypes.size()) {
            env.diag.report(E_CALL_FUNC_MISSING_ARGS, cc);
            return;
        }
        
        // too many args
        if(cc._argl.size() > funt._argTypes.size() && !funt._moreArgs) {
            env.diag.report(E_CALL_FUNC_EXTRA_ARGS, cc);
            return;
        }
        
        // check those args that we have
        for(int i = 0; i < funt._argTypes.size(); ++i) {
            CType argt = funt._argTypes.get(i);
            CodeExpr expr = cc._argl.get(i);
            CType part = expr._type;
            
            if(!part.canBeAssignedTo(argt)) {
                env.diag.report(E_CALL_BAD_ARG_TYPE, expr);
                return;
            }
            
            // add in a type cast :D
            cc._argl.set(i, new CodeExprCast(part, expr));
        }
        
        cc._type = funt._retType;
    }

    public void visit(CodeExprSubscript cc) {
        cc._idx.acceptVisitor(this);
        cc._arr.acceptVisitor(this);

        if(!(cc._arr._type instanceof CTypePointing)) {
            env.diag.report(E_SUBSCRIPT_POINTER, cc);
            cc._type = builder.INVALID_TYPE;
        } else if(!(cc._idx._type instanceof CTypeIntegral)) {
            env.diag.report(E_SUBSCRIPT_INTEGRAL, cc);
            cc._type = builder.INVALID_TYPE;
        } else {
            cc._type = ((CTypePointing) cc._arr._type)._pointeeType;
        }
    }
    
    /**
     * Go check for a field in a struct/union.
     * @param expr
     * @param field_name
     * @param is_pointer
     * @return
     */
    private CType checkField(CodeExpr expr, String field_name, boolean is_pointer) {
        CType tt            = expr._type;
        CTypeCompound ct    = null;
        
        if(is_pointer) {
            if(!(expr._type instanceof CTypePointing)) {
                env.diag.report(E_EXPR_NOT_COMPOUND_PT, expr);
                return builder.INVALID_TYPE;
            }
        
            tt = ((CTypePointing) expr._type)._pointeeType;
        }
        
        // make sure we're looking at a compound type
        if(!(tt instanceof CTypeCompound)) {
            env.diag.report(
                is_pointer ? E_EXPR_NOT_COMPOUND_PT : E_EXPR_NOT_COMPOUND_T, 
                expr
            );
            return builder.INVALID_TYPE;
        }
        
        // go explore the field
        ct = (CTypeCompound) tt;
        for(CTypeField field : ct._fields) {
            if(0 == field._id._s.compareTo(field_name)) {
                return field._type;
            }
        }
        
        return builder.INVALID_TYPE;
    }

    public void visit(CodeExprField cc) {
        cc._ob.acceptVisitor(this);
        cc._type = checkField(cc._ob, cc._id._s, false);
    }

    public void visit(CodeExprPointsTo cc) {
        cc._ptr.acceptVisitor(this);
        cc._type = checkField(cc._ptr, cc._id._s, true);
    }
}
