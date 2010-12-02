//////////////////////////////////////////////////////////////////////////////
//
// Helper classes required by CUP.
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.util.ArrayList;

//public class ListOf<T> extends ArrayList<T> { }

class ListOf {
    
    @SuppressWarnings("serial")
    public static class CCode            extends ArrayList<com.smwatt.comp.C.Code> { }
    
    @SuppressWarnings("serial")
    public static class CCodeDeclaration extends ArrayList<com.smwatt.comp.C.CodeDeclaration> { }
    
    @SuppressWarnings("serial")
    public static class CCodeId  	     extends ArrayList<com.smwatt.comp.C.CodeId> { }
    
    @SuppressWarnings("serial")
    public static class CCodeSpecifier   extends ArrayList<com.smwatt.comp.C.CodeSpecifier> { }
    
    @SuppressWarnings("serial")
    public static class CCodeEnumerator  extends ArrayList<com.smwatt.comp.C.CodeEnumerator> { }
    
    @SuppressWarnings("serial")
    public static class CCodeDeclarator  extends ArrayList<com.smwatt.comp.C.CodeDeclarator> { }
    
    @SuppressWarnings("serial")
    public static class CCodeInitializer extends ArrayList<com.smwatt.comp.C.CodeInitializer> { }
    
    @SuppressWarnings("serial")
    public static class CCodeStat        extends ArrayList<com.smwatt.comp.C.CodeStat> { }
    
    @SuppressWarnings("serial")
    public static class CCodeExpr        extends ArrayList<com.smwatt.comp.C.CodeExpr> { }
}
