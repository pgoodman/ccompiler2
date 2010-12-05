package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.Env;
import com.pag.sym.Type;
import com.smwatt.comp.CTypeBuilder;
import com.smwatt.comp.CTypePrinter;
import com.smwatt.comp.CType;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.C.CodeDeclaration;
import com.smwatt.comp.CType.CTypePointing;

import static com.smwatt.comp.CType.*;

public class TypeInferenceVisitor implements CodeVisitor {
    
    private Env env;
    private CTypeBuilder builder;
    private CTypePrinter printer;
    //private CType STRING_TYPE;
    private CType CHAR_TYPE;
    
    public TypeInferenceVisitor(Env ee) {
        env = ee;
        builder = new CTypeBuilder(ee);
        printer = new CTypePrinter(System.out);
        
        //STRING_TYPE = new CTypePointer(new CTypeChar());
        CHAR_TYPE = new CTypeChar();
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
                
                // makes sure we don't try to use function types unless they
                // are function pointers
                if(!dtor._is_typedef) {
                    if(id._type instanceof CTypeFunction) {
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
        
        cc._const_val = cc._s;
    }

    public void visit(CodeCharacterConstant cc) {
        cc._type = CHAR_TYPE;
        char[] as_array = cc._s.toCharArray();
        cc._const_val = new Integer(as_array[0]);
    }

    public void visit(CodeIntegerConstant cc) {
        cc._type = new CTypeInt();
        cc._const_val = Integer.valueOf(cc._s);
    }

    public void visit(CodeFloatingConstant cc) {
        cc._type = new CTypeDouble();
        cc._const_val = Double.valueOf(cc._s);
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
        if(null != cc._optParts) {
            for(Code part : cc._optParts) {
                part.acceptVisitor(this);
            }
        }
    }

    public void visit(CodeSpecifierUnion cc) {
        if(null != cc._optParts) {
            for(Code part : cc._optParts) {
                part.acceptVisitor(this);
            }
        }
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
        // TODO ?
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

    public void visit(CodeExprInfix cc) {
        // TODO check types of both sides with operator
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        // TODO yield type
    }

    public void visit(CodeExprParen cc) {
        cc._expr.acceptVisitor(this);
        cc._type = cc._expr._type;
    }

    public void visit(CodeExprPostfix cc) {
        // TODO check type of expression with operator
        cc._a.acceptVisitor(this);
        // TODO yield type
    }

    public void visit(CodeExprPrefix cc) {
        
        // TODO function pointers?
        
        // TODO check type of expression with operator
        cc._a.acceptVisitor(this);
        // TODO yield type
    }

    public void visit(CodeExprId cc) {
        cc._type = cc._scope.get(cc._id._s).code._type;
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
        
        // TODO check expression?
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
        
        // TODO check type?
                
        cc._tname.acceptVisitor(this);
        cc._type = new CSizeT();
    }

    public void visit(CodeExprCall cc) {
        
        // TODO: check func arg types against function type
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
