package com.pag.llvm;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import com.pag.val.CompileTimeFloat;
import com.pag.val.CompileTimeInteger;
import com.pag.val.CompileTimeString;
import com.smwatt.comp.CTokenType;
import com.smwatt.comp.CType;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.C.CodeDeclarator;
import com.smwatt.comp.CType.CTypeIntegral;

import static com.smwatt.comp.CType.*;
import static com.smwatt.comp.C.*;

/**
 * LLVM IR code generation visitor.
 * 
 * @author petergoodman
 *
 */
public class CodeGenerator implements CodeVisitor {
    
    // compilation environment
    Env env;
    
    // LLVM IR type builder
    IRTypeBuilder ir_type;
    
    // ids for function parameters without ids
    int unnamed_var = 0;
    
    // stores ids of temporary variables
    int next_temp = 1;
    LinkedList<String> temps = new LinkedList<String>();
    
    // declared funcs, don't do any repeats
    HashSet<String> declared_funcs = new HashSet<String>();
    Hashtable<String,String> const_table = new Hashtable<String,String>();
    
    static boolean DEBUG = true;
    
    // basic interface for a code buffer
    public interface CodeBuffer {
        public CodeBuffer append(String ss);
        public void indent();
        public void outdent();
        public CodeBuffer nl();
        public String toString();
        public void label(String name);
    }
    
    // simple code buffer implementation
    private class SimpleCodeBuffer implements CodeBuffer {
        private StringBuffer _code = new StringBuffer();
        private String _indent = "";
        
        public CodeBuffer append(String ss) {
            _code.append(ss);
            return this;
        }
        
        public CodeBuffer nl() {
            _code.append("\n");
            _code.append(_indent);
            return this;
        }

        public void indent() {
            _indent += "  ";
        }

        public void outdent() {
            _indent = _indent.substring(0, _indent.length() - 2);
        }
        
        public String toString() {
            return _code.toString();
        }
        
        public void label(String name) {
            _code.append("\n").append(name).append(":");
        }
    };
    
    public String toString() {
        StringBuffer buff = new StringBuffer();
        //buff.append("target datalayout = \"e-p:64:64:64-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:64:64-f32:32:32-f64:64:64-v64:64:64-v128:128:128-a0:0:64-s0:64:64-f80:128:128-n8:16:32:64\"\n");
        buff.append("\n; global vars declarations");
        //if(DEBUG) {
        //    buff.append("\n@.DEBUG_DBL_TO_FLT = internal constant [21 x i8] c\"converted %lf to %f\\0A\\00\"");
        //}
        buff.append(gdecl.toString());
        buff.append("\n\n; integer -> floating point cast fixer");
        buff.append("\ndefine ccc float @double$float(double %x) nounwind ssp {");
        //buff.append("\n  %1 = fadd double %x, 0.0");
        buff.append("\n  %1 = fptrunc double %x to float");
        //buff.append("\n  %2 = alloca double");
        //buff.append("\n  store double %x, double* %2");
        
        //if(DEBUG) {
        //    buff.append("\n  call i32 (i8*, ...)* @printf(i8* getelementptr ([21 x i8]* @.DEBUG_DBL_TO_FLT, i32 0, i32 0), double %x, float %2)");
        //}
        buff.append("\n  ret float %1");
        buff.append("\n}");
        buff.append("\n\n; global var definitions");
        buff.append("\ndefine ccc void @init$vars() nounwind ssp {");
        buff.append(gdef.toString());
        buff.append("\n  ret void\n}\n\n; code");
        buff.append(code.toString());
        return buff.toString();
    }
    
    // buffer for main userland code
    public CodeBuffer code = new SimpleCodeBuffer();
    
    // buffer for initialization (of values) code of global variables
    public CodeBuffer gdef = new SimpleCodeBuffer();
    
    // buffer for declaration of global variables
    public CodeBuffer gdecl = new SimpleCodeBuffer();
    
    public CodeGenerator(Env ee) {
        env = ee;
        ir_type = new IRTypeBuilder(ee);
        gdef.indent();
    }
    
    private CodeBuffer b(Code cc) {
        return 0 == cc._scope.depth ? gdef : code;
    }
    
    private String local(Code cc) {
        return 0 == cc._scope.depth ? global() : local();
    }
    
    private String local() {
        return "%." + Integer.toString(next_temp++);
    }
    
    private String global() {
        return "@." + Integer.toString(next_temp++);
    }
    
    private String label() {
        return "Label." + Integer.toString(next_temp++);
    }
    
    private String label(String id) {
        return "Label." + id;
    }
    
