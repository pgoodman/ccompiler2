package com.pag.comp;

import static com.smwatt.comp.C.*;

import static com.pag.diag.Message.*;

import com.pag.sym.Env;

/**
 * Collect all labels inside a function into a hashtable stored in that
 * function. This lets us check that we can actually do a goto to a label.
 * 
 * This also makes sure that no inner functions are defined.
 * 
 * @author petergoodman
 *
 */
public class LabelVisitor implements CodeVisitor {
    
    private Env env;
    private CodeFunction last_func = null;
    
    public LabelVisitor(Env ee) {
        env = ee;
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
        if(null != last_func) {
            env.diag.report(E_INNER_FUNC, cc);
        } else {
            last_func = cc;
            cc._body.acceptVisitor(this);
            last_func = null;
        }
    }

    public void visit(CodeDeclaration cc) {
        
    }

    public void visit(CodeId cc) {
        
    }

    public void visit(CodeTypeName cc) {
        
    }

    public void visit(CodeString cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeCharacterConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeIntegerConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeFloatingConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeEnumerationConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDotDotDot cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierStorage cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierQualifier cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierType cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierTypedefName cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierStruct cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierUnion cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierEnum cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeEnumerator cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorArray cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorFunction cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorInit cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorPointer cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorWidth cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorId cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodePointerStar cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeInitializerValue cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeInitializerList cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatBreak cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatCase cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatCompound cc) {
        for(CodeStat stat : cc._lstat) {
            stat.acceptVisitor(this);
        }        
    }

    public void visit(CodeStatContinue cc) {
        // TODO Auto-generated method stub
    }

    public void visit(CodeStatDefault cc) {
        // TODO Auto-generated method stub
        cc._stat.acceptVisitor(this);        
    }

    public void visit(CodeStatDo cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatExpression cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatFor cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatGoto cc) {
        // TODO Auto-generated method stub
    }

    public void visit(CodeStatIf cc) {
        cc._thstat.acceptVisitor(this);
        if(null != cc._optElstat) {
            cc._optElstat.acceptVisitor(this);
        }
    }

    public void visit(CodeStatLabeled cc) {
        String label = cc._label._s;
        CodeStatLabeled label_cc = last_func.labels.get(label);
        if(null != label_cc) {
            env.diag.report(E_LABEL_REPEAT, cc, label, label_cc.getSourcePosition());
        } else {
            last_func.labels.put(label, cc);
        }
        cc._stat.acceptVisitor(this);        
    }

    public void visit(CodeStatReturn cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatSwitch cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatWhile cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeExprAssignment cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprCast cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprConditional cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprInfix cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprParen cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprPostfix cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprPrefix cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprId cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprSizeofValue cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprSizeofType cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprCall cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprSubscript cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprField cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprPointsTo cc) {
        // TODO Auto-generated method stub
        
    }
    
}
