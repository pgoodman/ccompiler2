package com.pag.llvm;

import java.util.LinkedList;

import com.pag.sym.Env;
import com.pag.val.CompileTimeInteger;
import com.smwatt.comp.CType;
import com.smwatt.comp.CTypeVisitor;
import com.smwatt.comp.C.CodeExpr;

import static com.smwatt.comp.CType.*;

/**
 * Convert C-types into LLVM IR types.
 * 
 * Simplifying assumptions:
 *      1) constness can be erased without changing the behavior of the 
 *         program.
 *      2) volatile will be ignored.
 * 
 * @author petergoodman
 *
 */
public class IRTypeBuilder implements CTypeVisitor {
    
    private LinkedList<CTypeStruct> struct_stack = new LinkedList<CTypeStruct>();
    private LinkedList<String> parts = new LinkedList<String>();
    private Env env;
    
    public String toString(CType tt) {
        visit(tt);
        return parts.pop();
    }
    
    public String toString(CType tt, boolean array_to_ptr) {
        if(array_to_ptr && tt instanceof CTypeArray) {
            visit((CTypePointer) tt);
        } else {
            visit(tt);
        }
        return parts.pop();
    }
    
    private int ii(CodeExpr cc) {
        return ((CompileTimeInteger) env.interpret(cc)).value;
    }
    
    private String iis(CodeExpr cc) {
        return Integer.toString(ii(cc));
    }
    
    public IRTypeBuilder(Env ee) {
        env = ee;
    }
    
    public void visit(CType ct) {
        if(null == ct) {
            parts.push("i32");
        } else {
            ct.acceptVisitor(this);
        }
    }
    
    public void visit(CTypeInvalid ct) { }
    
    public void visit(CTypeVoid ct) {
        parts.push("i32");
    }
    
    public void visit(CTypeInt ct) {
        parts.push("i" + Integer.toString(ct.sizeOf(env) * 8));
    }
    
    public void visit(CTypeChar ct) {
        parts.push("i8" /* + Integer.toString(ct.sizeOf(env) * 8)*/);
    }
    
    public void visit(CTypeFloat ct) {
        parts.push("float");
    }
    
    public void visit(CTypeDouble ct) {
        if(8 == ct.sizeOf(env)) {
            parts.push("double");
        } else {
            parts.push("fp128");
        }
    }
    
    public void visit(CTypeFunction ct) {
        if(ct._retType instanceof CTypeVoid) {
            parts.push("void");
        } else {
            ct._retType.acceptVisitor(this);
        }
        
        String tt = parts.pop() + " (";
        String sep = "";
        
        for(CType argt : ct._argTypes) {

            argt.acceptVisitor(this);
            tt += sep + parts.pop();
            sep = ", ";
        }
        
        if(ct._moreArgs) {
            tt += sep + "...";
        }
        
        tt += ")";
        parts.push(tt);
    }
    
    public void visit(CTypeArray ct) {
        if(ct._pointeeType instanceof CTypeChar) {
            parts.push("i8");
        } else {
            ct._pointeeType.acceptVisitor(this);
        }
        
        // pointer (possibly to an array
        if(null == ct._optSize) {
            parts.push(parts.pop() + "*");
        } else {
            parts.push(
                "[" + iis(ct._optSize._expr) + " x " + parts.pop() + "]"
            );
        }
    }
    
    public void visit(CTypePointer ct) {
        if(ct._pointeeType instanceof CTypeChar) {
            parts.push("i8");
        } else {
            ct._pointeeType.acceptVisitor(this);
        }
        parts.push(parts.pop() + "*");
    }
    
    public void visit(CTypeFunctionPointer ct) {
        ct._pointeeType.acceptVisitor(this);
        parts.push(parts.pop() + "*");
    }
    
    public void visit(CTypeEnum ct) {
        parts.push("i32");
    }
    
    public void visit(CTypeStruct ct) {
        
        int depth = 1;
        boolean found = false;
        for(CTypeStruct ss : struct_stack) {
            if(ss == ct) {
                found = true;
                break;
            }
            ++depth;
        }
        
        if(found) {
            parts.push("\\" + depth);
            return;
        }
        
        struct_stack.push(ct);
        
        String tt = "{";
        String sep = "";
        
        for(CTypeField f : ct._fields) {
            f.acceptVisitor(this);
            
            // add padding into the structs for chars
            if(0 != f._padding) {
                tt += sep + "i" + Integer.toString(8 * f._padding);
                sep = ",";
            }
            tt += sep + parts.pop();
            sep = ", ";
        }
        
        tt += "}";
        
        struct_stack.pop();
        
        parts.push(tt);
    }
    
    public void visit(CTypeUnion ct) {
        visit((CTypeStruct) ct);
    }
    
    public void visit(CTypeConstExpr ob) { }
    
    public void visit(CTypeEnumerator ob) { }
    
    public void visit(CTypeField ob) {
        ob._type.acceptVisitor(this);
    }    
}