    /**
     * Generate a constant value.
     */
    private String constant(CodeExpr cc) {
        if(cc._const_val instanceof CompileTimeString) {
            // TODO?
        } else if(cc._const_val instanceof CompileTimeFloat) {
            return "0x" + Long.toHexString(
                Double.doubleToRawLongBits(
                    (double) (float) ((CompileTimeFloat) cc._const_val).value
                )
            ).toUpperCase();
        }
        
        return cc._const_val.toString();
    }
    
    /**
     * Generate a variable name for a CodeId.
     */
    private String name(CodeId id) {
        CSymbol sym = id._scope.get(id._s);
        if(Type.FUNC_DECL == sym.type 
        || Type.FUNC_DEF == sym.type) {
            return "@" + id._s;
        }
        
        return (0 == sym.scope.depth ? "@" : "%") 
             + id._s + "$" + Integer.toString(sym.scope.id);
    }
    
    /**
     * LLVM instructions.
     * 
     * @param buff
     * @param tt
     * @param arr_to_ptr
     * @return
     */
    private String alloca(CodeBuffer buff, CType tt, boolean arr_to_ptr, String var) {
        return alloca(buff, ir_type.toString(tt, arr_to_ptr), var);
    }
    
    private String alloca(CodeBuffer buff, String tt, String var) {
        var = null == var ? local() : var;
        buff.nl().append(var).append(" = alloca ").append(tt).append(", align 8");
        return var;
    }
    
    private String alloca(Code cc, CType tt, boolean arr_to_ptr, String var) {
        return alloca(cc, ir_type.toString(tt, arr_to_ptr), var);
    }
    
    private String alloca(Code cc, String tt, String var) {
        var = null == var ? local() : var;
        // global
        if(0 == cc._scope.depth) {
            gdecl.nl().append(var).append(" = global ").append(tt)
                 .append(" undef");
            
        // local
        } else {
            alloca(code, tt, var);
        }
        
        return var;
    }
    
    private String load(CodeBuffer buff, CType tt, String from_loc) {
        return load(buff, ir_type.toString(tt, true), from_loc);
    }
    
    private String load(CodeBuffer buff, String tt, String from_loc) {
        String var = local();
        buff.nl().append(var).append(" = load ").append(tt).append("* ")
            .append(from_loc).append(", align 8");;
        return var;
    }
    
    private String store(CodeBuffer buff, CType tt, String from_loc, String to_loc) {
        return store(buff, ir_type.toString(tt, true), from_loc, to_loc);
    }
    
    private String store(CodeBuffer buff, String tt, String from_loc, String to_loc) {
        buff.nl().append("store ").append(tt).append(" ").append(from_loc)
            .append(", ").append(tt).append("* ").append(to_loc);
        return to_loc;
    }
    
    private String binary(CodeBuffer buff, String op, String type, String left_val, String right_val) {
        String val = local();
        buff.nl().append(val).append(" = ").append(op).append(" ")
            .append(type).append(" ").append(left_val).append(", ")
            .append(right_val);
        return val;
    }
    
    private void br(CodeBuffer buff, String test, String label_true, String label_false) {
        buff.nl().append("br i1 ").append(test).append(", label %")
            .append(label_true).append(", label %").append(label_false);
    }
    
    private void br(CodeBuffer buff, String label) {
        buff.nl().append("br label %").append(label);
    }
    
    private String op(CodeBuffer buff, String ... parts) {
        String val = local();
        buff.nl().append(val).append(" = ");
        for(String part : parts) {
            buff.append(part);
        }
        return val;
    }
        
    /**
     * Visitors.
     */
    
    public void visit(Code cc) {
        cc.acceptVisitor(this);
    }
    
    public void visit(CodeUnit cc) {        
        for(Code code : cc._l) {
            code.acceptVisitor(this);
        }
    }
    
