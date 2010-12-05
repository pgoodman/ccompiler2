package com.pag.comp;

import static com.smwatt.comp.C.*;

import static com.pag.diag.Message.*;

import com.pag.sym.Env;
import com.smwatt.comp.C.CodeDeclaratorParen;

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

    }

    public void visit(CodeCharacterConstant cc) {

    }

    public void visit(CodeIntegerConstant cc) {

    }

    public void visit(CodeFloatingConstant cc) {

    }

    public void visit(CodeEnumerationConstant cc) {

    }

    public void visit(CodeDotDotDot cc) {

    }

    public void visit(CodeSpecifierStorage cc) {

    }

    public void visit(CodeSpecifierQualifier cc) {

    }

    public void visit(CodeSpecifierType cc) {

    }

    public void visit(CodeSpecifierTypedefName cc) {

    }

    public void visit(CodeSpecifierStruct cc) {

    }

    public void visit(CodeSpecifierUnion cc) {

    }

    public void visit(CodeSpecifierEnum cc) {

    }

    public void visit(CodeEnumerator cc) {

    }
    
    public void visit(CodeDeclaratorParen cc) {
    }

    public void visit(CodeDeclaratorArray cc) {

    }

    public void visit(CodeDeclaratorFunction cc) {

    }

    public void visit(CodeDeclaratorInit cc) {
 
    }

    public void visit(CodeDeclaratorPointer cc) {
 
    }

    public void visit(CodeDeclaratorWidth cc) {

    }

    public void visit(CodeDeclaratorId cc) {

    }

    public void visit(CodePointerStar cc) {

    }

    public void visit(CodeInitializerValue cc) {

    }

    public void visit(CodeInitializerList cc) {

    }

    public void visit(CodeStatBreak cc) {

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
    }

    public void visit(CodeStatDefault cc) {
        cc._stat.acceptVisitor(this);        
    }

    public void visit(CodeStatDo cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatExpression cc) {

    }

    public void visit(CodeStatFor cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatGoto cc) {
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

    }

    public void visit(CodeStatSwitch cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatWhile cc) {
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeExprAssignment cc) {

    }

    public void visit(CodeExprCast cc) {

    }

    public void visit(CodeExprConditional cc) {

    }

    public void visit(CodeExprInfix cc) {

    }

    public void visit(CodeExprParen cc) {

    }

    public void visit(CodeExprPostfix cc) {

    }

    public void visit(CodeExprPrefix cc) {

    }

    public void visit(CodeExprId cc) {

    }

    public void visit(CodeExprSizeofValue cc) {

    }

    public void visit(CodeExprSizeofType cc) {

    }

    public void visit(CodeExprCall cc) {

    }

    public void visit(CodeExprSubscript cc) {

    }

    public void visit(CodeExprField cc) {

    }

    public void visit(CodeExprPointsTo cc) {

    }
    
}
