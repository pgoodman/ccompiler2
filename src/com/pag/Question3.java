package com.pag;

import com.pag.comp.TypeInferenceVisitor;
import com.pag.comp.Phase;
import com.pag.diag.Message;
import com.pag.diag.MessageHandler;
import com.pag.diag.Reporter;
import com.pag.sym.Env;
import com.smwatt.comp.SourcePosition;
import com.smwatt.comp.C.Code;

public class Question3 {
        public static void main(String[] args) {        
        Compiler.run(args, new MessageHandler() {
            public void report(Message msg, SourcePosition pos, Object ... args) {
                Reporter.reportLater(msg, pos, args);
            }

            public void report(Message msg, Code pos, Object... args) {
                Reporter.reportLater(msg, pos.getSourcePosition(), args);
            }
        }, 
        
        // type inference phase of the compiler
        new Phase() {
            public boolean apply(Env env, Code code) {
                TypeInferenceVisitor visitor = new TypeInferenceVisitor(env);
                visitor.visit(code);
                
                return !Reporter.errorReported();
            }
        });
    }
}
