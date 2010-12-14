package com.pag;

import com.pag.comp.TypeInferenceVisitor;
import com.pag.comp.Phase;
import com.pag.diag.Message;
import com.pag.diag.MessageHandler;
import com.pag.diag.Reporter;
import com.pag.llvm.CodeGenerator;
import com.pag.sym.Env;
import com.smwatt.comp.SourcePosition;
import com.smwatt.comp.C.Code;

public class Question4 {
    public static void main(String[] args) {
        
        Env.DEBUG = false;
        
        // delay reporting of messages until whatever phase is run last is
        // complete. note: not all phases will necessarily be run!
        Compiler.run(args, new MessageHandler() {
            public void report(Message msg, SourcePosition pos, Object ... args) {
                Reporter.reportLater(msg, pos, args);
            }

            public void report(Message msg, Code pos, Object... args) {
                Reporter.reportLater(msg, pos.getSourcePosition(), args);
            }
        }, 
        
        // deduce the types of all names in the program. this phase adds
        // small step phases that evaluate compile-time expressions in order 
        // to figure out the sizes of types, values of enumerators, etc.
        //
        // once all types of names have been deduced (top-down), the types
        // of expressions are inferred (bottom-up)
        new Phase() {
            public boolean apply(Env env, Code code) {
                TypeInferenceVisitor visitor = new TypeInferenceVisitor(env);
                
                try {
                    visitor.visit(code);
                    
                // 
                } catch(java.lang.ClassCastException ex) {
                    ex.printStackTrace();
                    return false;
                }
                
                return !Reporter.errorReported();
            }
        },
        
        // code generation phase
        new Phase() {

            public boolean apply(Env env, Code code) {
                
                CodeGenerator gen = new CodeGenerator(env);
                gen.visit(code);
                
                if(!Reporter.errorReported()) {
                    System.out.println(gen.toString());
                }
                
                return false;
            }
            
        });
    }
}
