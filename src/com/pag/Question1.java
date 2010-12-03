package com.pag;

import com.smwatt.comp.SourcePosition;
import com.smwatt.comp.C.Code;

import com.pag.diag.*;

public class Question1 {
    public static void main(String[] args) {        
        Compiler.run(args, new MessageHandler() {
            public void report(Message msg, SourcePosition pos, Object ... args) {
                Reporter.reportNow(msg, pos, args);
                
                if(Reporter.errorReported()) {
                    System.exit(1);
                }
            }

            public void report(Message msg, Code pos, Object... args) {
                Reporter.reportNow(msg, pos.getSourcePosition(), args);
                
                if(Reporter.errorReported()) {
                    System.exit(1);
                }
            }
        });
    }
}
