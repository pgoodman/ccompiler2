package com.pag.sym;

import java.util.ArrayList;
import java.util.List;

import com.smwatt.comp.C;

public class CSymbol {
    
    /// type of this symbol, e.g. variable, parameter, field, typedef name.
    public Type type = Type.UNDEFINED;
    
    /// position of definition / declaration.
    public C.Code code;
    public List<C.Code> declarations = new ArrayList<C.Code>();
    
    /// name of this symbol
    final public String name;
    
    /// scope that this symbol was defined in
    public Scope scope;
    
    public boolean is_static = false;
    
    /// the type of the code node / expression / statement
    //public CType type_of_code;
    
    /// was this symbol ever used?
    public boolean is_used = false;
    
    /**
     * Basic constructor.
     */
    public CSymbol(String nn, Type tt, C.Code cc) {
        name = nn;
        type = tt;
        code = cc;
    }
}
