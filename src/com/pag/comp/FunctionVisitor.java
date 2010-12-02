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
    private CodeId func_id;
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
        
        if(found_static) {
            return;
        }
        cc._head.acceptVisitor(this);
        
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
        func_id = null;
        found_static = false;
        
        for(CodeSpecifier spec : cc._lspec) {
            spec.acceptVisitor(this);
        }
        
        if(found_static) {
            return;
        }
        
        for(CodeDeclarator decl : cc._ldtor) {
            decl.acceptVisitor(this);
        }
        
        if(null != func_id) {
            CSymbol sym = env.getSymbol(func_id._s);
            if(null != sym) {
                if(Type.FUNC_DEF == sym.type) {
                    env.diag.report(N_FUNC_DECL_REDUNDANT, cc, sym.code.getSourcePosition());
                    sym.declarations.add(cc);
                } else if(Type.FUNC_DECL == sym.type) {
                    env.diag.report(N_FUNC_DECL_REPEAT, cc);
                    sym.declarations.add(cc);
                }
            } else {
                sym = env.addSymbol(func_id._s, Type.FUNC_DECL, cc);
                sym.declarations.add(cc);
            }
        }
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
        found_static = found_static || cc._spec.isStatic();
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
        if(null != cc._optFn) {
            cc._optFn.acceptVisitor(this);
        }
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
        func_id = cc._id;
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
