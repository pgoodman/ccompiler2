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

import static com.smwatt.comp.CType.*;

public class TypeInferenceVisitor implements CodeVisitor {
    
    private Env env;
    private CTypeBuilder builder;
    private CTypePrinter printer;
    private CType STRING_TYPE;
    
    public TypeInferenceVisitor(Env ee) {
        env = ee;
        builder = new CTypeBuilder(ee);
        printer = new CTypePrinter(System.out);
        
        STRING_TYPE = new CTypePointer(new CTypeChar());
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
        cc._type = builder.formType(cc._lspec, cc._dtor);
    }

    public void visit(CodeString cc) {
        cc._type = STRING_TYPE;
        cc._const_val = cc._s;
    }

    public void visit(CodeCharacterConstant cc) {
        cc._type = new CTypeChar();
        char[] as_array = cc._s.toCharArray();
        cc._const_val = new Integer(as_array[0]);
    }

    public void visit(CodeIntegerConstant cc) {
        cc._type = new CTypeInt();
        cc._const_val = Integer.valueOf(cc._s);
    }

    public void visit(CodeFloatingConstant cc) {
        // TODO: float or double?
        cc._type = new CTypeFloat();
        cc._const_val = Float.valueOf(cc._s);
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
            // TODO: check that type is integer
        }
    }

    public void visit(CodeDeclaratorArray cc) {
        if(null != cc._optIndex) {
            cc._optIndex.acceptVisitor(this);
            // TODO check that type is integer
        }
    }

    public void visit(CodeDeclaratorFunction cc) {
        if(null != cc._argl) {
            for(Code arg : cc._argl) {
                arg.acceptVisitor(this);
            }
        }
        
        // note: functions can't return arrays, and so we don't need
        //       to worry about checking any types of constant expressions
        //       in the return type of a function.
    }

    public void visit(CodeDeclaratorInit cc) {
        cc._initializer.acceptVisitor(this);
        cc._dtor.acceptVisitor(this);
        cc._initializer.acceptVisitor(this);
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
        cc._expr.acceptVisitor(this);
        
 
        // TODO check expression?
        cc._type = new CSizeT();
        
        env.addPhase(new ParseTreeNodePhase(cc) {
            public boolean apply(Env env, Code code) {
                CodeExprSizeofValue val = (CodeExprSizeofValue) node;
                val._const_val = new Integer(val._expr._type.sizeOf());                
                if(Env.DEBUG) {
                    System.out.println("DEBUG: sizeof(expr) " + val + " = " + val._const_val);
                }
                return true;
            }
        });
    }

    public void visit(CodeExprSizeofType cc) {
        // TODO check type?
                
        cc._tname.acceptVisitor(this);
        cc._type = new CSizeT();
        
        env.addPhase(new ParseTreeNodePhase(cc) {
            public boolean apply(Env env, Code code) {
                CodeExprSizeofType ty = (CodeExprSizeofType) node;
                ty._const_val = new Integer(ty._tname._type.sizeOf());                
                if(Env.DEBUG) {
                    System.out.println("DEBUG sizeof(type) " + ty + " = " + ty._const_val);
                }
                return true;
            }
        });
    }

    public void visit(CodeExprCall cc) {
        // TODO: check func arg types against function type
        cc._fun.acceptVisitor(this);
        for(CodeExpr expr : cc._argl) {
            expr.acceptVisitor(this);
        }
        
        // TODO function pointers?
        // TODO yield type
        
    }

    public void visit(CodeExprSubscript cc) {
        // TODO check type of index
        cc._idx.acceptVisitor(this);
        
        // TODO check type of expression
        cc._arr.acceptVisitor(this);

        // TODO yield type
    }

    public void visit(CodeExprField cc) {
        // TODO check that the expr is a struct/union
        cc._ob.acceptVisitor(this);
        
        // TODO check that the struct/union has the field
        // TODO yield type
    }

    public void visit(CodeExprPointsTo cc) {
        // TODO check that the expr is a struct/union pointer
        cc._ptr.acceptVisitor(this);
        
        // TODO check that the struct/union has the field
        // TODO yield type
    }
}
