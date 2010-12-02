
package com.pag.sym;

import java.util.Hashtable;

import com.smwatt.comp.C;

/**
 * Represents a single symbol table for some scope.
 * 
 * @author petergoodman
 *
 */
public class Scope {
    
    public final C.CodeFunction enclosing_func;
    public final Scope parent;
    
    private Hashtable<String, CSymbol> symbols;
    //private Hashtable<String, CType> types; // TODO
    
    /**
     * Constructors
     */
    
    public Scope(Scope pp, C.CodeFunction func) {
        parent = pp;
        symbols = new Hashtable<String,CSymbol>();
        enclosing_func = func;
    }
    
    /**
     * Get a symbol's entry information from the table.
     * @param name
     * @return
     */
    public CSymbol get(String name) {
        CSymbol ee = null;
        Scope tab = this;
        for(; null != tab; tab = tab.parent) {
            ee = tab.symbols.get(name);
            if(null != ee) {
                break;
            }
        }
        return ee;
    }
    
    /**
     * Add a symbol to this table.
     */
    public void add(CSymbol sym) {
        sym.scope = this;
        symbols.put(sym.name, sym);
    }
    
    /**
     * Use a type.
     */
    public void useType(String name) {
        
    }
}
