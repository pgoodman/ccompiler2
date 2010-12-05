package com.pag.comp;

import java.util.LinkedList;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import static com.pag.diag.Message.*;

import com.smwatt.comp.CTokenType;
import com.smwatt.comp.C.Code;

import static com.smwatt.comp.C.*;

/**
 * A visitor that will go an interpret constant expressions. It will report
 * errors if a constant expression cannot be interpreted. In this pass, we
 * assume that type checking has completed successfully.
 * 
 * @author petergoodman
 *
 */
public class ExpressionInterpreterVisitor implements CodeVisitor {
    
    private Env env;
    private LinkedList<Object> results = new LinkedList<Object>();
    
    public ExpressionInterpreterVisitor(Env ee) {
        env = ee;
    }
    
    public Object yield() {
        return results.pop();
    }
    
    public void visit(Code cc) {
        cc.acceptVisitor(this);
    }
    
    public void visit(CodeUnit cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeFunction cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaration cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeId cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeTypeName cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeString cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeCharacterConstant cc) {
        results.push(cc._const_val);
    }
    
    public void visit(CodeIntegerConstant cc) {
        results.push(cc._const_val);
    }
    
    public void visit(CodeFloatingConstant cc) {
        results.push(cc._const_val);
    }
    
    // by this time we expect that the enumerator opt values have all been
    // substituted with actual values.
    public void visit(CodeEnumerationConstant cc) {
        CSymbol sym = cc._scope.get(cc._s, Type.ENUMERATOR);
        results.push(((CodeEnumerator) sym.code)._optValue._const_val);
    }
    
    public void visit(CodeDotDotDot cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierStorage cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierQualifier cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierType cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierTypedefName cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierStruct cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierUnion cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeSpecifierEnum cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeEnumerator cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaratorArray cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaratorFunction cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaratorInit cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaratorPointer cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaratorWidth cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeDeclaratorId cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodePointerStar cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeInitializerValue cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeInitializerList cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatBreak cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatCase cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatCompound cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatContinue cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatDefault cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatDo cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatExpression cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatFor cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatGoto cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatIf cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatLabeled cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatReturn cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatSwitch cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeStatWhile cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeExprAssignment cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeExprCast cc) {
        // TODO
        cc._expr.acceptVisitor(this);
    }
    
    public void visit(CodeExprConditional cc) {
        // TODO
        cc._test.acceptVisitor(this);
        cc._thexpr.acceptVisitor(this);
        cc._elexpr.acceptVisitor(this);
    }
    
    public void visit(CodeExprInfix cc) {
        // TODO check op
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        switch(cc._op._type) {
            case CTokenType.COMMA:
            case CTokenType.VBAR_VBAR:
            case CTokenType.AMP_AMP:
            case CTokenType.VBAR:
            case CTokenType.XOR:
            case CTokenType.AMP:
            case CTokenType.EQUALS:
            case CTokenType.NOT_EQUALS:
            case CTokenType.LT:
            case CTokenType.GT:
            case CTokenType.LT_EQ:
            case CTokenType.GT_EQ:
            case CTokenType.LSH:
            case CTokenType.RSH:
            case CTokenType.PLUS:
            case CTokenType.MINUS:
            case CTokenType.STAR:
            case CTokenType.SLASH:
            case CTokenType.MOD:
            
        }
    }
    
    public void visit(CodeExprParen cc) {
        cc._expr.acceptVisitor(this);
    }
    
    public void visit(CodeExprPostfix cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeExprPrefix cc) {
        // TODO
        switch(cc._op._type) {
            case CTokenType.PLUS_PLUS:
            case CTokenType.MINUS_MINUS:
            case CTokenType.AMP:
            case CTokenType.STAR:
            case CTokenType.PLUS:
            case CTokenType.MINUS:
            case CTokenType.TILDE:
            case CTokenType.NOT:
        }
    }
    
    public void visit(CodeExprId cc) {
        CSymbol sym = cc._scope.get(cc._id._s);
        if(Type.ENUMERATOR != sym.type) {
            env.diag.report(E_NON_CONSTANT_EXPR, cc);
        } else {
            cc._const_val = ((CodeEnumerator) sym.code)._optValue._const_val;
            results.push(cc._const_val);
        }
    }
    
    public void visit(CodeExprSizeofValue cc) {
        if(null == cc._const_val) {
            cc._const_val = new Integer(cc._expr._type.sizeOf(env));
        }
        results.push(cc._const_val);
    }
    
    public void visit(CodeExprSizeofType cc) {
        if(null == cc._const_val) {
            cc._const_val = new Integer(cc._tname._type.sizeOf(env));
        }
        results.push(cc._const_val);
    }
    
    public void visit(CodeExprCall cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    /*public void visit(CodeExprSubscript cc) {
        // TODO 
        
    }*/
    
    public void visit(CodeExprField cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeExprPointsTo cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }

    public void visit(CodeDeclaratorParen cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
}
