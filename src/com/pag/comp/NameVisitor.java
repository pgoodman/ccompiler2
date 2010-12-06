package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import com.smwatt.comp.C.CodeDeclaratorFunction;
import com.smwatt.comp.C.CodeDeclaratorParen;

/**
 * Go through the parse tree and look at the uses of different types of
 * names. The purpose of this pass is to find uses of undefined names,
 * illegal re-definitions of names, note/warn about name shadowing, etc.
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
    
    // does this declaration look like a forward declaration?
    private boolean is_forward_declaration = false;
    
    // is this a typedef?
    private boolean in_typedef = false;
    
    // counters for keeping track of the context
    private int declaration_count = 0;
    private int declarator_count = 0;
    private int func_declarator_count = 0;
    private int func_param_list_count = 0;
    private int pointer_declarator_count = 0;
    
    // special counters + toggle to figure out if we're currently in a struct
    // or a union.
    private int struct_count = 0;
    private int union_count = 0;
    private boolean in_struct = false;
    
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
        
        func._internal_scope = env.pushScope(func);
        
        //CodeDeclaratorFunction func = (CodeDeclaratorFunction) cc._head;
        
        
        
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
        is_forward_declaration = (null == cc._ldtor || cc._ldtor.isEmpty());
        
        boolean found_typedef = false;
        
        if(null != cc._lspec) {
            for(CodeSpecifier spec : cc._lspec) {
                found_typedef = !in_typedef && (found_typedef || spec.isTypedef());
                spec.acceptVisitor(this);
            }
        }
        
        in_typedef = found_typedef;
        
        if(!is_forward_declaration) {
            for(CodeDeclarator decl : cc._ldtor) {
                if(null != decl) {
                    decl.acceptVisitor(this);
                }
            }
        }
        
        in_typedef = false;
        is_forward_declaration = false;
        
        --declaration_count;
    }

    public void visit(CodeId cc) {
        cc._scope = env.getScope();
        String name = cc._s;
        CSymbol sym = env.getSymbol(name);
        
        if(in_func_head) {
            visitFuncParam(name, sym, cc);
        
        } else if(null == sym) {
            env.diag.report(E_VAR_UNKNOWN, cc, name);
        } else {
            //System.out.println("CodeId: " + sym.name + " " + sym.type);
            // TODO ?
        }
    }

    public void visit(CodeTypeName cc) {
        cc._scope = env.getScope();
        for(CodeSpecifier spec : cc._lspec) {
            spec.acceptVisitor(this);
        }
        if(null != cc._dtor) {
            cc._dtor.acceptVisitor(this);
        }
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
        //CSymbol sym = cc._s
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
    
    private void handleNamedCompoundType(CodeSpecifierStructUnionEnum cc) {
        
        String name = cc._optId._s;
        Type sym_type = cc instanceof CodeSpecifierStruct ? Type.STRUCT_NAME : (
            cc instanceof CodeSpecifierUnion ? Type.UNION_NAME : Type.ENUM_NAME
        );
        CSymbol sym = env.getSymbol(name, sym_type);
        String prefix = Type.STRUCT_NAME == sym_type ? "struct" : (
            Type.UNION_NAME == sym_type ? "union" : "enum"
        );
        
        is_forward_declaration = is_forward_declaration && null == cc._optParts;
        
        // was this struct/union ever defined?
        if(null == cc._optParts && !is_forward_declaration) {
            if(null == sym) {
                env.diag.report(
                    E_COMPOUND_TYPE_UNKNOWN, 
                    cc, prefix, name
                );
            }
        
        // re-declaring at same scope
        } else if(null != sym && sym.scope == cc._scope) {
                        
            // re-defining it
            if(null != sym.code) {
                
                env.diag.report(
                    is_forward_declaration ? N_COMPOUND_TYPE_REDEF : E_COMPOUND_TYPE_REDEF, cc,
                    prefix, name,
                    sym.code.getSourcePosition()
                );
            
            // defining a forward-declared version of this type
            } else {
                sym.code = cc;
            }
        
        // declare it
        } else {
                        
            // if this is a forward declaration then leave the code empty
            sym = env.addSymbol(
                name, sym_type, is_forward_declaration ? null : cc
            );
            
            if(is_forward_declaration) {
                sym.declarations.add(cc);
            }
        }
    }
    
    private void visitStructOrUnion(CodeSpecifierStructUnionEnum cc) {
        cc._scope = env.getScope();
        
        boolean got_struct = cc instanceof CodeSpecifierStruct;
        
        // named struct/union
        if(null != cc._optId) {
            handleNamedCompoundType(cc);
        }
        
        // definition
        if(null != cc._optParts) {
            in_struct = got_struct;
            int struct_incr = in_struct ? 1 : 0;
            
            struct_count += struct_incr;
            union_count += 1 - struct_incr;
            
            env.pushScope(func);
            
            for(Code decl : cc._optParts) {
                decl.acceptVisitor(this);
            }
            
            env.popScope();
            
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
        if(null != cc._optId) {            
            handleNamedCompoundType(cc);
        }
        if(null != cc._optParts) {
            in_struct = false;
            
            // !!! no scope is pushed because enumeration ids go into the
            //     enums enclosing scope.
            
            for(Code enumerator : cc._optParts) {
                enumerator.acceptVisitor(this);
            }
            
            in_struct = struct_count > 0;
        }
    }

    public void visit(CodeEnumerator cc) {
        cc._scope = env.getScope();
        
        // make sure the value doesn't depend on the enumerator being
        // defined
        if(null != cc._optValue) {
            cc._optValue.acceptVisitor(this);
        }
        
        String name = cc._id._s;
        CSymbol sym = env.getSymbol(name);
        
        if(null != sym && sym.scope == cc._scope) {
            env.diag.report(
                E_ENUMERATOR_SHADOW, cc, name, sym.code.getSourcePosition()
            );
        } else {
            env.addSymbol(name, Type.ENUMERATOR, cc);
        }
    }
    
    public void visit(CodeDeclaratorParen cc) {
        cc._decl.acceptVisitor(this);
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
            ++func_param_list_count;
            for(Code decl : cc._argl) {
                if(decl instanceof CodeId) {
                    env.addSymbol(((CodeId) decl)._s, Type.VARIABLE, decl);
                } else {
                    decl.acceptVisitor(this);
                }
            }
            --func_param_list_count;
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
        ++pointer_declarator_count;
        if(null != cc._optPointee) {
            cc._optPointee.acceptVisitor(this);
        }
        cc._star.acceptVisitor(this);
        --pointer_declarator_count;
    }

    public void visit(CodeDeclaratorWidth cc) {
        cc._scope = env.getScope();
        cc._width.acceptVisitor(this);
        cc._dtor.acceptVisitor(this);
    }
    
    public void visitFuncParam(String name, CSymbol sym, Code cc) {
        if(null != sym) {            
            // repeated parameter
            env.diag.report(
                (env.getScope() == sym.scope) ? E_PARAM_SHADOW : W_PARAM_SHADOW, 
                cc, 
                name, 
                sym.code.getSourcePosition()
            );
        } else {
            sym = env.addSymbol(name, Type.VARIABLE, cc);
        }
    }

    public void visit(CodeDeclaratorId cc) {
        cc._scope = env.getScope();
        String name = cc.getOptId()._s;
        CSymbol sym = env.getSymbol(name);
        CodeId id = cc._id;
        
        // function parameter list
        if(in_func_head && 0 < func_param_list_count) {
            visitFuncParam(name, sym, cc.getOptId());
        
        // function declaration list
        } else if(in_func_decl_list) {
            
            // declaration without accompanying parameter
            if(0 == (union_count + struct_count)
            && (null == sym || cc._scope != sym.scope)) {
                env.diag.report(E_UNKNOWN_PARAM_DECL, cc, name);                
            }
        
        // inside of a struct / union
        } else if(in_struct || union_count > 0) {
            
            if(null != sym && sym.scope == cc._scope) {
                env.diag.report(E_FIELD_REDEF, cc, name, sym.code.getSourcePosition());
            } else {
                env.addSymbol(name, Type.FIELD, cc);
            }
            
        // global scope or function body, symbol with same name exists
        } else if(null != sym) {
            
            // we're looking at the same symbol :P
            if(sym.code == id) {
                return;
            }
            
            // variable already declared, it will either shadow a global or
            // re-declare a local
            if(Type.VARIABLE == sym.type) {
                env.diag.report(
                    null != func && sym.scope.depth < func._internal_scope.depth ? W_VAR_REDEF : E_VAR_REDEF, 
                    cc, name, sym.code.getSourcePosition()
                );
            
            // enumerator declared with same name as variable being declared
            } else if(Type.ENUMERATOR == sym.type) {
                env.diag.report(
                    !in_func_body ? E_VAR_SHADOW_ENUMERATOR : W_VAR_SHADOW_ENUMERATOR, 
                    cc, name, sym.code.getSourcePosition()
                );
            
            // name clash between global var and func.
            } else if(Type.FUNC_DECL == sym.type || Type.FUNC_DEF == sym.type) {
                if(0 == func_declarator_count 
                || pointer_declarator_count > 0) {
                    
                    env.diag.report(
                        !in_func_body ? E_VAR_REDEF_FUNC : W_VAR_REDEF_FUNC, 
                        cc, name, sym.code.getSourcePosition()
                    );
                }
            
            // name clash with a typedef name
            } else if(Type.TYPEDEF_NAME == sym.type) {
                
                // this typedef name already exists
                if(cc._is_typedef) {
                    
                    // TODO ?
                    System.out.println("typedef/typedef clash.");
                    
                // variable shadowing a typedef name
                } else {
                    
                    // TODO ?
                    System.out.println("var/typedef clash.");
                    
                }
            }
        
        // global scope or function body, no symbol exists with same name
        } else if(0 == func_declarator_count || pointer_declarator_count > 0) {
            //System.out.println("name=" + name +" in_typedef=" + in_typedef + " cc._is_typedef=" + cc._is_typedef);
            cc._is_typedef = in_typedef || cc._is_typedef;
            env.addSymbol(
                name, 
                cc._is_typedef ? Type.TYPEDEF_NAME : Type.VARIABLE, 
                cc._id
            );
        }
    }

    public void visit(CodePointerStar cc) {
        cc._scope = env.getScope();
        if(null != cc._optStar) {
            cc._optStar.acceptVisitor(this);
        }
    }

    public void visit(CodeInitializerValue cc) {
        cc._scope = env.getScope();
        cc._value.acceptVisitor(this);
    }

    public void visit(CodeInitializerList cc) {
        cc._scope = env.getScope();
        for(CodeInitializer init : cc._list) {
            init.acceptVisitor(this);
        }
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
        if(0 == switch_count) {
            env.diag.report(E_DEFAULT_OUTSIDE_SWITCH, cc);
        } else {
            cc._scope = env.getScope();
            cc._stat.acceptVisitor(this);
        }
    }

    public void visit(CodeStatDo cc) {
        cc._scope = env.getScope();
        env.pushScope(func);
        cc._stat.acceptVisitor(this);
        env.popScope();
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
        
        env.pushScope(func);
        cc._stat.acceptVisitor(this);
        env.popScope();
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
        
        env.pushScope(func);
        cc._thstat.acceptVisitor(this);
        env.popScope();
        
        if(null != cc._optElstat) {
            env.pushScope(func);
            cc._optElstat.acceptVisitor(this);
            env.popScope();
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
        env.pushScope(func);
        cc._stat.acceptVisitor(this);
        env.popScope();
        --switch_count;
    }

    public void visit(CodeStatWhile cc) {
        cc._scope = env.getScope();
        cc._test.acceptVisitor(this);
        env.pushScope(func);
        cc._stat.acceptVisitor(this);
        env.popScope();
    }

    public void visit(CodeExprAssignment cc) {
        cc._scope = env.getScope();
        cc._b.acceptVisitor(this);
        cc._a.acceptVisitor(this);
    }

    public void visit(CodeExprCast cc) {
        cc._scope = env.getScope();
        cc._typename.acceptVisitor(this);
        cc._expr.acceptVisitor(this);
    }

    public void visit(CodeExprConditional cc) {
        cc._scope = env.getScope();
        cc._test.acceptVisitor(this);
        env.pushScope(func);
        cc._a.acceptVisitor(this);
        env.popScope();
        env.pushScope(func);
        cc._b.acceptVisitor(this);
        env.popScope();
    }

    public void visit(CodeExprInfix cc) {
        cc._scope = env.getScope();
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);
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
        cc._scope = env.getScope();
        cc._id.acceptVisitor(this);
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
        for(CodeExpr expr : cc._argl) {
            expr.acceptVisitor(this);
        }
        cc._fun.acceptVisitor(this);
    }
    
    /*
    public void visit(CodeExprSubscript cc) {
        cc._scope = env.getScope();
        cc._arr.acceptVisitor(this);
        cc._idx.acceptVisitor(this);
    }
    */

    public void visit(CodeExprField cc) {
        cc._ob.acceptVisitor(this);
    }

    public void visit(CodeExprPointsTo cc) {
        cc._ptr.acceptVisitor(this);
    }
    
}
