package com.pag;

import com.pag.diag.Message;
import com.pag.diag.MessageHandler;
import com.pag.diag.Reporter;
import com.smwatt.comp.SourcePosition;
import com.smwatt.comp.C.Code;

public class Question2 {
        public static void main(String[] args) {        
        Compiler.run(args, new MessageHandler() {
            public void report(Message msg, SourcePosition pos, Object ... args) {
                Reporter.reportLater(msg, pos, args);
            }

            public void report(Message msg, Code pos, Object... args) {
                Reporter.reportLater(msg, pos.getSourcePosition(), args);
            }
        });
    }
}
