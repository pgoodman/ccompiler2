package com.pag.comp;

import static com.smwatt.comp.C.*;

import static com.pag.diag.Message.*;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;

/**
 * Visitor for doing a single pass through a C file and collecting all of the
 * top-level function declarations. This is so that we don't report errors
 * on functions that are not static but also not defined.
 * 
 * This class is also responsible for reporting a number of errors relating
 * to what cannot be done at the top-level scope.
 * 
 * @author petergoodman
 *
 */
public class FunctionVisitor implements CodeVisitor {
    
    private Env env;
    private boolean found_static;
    
    public FunctionVisitor(Env static_env) {
        env = static_env;
    }

    public void visit(Code cc) {
        cc.acceptVisitor(this);
    }

    public void visit(CodeUnit cc) {
        cc._scope = env.getScope();
        for(Code code : cc._l) {
            code.acceptVisitor(this);
        }
    }

    /**
     * Visit a function definition.
     */
    public void visit(CodeFunction cc) {
        
        found_static = false;
        for(CodeSpecifier spec : cc._lspec) {
            spec.acceptVisitor(this);
        }
        
        cc._is_static = found_static;
        
        if(found_static) {
            return;
        }
        cc._head.acceptVisitor(this);
        
        CodeId func_id = cc._head.getOptId();
        CSymbol sym = env.getSymbol(func_id._s);
        
        if(null != sym) {
            
            // multiple definitions of the same function
            if(Type.FUNC_DEF == sym.type) {
                env.diag.report(
                    E_FUNC_DEF_REPEAT, 
                    cc, 
                    func_id._s, 
                    sym.code.getSourcePosition()
                );
            
            // we've previously declared this function
            } else if(Type.FUNC_DECL == sym.type) {
                sym.type = Type.FUNC_DEF;
                sym.code = cc;
            }
        } else {
            env.addSymbol(func_id._s, Type.FUNC_DEF, cc);
        }
    }

    public void visit(CodeDeclaration cc) {
        //func_id = null;
        found_static = false;
        
        if(null != cc._lspec) {
            for(CodeSpecifier spec : cc._lspec) {
                spec.acceptVisitor(this);
            }
        }
        
        cc._is_static = found_static;
        
        if(found_static) {
            return;
        }
        
        if(null != cc._ldtor) {
            for(CodeDeclarator dtor : cc._ldtor) {
                if(null == dtor) {
                    continue;
                }
                
                //func_id = null;
                dtor.acceptVisitor(this);

                CodeDeclaratorFunction func = dtor.getOptFunction();
                
                if(null == func) {
                    continue;
                }
                
                CodeId func_id = func.getOptId();
                CSymbol sym = env.getSymbol(func_id._s);
                
                if(null != sym) {
                    if(Type.FUNC_DEF == sym.type) {
                        env.diag.report(
                            N_FUNC_DECL_REDUNDANT, 
                            func_id, sym.code.getSourcePosition()
                        );
                        
                        sym.declarations.add(cc);
                    } else if(Type.FUNC_DECL == sym.type) {
                        env.diag.report(N_FUNC_DECL_REPEAT, func_id);
                        sym.declarations.add(cc);
                    }
                } else {
                    sym = env.addSymbol(func_id._s, Type.FUNC_DECL, func_id);
                    sym.declarations.add(cc);
                }
            }
        }
        
        
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
        found_static = found_static || cc._spec.isStatic();
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
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatCase cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatCompound cc) {
        for(CodeStat stat : cc._lstat) {
            stat.acceptVisitor(this);
        }
    }

    public void visit(CodeStatContinue cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatDefault cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatDo cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatExpression cc) {
        env.diag.report(R_TOP_LEVEL_EXPR, cc);
    }

    public void visit(CodeStatFor cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatGoto cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatIf cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatLabeled cc) {
        env.diag.report(R_TOP_LEVEL_LABEL, cc, cc);
        cc._stat.acceptVisitor(this);
    }

    public void visit(CodeStatReturn cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatSwitch cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
    }

    public void visit(CodeStatWhile cc) {
        env.diag.report(R_TOP_LEVEL_STAT, cc);
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

    public void visit(CodeDeclaratorParen cc) {

    }
    
}
