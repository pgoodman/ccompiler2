package com.pag.comp;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import com.pag.val.CompileTimeFloat;
import com.pag.val.CompileTimeValue;
import com.pag.val.CompileTimeInteger;

import static com.pag.diag.Message.*;

import com.smwatt.comp.CTokenType;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.CType.CTypeFloating;
import com.smwatt.comp.CType.CTypeIntegral;

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
    
    public ExpressionInterpreterVisitor(Env ee) {
        env = ee;
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
        //env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeCharacterConstant cc) { }
    
    public void visit(CodeIntegerConstant cc) { }
    
    public void visit(CodeFloatingConstant cc) { }
    
    // by this time we expect that the enumerator opt values have all been
    // substituted with actual values.
    public void visit(CodeEnumerationConstant cc) {
        CSymbol sym = cc._scope.get(cc._s, Type.ENUMERATOR);
        cc._const_val = ((CodeEnumerator) sym.code)._optValue._const_val;
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
        cc._value.acceptVisitor(this);
        cc._const_val = cc._value._const_val;
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
        if(null != cc._const_val) {
            return;
        }
        
        cc._expr.acceptVisitor(this);
        
        if(null == cc._expr._const_val) {
            return;
        }
        
        if(cc._type instanceof CTypeIntegral
        && cc._expr._type instanceof CTypeFloating) {
            cc._const_val = new CompileTimeInteger(
                (int) ((CompileTimeFloat) cc._expr._const_val).value
            );
        } else if(cc._type instanceof CTypeFloating
               && cc._expr._type instanceof CTypeIntegral) {
            cc._const_val = new CompileTimeFloat(
                (double) ((CompileTimeInteger) cc._expr._const_val).value
            );
        }
    }
    
    public void visit(CodeExprConditional cc) {
        
        if(null != cc._const_val) {
            return;
        }
        
        cc._test.acceptVisitor(this);
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        if(null == cc._test._const_val
        || null == cc._a._const_val
        || null == cc._b._const_val) {
            return;
        }
        
        CompileTimeInteger test = (CompileTimeInteger) cc._test._const_val;
        cc._const_val = test.value == 1 ? cc._a._const_val : cc._b._const_val;
    }
    
    public void visit(CodeExprInfix cc) {
        
        if(null != cc._const_val) {
            return;
        }
        
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
        
        CompileTimeValue av = cc._a._const_val;
        CompileTimeValue bv = cc._b._const_val;
        CompileTimeValue ret = null;
        
        if(null == av || null == bv) {
            return;
        }
        
        switch(cc._op._type) {
            case CTokenType.COMMA: ret = bv; break;
            case CTokenType.VBAR_VBAR: ret = av.log_or(bv); break;
            case CTokenType.AMP_AMP: ret = av.log_and(bv); break;
            case CTokenType.VBAR: ret = av.bit_or(bv); break;
            case CTokenType.XOR: ret = av.bit_xor(bv); break;
            case CTokenType.AMP: ret = av.bit_and(bv); break;
            case CTokenType.EQUALS: ret = av.equals(bv); break;
            case CTokenType.NOT_EQUALS: ret = av.not_equals(bv); break;
            case CTokenType.LT: ret = av.bit_or(bv); break;
            case CTokenType.GT: ret = av.less_than(bv); break;
            case CTokenType.LT_EQ: ret = av.less_than_equal(bv); break;
            case CTokenType.GT_EQ: ret = av.bit_or(bv); break;
            case CTokenType.LSH: ret = av.bit_left_shift(bv); break;
            case CTokenType.RSH: ret = av.bit_right_shift(bv); break;
            case CTokenType.PLUS: ret = av.add(bv); break;
            case CTokenType.MINUS: ret = av.subtract(bv); break;
            case CTokenType.STAR: ret = av.multiply(bv); break;
            case CTokenType.SLASH: ret = av.divide(bv); break;
            case CTokenType.MOD: ret = av.modulo(bv); break;
        }
        
        if(null == ret) {
            env.diag.report(E_NON_CONSTANT_EXPR, cc);
            return;
        }
        
        cc._const_val = ret;
    }
    
    public void visit(CodeExprParen cc) {
        cc._expr.acceptVisitor(this);
    }
    
    public void visit(CodeExprPostfix cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
    public void visit(CodeExprPrefix cc) {
        
        if(null != cc._const_val) {
            return;
        }
        
        this.visit(cc._a);
        
        switch(cc._op._type) {
            case CTokenType.PLUS_PLUS:
            case CTokenType.MINUS_MINUS:
            case CTokenType.AMP:
                env.diag.report(E_NON_CONSTANT_EXPR, cc);
                return;
                
            case CTokenType.STAR:
                cc._const_val = cc._a._const_val.dereference();                
                if(null == cc._const_val) {
                    env.diag.report(E_NON_CONSTANT_EXPR, cc);
                }
                return;
        }
        
        CompileTimeValue res = cc._a._const_val;
        
        switch(cc._op._type) {
            case CTokenType.PLUS: 
                cc._const_val = res.positive();
                break;
            case CTokenType.MINUS:
                cc._const_val = res.negative();
                break;
            case CTokenType.TILDE:
                cc._const_val = res.bit_not();
                break;
            case CTokenType.NOT:
                cc._const_val = res.log_not();
                break;
                
        }
    }
    
    public void visit(CodeExprId cc) {
        CSymbol sym = cc._scope.get(cc._id._s);
        if(Type.ENUMERATOR != sym.type) {
            env.diag.report(E_NON_CONSTANT_EXPR, cc);
        } else {
            cc._const_val = ((CodeEnumerator) sym.code)._optValue._const_val;
        }
    }
    
    public void visit(CodeExprSizeofValue cc) {
        if(null == cc._const_val) {
            cc._const_val = new CompileTimeInteger(cc._expr._type.sizeOf(env));
        }
    }
    
    public void visit(CodeExprSizeofType cc) {
        if(null == cc._const_val) {
            cc._const_val = new CompileTimeInteger(cc._tname._type.sizeOf(env));
        }
    }
    
    public void visit(CodeExprCall cc) {
        env.diag.report(E_NON_CONSTANT_EXPR, cc);
    }
    
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
