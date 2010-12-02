package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;

/**
 * Go through the parse tree and look at the uses of different types of
 * names.
 * 
 * @author petergoodman
 *
 */
public class NameVisitor implements CodeVisitor {
    
    // compilation environment
    private Env env;
    
    // the function that we are inside
    private CodeFunction func = null;
    
    // reference counter for the number of switch statements we are inside.
    // used to detech invalid case statements
    private int switch_count = 0;
    
    // if we're parsing function parameters, then how should we be looking
    // at those parameters?
    private boolean in_func_head = false;
    private boolean in_func_decl_list = false;
    
    private boolean in_func_body = false;
    
    private int declaration_count = 0;
    private int declarator_count = 0;
    private int func_declarator_count = 0;
    
    private int struct_count = 0;
    private int union_count = 0;
    private boolean in_struct = false;
    
    private boolean in_enum = false;
    
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
        String name = cc._head.getOptId()._s;
        
        if(cc._is_static) {
            env.addSymbol(name, Type.FUNC_DEF, cc);
        }
        
        env.pushScope(func);
        
        // go collect function parameter names. if this is an new-style
        // function then we will visit cc._head twice. two passes are done
        // in order to see function parameters of old/new style functions
        // in a canonical way.
        in_func_head = true;
        cc._head.acceptVisitor(this);
        in_func_head = false;
        
        // visit all of the declarations of the function parameters
        in_func_decl_list = true;
        if(null == cc._ldecl) {
            cc._head.acceptVisitor(this);
        } else {
            for(CodeDeclaration decl : cc._ldecl) {
                decl.acceptVisitor(this);
            }
        }
        in_func_decl_list = false;
        
        // visit the body, this will implicitly push on yet another scope
        // as the body is a compound statement
        in_func_body = true;
        cc._body.acceptVisitor(this);
        in_func_body = false;
        
        // go look for unused labels
        for(CodeStatLabeled label : func.labels.values()) {
            if(!label.is_used) {
                env.diag.report(N_LABEL_NOT_USED, label, label._label._s);
            }
        }
        