    /**
     * Generate the code for a function definition.
     */
    public void visit(CodeFunction cc) {
        
        CTypeFunction ft = (CTypeFunction) cc._type;
        String func_name = cc._head.getOptId()._s;
        
        String ret_type = ft._retType instanceof CTypeVoid 
                        ? "void" : ir_type.toString(ft._retType, true);
        
        code.nl().nl().append("; ").append(cc.getSourcePosition().toString())
            .append("\ndefine ccc ").append(ret_type)
            .append(" @").append(func_name).append("(");
        
        String sep = "";
        CodeDeclaratorFunction fdtor = (CodeDeclaratorFunction) cc._head;
        
        LinkedList<CodeDeclarator> useful_dtors = new LinkedList<CodeDeclarator>();
        
        for(int i = 0; i < ft._argTypes.size(); ++i) {
            
            Code arg = fdtor._argl.get(i);
            
            if(null == arg) {
                continue;
            }
            
            String arg_name;
            CType arg_type;
            
            if(arg instanceof CodeId) {
                arg_type = arg._type;
                arg_name = (((CodeId) arg))._s;
            } else {
                CodeDeclaration decl = (CodeDeclaration) arg;
                
                // a parameter that we never use (it wasn't given a name)
                if(0 == decl._ldtor.size()) {
                    arg_name = "%u$" + Integer.toString(unnamed_var++);
                    arg_type = cc._type;
                } else {
                    CodeDeclarator dtor = decl._ldtor.get(0);
                    arg_name = "%p$" + dtor.getOptId()._s;
                    useful_dtors.add(dtor);
                    arg_type = dtor._type;
                }
            }
            

            code.append(sep)
                .append(ir_type.toString(arg_type, true))
                .append(" ").append(arg_name);
                
            
            sep = ", ";
        }
        
        code.append(") nounwind ssp {").indent();
        
        // initialize the global variables in the main function
        if(0 == func_name.compareTo("main")) {
            code.nl().append("; global vars");
            code.nl().append("call ccc void ()* @init$vars()");
        }
        
        code.nl().append("; args");
        for(CodeDeclarator dtor : useful_dtors) {
            stackAllocateParam(dtor);
        }
        code.nl().append("; body");
        
        cc._body.acceptVisitor(this);
        
        // make sure we have a return for void functions
        if(ft._retType instanceof CTypeVoid) {
            code.nl().append("ret void");
        }
        
        code.outdent();
        code.nl().append("}");
    }
    
    private void stackAllocateParam(CodeDeclarator dtor) {
        
        CodeId id = dtor.getOptId();
        CType tt = id._type;
        String ir_tt = ir_type.toString(tt, true);
        String var = name(id);
        
        alloca(code, ir_tt, var);
        store(code, ir_tt, "%p$" + id._s, var);
    }
    
    public void visit(CodeDeclaration cc) {
        CodeBuffer buff = 0 == cc._scope.depth ? gdecl : code;
        for(CodeDeclarator dtor : cc._ldtor) {
            
            if(null == dtor) {
                continue;
            }
            
            if(dtor._is_typedef) {
                continue;
            }
            
            if(dtor instanceof CodeDeclaratorFunction) {
                dtor.acceptVisitor(this);
                
            } else {
                CodeId id = dtor.getOptId();
                String type = ir_type.toString(id._type);
                
                if(dtor._type instanceof CTypeArray) {
                    String loc = local(cc);
                    String ptr_type = ir_type.toString(id._type, true);
                    String var = name(id);
                    alloca(cc, type, loc);
                    
                    if(gdecl == buff) {
                        buff.nl().append(var).append(" = ")
                            .append("global ")
                            .append(ptr_type)
                            .append(" getelementptr (")
                            .append(type).append("* ")
                            .append(loc).append(", i32 0, i32 0)");
                    } else {
                        alloca(buff, ptr_type, var);
                        String temp = local();
                        buff.nl().append(temp).append(" = getelementptr ")
                            .append(type).append("* ")
                            .append(loc).append(", i32 0, i32 0");
                        store(buff, ptr_type, temp, var);
                    }
                } else {
                    alloca(cc, type, name(id));
                }
                
                buff.nl();
                
                if(dtor instanceof CodeDeclaratorInit) {
                    dtor.acceptVisitor(this);
                }
            }
        }
    }
    
    public void visit(CodeId cc) { }
    
    public void visit(CodeTypeName cc) { }
    
    public void visit(CodeString cc) {
        StringBuffer buff = new StringBuffer();
        String mem = global();
        String str = global();
        
        buff.append(" c\"");
        for(char c : cc._s.toCharArray()) {
            if(c >= 32 && c < 127) {
                buff.append(c);
            } else {
                buff.append("\\0");
                buff.append(Integer.toHexString(c).toUpperCase());
            }
        }
        buff.append("\"");
        
        String arr_type = "[" + Integer.toString(cc._s.length()) + " x i8]";
        gdecl.nl().append(mem).append(" = internal constant ")
            .append(arr_type).append(buff.toString());
        
        gdecl.nl().append(str).append(" = constant i8* getelementptr (")
            .append(arr_type).append("* ").append(mem).append(", i32 0, i32 0)");
        
        temps.push(str);
    }
    
    private void visitConstant(CodeExpr cc) {
        String const_val = constant(cc);
        String type = ir_type.toString(cc._type);
        String key = type + ":" + const_val;
        
        String val = const_table.get(key);
        if(null == val) {
            val = global();
            gdecl.nl().append(val).append(" = internal constant ")
                .append(type).append(" ").append(const_val);
            const_table.put(key, val);
        }
        temps.push(val);
    }
    
    public void visit(CodeCharacterConstant cc) {
        visitConstant(cc);
    }
    
    public void visit(CodeIntegerConstant cc) {
        visitConstant(cc);
    }
    
    public void visit(CodeFloatingConstant cc) {
        visitConstant(cc);
    }
    
    public void visit(CodeEnumerationConstant cc) {
        visitConstant(cc);
    }
    
