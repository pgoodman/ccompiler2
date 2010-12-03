package com.pag;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java_cup.runtime.Symbol;

import com.pag.comp.FunctionVisitor;
import com.pag.comp.LabelVisitor;
import com.pag.comp.NameVisitor;
import com.pag.comp.Phase;
import com.pag.diag.MessageHandler;
import com.pag.diag.Reporter;
import com.pag.sym.Env;
import com.smwatt.comp.C;
import com.smwatt.comp.C89Parser;
import com.smwatt.comp.C89Scanner;

public class Compiler {
    public static void run(String[] args, MessageHandler handler, Phase ... phases) {        
        if(0 == args.length) {
            System.err.println(
                "Error: Please supply a file name to a C source file."
            );
            return;
        }
        
        try {
            
            C89Scanner scanner = new C89Scanner(new FileInputStream(args[0]));
            C89Parser parser = new C89Parser(scanner);
            
            scanner.setFilename(args[0]);

            Env env = new Env(handler);
            
            // parse the code
            Symbol parse_tree = parser.parse();
            C.CodeUnit ccu = (C.CodeUnit) parse_tree.value;
            
            // collect top-level information and report certain errors
            FunctionVisitor main_scope = new FunctionVisitor(env);
            main_scope.visit(ccu);
            
            if(!Reporter.errorReported()) {
            
                // collect the labels local to functions, report on 
                // label-related errors and inner function errors.
                LabelVisitor labels = new LabelVisitor(env);
                labels.visit(ccu);
                
                // look for misuses of names
                NameVisitor names = new NameVisitor(env);
                names.visit(ccu);
                
                // apply any final compilation phases
                if(!Reporter.errorReported()) {
                    for(Phase phase : phases) {
                        if(!phase.apply(env, ccu)) {
                            break;
                        }
                    }
                }
            }
            
            Reporter.flush();
            
        } catch (FileNotFoundException e) {
            System.err.println(
                "Error: the file name provided cannot be opened."
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
