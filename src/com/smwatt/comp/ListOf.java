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
    public static class CCodeSpecifier   extends ArrayList<CodeSpecifier> {
        /*public boolean add(CodeSpecifier spec) {
            
            if(spec instanceof CodeSpecifierQualifier
            || spec instanceof CodeSpecifierStorage) {
                return super.add(spec);
            }
                        
            for(CodeSpecifier s : this) {
                if(s instanceof CodeSpecifierType) {
                    throw new AbortCompilation("Cannot have two types i");
                }
            }
            return super.add(spec);
        }*/
    }
    
    @SuppressWarnings("serial")
    public static class CCodeEnumerator  extends ArrayList<CodeEnumerator> { }
    
    @SuppressWarnings("serial")
    public static class CCodeDeclarator  extends ArrayList<CodeDeclarator> { }
    
    @SuppressWarnings("serial")
    public static class CCodeInitializer extends ArrayList<CodeInitializer> { }
    
    @SuppressWarnings("serial")
    public static class CCodeStat        extends ArrayList<CodeStat> { }
    
    @SuppressWarnings("serial")
    public static class CCodeExpr        extends ArrayList<CodeExpr> { }
}