    public void visit(CodeDotDotDot cc) { }
    
    public void visit(CodeSpecifierStorage cc) { }
    
    public void visit(CodeSpecifierQualifier cc) { }
    
    public void visit(CodeSpecifierType cc) { }
    
    public void visit(CodeSpecifierTypedefName cc) { }
    
    public void visit(CodeSpecifierStruct cc) { }
    
    public void visit(CodeSpecifierUnion cc) { }
    
    public void visit(CodeSpecifierEnum cc) { }
    
    public void visit(CodeEnumerator cc) { }
    
    public void visit(CodeDeclaratorArray cc) { }
    
    /**
     * Visit a function declaration. If the code stored in this declarations
     * scope for the function of the same name is actually the id of this
     * declaration then it means we never found a definition and hence should
     * output a declaration. 
     */
    public void visit(CodeDeclaratorFunction cc) {
        
        CodeId id = cc.getOptId();
        
        if(declared_funcs.contains(id._s)) {
            return;
        }
        
        CSymbol sym = cc._scope.get(id._s, com.pag.sym.Type.FUNC_DECL);
        
        if(sym.code == id) {
            
            declared_funcs.add(id._s);
            
            CTypeFunction ft = (CTypeFunction) id._type;
            
            gdecl.nl().nl().append("; ").append(cc.getSourcePosition().toString());
            gdecl.append("\ndeclare ").append(ir_type.toString(ft._retType));
            gdecl.append(" @").append(id._s).append("(");
            String sep = "";
            for(CType at : ft._argTypes) {
                gdecl.append(sep).append(ir_type.toString(at));
                sep = ", ";
            }
            if(ft._moreArgs) {
                gdecl.append(sep).append("...");
            }
            gdecl.append(")").nl();
        }
    }
    
    public void visit(CodeDeclaratorInit cc) {
        CodeId id = cc.getOptId();
        
        b(cc).nl().nl().append("; initializer");
        temps.push(name(id));
        temps.push(ir_type.toString(id._type, true));
        cc._initializer.acceptVisitor(this);
    }
    
    public void visit(CodeInitializerValue cc) {
        cc._value.acceptVisitor(this);
        CodeBuffer buff = b(cc);
        String val = temps.pop();
        String var_type = temps.pop();
        String var = temps.pop();
        store(buff, var_type, load(buff, var_type, val), var);
    }
    
    /**
     * The parser and type inference visitors automatically flatten
     * initializer lists and so it would be painful to recover their
     * structure just to initialize them using the constant array syntax.
     * As such, initializer lists handled by initializing the slots in the
     * array one at a time by indexing into them.
     */
    public void visit(CodeInitializerList cc) {
        
        CodeBuffer buff = b(cc);
        String var_type = temps.pop();
        String var = temps.pop();
        String list_type = ir_type.toString(cc._type, true);
        
        String im = load(buff, var_type, var);
        String internal_type = ir_type.toString(cc._type.getInternalType());
        
        buff.nl().nl().append("; initializer list");
        
        int i = 0;
        for(CodeInitializer init : cc._list) {
            String addr = local();
            buff.nl().append(addr).append(" = ")
                .append("getelementptr ").append(list_type)
                .append(" ").append(im).append(", i32 ")
                .append(Integer.toString(i++));
            temps.push(addr);
            temps.push(internal_type);
            
            init.acceptVisitor(this);
        }
    }
    
    public void visit(CodeDeclaratorPointer cc) { }
    
    public void visit(CodeDeclaratorWidth cc) { }
    
    public void visit(CodeDeclaratorId cc) { }
    
    public void visit(CodePointerStar cc) { }
    
    public void visit(CodeDeclaratorParen cc) { }
    
    public void visit(CodeStatBreak cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatCase cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatCompound cc) {
        
        for(CodeDeclaration decl : cc._ldecl) {
            decl.acceptVisitor(this);
        }
        
        for(CodeStat stat : cc._lstat) {
            stat.acceptVisitor(this);
        }
    }
    
    public void visit(CodeStatContinue cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatDefault cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatDo cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatExpression cc) {
        if(null != cc._optExpr) {
            cc._optExpr.acceptVisitor(this);
        }
    }
    
    public void visit(CodeStatFor cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatGoto cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatIf cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatLabeled cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatReturn cc) {
        if(null == cc._optExpr 
        || cc._optExpr._type instanceof CTypeVoid) {
            code.nl().append("ret void");
        } else {
            cc._optExpr.acceptVisitor(this);
            String ret_type = ir_type.toString(cc._optExpr._type, true);
            String ret = load(code, ret_type, temps.pop());
            code.nl().append("ret ").append(ret_type)
                .append(" ").append(ret);
        }
    }
    
