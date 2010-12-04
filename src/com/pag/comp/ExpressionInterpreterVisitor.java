package com.pag.comp;

import com.pag.sym.Env;
import com.smwatt.comp.C.Code;
import static com.smwatt.comp.C.*;

/**
 * A visitor that will go an interpret constant expressions. It will report
 * errors if a constant expression cannot be interpreted.
 * 
 * @author petergoodman
 *
 */
public class ExpressionInterpreterVisitor implements CodeVisitor {
    
    private Env env;
    private Object result = null;
    
    public ExpressionInterpreterVisitor(Env ee) {
        env = ee;
    }
    
    public Object yield() {
        Object ret = result;
        result = null;
        return ret;
    }
    
    public void visit(Code cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeUnit cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeFunction cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeDeclaration cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeId cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeTypeName cc) {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatCompound cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatContinue cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatDefault cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatDo cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatExpression cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatFor cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatGoto cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatIf cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatLabeled cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatReturn cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatSwitch cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatWhile cc) {
        // TODO Auto-generated method stub
        
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
