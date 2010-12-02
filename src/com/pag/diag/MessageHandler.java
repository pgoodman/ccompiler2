
package com.pag.diag;

import com.smwatt.comp.C;
import com.smwatt.comp.SourcePosition;

public interface MessageHandler {
    public void report(Message msg, SourcePosition pos, Object ... args);
    public void report(Message msg, C.Code pos, Object ... args);
}
