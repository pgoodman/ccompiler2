
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
    public final int depth;
    
    static private final int NAMES = 0;
    static private final int TYPEDEF_NAMES = 1;
    static private final int COMPOUND_TYPES = 2;
    
    static private int next_id = 0;
    public final int id;
    
    private Hashtable<String, CSymbol>[] symbols;
    
    /**
     * Static helpers.
     */
        
    static private Hashtable<String,CSymbol>[] array(Hashtable<String,CSymbol> ... arr) {
        return arr;
    }
    static private CSymbol or(CSymbol ... syms) {
        for(CSymbol obj : syms) {
            if(null != obj) {
                return obj;
            }
        }
        return null;
    }
    static private int table_id(Type type) {
        switch(type) {
            case TYPEDEF_NAME:
                return TYPEDEF_NAMES;
            case STRUCT_NAME:
            case UNION_NAME:
            case ENUM_NAME:
                return COMPOUND_TYPES;
            default:
                return NAMES;
        }
    }
    
    /**
     * Constructors
     */
    
    @SuppressWarnings("unchecked")
    public Scope(Scope pp, C.CodeFunction func) {
        parent = pp;
        depth = null == pp ? 0 : pp.depth + 1;
        symbols = array(
            new Hashtable<String,CSymbol>(),
            new Hashtable<String,CSymbol>(),
            new Hashtable<String,CSymbol>()
        );
        enclosing_func = func;
        id = ++next_id;
    }
    
    /**
     * Get a symbol's entry information from the table.
     * @param name
     * @return
     */
    public CSymbol get(String name) {
        CSymbol ee = null;
        Scope tab = this;
        for(; null == ee && null != tab; tab = tab.parent) {
            ee = or(
                tab.symbols[NAMES].get(name), 
                tab.symbols[TYPEDEF_NAMES].get(name), 
                tab.symbols[COMPOUND_TYPES].get(name)
            );
        }
        return ee;
    }
    
    public CSymbol get(String name, Type type) {
        int tab_id = table_id(type);
        Scope tab = this;
        CSymbol ee = null;
        
        for(; null == ee && null != tab; tab = tab.parent) {
            ee = tab.symbols[tab_id].get(name);
        }
        
        return ee;
    }
    
    /**
     * Add a symbol to this table.
     */
    public void add(CSymbol sym) {
        sym.scope = this;        
        symbols[table_id(sym.type)].put(sym.name, sym);
    }
    
    /**
     * Use a type.
     */
    public void useType(String name) {
        
    }
}