    public void visit(CodeStatSwitch cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeStatWhile cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeExprAssignment cc) {
        cc._a.acceptVisitor(this);
        cc._b.acceptVisitor(this);

        String val = temps.pop();
        String ob = temps.pop();
        String val_type = ir_type.toString(cc._b._type, true);
        
        if(CTokenType.ASSIGN == cc._op._type) {
            store(b(cc), val_type, load(b(cc), val_type, val), ob);
        }
        // TODO
        // TODO
        // TODO
    }
    
    public void visit(CodeExprCast cc) {
        
        cc._expr.acceptVisitor(this);
        
        CType st = cc._expr._type;
        CType dt = cc._type;
        
        String st_str = ir_type.toString(st, true);
        String dt_str = ir_type.toString(dt, true);
        
        // these types are structurally the same.
        if(0 == st_str.compareTo(dt_str)) {
            /*
            // ... but one is signed and the other isn't
            if(st instanceof CTypeIntegral && dt instanceof CTypeIntegral) {
                CTypeIntegral ist = (CTypeIntegral) st;
                CTypeIntegral idt = (CTypeIntegral) dt;
                
                //System.out.println(st + " " + (((CTypeIntegral) st)._signed) + " -> " + dt + " " + (((CTypeIntegral) dt)._signed));                
                if((ist._signed < 0) != (idt._signed < 0)) {
                    System.out.println(st + " " + ist._signed + " -> " + dt + " " + idt._signed);
                    // different signs, need to cast
                } else {
                    return;
                }
            } else {
                return;
            }*/
            return;
        }
        
        String temp = temps.pop();
        String cast = local();
        
        temps.push(cast);
        CodeBuffer buff = b(cc);        
        boolean do_store = false;
        
        if(st instanceof CTypeIntegral) {
            if(dt instanceof CTypePointing) {
                do_store = true;
            } else if(dt instanceof CTypeFloating) {
                do_store = true;
            } else if(dt instanceof CTypeIntegral) {
                do_store = true;
            }
        } else if(st instanceof CTypeFloating) {
            if(dt instanceof CTypeIntegral) {
                do_store = true;
            } else if(dt instanceof CTypeFloating) {
                do_store = true;
            }
        }
        
        // bitcast if there are no primitives for the casting operation
        if(!do_store) {
            buff.nl().append(cast).append(" = bitcast ")
                .append(st_str).append("* ").append(temp).append(" to ")
                .append(dt_str).append("*");
            return;
        }
                
        String new_val = local();
        
        String old_val = load(buff, st_str, temp);
        alloca(buff, dt_str, cast);
        
        if(st instanceof CTypeIntegral) {
            if(dt instanceof CTypePointing) {
                System.out.println("; !!!! TODO 1"); // TODO
            } else if(dt instanceof CTypeFloating) {
                
                CTypeIntegral it = (CTypeIntegral) st;
                
                //String conv_val1 = local();
                //String conv_val2 = local();
                
                buff.nl().append(new_val).append(" = ")
                    .append(-1 == it._signed ? "uitofp " : "sitofp ")
                    .append(st_str).append(" ")
                    .append(old_val).append(" to ").append(dt_str);
                
                // int -> float. this is an unusual hack where the result
                // of *tofp for a float results in nan/zero, but where once
                // extended, it gets us the right number. so, we call a made
                // up conversion function which does the trick, although I've
                // noticed that the results remain slightly wrong.
                if(0 == dt_str.compareTo("float")) {
                    temp = local();
                    buff.nl().append(temp).append(" = fpext float ")
                        .append(new_val).append(" to double");
                    
                    // such an unusual hack
                    new_val = local();
                    buff.nl().append(new_val)
                        .append(" = call float (double)* @double$float(double ")
                        .append(temp).append(")");
                }
                
            } else if(dt instanceof CTypeIntegral) {
                CTypeIntegral it = (CTypeIntegral) dt;
                buff.nl().append(new_val).append(" = ")
                    .append(-1 == it._signed ? "zext " : "sext ")
                    .append(st_str).append(" ")
                    .append(old_val).append(" to ").append(dt_str);
            }
        } else if(st instanceof CTypeFloating) {
            if(dt instanceof CTypeIntegral) {
                
                CTypeIntegral it = (CTypeIntegral) dt;
                
                buff.nl().append(new_val).append(" = ")
                    .append(-1 == it._signed ? "fptoui " : "fptosi ")
                    .append(st_str).append(" ")
                    .append(old_val).append(" to ").append(dt_str);                
            } else if(dt instanceof CTypeFloating) {
                
                //String adder = binary(buff, "fadd", st_str, old_val, "0.0");
                //if(st.sizeOf(env) < dt.sizeOf(env)) {
                    buff.nl().append(new_val).append(" = ")
                        .append(st.sizeOf(env) < dt.sizeOf(env) ? "fpext " : "fptrunc ")
                        .append(st_str).append(" ")
                        .append(old_val).append(" to ").append(dt_str);
                /*} else {
                    buff.nl().append(new_val)
                        .append(" = call float (double)* @double$float(double ")
                        .append(old_val).append(")");
                }*/
            }
        }

        store(buff, dt_str, new_val, cast);
    }
    
