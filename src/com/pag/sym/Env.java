package com.pag.sym;

import java.util.List;
import java.util.LinkedList;

import com.pag.comp.ExpressionInterpreterVisitor;
import com.pag.comp.Phase;
import com.pag.diag.MessageHandler;
import com.pag.val.CompileTimeValue;
import com.smwatt.comp.C;
import com.smwatt.comp.C.CodeExpr;

public class Env {
    
    public static boolean DEBUG = true;
    
    /// active scope
    private Scope scope = null;
    
    /// all scopes
    private List<Scope> scopes = new LinkedList<Scope>();
    
    /// handler for diagnostic messages
    final public MessageHandler diag;
    
    /// compiler phases
    private LinkedList<Phase> small_phases = new LinkedList<Phase>();
    private LinkedList<Phase> big_phases = new LinkedList<Phase>();
    
    /// interpreter
    private ExpressionInterpreterVisitor interpreter;
    
    
    
    /**
     * Constructor, take in a message handler.
     * @param handler
     */
    public Env(MessageHandler handler, Phase ... phases) {
        diag = handler;
        pushScope(null);
        
        for(Phase phase : phases) {
            big_phases.addLast(phase);
        }
        
        interpreter = new ExpressionInterpreterVisitor(this);
    }
    
    /**
     * Push a scope onto the scope stack.
     */
    public Scope pushScope(C.CodeFunction func) {
        scope = new Scope(scope, func);
        scopes.add(scope);
        //System.out.println("pushing scope.");
        return scope;
    }
    
    /**
     * Pop a scope from the stack.
     */
    public void popScope() {
        scope = scope.parent;
        //System.out.println("popping scope.");
    }
    
    public Scope getScope() {
        return scope;
    }
    
    /**
     * Add in a new symbol to the top scope.
     */
    public CSymbol addSymbol(String nn, Type tt, C.Code cc) {
        CSymbol sym = new CSymbol(nn, tt, cc);
        scope.add(sym);
        return sym;
    }
    
    /**
     * Get a (possibly) previously defined symbol.
     */
    public CSymbol getSymbol(String nn) {
        return scope.get(nn);
    }
    
    public CSymbol getSymbol(String nn, Type tt) {
        return scope.get(nn, tt);
    }
    
    /**
     * Add a new phase into the compiler.
     */
    public void addPhase(Phase phase) {
        small_phases.addLast(phase);
    }
    
    public Phase popPhase() {
        if(!small_phases.isEmpty()) {
            return small_phases.remove();
        }
        if(!big_phases.isEmpty()) {
            return big_phases.remove();
        }
        return null;
    }
    
    /**
     * Interpret some CCode object.
     */
    public CompileTimeValue interpret(C.Code cc) {
        
        if(cc instanceof CodeExpr) {
            CodeExpr expr = (CodeExpr) cc;
            if(null == expr._const_val) {
                interpreter.visit(expr);
            }
            return expr._const_val;
        
        // will give an error ;)
        } else {
            interpreter.visit(cc);
        }
        return null;
    }
}
