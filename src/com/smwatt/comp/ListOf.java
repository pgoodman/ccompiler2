//////////////////////////////////////////////////////////////////////////////
//
// Helper classes required by CUP.
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.util.ArrayList;

//public class ListOf<T> extends ArrayList<T> { }

import static com.smwatt.comp.C.*;

class ListOf {
    
    @SuppressWarnings("serial")
    public static class CCode            extends ArrayList<Code> { }
    
    @SuppressWarnings("serial")
    public static class CCodeDeclaration extends ArrayList<CodeDeclaration> { }
    
    @SuppressWarnings("serial")
    public static class CCodeId  	     extends ArrayList<CodeId> { }
    
    @SuppressWarnings("serial")
    public static class CCodeSpecifier   extends ArrayList<CodeSpecifier> { }
    
    @SuppressWarnings("serial")
    public static class CCodeEnumerator  extends ArrayList<CodeEnumerator> { }
    
    @SuppressWarnings("serial")
    public static class CCodeDeclarator  extends ArrayList<CodeDeclarator> { }
    
    
    @SuppressWarnings("serial")
    public static class CCodeInitializer extends ArrayList<CodeInitializer> {
        
        // not exactly correct, but simplifies things by collapsing sub-lists
        // into one top-level list
        public boolean add(CodeInitializer init) {
            if(init instanceof CodeInitializerList) {
                CodeInitializerList ls = (CodeInitializerList) init;
                for(CodeInitializer sub_i : ls._list) {
                    super.add(sub_i);
                }
                return true;
            }
            
            return super.add(init);
        }
    }
    
    @SuppressWarnings("serial")
    public static class CCodeStat        extends ArrayList<CodeStat> { }
    
    @SuppressWarnings("serial")
    public static class CCodeExpr        extends ArrayList<CodeExpr> { }
}