    public void visit(CodeExprConditional cc) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Handle pointer arithmetic.
     */
    private void visitPointerArithmetic(
        CodeBuffer buff, 
        CodeExpr ptr, String ptr_var, 
        CodeExpr num, String num_var
    ) {
        CTypePointing ptr_t = (CTypePointing) ptr._type;
        CTypeIntegral num_t = (CTypeIntegral) num._type;
        
        String ptr_t_str = ir_type.toString(ptr_t, true);
        String temp = local();
        
        num_var = load(buff, num_t, num_var);
        String ob = load(buff, ptr_t_str, ptr_var);
        
        buff.nl().append(temp).append(" = getelementptr ")
            .append(ptr_t_str).append(" ")
            .append(ob).append(", ")
            .append(ir_type.toString(num_t)).append(" ").append(num_var);
        
        temps.push(store(buff, ptr_t_str, temp, alloca(buff, ptr_t_str, local())));
    }
    
    public void visit(CodeExprInfix cc) {
        CType ta = cc._a._type;
        CType tb = cc._b._type;
        
        CodeBuffer buff = b(cc);
        
        cc._a.acceptVisitor(this);
        String left_addr = temps.pop();
        String right_addr;
        
        buff.nl().nl().append("; infix op");
        
        // deal with pointer arithmetic
        switch(cc._op._type) {
            case CTokenType.COMMA:
                cc._b.acceptVisitor(this);
                temps.push(temps.pop());
                return;
            case CTokenType.PLUS:
                if(ta instanceof CTypePointing) {
                    cc._b.acceptVisitor(this);
                    visitPointerArithmetic(buff, cc._a, left_addr, cc._b, temps.pop());
                    return;
                } else if(tb instanceof CTypePointing) {
                    cc._b.acceptVisitor(this);
                    visitPointerArithmetic(buff, cc._b, temps.pop(), cc._a, left_addr);
                    return;
                }
        }
        
        String left_type = ir_type.toString(ta, true);
        String right_type = ir_type.toString(tb, true);
        String result_type = ir_type.toString(cc._type, true);
        
        String left = load(buff, left_type, left_addr);
        String right = null;
        
        // deal with the short-circuiting operators
        switch(cc._op._type) {
            case CTokenType.VBAR_VBAR: {
                String init = label();
                String if_no = label();
                String done = label();
                String res_addr = alloca(buff, left_type, local());
                
                br(buff, init);
                buff.label(init);
                br(buff, binary(buff, "icmp eq", left_type, "1", left), done, if_no);
                
                buff.label(if_no);
                cc._b.acceptVisitor(this);
                
                String rhs_val = load(buff, right_type, temps.pop());
                br(buff, done);
                
                buff.label(done);
                store(buff, result_type, binary(
                    buff, "phi", result_type, "[1, %" + init +"]", "[" + rhs_val + ", %" + if_no +"]" 
                ), res_addr);
                temps.push(res_addr);
                
                return;
            }
            case CTokenType.AMP_AMP: {
                String if_yes = label();
                String if_no = label();
                String done = label();
                String res_addr = alloca(buff, left_type, local());
                
                br(buff, if_no); // annoying hack
                buff.label(if_no);
                br(buff, binary(buff, "icmp eq", left_type, "1", left), if_yes, done);
                
                buff.label(if_yes);
                cc._b.acceptVisitor(this);
                
                String rhs_val = load(buff, right_type, temps.pop());
                br(buff, done);
                
                buff.label(done);
                store(buff, result_type, binary(
                    buff, "phi", result_type, "[0, %" + if_no +"]", "[" + rhs_val + ", %" + if_yes +"]" 
                ), res_addr);
                temps.push(res_addr);
                return;
            }
        }
        
        cc._b.acceptVisitor(this);
        right_addr = temps.pop();
        
        right = load(buff, right_type, right_addr);
        String result_addr = alloca(buff, result_type, local());
        String result = null;
        String sign_prefix = "";
        String type_prefix = "";
        String ptr_prefix = "i";
        String comp_prefix = "u";
        
        if(ta instanceof CTypeIntegral) {
            CTypeIntegral it = (CTypeIntegral) ta;
            sign_prefix = (-1 == it._signed) ? "u" : "s";
            comp_prefix = sign_prefix;
        } else {
            type_prefix = "f";
            ptr_prefix = "f";
        }
        
        switch(cc._op._type) {
            case CTokenType.VBAR:
                result = binary(buff, "or", result_type, left, right);
                break;
                
            case CTokenType.XOR:
                result = binary(buff, "xor", result_type, left, right);
                break;
                
            case CTokenType.AMP:
                result = binary(buff, "and", result_type, left, right);
                break;
                
            case CTokenType.MOD:
                result = binary(
                    buff, 
                    sign_prefix + type_prefix + "rem", 
                    result_type, left, right
                );
                break;
            
            case CTokenType.LSH:
                result = binary(buff, "shl", result_type, left, right);
                break;
                
            case CTokenType.RSH:
                if(0 == "u".compareTo(sign_prefix)) {
                    result = binary(buff, "lshr", result_type, left, right);
                } else {
                    result = binary(buff, "ashr", result_type, left, right);
                }
                break;
                
            case CTokenType.PLUS:
                result = binary(buff, type_prefix + "add", result_type, left, right);
                break;
                
            case CTokenType.MINUS:
                result = binary(buff, type_prefix + "sub", result_type, left, right);
                break;
                
            case CTokenType.STAR:
                result = binary(buff, type_prefix + "mul", result_type, left, right);
                break;
                
            case CTokenType.SLASH:
                result = binary(
                    buff, 
                    sign_prefix + type_prefix + "div", 
                    result_type, left, right
                );
                break;                
            
            // relational
            case CTokenType.EQUALS:
                result = op(buff, "zext i1 ", 
                    binary(buff, ptr_prefix + "cmp eq", left_type, left, right), 
                    " to i32"
                );
                break;
            case CTokenType.NOT_EQUALS:
                result = op(buff, "zext i1 ", 
                    binary(buff, ptr_prefix + "cmp ne", left_type, left, right), 
                    " to i32"
                );
                break;
            case CTokenType.LT:
                result = op(buff, "zext i1 ", 
                    binary(buff, ptr_prefix + "cmp " + comp_prefix + "lt", left_type, left, right), 
                    " to i32"
                );
                break;
            case CTokenType.GT:
                result = op(buff, "zext i1 ", 
                    binary(buff, ptr_prefix + "cmp " + comp_prefix + "gt", left_type, left, right), 
                    " to i32"
                );
                break;
            case CTokenType.LT_EQ:
                result = op(buff, "zext i1 ", 
                    binary(buff, ptr_prefix + "cmp " + comp_prefix + "le", left_type, left, right), 
                    " to i32"
                );
                break;
            case CTokenType.GT_EQ:
                result = op(buff, "zext i1 ", 
                    binary(buff, ptr_prefix + "cmp " + comp_prefix + "ge", left_type, left, right), 
                    " to i32"
                );
                break;
        }
        store(buff, result_type, result, result_addr);
        temps.push(result_addr);
    }
    
