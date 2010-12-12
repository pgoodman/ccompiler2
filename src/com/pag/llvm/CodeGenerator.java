package com.pag.llvm;

import java.util.LinkedList;

import sun.font.Font2D;

import com.pag.sym.CSymbol;
import com.pag.sym.Env;
import com.pag.sym.Type;
import com.pag.val.CompileTimeInteger;
import com.pag.val.CompileTimeString;
import com.smwatt.comp.CTokenType;
import com.smwatt.comp.CType;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.C.CodeDeclarator;

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
    
    public interface CodeBuffer {
        public CodeBuffer append(String ss);
        public void indent();
        public void outdent();
        public CodeBuffer nl();
        public String toString();
    }
    
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
    };
    
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("\n; global vars declarations");
        buff.append(ginit.toString());
        buff.append("\n\n; global var definitions");
        buff.append("\ndefine ccc void @init$vars() nounwind ssp {");
        buff.append(gvars.toString());
        buff.append("\n  ret void\n}\n\n; code");
        buff.append(code.toString());
        return buff.toString();
    }
    
    /**
     * Simple tabbed code codeer.
     */
    public CodeBuffer code = new SimpleCodeBuffer();
    public CodeBuffer gvars = new SimpleCodeBuffer();
    public CodeBuffer ginit = new SimpleCodeBuffer();
    
    public CodeGenerator(Env ee) {
        env = ee;
        ir_type = new IRTypeBuilder(ee);
        gvars.indent();
    }
    
    /**
     * Make and return the name of a new temporary variable.
     * @return
     */
    /*private String temp(Code cc) {
        return (0 == cc._scope.depth ? "@t$" : "%t$") + Integer.toString(next_temp++);
    }*/
    
    private CodeBuffer b(Code cc) {
        return 0 == cc._scope.depth ? gvars : code;
    }
    
    private String local() {
        return "%t$" + Integer.toString(next_temp++);
    }
    
    private String global() {
        return "@t$" + Integer.toString(next_temp++);
    }
    
    /**
     * Generate a constant value.
     */
    private String constant(CodeExpr cc) {
        if(cc._const_val instanceof CompileTimeString) {
            
        }
        return cc._const_val.toString();
    }
    
    /**
     * Generate a variable name for a CodeId.
     */
    private String name(CodeId id) {
        CSymbol sym = id._scope.get(id._s);
        if(Type.FUNC_DECL == sym.type || Type.FUNC_DEF == sym.type) {
            return "@" + id._s;
        }
        
        return (0 == id._scope.depth ? "@" : "%") 
             + id._s + "$" + Integer.toString(sym.scope.id);
    }
    
    public void visit(Code cc) {
        cc.acceptVisitor(this);
    }
    
    public void visit(CodeUnit cc) {        
        for(Code code : cc._l) {
            code.acceptVisitor(this);
        }
    }
    
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
                
                if(0 == decl._ldtor.size()) {
                    arg_name = "u$" + Integer.toString(unnamed_var++);
                    arg_type = cc._type;
                } else {
                    CodeDeclarator dtor = decl._ldtor.get(0);
                    arg_name = "p$" + dtor.getOptId()._s;
                    arg_type = dtor._type;
                    useful_dtors.add(dtor);
                }
            }
            if(!(arg_type instanceof CTypeStruct)) {
                code.append(sep)
                    .append(ir_type.toString(arg_type, true))
                    .append(" %").append(arg_name);
            } else {
                code.append(sep).append("i")
                    .append(Integer.toString(arg_type.sizeOf(env) * 8))
                    .append(" %").append(arg_name);
            }
            
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
                
        if(tt instanceof CTypeStruct) {
            String temp_var = local();
            String bit_len = Integer.toString(tt.sizeOf(env) * 8);
            
            code.nl().append(var).append(" = alloca ")
                .append(ir_tt).append(", align ")
                .append(Integer.toString(Math.max(4, Math.min(8, tt.sizeOf(env)))));
            code.nl().append(temp_var).append(" = bitcast ").append(ir_tt)
                .append("* ").append(var).append(" to i").append(bit_len)
                .append("*");
            code.nl().append("store i").append(bit_len).append(" %p$")
                .append(id._s).append(", i").append(bit_len).append("* ")
                .append(temp_var).append(", align 1");
        } else {
            code.nl().append(var).append(" = alloca ")
                .append(ir_tt).append(", align ")
                .append(Integer.toString(Math.max(4, Math.min(8, tt.sizeOf(env)))));
            code.nl().append(tt._isVolatile ? "volatile " : "")
                .append("store ").append(ir_tt).append(" %p$")
                .append(id._s).append(", ").append(ir_tt).append("* ")
                .append(var);
        }
    }
    
    public void visit(CodeDeclaration cc) {
        for(CodeDeclarator dtor : cc._ldtor) {
            
            if(null == dtor) {
                continue;
            }
            
            dtor.acceptVisitor(this);
        }
    }
    
    public void visit(CodeId cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeTypeName cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeString cc) {
        StringBuffer buff = new StringBuffer();
        String temp = global();
        String var = global();
        
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
        
        ginit.nl().append(temp).append(" = internal constant ")
            .append(arr_type).append(buff.toString());
        
        ginit.nl().append(var).append(" = constant i8* getelementptr (").append(arr_type)
            .append("* ").append(temp).append(", i32 0, i32 0)");
        
        temps.push(var);
    }
    
    private void visitConstant(CodeExpr cc) {
        String var = local();
        String type = ir_type.toString(cc._type);
        
        temps.push(var);
        
        b(cc).nl().append(var).append(" = alloca ")
            .append(type).append(", align ")
            .append(Integer.toString(Math.min(8, cc._type.sizeOf(env))))
            .nl().append("store ").append(type).append(" ")
            .append(constant(cc)).append(", ").append(type)
            .append("* ").append(var);
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
        /*String tt = temp(cc);
        temps.push(tt);
        code.nl().append(tt).append(" = alloca ")
            .append(ir_type.toString(cc._type))
            .append(" ").append(cc._const_val.toString());*/
        visitConstant(cc);
    }
    
    public void visit(CodeDotDotDot cc) { }
    
    public void visit(CodeSpecifierStorage cc) { }
    
    public void visit(CodeSpecifierQualifier cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeSpecifierType cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeSpecifierTypedefName cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeSpecifierStruct cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeSpecifierUnion cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeSpecifierEnum cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeEnumerator cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeDeclaratorArray cc) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Visit a function declaration. If the code stored in this declarations
     * scope for the function of the same name is actually the id of this
     * declaration then it means we never found a definition and hence should
     * output a declaration. 
     */
    public void visit(CodeDeclaratorFunction cc) {
        
        CodeId id = cc.getOptId();
        CSymbol sym = cc._scope.get(id._s, com.pag.sym.Type.FUNC_DECL);
        
        if(sym.code == id) {
            
            CTypeFunction ft = (CTypeFunction) id._type;
            
            code.nl().nl().append("; ").append(cc.getSourcePosition().toString());
            code.append("\ndeclare ").append(ir_type.toString(ft._retType));
            code.append(" @").append(id._s).append("(");
            String sep = "";
            for(CType at : ft._argTypes) {
                code.append(sep).append(ir_type.toString(at));
                sep = ", ";
            }
            if(ft._moreArgs) {
                code.append(sep).append("...");
            }
            code.append(")").nl();
        }
    }
    
    public void visit(CodeDeclaratorInit cc) {
        CodeId id = cc.getOptId();
        String var = name(id);
        String ir_tt = ir_type.toString(id._type);
        //String ir_tt_ptr = ir_type.toString(id._type, true);
        //String temp_var = temp(id);
        
        
        //CodeBuffer buff = 0 == cc._scope.depth ? gvars : code;
        
        //String val_var = temps.pop();
        
        
        
        // global
        if(0 == cc._scope.depth) {
            
            ginit.nl().append(var).append(" = global ").append(ir_tt)
                 .append(" undef");
            
        // local
        } else {
            
            code.nl().append(var).append(" = alloca ").append(ir_tt);
            
            /*
            code.nl().append(temp_var).append(" = bitcast ").append(ir_tt)
                .append(" ").append(val_var).append(" to ").append(ir_tt_ptr);
            code.nl().append("store ").append(var).append(" to")
            */
            /*if(cc._initializer instanceof CodeInitializerValue) {
                CodeInitializerValue val = (CodeInitializerValue) cc._initializer;
                
                
                
            } else {
                
            }*/
            
            //code.nl().append(var).append(" = alloca ")
            //    .append(type).append(", align 4");
            
            
            
            if(cc._initializer instanceof CodeInitializerValue) {
                //cc._initializer.acceptVisitor(this);
                //code.nl().append("store ").append(ir_tt).append(" ").append(temps.pop())
                //    .append(", ").append(ir_tt).append(" ").append(var);
                
                
            }
        }
        
        temps.push(var);
        cc._initializer.acceptVisitor(this);
        
        /*CodeBuffer buff = 0 == cc._scope.depth ? gvars : code;
        String temp = local();
        buff.nl().append(temp).append(" = load ")
            .append(ir_type.toString(cc._type, true)).append("* ")
            .append(temps.pop());*/
    }
    
    public void visit(CodeInitializerValue cc) {
        cc._value.acceptVisitor(this);
        CodeBuffer buff = 0 == cc._scope.depth ? gvars : code;
        String val = temps.pop();
        String temp = local();
        String var = temps.pop();
        String type = ir_type.toString(cc._value._type, true);
        b(cc).nl().append(temp).append(" = load ")
            .append(type).append("* ").append(val)
            .nl().append("store ").append(type).append(" ").append(temp)
            .append(", ").append(type).append("* ").append(var);
        
        
        //String type = ir_type.toString(cc._value._type);
        /*
        if(0 == cc._scope.depth) {
            code.nl().append(var).append(" = alias ")
                .append(type).append("* ").append(temp);
        } else {
            //code.nl().append(var).append(" = alloca ")
            //    .append(type);
            code.nl().append("store ").append(type).append(" ").append(temp)
                .append(", ").append(type).append(" ").append(var);
        }*/
        
    }
    
    public void visit(CodeInitializerList cc) {
        /*
        String var = temps.pop();
        String type = ir_type.toString(cc._type);
        temps.push(var);
        
        // global
        if(0 == cc._scope.depth) {
            code.nl().append(var).append(" = global ").append(type);
            
            // build up the initializer list
            String sep = "";
            code.append(" [");
            for(CodeInitializer init : cc._list) {
                code.append(sep).append(ir_type.toString(init._type))
                    .append(" ").append(constant(init));
                sep = ", ";
            }
            code.append("], align 4");
            
        // local
        } else {
            
            // compute all the values
            for(CodeInitializer init : cc._list) {
                init.acceptVisitor(this);
            }
            
            // get the temporaries in the right order
            LinkedList<String> vals = new LinkedList<String>();
            for(CodeInitializer _ : cc._list) {
                vals.push(temps.pop());
            }
            
            code.nl().append(var).append(" = alloca ").append(type)
                .append(", align 4");
        }
        */
        
//        // build up the initializer list
//        String sep = "";
//        code.nl().append(var).append(" = [");
//        for(CodeInitializer init : cc._list) {
//            code.append(sep).append(ir_type.toString(init._type))
//                .append(" ").append(vals.pop());
//            sep = ", ";
//        }
//        code.append("]");
        
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
        if(null == cc._optExpr) {
            code.nl().append("ret void");
        } else {
            cc._optExpr.acceptVisitor(this);
            String expr = temps.pop();
            String temp = local();
            String ret_type = ir_type.toString(cc._optExpr._type, true);
            
            code.nl().append(temp).append(" = load ")
                .append(ret_type)
                .append("* ").append(expr);
            
            code.nl().append("ret ").append(ret_type)
                .append(" ").append(temp);
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
        String temp = local();
        String val_type = ir_type.toString(cc._b._type, true);
        
        if(CTokenType.ASSIGN == cc._op._type) {
            b(cc).nl().append(temp).append(" = load ")
                .append(val_type)
                .append("* ").append(val);
            b(cc).nl().append("store ").append(val_type).append(" ")
                .append(temp).append(", ").append(val_type).append("* ")
                .append(ob);
        }
    }
    
    public void visit(CodeExprCast cc) {
        
        cc._expr.acceptVisitor(this);
        
        CType st = cc._expr._type;
        CType dt = cc._type;
        
        String temp = temps.pop();
        String cast = local();
        
        temps.push(cast);
        
        CodeBuffer buff = b(cc);
        
        if(st instanceof CTypeIntegral) {
            if(dt instanceof CTypePointing) {
                
            } else if(st instanceof CTypeFloating) {
                
            } else if(st instanceof CTypeIntegral) {

            }
        } else if(st instanceof CTypeFloating) {
            
        }
        
        // bitcast if nothing above did anything
        buff.nl().append(cast).append(" = bitcast ")
            .append(ir_type.toString(cc._expr._type, true))
            .append("* ").append(temp).append(" to ")
            .append(ir_type.toString(cc._type, true)).append("*");
    }
    
    public void visit(CodeExprConditional cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeExprInfix cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeExprParen cc) {
        cc._expr.acceptVisitor(this);
    }
    
    public void visit(CodeExprPostfix cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeExprPrefix cc) {
        // TODO Auto-generated method stub
        
    }
    
    public void visit(CodeExprId cc) {
        
        temps.push(name(cc._id));
        /*String temp = local();
        CodeBuffer buff = 0 == cc._scope.depth ? gvars : code;
        buff.nl().append(temp).append(" = load ")
            .append(ir_type.toString(cc._type, true)).append("* ")
            .append(name(cc._id));
        temps.push(temp);*/
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
            //params.add(temps.pop());
            String temp = local(); //temps.pop();
            
            buff.nl().append(temp).append(" = load ")
                .append(ir_type.toString(param._type, true))
                .append("* ").append(temps.pop());
            
            params.add(temp);
            
        }
        cc._fun.acceptVisitor(this);
        String func = temps.pop();
        CTypeFunctionPointer fptr = (CTypeFunctionPointer) cc._fun._type;
        CTypeFunction ft = (CTypeFunction) fptr._pointeeType;
        
        
        
        buff.nl();
        
        if(!(ft._retType instanceof CTypeVoid)) {
            String res = local();
            temps.push(res);
            buff.append(res).append(" = ");
        }
        
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
    }
    
    public void visit(CodeExprField cc) {
        cc._ob.acceptVisitor(this);
        String ob = temps.pop();
        String temp = local();
        
        b(cc).nl().append(temp).append(" = getelementptr ")
            .append(ir_type.toString(cc._ob._type)).append("* ")
            .append(ob).append(", i32 0, i32 ")
            .append(Integer.toString(cc._offset));
        
        temps.push(temp);
    }
    
    public void visit(CodeExprPointsTo cc) {
        cc._ptr.acceptVisitor(this);
        String ob = temps.pop();
        String temp = local();
        
        b(cc).nl().append(temp).append(" = getelementptr ")
            .append(ir_type.toString(cc._ptr._type)).append("* ")
            .append(ob).append(", i32 0, i32 0, i32 ")
            .append(Integer.toString(cc._offset));
    }
    
}
