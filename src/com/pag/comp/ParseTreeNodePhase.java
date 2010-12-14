package com.pag.comp;

import com.smwatt.comp.C;

abstract public class ParseTreeNodePhase implements Phase {
    public C.Code node;
    public C.CodeVisitor visitor = null;
    
    public ParseTreeNodePhase(C.Code ex) {
        node = ex;
    }
    
    public ParseTreeNodePhase(C.Code ex, C.CodeVisitor vv) {
        node = ex;
        visitor = vv;
    }    
}
