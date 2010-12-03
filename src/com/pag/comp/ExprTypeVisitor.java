package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.Env;
import com.smwatt.comp.CTypeBuilder;
import com.smwatt.comp.CTypePrinter;
import com.smwatt.comp.CType;
import com.smwatt.comp.C.CodeDeclaration;

import static com.smwatt.comp.CType.*;

public class ExprTypeVisitor implements CodeVisitor {
    
    private Env env;
    private CTypeBuilder builder;
    private CTypePrinter printer;
    
    public ExprTypeVisitor(Env ee) {
        env = ee;
        builder = new CTypeBuilder(ee);
        printer = new CTypePrinter(System.out);
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
        
        // handle old-style types
        for(CodeDeclaration decl : cc._ldecl) {
            for(CodeDeclarator dtor : decl._ldtor) {
                CType arg_type = builder.formType(decl._lspec, dtor);
                dtor._type = arg_type;
                CodeId id = dtor.getOptId();
                if(null != id) {
                    id._type = dtor._type;
                }
                if(null != arg_type) {
                    type._argTypes.add(arg_type);
                }
            }
        }
    }

    public void visit(CodeDeclaration cc) {
        for(CodeDeclarator dtor : cc._ldtor) {
            CType arg_type = builder.formType(cc._lspec, dtor);
            dtor._type = arg_type;
            
            CodeId id = dtor.getOptId();
            if(null != id) {
                id._type = dtor._type;
            }
        }
    }

    public void visit(CodeId cc) {
        if(null == cc._type) {
            
        }
    }

    public void visit(CodeTypeName cc) { }

    public void visit(CodeString cc) { }

    public void visit(CodeCharacterConstant cc) {
        cc._type = new CTypeChar();
    }

    public void visit(CodeIntegerConstant cc) {
        cc._type = new CTypeInt();
    }

    public void visit(CodeFloatingConstant cc) {
        // TODO: float or double?
        cc._type = new CTypeFloat();
    }

    public void visit(CodeEnumerationConstant cc) {
        // TODO
    }

    public void visit(CodeDotDotDot cc) { }

    public void visit(CodeSpecifierStorage cc) { }

    public void visit(CodeSpecifierQualifier cc) { }

    public void visit(CodeSpecifierType cc) { }

    public void visit(CodeSpecifierTypedefName cc) { }

    public void visit(CodeSpecifierStruct cc) { }

    public void visit(CodeSpecifierUnion cc) { }

    public void visit(CodeSpecifierEnum cc) { }

    public void visit(CodeEnumerator cc) { }

    public void visit(CodeDeclaratorArray cc) { }

    public void visit(CodeDeclaratorFunction cc) { }

    public void visit(CodeDeclaratorInit cc) {
        cc._initializer.acceptVisitor(this);
    }

    public void visit(CodeDeclaratorPointer cc) { }

    public void visit(CodeDeclaratorWidth cc) { }

    public void visit(CodeDeclaratorId cc) { }

    public void visit(CodePointerStar cc) { }

    public void visit(CodeInitializerValue cc) {
        cc._value.acceptVisitor(this);
        cc._type = cc._value._type;
    }

    public void visit(CodeInitializerList cc) {
        for(CodeInitializer init : cc._list) {
            init.acceptVisitor(this);
        }
    }

    public void visit(CodeStatBreak cc) { }

    public void visit(CodeStatCase cc) {
        cc._value.acceptVisitor(this);
        // TODO check type of value
        
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
        // TODO yield type
    }

    public void visit(CodeExprCast cc) {
        cc._expr.acceptVisitor(this);
        
        // TODO look for illegal cast
        // TODO calculate type of cast
        // TODO yield type
    }

    public void visit(CodeExprConditional cc) {
        // TODO check test type
        // TODO check that both sides have same type
        cc._test.acceptVisitor(this);
        cc._thexpr.acceptVisitor(this);
        cc._elexpr.acceptVisitor(this);
        // TODO yield type
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
        // TODO check type of expression with operator
        cc._a.acceptVisitor(this);
        // TODO yield type
    }

    public void visit(CodeExprId cc) {
        cc._id.acceptVisitor(this);
        cc._type = cc._id._type;
    }

    public void visit(CodeExprSizeofValue cc) {
        cc._expr.acceptVisitor(this);
        // TODO check expression?
        cc._type = new CSizeT();
    }

    public void visit(CodeExprSizeofType cc) {
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
