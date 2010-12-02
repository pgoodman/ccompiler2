package com.pag.sym;

import java.util.List;
import java.util.LinkedList;

import com.pag.diag.MessageHandler;
import com.smwatt.comp.C;

public class Env {
    
    /// active scope
    private Scope scope = null;
    
    /// all scopes
    private List<Scope> scopes = new LinkedList<Scope>();
    
    /// handler for diagnostic messages
    final public MessageHandler diag;
    
    /**
     * Constructor, take in a message handler.
     * @param handler
     */
    public Env(MessageHandler handler) {
        diag = handler;
        pushScope(null);
    }
    
    /**
     * Push a scope onto the scope stack.
     */
    public void pushScope(C.CodeFunction func) {
        scope = new Scope(scope, func);
        scopes.add(scope);
        //System.out.println("pushing scope.");
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
}
