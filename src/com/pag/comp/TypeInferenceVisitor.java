package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import com.pag.diag.Reporter;
import com.pag.llvm.IRTypeBuilder;
import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Scope;
import com.pag.sym.Type;
import com.pag.val.CompileTimeFloat;
import com.pag.val.CompileTimeInteger;
import com.pag.val.CompileTimeString;
import com.smwatt.comp.CTokenOperator;
import com.smwatt.comp.CTokenType;
import com.smwatt.comp.CTypeBuilder;
import com.smwatt.comp.CTypePrinter;
import com.smwatt.comp.CType;
import com.smwatt.comp.SourcePosition;
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
    
    private static CTypeArithmetic CHAR_TYPE;
    private static final CTypeArithmetic INT_TYPE;
    private static CTypeIntegral UNSIGNED_INT_TYPE;
    private static CTypeArithmetic LONG_INT_TYPE;
    private static CTypeArithmetic DOUBLE_TYPE;
    private static CTypePointing VOID_POINTER_TYPE;
    private static CType POINTER_DIFF_TYPE;
    
    private static CodeExpr NULL_POINTER;
    private static CodeExpr ZERO_INT;
    private static CodeExpr LONG_ZERO_INT;
    private static CodeExpr ZERO_FLOAT;
    
    private final CType INVALID_TYPE;
    
    // LLVM IR type builder
    private IRTypeBuilder ir_type;
    
    static {
        // basic types used in substitution expressions
        CHAR_TYPE = new CTypeChar();
        INT_TYPE = new CTypeInt();
        UNSIGNED_INT_TYPE = new CTypeInt();
        LONG_INT_TYPE = new CTypeInt();
        DOUBLE_TYPE = new CTypeDouble();
        VOID_POINTER_TYPE = new CTypePointer(new CTypeVoid());
        POINTER_DIFF_TYPE = new CTypePtrDiffT();
        
        // basic constructions of substitution expressions
        ZERO_INT = new CodeIntegerConstant(0);
        ZERO_INT._type = INT_TYPE;
        
        LONG_ZERO_INT = new CodeIntegerConstant(0);
        LONG_ZERO_INT._type = LONG_INT_TYPE;
        
        ZERO_FLOAT = new CodeFloatingConstant(0.0);
        ZERO_FLOAT._type = DOUBLE_TYPE;
        
        NULL_POINTER = castTo(ZERO_INT, VOID_POINTER_TYPE);
        
        LONG_INT_TYPE.makeLong();
        UNSIGNED_INT_TYPE._signed = -1;
    }
    
    private LinkedList<CodeStatSwitch> switch_stack = new LinkedList<CodeStatSwitch>(); 
    
    // are we inside a compound type?
    private int compound_count = 0;
    private int func_decl_count = 0;
    
    public TypeInferenceVisitor(Env ee) {
        env = ee;
        builder = new CTypeBuilder(ee);
        printer = new CTypePrinter(System.out);
        
        INVALID_TYPE = builder.INVALID_TYPE;
        
        // initialize the scopes
        ZERO_INT._scope = env.getScope();
        LONG_ZERO_INT._scope = env.getScope();
        ZERO_FLOAT._scope = env.getScope();
        NULL_POINTER._scope = env.getScope();
        
        ir_type = new IRTypeBuilder(ee);
    }
    
    /**
     * Create code to be substituted into the parse tree that will perform
     * the needed conversion to go pointer -> boolean context (int).
     * @param expr
     * @return
     */
    private static CodeExpr pointerToBool(CodeExpr expr) {
        CodeExpr ret = new CodeExprInfix(
            new CTokenOperator(CTokenType.NOT_EQUALS),
            expr._type instanceof CTypeFunctionPointer 
                ? castTo(expr, VOID_POINTER_TYPE) 
                : expr,
            NULL_POINTER 
        );
        ret._type = INT_TYPE;
        ret._scope = expr._scope;
        return ret;
    }
    
    /**
     * Create code to be substituted into the parse tree that will perform
     * the needed conversion to go float -> boolean context (int).
     * @param expr
     * @return
     */
    private static CodeExpr floatToBool(CodeExpr expr) {
        
        CodeExpr ret = new CodeExprInfix(
            new CTokenOperator(CTokenType.NOT_EQUALS),
            expr._type instanceof CTypeFloat 
                ? castTo(expr, DOUBLE_TYPE) 
                : expr,
            ZERO_FLOAT 
        );
        ret._type = INT_TYPE;
        ret._scope = expr._scope;
        return ret;
    }
    
    private static CodeExpr intToBool(CodeExpr expr) {
        
        CodeExpr ret = new CodeExprInfix(
            new CTokenOperator(CTokenType.NOT_EQUALS),
            castTo(expr, LONG_INT_TYPE),
            LONG_ZERO_INT
        );
        ret._type = INT_TYPE;
        ret._scope = expr._scope;
        return ret;
    }
    
    /**
     * Convert an expression node that we know to have a boolean sensitive
     * type to a boolean test.
     * 
     * @param expr
     * @return
     */
    private static CodeExpr exprToBool(CodeExpr expr) {
        if(expr._type instanceof CTypeFloating) {
            return floatToBool(expr);
        } else if(expr._type instanceof CTypePointing) {
            return pointerToBool(expr);
        } else if(expr._type instanceof CTypeIntegral) {
            return intToBool(expr);
        }
        return null;
    }
    
    private CodeExpr assignTo(CodeExpr expr, CType dt) {
        
        CType st = expr._type;
        
        if(st instanceof CTypeIntegral && dt instanceof CTypeIntegral) {
            if((((CTypeIntegral) st)._signed < 0) 
            != (((CTypeIntegral) dt)._signed < 0)) {
                return castTo(expr, dt);
            }
        }
        
        String st_str = ir_type.toString(st);
        String dt_str = ir_type.toString(dt);
        
        if(0 == st_str.compareTo(dt_str)) {
            return expr;
        }
        
        return castTo(expr, dt);
    }
    
    /**
     * Create code to be substituted into the parse tree that will perform
     * a type cast.
     * 
     * @param expr
     * @param type
     * @return
     */
    private static CodeExpr castTo(CodeExpr expr, CType type) {
        
        CodeExpr ret = new CodeExprCast(type, expr);
        ret._scope = expr._scope;
        ret._type = type;
        
        // copy over the properties if the old type doesn't use the default
        // ones
        if(expr._type._isAddressable
        || expr._type._isConst
        || expr._type._isVolatile) {
            
            ret._type = ret._type.copy();
            
            ret._type._isAddressable = expr._type._isAddressable;
            ret._type._isConst = expr._type._isConst;
            ret._type._isVolatile = expr._type._isVolatile;
        }
        
        return ret;
    }
    
    private void binaryOpMeetTypes(CodeExprTwoArgsMeet cc) {
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
    
    private void binaryOpMeetFloats(CodeExprTwoArgsMeet cc) {
        
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
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
        }
    }
    
    private void checkTest(CodeStatTestable cc) {
        if(null == cc._test) {
            return;
        }
        if(!(cc._test._type instanceof CTypeBooleanSensitive)) {
            env.diag.report(E_TEST_CANT_GO_BOOL, cc._test);
            cc._test._type = builder.INVALID_TYPE;
        } else {
            cc._test = exprToBool(cc._test);
        }
    }
    
        /**
     * Check the type of an infix expression.
     * @param tok_type
     * @param cc
     */
    public void checkInfixExpr(int tok_type, CodeExprInfix cc) {
        
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
        // step 1: fail fast on relational operators
        switch(tok_type) {
            case CTokenType.EQUALS:
            case CTokenType.NOT_EQUALS:
            case CTokenType.LT:
            case CTokenType.GT:
            case CTokenType.LT_EQ:
            case CTokenType.GT_EQ:
                
                if(!(ta instanceof CTypeComparable)
                || !(tb instanceof CTypeComparable)) {
                    env.diag.report(E_TYPES_NOT_COMPARABLE, cc);
                    return ;
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
        
        // step 2: fail fast on arithmetic + relational.
        //         this step also dose some type promotion via casting..
        //         huzzah for strong-armed promotions!
        switch(tok_type) {
            
            case CTokenType.STAR:
            case CTokenType.SLASH:
            case CTokenType.MOD:
                
                if(!(ta instanceof CTypeMultiplicative)
                || !(tb instanceof CTypeMultiplicative)) {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "infix");
                    return;
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
                
                binaryOpMeetFloats(cc);
        }
        
        // re-compute
        ta = cc._a._type;
        tb = cc._b._type;
        
        boolean a_is_integral = ta instanceof CTypeIntegral;
        boolean b_is_integral = tb instanceof CTypeIntegral;
        
        boolean a_is_pointer = ta instanceof CTypePointing;
        boolean b_is_pointer = tb instanceof CTypePointing;

        //int size_of_a = ta.sizeOf(env);
        //int size_of_b = tb.sizeOf(env);
        
        boolean fall_through = false;
        
        // step 3: check the actual expressions, given all implied assumptions
        //         from getting to this point
        switch(tok_type) {
            
            case CTokenType.VBAR:
            case CTokenType.XOR:
            case CTokenType.AMP:
            case CTokenType.MOD:
                
                // require that LHS is integral
                if(!a_is_integral) {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "infix");
                    return;
                }
                
                // fall-through
                fall_through = true;
            
            case CTokenType.LSH:
            case CTokenType.RSH:
                
                // require that RHS is integral
                if(!b_is_integral) {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "infix");
                    return;
                }
                
                // both sides are integral, so force them to some common
                // type
                CTypeIntegral lhs_t = (CTypeIntegral) ta;
                CTypeIntegral rhs_t = (CTypeIntegral) tb;
                
                // don't allow signed shifts
                if(!fall_through) {
                    
                    if(0 <= rhs_t._signed) {
                        //env.diag.report(E_SIGNED_SHIFT, cc);
                        //return;
                    }
                    
                    if(rhs_t._length > 0) {
                        cc._b = castTo(cc._b, UNSIGNED_INT_TYPE);
                        tb = UNSIGNED_INT_TYPE;
                    }
                
                // make sure signs are in agreements, this is most likely
                // overly strict ;)
                } else if((lhs_t._signed >= 0) != (rhs_t._signed >= 0)) {
                    env.diag.report(E_SIGNS_DONT_AGREE, cc);
                    return;
                }
                
                // should we up-cast?
                binaryOpMeetTypes(cc);
                
                // yield the type
                cc._type = cc._a._type.copy();
                cc._type._isAddressable = false;
                break;
                
            case CTokenType.PLUS:
                fall_through = true;
                // fall-through
                
            case CTokenType.MINUS:
                
                if(a_is_pointer && b_is_pointer) {
                    
                    if(fall_through) {
                        env.diag.report(E_CANT_ADD_POINTERS, cc);
                        return;
                    }
                    
                    cc._a = castTo(cc._a, VOID_POINTER_TYPE);
                    cc._b = castTo(cc._b, VOID_POINTER_TYPE);
                    cc._type = POINTER_DIFF_TYPE;
                    return;
                
                // one is pointer, check that other is integral
                } else if(a_is_pointer || b_is_pointer) {
                    
                    if(!a_is_integral && !b_is_integral) {
                        env.diag.report(E_ONLY_ADD_INT_TO_PTR, cc);
                        return;
                    }
                    
                    cc._type = a_is_pointer ? ta.copy() : tb.copy();
                    return;
                }
                
                // both arithmetic
                // fall-through
                
            case CTokenType.STAR:
            case CTokenType.SLASH:
                cc._type = ta.copy();
                cc._type._isAddressable = false;
                break;
                
                
            // relational + boolean contexts
            case CTokenType.VBAR_VBAR:
            case CTokenType.AMP_AMP:
                
                if(!(ta instanceof CTypeBooleanSensitive)
                || !(tb instanceof CTypeBooleanSensitive)) {
                    env.diag.report(E_EXPR_CANT_GO_BOOL, cc);
                    return;
                }
                
                cc._a = exprToBool(cc._a);
                cc._b = exprToBool(cc._b);
                
                // fall-through
            
            // relational
            case CTokenType.EQUALS:
            case CTokenType.NOT_EQUALS:
            case CTokenType.LT:
            case CTokenType.GT:
            case CTokenType.LT_EQ:
            case CTokenType.GT_EQ:
                cc._type = INT_TYPE.copy();
                cc._type._isAddressable = false;
        }
    }
    
    private void checkDeclaratorForFunc(CodeDeclarator cc) {
        
        CodeId id = cc.getOptId();
        
        // make sure we're not using function types
        if(cc._type instanceof CTypeFunction
        && null == cc.getOptFunction()) {
            env.diag.report(E_SYMBOL_FUNC_TYPE, cc);
            cc._type = builder.INVALID_TYPE;
            if(null != id) {
                id._type = cc._type;
            }
        }
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
        
        CodeId id = cc._head.getOptId();
        
        // assume the worst
        id._type = builder.INVALID_TYPE;
        cc._type = builder.INVALID_TYPE;
        
        boolean has_old_style_decls = false;
        Hashtable<String, CType> params = new Hashtable<String, CType>();
        
        ++func_decl_count;
        
        // handle old-style function declarators. they can be in any order
        // so we will consider them first.
        for(CodeDeclaration decl : cc._ldecl) {
            for(CodeDeclarator dtor : decl._ldtor) {
                
                if(null == dtor) {
                    continue;
                }
                
                has_old_style_decls = true;
                
                CType arg_type = builder.formType(decl._lspec, dtor);
                dtor._type = arg_type;
                
                CodeId param_id = dtor.getOptId();
                
                if(null != param_id) {
                    param_id._type = dtor._type;
                    param_id._type._isAddressable = true;
                    
                    // add the type in
                    params.put(param_id._s, param_id._type);
                }
                
                dtor.acceptVisitor(this);
                
                checkDeclaratorForFunc(dtor);
            }
        }
        
        
        cc._type = builder.formType(cc._lspec, cc._head);
        id._type = cc._type;
        
        if(!(cc._type instanceof CTypeFunction)) {
            env.diag.report(E_INVALID_TYPE, cc);
            return;
        }
        
        CTypeFunction tt = (CTypeFunction) cc._type;
        
        CodeDeclaratorFunction func = (CodeDeclaratorFunction) cc._head;
        
        Hashtable<String,SourcePosition> seen_params = new Hashtable<String,SourcePosition>();
        
        for(Code code : func._argl) {
            
            // make sure we're not mixing things
            if(code instanceof CodeDeclaration) {
                if(has_old_style_decls) {
                    env.diag.report(E_FUNC_MIX_DECLS, cc);
                    break;
                }
                
                CodeDeclaration decl = (CodeDeclaration) code;
                
                // go look for function types in the specifiers of a function
                // parameter that isn't given a name.
                if(0 == decl._ldtor.size()) {
                    for(CodeSpecifier spec : decl._lspec) {
                        if(null != spec 
                        && spec._type instanceof CTypeFunction) {
                            env.diag.report(E_SYMBOL_FUNC_TYPE, spec);
                        }
                    }
                }
                
                code.acceptVisitor(this);
            
            // get the types in the right order
            } else if(code instanceof CodeId) {
                CodeId param_id = (CodeId) code;
                SourcePosition pos = seen_params.get(param_id._s);
                if(null != pos) {
                    env.diag.report(E_PARAM_SHADOW, param_id, param_id._s, pos.toString());
                    continue;
                }
                
                CType param_type = params.get(param_id._s);
                tt._argTypes.add(param_type);
                code._type = param_type;
                seen_params.put(param_id._s, param_id.getSourcePosition());
            }
        }
        
        if(Env.DEBUG) {
            System.out.print("DEBUG: " + id.getSourcePosition().toString());
            printer.print(id._type);
        }
        
        --func_decl_count;
        
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
            
            // set before we explore the declarator so that we have the
            // type of the id if we have an initializer
            if(null != id) {
                id._type = dtor._type;
                id._type._isAddressable = true;
                
                // give the function a type to start with in the case that we've
                // forward declared it, used it, but still not defined it.
                if(0 == func_decl_count) {
                    
                    // make sure to const qualify arrays (that aren't in
                    // the declaration list of a function) so that we can't
                    // do ++/-- on them.
                    if(id._type instanceof CTypeArray) {
                        id._type._isConst = true;
                    }
                    
                    CSymbol sym = cc._scope.get(id._s);
                    if(null == sym.code._type) {
                        sym.code._type = id._type;
                    }
                }
            }
            
            // we need to check the type of constant expressions
            dtor.acceptVisitor(this);
                        
            if(null != id) {
                
                // make sure that if we're declaring a one-dimensional
                // array that it has a complete type.
                if(id._type instanceof CTypeArray
                && 0 == func_decl_count) {
                    CTypeArray arr_t = (CTypeArray) id._type;
                    
                    if(null == arr_t._optSize) {
                        env.diag.report(E_INCOMPLETE_ARRAY, id);
                        id._type = builder.INVALID_TYPE;
                    }
                }
                
                dtor._type = id._type;
            }
            
            checkDeclaratorForFunc(dtor);
            
            if(Env.DEBUG) {
                printer.print(dtor._type);
            }
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
        
        cc._const_val = new CompileTimeString(cc._s);
    }

    public void visit(CodeCharacterConstant cc) {
        cc._type = CHAR_TYPE;
        cc._const_val = new CompileTimeInteger(cc._s.charAt(0));
    }

    public void visit(CodeIntegerConstant cc) {
        cc._type = new CTypeInt();
        int val = 0;
        
        // handle different bases of integral constants
        if('0' != cc._s.charAt(0) || 0 == "0".compareTo(cc._s)) {
            val = Integer.parseInt(cc._s);
        } else {
            if('x' == cc._s.charAt(1) || 'X' == cc._s.charAt(1)) {
                val = Integer.parseInt(cc._s.substring(2), 16);
            } else {
                val = Integer.parseInt(cc._s.substring(1), 8);
            }
        }
        
        cc._const_val = new CompileTimeInteger(val);
    }

    public void visit(CodeFloatingConstant cc) {
        cc._type = new CTypeFloat();
        cc._const_val = new CompileTimeFloat(Double.parseDouble(cc._s));
    }

    public void visit(CodeEnumerationConstant cc) {
        cc._type = cc._scope.get(cc._s, Type.ENUMERATOR).code._type;
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
            
            if(!(cc._optIndex._type instanceof CTypeIntegral)
            && !(cc._optIndex._type instanceof CTypeEnum)) {
                env.diag.report(E_ARRAY_DIM_INTEGRAL, cc._optIndex);
            }
        }
        
        if(null != cc._optAr) {
            cc._optAr.acceptVisitor(this);
        }
    }

    public void visit(CodeDeclaratorFunction cc) {        
        ++func_decl_count;
        cc._optFn.acceptVisitor(this);
        
        if(null != cc._argl) {
            for(Code arg : cc._argl) {
                if(null != arg) {
                    arg.acceptVisitor(this);
                }
            }
        }
        --func_decl_count;
    }

    public void visit(CodeDeclaratorInit cc) {
        cc._initializer.acceptVisitor(this);
        cc._dtor.acceptVisitor(this);
        
        CodeId id = cc._dtor.getOptId();
        if(null == id) {
            return;
        }
        
        // don't let us initialize a function type
        if(id._type instanceof CTypeFunction) {
            env.diag.report(E_SYMBOL_FUNC_TYPE, cc);
            id._type = builder.INVALID_TYPE;
            return;
        }
                
        if(id._type instanceof CTypeArray) {
            
            
            
            if(!(cc._initializer instanceof CodeInitializerList)) {
                env.diag.report(E_INIT_ARRAY_WITHOUT_LIST, cc);
                id._type = builder.INVALID_TYPE;
                return;
            }
            
            final CType expected_type = id._type.getInternalType();
            final CType got_type = cc._initializer._type.getInternalType();
            
            // bad internal type in the array
            if(!got_type.canBeAssignedTo(expected_type)) {
                env.diag.report(E_INVALID_INIT_LIST_TYPE, cc);
                id._type = builder.INVALID_TYPE;
                return;
            }
            
            final CTypeArray arr_t = (CTypeArray) id._type;

            // the array has an incomplete type
            if(null == arr_t._optSize) {
                
                arr_t._optSize = new CTypeConstExpr(
                    new CodeIntegerConstant(-1) /* evil! */
                );
                
                env.addPhase(new ParseTreeNodePhase(cc) {
                    public boolean apply(Env env, Code code) {
                        CodeDeclaratorInit cc = (CodeDeclaratorInit) node;
                        CodeInitializerList ls = (CodeInitializerList) cc._initializer;
                        CodeId id = cc.getOptId();
                        
                        int mult = -1 * arr_t.sizeOf(env) / expected_type.sizeOf(env);
                        int ls_len = ls._list.size();
                        
                        CodeIntegerConstant dim = (CodeIntegerConstant)(
                            ((CTypeArray) id._type)._optSize._expr
                        );
                        
                        if(0 != (ls_len % mult)) {
                            env.diag.report(E_INIT_LIST_NOT_DIVISIBLE, ls);
                            id._type = INVALID_TYPE;
                            return false;
                            
                        // properly complete the type
                        } else {
                            ((CompileTimeInteger) dim._const_val).value = ls_len / mult;
                        }
                        
                        return true;
                    }
                });
            
            // the array has complete type, we will need to check the
            // dimensions of the list when we've actually computed the
            // array's size
            } else {
                
                env.addPhase(new ParseTreeNodePhase(cc) {
                    public boolean apply(Env env, Code code) {
                        CodeDeclaratorInit cc = (CodeDeclaratorInit) node;
                        CodeInitializerList ls = (CodeInitializerList) cc._initializer;
                        CodeId id = cc.getOptId();
                        
                        int mult = arr_t.sizeOf(env) / expected_type.sizeOf(env);
                        int ls_len = ls._list.size();
                        
                        CodeIntegerConstant dim = (CodeIntegerConstant)(
                            ((CTypeArray) id._type)._optSize._expr
                        );
                        
                        if(ls_len > mult) {
                            env.diag.report(E_INIT_LIST_TOO_LONG, ls);
                            id._type = INVALID_TYPE;
                            return false;
                        
                        // fill in zeros if possible
                        } else if(ls_len < mult) {
                            if(!INT_TYPE.canBeAssignedTo(got_type)) {
                                env.diag.report(E_INIT_LIST_AUTO, ls);
                                id._type = INVALID_TYPE;
                                return false;
                            }
                            
                            Scope ss = ls._list.get(0)._scope;
                            
                            // add in the zeros
                            for(int i = 0; i < mult - ls_len; ++i) {
                                CodeInitializerValue iv = new CodeInitializerValue(
                                    castTo(ZERO_INT, got_type)
                                );
                                iv._value._scope = ss;
                                iv._scope = ss;
                                ls._list.add(iv);
                            }
                        }
                        
                        return true;
                    }
                });
                
            }
        }
    }

    public void visit(CodeDeclaratorPointer cc) {
        if(null != cc._optPointee) {
            cc._optPointee.acceptVisitor(this);
        }
        cc._star.acceptVisitor(this);
    }

    public void visit(CodeDeclaratorWidth cc) {
        if(null != cc._dtor) {
            cc._dtor.acceptVisitor(this);
        }
        cc._width.acceptVisitor(this);
        
        if(!(cc._width._type instanceof CTypeIntegral)) {
            env.diag.report(E_FIELD_WIDTH_INTEGRAL, cc._width);
        }
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
        
        // check the types of the initializers
        for(CodeInitializer init : cc._list) {
            init.acceptVisitor(this);
            
            if(null != last) {
                if(!last._type.canBeAssignedTo(init._type)) {
                    env.diag.report(E_INVALID_INIT_LIST_TYPE, init);
                    cc._type = builder.INVALID_TYPE;
                    return;
                }
            }
            
            last = init;
        }
        
        // interpret all initializer values
        env.addPhase(new ParseTreeNodePhase(cc) {
            public boolean apply(Env env, Code code) {
                CodeInitializerList cc = (CodeInitializerList) node;
                for(CodeInitializer init : cc._list) {
                    if(null == env.interpret(init)) {
                        return false;
                    }
                }
                return true;
            }
        });
        
        cc._type = new CTypeArray(
            last._type, 
            new CTypeConstExpr(new CodeIntegerConstant(cc._list.size()))
        );
    }

    public void visit(CodeStatBreak cc) { }

    public void visit(CodeStatCase cc) {
        cc._value.acceptVisitor(this);
        CodeStatSwitch sstat = switch_stack.peek();
        
        // bad type for case
        if(!sstat._expr._type.canBeAssignedTo(cc._value._type)) {
            
            env.diag.report(
                E_CASE_SWITCH_DISAGREE, 
                cc, sstat._expr.getSourcePosition().toString()
            );
        
        // make sure that the case expression can be evaluated at compile
        // time
        } else {
            env.addPhase(new ParseTreeNodePhase(cc) {
                public boolean apply(Env env, Code code) {
                    CodeStatCase cs = (CodeStatCase) node;
                    cs._value._const_val = env.interpret(cs._value);
                    return true;
                }
            });
            
            sstat._cases.add(cc);
        }
        
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
        switch_stack.peek()._cases.add(cc);
    }

    public void visit(CodeStatDo cc) {
        cc._stat.acceptVisitor(this);
        cc._test.acceptVisitor(this);
        checkTest(cc);
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
        
        if(null != cc._test) {
            cc._test.acceptVisitor(this);
            checkTest(cc);
        }
        
        if(null != cc._optStep) {
            cc._optStep.acceptVisitor(this);
        }
        
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatGoto cc) { }

    public void visit(CodeStatIf cc) {
        cc._test.acceptVisitor(this);
        cc._stat.acceptVisitor(this);
        
        checkTest(cc);
        
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
        
        // make sure the return types are okay
        if(!cc._type.canBeAssignedTo(((CTypeFunction) cc._func._type)._retType)) {
            
        }
    }

    public void visit(CodeStatSwitch cc) {
        switch_stack.push(cc);
        cc._expr.acceptVisitor(this);
        
        // check the test type
        if(!(cc._expr._type instanceof CTypeArithmetic)) {
            env.diag.report(E_SWITCH_ON_ARITHMETIC, cc._expr);
            cc._expr._type = builder.INVALID_TYPE;
        }
        
        cc._stat.acceptVisitor(this);
        switch_stack.pop();
    }

    public void visit(CodeStatWhile cc) {
        cc._test.acceptVisitor(this);
        
        checkTest(cc);
        
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeExprAssignment cc) {
        
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        cc._type = builder.INVALID_TYPE;
        
        // trying to assign to a const
        if(cc._a._type._isConst) {
            env.diag.report(E_ASSIGN_TO_CONST, cc);
            return;
        
        // trying to assign to a non-addressable place
        } else if(!cc._a._type._isAddressable) {
            env.diag.report(E_ASSIGN_TO_VALUE, cc);
            return;
        }
        
        CType rhs_type = cc._b._type;
        CodeExpr orig_a = cc._a;
        int op_as_infix = cc._op.opAsInfix();
        
        // translate the operators into operators on infix expression
        switch(cc._op._type) {
            case CTokenType.STAR_ASSIGN: op_as_infix = CTokenType.STAR; break;
            case CTokenType.SLASH_ASSIGN: op_as_infix = CTokenType.SLASH; break;
            case CTokenType.MOD_ASSIGN: op_as_infix = CTokenType.MOD; break;
            case CTokenType.PLUS_ASSIGN: op_as_infix = CTokenType.PLUS; break;
            case CTokenType.MINUS_ASSIGN: op_as_infix = CTokenType.MINUS; break;
            case CTokenType.LSH_ASSIGN: op_as_infix = CTokenType.LSH; break;
            case CTokenType.RSH_ASSIGN: op_as_infix = CTokenType.RSH; break;
            case CTokenType.AMP_ASSIGN: op_as_infix = CTokenType.AMP; break;
            case CTokenType.XOR_ASSIGN: op_as_infix = CTokenType.XOR; break;
            case CTokenType.VBAR_ASSIGN: op_as_infix = CTokenType.VBAR; break;
        }
        
        switch(cc._op._type) {
            default:
                checkInfixExpr(op_as_infix, cc);
                
                // fall-through
                
            case CTokenType.ASSIGN:
                
                if(!rhs_type.canBeAssignedTo(cc._a._type)
                || cc._a._type instanceof CTypeArray
                || orig_a._type instanceof CTypeArray) {
                    env.diag.report(E_CANT_DO_ASSIGNMENT, cc);
                } else {
                    cc._b = assignTo(cc._b, cc._a._type);
                }
        }
        
        cc._a = orig_a;
        cc._type = cc._a._type;
    }

    public void visit(CodeExprCast cc) {
        cc._expr.acceptVisitor(this);
        cc._typename.acceptVisitor(this);
        
        if(!cc._expr._type.canBeCastTo(cc._typename._type)) {
            env.diag.report(E_ILLEGAL_CAST, cc);
            cc._type = builder.INVALID_TYPE;
        } else {
            cc._type = cc._typename._type.copy();
            if(!(cc._expr._type instanceof CTypePointing)) {
                cc._type._isAddressable = false;
            }
        }
    }

    public void visit(CodeExprConditional cc) {
        cc._test.acceptVisitor(this);
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        cc._type = builder.INVALID_TYPE;
        
        if(!(cc._test._type instanceof CTypeBooleanSensitive)) {
            env.diag.report(E_TEST_CANT_GO_BOOL, cc._test);
            return;
        }
        
        cc._test = exprToBool(cc._test);
        
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
        // check that we can assign one of the types to the other
        if(!ta.canBeCastTo(tb) || !tb.canBeCastTo(ta)) {
            env.diag.report(E_BAD_OP_FOR_TYPE, cc, "conditional");
            return;
        }
        
        // both are pointers
        if(ta instanceof CTypePointing 
        && tb instanceof CTypePointing) {
            
            CTypePointing ptr_a_t = (CTypePointing) ta;
            CTypePointing ptr_b_t = (CTypePointing) tb;
            
            // can inherit the specifiers and become both const and volatile
            if(ptr_a_t._isVolatile && ptr_b_t._isConst
            || ptr_a_t._isConst && ptr_b_t._isVolatile) {
                env.diag.report(E_MULTI_QUALIFIER, cc);
                return;
            }
            
            // make one side void *
            if(ptr_a_t._pointeeType instanceof CTypeVoid
            && !(ptr_b_t._pointeeType instanceof CTypeVoid)) {
                
                cc._b = castTo(cc._b, VOID_POINTER_TYPE);
                tb = cc._b._type;
            
            // make the other size void *
            } else if(!(ptr_a_t._pointeeType instanceof CTypeVoid)
                   && ptr_b_t._pointeeType instanceof CTypeVoid) {
                
                cc._a = castTo(cc._a, VOID_POINTER_TYPE);
                ta = cc._a._type;
            
            // can't assign, so die
            } else if(!ta.canBeAssignedTo(tb) || !tb.canBeAssignedTo(ta)) {
                env.diag.report(E_BAD_OP_FOR_TYPE, cc, "conditional");
                return;
            }
            
            // create the type and merge the properties of both
            cc._type = ta.copy();
            
        
        // both are arithmetic, do the basic conversions
        } else if(ta instanceof CTypeArithmetic 
               && tb instanceof CTypeArithmetic) {
            
            binaryOpMeetFloats(cc);
            binaryOpMeetTypes(cc);
            cc._type = cc._a._type.copy();
            
        // can't assign, so die
        } else if(!ta.canBeAssignedTo(tb) || !tb.canBeAssignedTo(ta)) {
            env.diag.report(E_BAD_OP_FOR_TYPE, cc, "conditional");
            return;
            
        } else {
            
            cc._type = ta.copy();
        }
        
        cc._type._isAddressable = cc._type._isAddressable && tb._isAddressable;
        cc._type._isConst = cc._type._isConst || tb._isConst;
        cc._type._isVolatile = cc._type._isVolatile || tb._isVolatile;
    }

    public void visit(CodeExprInfix cc) {
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        cc._type = builder.INVALID_TYPE;
        
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
        // comma operator is easiest to deal with :D
        if(CTokenType.COMMA == cc._op._type) {
            cc._type = cc._b._type;
            return;
        }
        
        checkInfixExpr(cc._op._type, cc);
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
                    cc._type._isAddressable = false;
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
                if(tt instanceof CTypeArithmetic
                || tt instanceof CTypePointer) {
                    cc._type = cc._a._type.copy();
                    cc._type._isAddressable = false;
                } else {
                    env.diag.report(E_BAD_OP_FOR_TYPE, cc, "unary");
                }
                break;
                
            case CTokenType.NOT:
                if(!(tt instanceof CTypeBooleanSensitive)) {
                    env.diag.report(E_EXPR_CANT_GO_BOOL, cc._a);
                    return;
                }
                
                cc._a = exprToBool(cc._a);
                cc._type = cc._a._type;
                
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
                    val._const_val = new CompileTimeInteger(val._expr._type.sizeOf(env));                
                    if(Env.DEBUG) {
                        System.out.println("DEBUG: sizeof(expr) " + val + " = " + val._const_val.toString());
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
                    ty._const_val = new CompileTimeInteger(ty._tname._type.sizeOf(env));                
                    if(Env.DEBUG) {
                        System.out.println("DEBUG: sizeof(type) " + ty + " = " + ty._const_val.toString());
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
            
            if(cc._fun instanceof CodeExprId) {
                
                cc._type = new CTypeBase();
                
                CSymbol sym = env.getSymbol(((CodeExprId) cc._fun)._id._s);
                
                // we've found a function name, but we haven't checked that
                // function yet; add in a phase to re-check the function
                // and we'll assume for now that it's correct.
                if(Type.FUNC_DECL == sym.type || Type.FUNC_DEF == sym.type) {
                    
                    env.addPhase(new ParseTreeNodePhase(cc, this) {
                        public boolean apply(Env env, Code code) {
                            visitor.visit(code);
                            return !Reporter.errorReported();
                        }
                    });
                    return;
                }
            }
            
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
            
            // add in a type cast if necessary
            cc._argl.set(i, assignTo(expr, argt));
        }
        
        cc._type = funt._retType;
    }
    
    /*
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
    */
    
    /**
     * Go check for a field in a struct/union.
     * @param expr
     * @param field_name
     * @param is_pointer
     * @return
     */
    private CType checkField(CodeExprAccess cc, CodeExpr expr, String field_name, boolean is_pointer) {
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
        int i = 0;
        int size = 0;
        
        for(CTypeField field : ct._fields) {
            
            if(0 != field._padding) {
                ++i; // add in "padding"
            }
            
            if(0 == field._id._s.compareTo(field_name)) {
                if(tt._isAddressable) {
                    field._type._isAddressable = true;
                }
                cc._offset = i;
                return field._type;
            }
            ++i;
        }
        
        return builder.INVALID_TYPE;
    }

    public void visit(CodeExprField cc) {
        cc._ob.acceptVisitor(this);
        cc._type = checkField(cc, cc._ob, cc._id._s, false);
    }

    public void visit(CodeExprPointsTo cc) {
        cc._ob.acceptVisitor(this);
        cc._type = checkField(cc, cc._ob, cc._id._s, true);
    }
}