        env.popScope();
        func = null;
    }

    public void visit(CodeDeclaration cc) {
        cc._scope = env.getScope();   
        ++declaration_count;
        if(null != cc._lspec) {
            for(CodeSpecifier spec : cc._lspec) {
                spec.acceptVisitor(this);
            }
        }
        if(null != cc._ldtor) {
            for(CodeDeclarator decl : cc._ldtor) {
                if(null != decl) {
                    decl.acceptVisitor(this);
                }
            }
        }
        --declaration_count;
    }

    public void visit(CodeId cc) {
        cc._scope = env.getScope();
        String name = cc._s;
        CSymbol sym = env.getSymbol(name);
        
        if(in_func_head) {
            visitFuncParam(name, sym, cc);
        }
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
        String name = cc._id._s;
        CSymbol sym = env.getSymbol(name);
        
        if(null == sym) {
            env.diag.report(B_UNKNOWN_TYPEDEF_NAME, cc, name);
            return;
        }
        
        // variable shadowing this typedef name.
        if(Type.TYPEDEF_NAME != sym.type) {
            env.diag.report(
                E_VAR_SHADOW_TYPEDEF, 
                cc, 
                sym.name, 
                sym.code.getSourcePosition()
            );
        }
    }
    
    private void visitStructOrUnion(CodeSpecifierStructUnionEnum cc) {
        cc._scope = env.getScope();
        
        // named struct/union
        if(null != cc._optId) {
            // TODO
        }
        
        // definition
        if(null != cc._optParts) {
            in_struct = cc instanceof CodeSpecifierStruct;
            int struct_incr = in_struct ? 1 : 0;
            
            struct_count += struct_incr;
            union_count += 1 - struct_incr;
            
            // TODO
            
            union_count -= 1 - struct_incr;
            struct_count -= struct_incr;
            
            in_struct = struct_count > 0;
        }
    }

    public void visit(CodeSpecifierStruct cc) {
        visitStructOrUnion(cc);
    }

    public void visit(CodeSpecifierUnion cc) {
        visitStructOrUnion(cc);
    }

    public void visit(CodeSpecifierEnum cc) {
        cc._scope = env.getScope();
        if(null != cc._optParts) {
            in_struct = false;
            in_enum = true;
            
            in_enum = false;
            in_struct = struct_count > 0;
        }
    }

    public void visit(CodeEnumerator cc) {
        cc._scope = env.getScope();
    }

    public void visit(CodeDeclaratorArray cc) {
        cc._scope = env.getScope();
        if(null != cc._optIndex) {
            cc._optIndex.acceptVisitor(this);
        }
        if(null != cc._optAr) {
            cc._optAr.acceptVisitor(this);
        }
    }

    public void visit(CodeDeclaratorFunction cc) {
        cc._scope = env.getScope();
        
        ++func_declarator_count;
        if(null != cc._optFn) {
            cc._optFn.acceptVisitor(this);
        }
        if(null != cc._argl) {
            for(Code decl : cc._argl) {
                decl.acceptVisitor(this);
            }
        }
        --func_declarator_count;
    }

    public void visit(CodeDeclaratorInit cc) {
        cc._scope = env.getScope();
        
        // visit the initializer before the declarator so that it is illegal
        // to do something like int foo = foo where no global definition of
        // foo exists.
        cc._initializer.acceptVisitor(this);
        
        ++declarator_count;
        cc._dtor.acceptVisitor(this);
        --declarator_count;
    }

    public void visit(CodeDeclaratorPointer cc) {
        cc._scope = env.getScope();
        if(null != cc._optPointee) {
            cc._optPointee.acceptVisitor(this);
        }
    }

    public void visit(CodeDeclaratorWidth cc) {
        cc._scope = env.getScope();
        cc._width.acceptVisitor(this);
        cc._dtor.acceptVisitor(this);
    }
    
    public void visitFuncParam(String name, CSymbol sym, Code cc) {
        if(null != sym) {            
            // repeated parameter
            if(env.getScope() == sym.scope) {
                env.diag.report(
                    E_PARAM_SHADOW, 
                    cc, 
                    name, 
                    sym.code.getSourcePosition()
                );
            }
        } else {
            sym = env.addSymbol(name, Type.VARIABLE, cc);
        }
    }

    public void visit(CodeDeclaratorId cc) {
        cc._scope = env.getScope();
        String name = cc.getOptId()._s;
        CSymbol sym = env.getSymbol(name);
        
        // function parameter list
        if(in_func_head) {
            visitFuncParam(name, sym, cc);
        
        // function declaration list
        } else if(in_func_decl_list) {
            
            // declaration without accompanying parameter
            if(null == sym || cc._scope != sym.scope) {
                env.diag.report(E_UNKNOWN_PARAM_DECL, cc, name);                
            }
        
        // inside of a struct
        } else if(struct_count > 0) {
            
        // inside of a union
        } else if(union_count > 0) {
            
        // global scope or function body, symbol with same name exists
        } else if(null != sym) {
            
            // variable already declared
            if(Type.VARIABLE == sym.type) {
                env.diag.report(
                    E_VAR_REDEF, cc, name, sym.code.getSourcePosition()
                );
            
            // enumerator declared with same name as variable being declared
            } else if(Type.ENUMERATOR == sym.type) {
                env.diag.report(
                    !in_func_body ? E_VAR_SHADOW_ENUMERATOR : W_VAR_SHADOW_ENUMERATOR, 
                    cc, name, sym.code.getSourcePosition()
                );
            
            // name clash between global var and func.
            } else if(Type.FUNC_DECL == sym.type || Type.FUNC_DEF == sym.type) {
                if(0 == func_declarator_count) {
                    env.diag.report(
                        !in_func_body ? E_VAR_REDEF_FUNC : W_VAR_REDEF_FUNC, 
                        cc, name, sym.code.getSourcePosition()
                    );
                }
            }
        
        // global scope or function body, no symbol exists with same name
        } else {
            
        }
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