    public void visit(CodeExprParen cc) {
        cc._expr.acceptVisitor(this);
    }
    
    public void visit(CodeExprPostfix cc) {
        
        cc._a.acceptVisitor(this);
        
        CodeBuffer buff = b(cc);
        CType tt = cc._a._type;
        String tt_str = ir_type.toString(tt, true);
        String val_addr = temps.pop();
        String val;
        int amount = 1;
        
        buff.nl().nl().append("; postfix op");
        
        switch(cc._op._type) {
            case CTokenType.PLUS_PLUS:
                amount = -1;
                // fall-through
            case CTokenType.MINUS_MINUS:
                String incr = Integer.toString(0 - amount);
                val = load(buff, tt_str, val_addr);
                String temp = null;
                
                if(tt instanceof CTypePointing) {
                    temp = local();
                    buff.nl().append(temp).append(" = getelementptr ")
                        .append(tt_str).append(" ").append(val)
                        .append(", i32 ").append(incr);
                    
                } else if(tt instanceof CTypeIntegral) {
                    temp = binary(buff, "add", tt_str, incr, val);
                } else if(tt instanceof CTypeFloating) {
                    temp = binary(buff, "fadd", tt_str, incr + ".0", val);
                }
                
                store(buff, tt_str, temp, val_addr);
                
                temps.push(store(
                    buff, tt_str, val, 
                    alloca(buff, tt_str, local())
                ));
        }
    }
    
