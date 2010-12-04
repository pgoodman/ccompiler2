package com.pag.comp;

import com.smwatt.comp.C;

abstract public class ParseTreeNodePhase implements Phase {
    public C.Code node;
    
    public ParseTreeNodePhase(C.Code ex) {
        node = ex;
    }    
}
