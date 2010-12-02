package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.Env;

/**
 * Go through the parse tree and look at the uses of different types of
 * names.
 * 
 * @author petergoodman
 *
 */
public class NameVisitor implements CodeVisitor {
    
    private Env env;
    private CodeFunction func = null;
    private int switch_count = 0;
    
    public NameVisitor(Env ee) {
        env = ee;
    }

    public void visit(Code cc) {
        cc._scope = env.getScope();
        cc.acceptVisitor(this);
    }

    public void visit(CodeUnit cc) {
        cc._scope = env.getScope();
        for(Code code : cc._l) {
            code.acceptVisitor(this);
        }
    }

    public void visit(CodeFunction cc) {
        func = cc;
        cc._scope = env.getScope();
        
        env.pushScope(func);
        
        CodeId id = cc._head.getOptId();
        
        cc._head.acceptVisitor(this);
        
        for(CodeDeclaration decl : cc._ldecl) {
            decl.acceptVisitor(this);
        }
        
        cc._body.acceptVisitor(this);
        
        env.popScope();
        func = null;
    }

    public void visit(CodeDeclaration cc) {
        cc._scope = env.getScope();        
    }

    public void visit(CodeId cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeTypeName cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeString cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeCharacterConstant cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeIntegerConstant cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeFloatingConstant cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeEnumerationConstant cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDotDotDot cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierStorage cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierQualifier cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierType cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierTypedefName cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierStruct cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierUnion cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeSpecifierEnum cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeEnumerator cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorArray cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorFunction cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorInit cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorPointer cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorWidth cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorId cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodePointerStar cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeInitializerValue cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeInitializerList cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeStatBreak cc) {
    }

    public void visit(CodeStatCase cc) {
        cc._scope = env.getScope();
        if(0 == switch_count) {
            env.diag.report(E_CASE_OUTSIDE_SWITCH, cc);
        }
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatCompound cc) {
        cc._scope = env.getScope();
        env.pushScope(func);
        for(CodeDeclaration decl : cc._ldecl) {
            decl.acceptVisitor(this);
        }
        for(CodeStat stat : cc._lstat) {
            stat.acceptVisitor(this);
        }
        env.popScope();
    }

    public void visit(CodeStatContinue cc) {
    }

    public void visit(CodeStatDefault cc) {
        cc._scope = env.getScope();
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatDo cc) {
        cc._scope = env.getScope();
        cc._stat.acceptVisitor(this);
        cc._test.acceptVisitor(this);
    }

    public void visit(CodeStatExpression cc) {
        cc._scope = env.getScope();
        if(null != cc._optExpr) {
            cc._optExpr.acceptVisitor(this);
        }
    }

    public void visit(CodeStatFor cc) {
        cc._scope = env.getScope();
        if(null != cc._optInit) {
            cc._optInit.acceptVisitor(this);
        }
        if(null != cc._optTest) {
            cc._optTest.acceptVisitor(this);
        }
        if(null != cc._optStep) {
            cc._optStep.acceptVisitor(this);
        }
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatGoto cc) {
        cc._scope = env.getScope();
        CodeStatLabeled label = func.labels.get(cc._label._s);
        if(null == label) {
            env.diag.report(E_LABEL_UNKNOWN, cc, cc._label._s, func._head.getOptId()._s);
        } else {
            label.is_used = true;
        }
    }

    public void visit(CodeStatIf cc) {
        cc._scope = env.getScope();
        cc._test.acceptVisitor(this);
        cc._thstat.acceptVisitor(this);
        if(null != cc._optElstat) {
            cc._optElstat.acceptVisitor(this);
        }
    }

    public void visit(CodeStatLabeled cc) {
        cc._scope = env.getScope();
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatReturn cc) {
        cc._scope = env.getScope();
        if(null != cc._optExpr) {
            cc._optExpr.acceptVisitor(this);
        }
    }

    public void visit(CodeStatSwitch cc) {
        cc._scope = env.getScope();
        ++switch_count;
        cc._expr.acceptVisitor(this);
        cc._stat.acceptVisitor(this);
        --switch_count;
    }

    public void visit(CodeStatWhile cc) {
        cc._scope = env.getScope();
        cc._test.acceptVisitor(this);
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeExprAssignment cc) {
        cc._scope = env.getScope();
        // TODO
    }

    public void visit(CodeExprCast cc) {
        cc._scope = env.getScope();
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprConditional cc) {
        cc._scope = env.getScope();
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprInfix cc) {
        cc._scope = env.getScope();
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprParen cc) {
        cc._scope = env.getScope();
        cc._expr.acceptVisitor(this);
    }

    public void visit(CodeExprPostfix cc) {
        cc._scope = env.getScope();
        cc._a.acceptVisitor(this);
    }

    public void visit(CodeExprPrefix cc) {
        cc._scope = env.getScope();
        cc._a.acceptVisitor(this);
    }

    public void visit(CodeExprId cc) {
        // TODO
    }

    public void visit(CodeExprSizeofValue cc) {
        cc._scope = env.getScope();
        cc._expr.acceptVisitor(this);
    }

    public void visit(CodeExprSizeofType cc) {
        cc._scope = env.getScope();
        cc._tname.acceptVisitor(this);
    }

    public void visit(CodeExprCall cc) {
        cc._scope = env.getScope();
        // TODO
    }

    public void visit(CodeExprSubscript cc) {
        cc._scope = env.getScope();
        cc._arr.acceptVisitor(this);
        cc._idx.acceptVisitor(this);
    }

    public void visit(CodeExprField cc) {
        // TODO
    }

    public void visit(CodeExprPointsTo cc) {
        // TODO
    }
    
}