    public void visit(CodeExprPrefix cc) {
        cc._a.acceptVisitor(this);
        
        CodeBuffer buff = b(cc);
        String type = ir_type.toString(cc._type, true);
        
        CType tt = cc._a._type;
        String tt_str = ir_type.toString(tt, true);
        String val_addr = temps.pop();
        String val;
        String temp = null;
        
        buff.nl().nl().append("; prefix op");
        
        int amount = 1;
        switch(cc._op._type) {
            case CTokenType.PLUS_PLUS:
                amount = -1;
                // fall-through
            case CTokenType.MINUS_MINUS:
                String incr = Integer.toString(0 - amount);
                val = load(buff, tt_str, val_addr);
                
                if(tt instanceof CTypePointing) {
                    temp = local();
                    buff.nl().append(temp).append(" = getelementptr ")
                        .append(tt_str).append(" ").append(val)
                        .append(", i32 ").append(incr);
                    
                } else if(tt instanceof CTypeIntegral) {
                    temp = binary(buff, "add", tt_str, incr, val);
                } else if(tt instanceof CTypeFloating) {
                    temp = binary(buff, "add", tt_str, incr + ".0", val);
                }
                
                temps.push(store(buff, tt_str, temp, val_addr));
                
                break;
            case CTokenType.AMP:
                temps.push(store(
                    buff, type, val_addr, alloca(buff, type, local())
                ));
                break;
            case CTokenType.STAR:
                buff.nl();
                temps.push(load(buff, tt_str, val_addr));
                break;
            case CTokenType.PLUS:
                temps.push(val_addr);
                break;
            case CTokenType.MINUS:
                val = load(buff, tt_str, val_addr);
                if(tt instanceof CTypeFloating) {
                    temp = binary(buff, "fsub", tt_str, "0.0", val);
                } else {
                    temp = binary(buff, "sub", tt_str, "0", val);
                }
                temps.push(store(buff, tt_str, temp, alloca(buff, tt_str, local())));
                break;
            
            // the TypeInferenceVisitor injects boolean tests, so we know
            // this value will take on 0 or 1. to logically negate it, we
            // need only subtract it from 1.
            case CTokenType.NOT:
                val = load(buff, tt_str, val_addr);
                temp = binary(buff, "sub", tt_str, "1", val);
                temps.push(store(buff, tt_str, temp, alloca(buff, tt_str, local())));
                break;
                
            case CTokenType.TILDE:
                val = load(buff, tt_str, val_addr);
                temp = local();
                temp = binary(buff, "xor", tt_str, "-1", val);
                temps.push(store(buff, tt_str, temp, alloca(buff, tt_str, local())));
                break;
        }
    }
    
    public void visit(CodeExprId cc) {
        temps.push(name(cc._id));
    }
    
    public void visit(CodeExprSizeofValue cc) {
        visitConstant(new CodeIntegerConstant(
            ((CompileTimeInteger) cc._const_val).value
        ));
    }
    
    public void visit(CodeExprSizeofType cc) {
        visitConstant(new CodeIntegerConstant(
            ((CompileTimeInteger) cc._const_val).value
        ));
    }
    
    public void visit(CodeExprCall cc) {
        CodeBuffer buff = b(cc);
        LinkedList<String> params = new LinkedList<String>();
        
        for(CodeExpr param : cc._argl) {
            param.acceptVisitor(this);
            params.add(load(buff, param._type, temps.pop()));
        }
        
        cc._fun.acceptVisitor(this);
        String func = temps.pop();
        CTypeFunctionPointer fptr = (CTypeFunctionPointer) cc._fun._type;
        CTypeFunction ft = (CTypeFunction) fptr._pointeeType;
        
        buff.nl().nl().append("; function call").nl();
        
        String res = "";
        
        if(!(ft._retType instanceof CTypeVoid)) {
            res = local();
            buff.append(res).append(" = ");
        }
        
        String ret_type = ir_type.toString(ft._retType, true);
        String type = ir_type.toString(fptr);
        
        buff.append("call ").append(type).append(" ")
            .append(func).append("(");
        String sep = "";
        
        for(CodeExpr param : cc._argl) {
            buff.append(sep).append(ir_type.toString(param._type, true))
                .append(" ").append(params.removeFirst());
            sep = ", ";
        }
        
        buff.append(")");
        
        if(!(ft._retType instanceof CTypeVoid)) {
            temps.push(store(buff, ret_type, res, alloca(buff, ret_type, null)));
        }
    }
    
    private void visitAccess(CodeExprAccess cc, String type) {
        
        String ob = temps.pop();
        String temp = local();
        
        b(cc).nl().append(temp).append(" = getelementptr ")
            .append(type).append("* ")
            .append(ob).append(", i32 0, i32 ")
            .append(Integer.toString(cc._offset));
        
        temps.push(temp);
    }
    
    public void visit(CodeExprField cc) {
        cc._ob.acceptVisitor(this);
        b(cc).nl().nl().append("; field access");
        visitAccess(cc, ir_type.toString(cc._ob._type, true));
    }
    
    public void visit(CodeExprPointsTo cc) {
        cc._ob.acceptVisitor(this);
        CTypePointing type = (CTypePointing) cc._ob._type;
        CodeBuffer buff = b(cc);
        buff.nl().nl().append("; pointer access");
        temps.push(load(buff, cc._ob._type, temps.pop()));
        visitAccess(
            cc, 
            ir_type.toString(((CTypePointing) cc._ob._type)._pointeeType, true)
        );
    }
    
}
