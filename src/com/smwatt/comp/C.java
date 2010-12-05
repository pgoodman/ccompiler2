//////////////////////////////////////////////////////////////////////////////
//
// Code -- A representation for C parse trees.
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt. All rights reserved.

package com.smwatt.comp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.pag.sym.Scope;

///////////////////////////////////////////////////////////////////////////////
//
// Abstract base classes
//
//////////////////////////////////////////////////////////////////////////////


public class C {
    
    static public interface CodeVisitor {
        
        // Top Level
        public void visit(Code cc);
        public void visit(CodeUnit cc);
        public void visit(CodeFunction cc);
        public void visit(CodeDeclaration cc);
        
        // Basic Items
        public void visit(CodeId cc);
        public void visit(CodeTypeName cc);
        public void visit(CodeString cc);
        public void visit(CodeCharacterConstant cc);
        public void visit(CodeIntegerConstant cc);
        public void visit(CodeFloatingConstant cc);
        public void visit(CodeEnumerationConstant cc);
        public void visit(CodeDotDotDot cc);
        
        // Specifiers
        public void visit(CodeSpecifierStorage cc);
        public void visit(CodeSpecifierQualifier cc);
        public void visit(CodeSpecifierType cc);
        public void visit(CodeSpecifierTypedefName cc);
        public void visit(CodeSpecifierStruct cc);
        public void visit(CodeSpecifierUnion cc);
        public void visit(CodeSpecifierEnum cc);
        public void visit(CodeEnumerator cc);
        
        // Declarators
        public void visit(CodeDeclaratorArray cc);
        public void visit(CodeDeclaratorFunction cc);
        public void visit(CodeDeclaratorInit cc);
        public void visit(CodeDeclaratorPointer cc);
        public void visit(CodeDeclaratorWidth cc);
        public void visit(CodeDeclaratorId cc);
        public void visit(CodePointerStar cc);
        public void visit(CodeDeclaratorParen cc);
    
        public void visit(CodeInitializerValue cc);
        public void visit(CodeInitializerList cc);
        
        // Statements
        public void visit(CodeStatBreak cc);
        public void visit(CodeStatCase cc);
        public void visit(CodeStatCompound cc);
        public void visit(CodeStatContinue cc);
        public void visit(CodeStatDefault cc);
        public void visit(CodeStatDo cc);
        public void visit(CodeStatExpression cc);
        public void visit(CodeStatFor cc);
        public void visit(CodeStatGoto cc);
        public void visit(CodeStatIf cc);
        public void visit(CodeStatLabeled cc);
        public void visit(CodeStatReturn cc);
        public void visit(CodeStatSwitch cc);
        public void visit(CodeStatWhile cc);
        
        // Expressions
        public void visit(CodeExprAssignment cc);
        public void visit(CodeExprCast cc);
        public void visit(CodeExprConditional cc);
        public void visit(CodeExprInfix cc);
        public void visit(CodeExprParen cc);
        public void visit(CodeExprPostfix cc);
        public void visit(CodeExprPrefix cc);
        public void visit(CodeExprId cc);
        public void visit(CodeExprSizeofValue cc);
        public void visit(CodeExprSizeofType cc);
        public void visit(CodeExprCall cc);
        public void visit(CodeExprSubscript cc);
        public void visit(CodeExprField cc);
        public void visit(CodeExprPointsTo cc);
    }
    
    static public abstract class Code {
    	//CodeAnnotation	_annot = null;
        public Scope _scope = null;
        public CType _type = null;
    	SourcePosition  _spos  = null;
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	public void copyPosition(CToken k) { 
    		_spos = new SourcePosition(k._fname, k._line, k._col);
    	}
    	public void copyPosition(Code cc) {
    	    if(null == _spos && null != cc) {
    	        _spos = cc._spos;
    	    }
    	}
    	public <T extends Code> void copyPosition(List<T> ls, Code ... ccs) {
    	    if(null != _spos) {
    	        return;
    	    }
    	    if(null != ls) {
        	    for(Code obj : ls) {
        	        if(null != obj._spos) {
        	            copyPosition(obj);
        	            return;
        	        }
        	    }
    	    }
    	    for(Code obj : ccs) {
    	        if(null != obj && null != obj._spos) {
                    copyPosition(obj);
                    return;
                }
    	    }
    	}
    	public SourcePosition getSourcePosition() { return _spos; }
    }
    
    static public class CodeAnnotation {
        String          _name;
        CodeAnnotation _next;
    }
    
    
    static public abstract class CodeSpecifier extends Code {
        public boolean isTypedef() {
            return false;
        }
    }
    
    static public abstract class CodeDeclarator extends Code {
        
        public boolean _is_typedef = false;
        
    	/**
    	 * Return the id delcared.
    	 * If this is an "abstract declarator" (with no id),
    	 * then null is returned.
    	 */
    	abstract public CodeId getOptId();
    	/**
    	 * If this is ultimately a declarator for a function,
    	 * return the function declarator.  Otherwise, return null.
    	 */
    	abstract public CodeDeclaratorFunction getOptFunction();
    }
    
    static public abstract class CodeStat extends Code {
    }
    
    static public abstract class CodeExpr extends Code {
    	//CType	_optTypeAnnotation = null;
        public Object _const_val = null; 
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Top Level
    //
    ///////////////////////////////////////////////////////////////////////////////
    //
    static public class CodeUnit extends Code {
    	public List<Code> _l;
    	//CStaticEnv	_scope; // TODO
    	//public Env _scope;
    	
    	CodeUnit(List<Code> l) { _l = l; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeFunction extends Code {
    	public List<CodeSpecifier>   _lspec;
    	public CodeDeclarator        _head;
    	public List<CodeDeclaration> _ldecl;	// Old C parameter declarations
    	public CodeStatCompound      _body;
    	public boolean               _is_static = false;
    	public Scope                 _internal_scope = null;
    	
    	public Hashtable<String,CodeStatLabeled> labels = new Hashtable<String,CodeStatLabeled>();
    	
    	CodeFunction(List<CodeSpecifier> lspec,
    		      CodeDeclarator          head,
    		      List<CodeDeclaration>   ldecl,
    		      CodeStatCompound        body) 
    	{ 
    	    _lspec = lspec; _head = head; _ldecl = ldecl; _body = body;
    	    copyPosition(lspec, head);
    	}
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeDeclaration extends Code {
    	public List<CodeSpecifier>  _lspec;
    	public List<CodeDeclarator> _ldtor;
    	public boolean              _is_static;
    	
    	CodeDeclaration(List<CodeSpecifier> lspec, List<CodeDeclarator> ldtor) 
    	{ 
    	    _lspec = lspec; _ldtor = ldtor;
    	    copyPosition(lspec);
    	    copyPosition(ldtor);
    	}
    	
    	CodeDeclaration(List<CodeSpecifier> lspec, CodeDeclarator dtor) {
    		_lspec = lspec;
    		_ldtor = new ArrayList<CodeDeclarator>();
    		_ldtor.add(dtor);
    		copyPosition(lspec, dtor);
    	}
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Basic Items
    //
    ///////////////////////////////////////////////////////////////////////////////
    
    static public class CodeId extends Code {
    	public String  _s;
    	//public CSymbol _optSymAnnotation = null; // TODO
    	
    	CodeId(String s) { _s = s; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    static public class CodeTypeName extends Code {
    	public List<CodeSpecifier>  _lspec;
    	public CodeDeclarator       _dtor;
    	
    	CodeTypeName(List<CodeSpecifier> lspec, CodeDeclarator dtor) 
    	{ 
    	    _lspec = lspec; _dtor = dtor; 
    	    copyPosition(lspec, dtor);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    static public abstract class CodeConstant extends CodeExpr {
    	public String _s;
    	
    	CodeConstant(String s) { _s = s; }
    	
    	// public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeString extends CodeConstant {
    	CodeString(String s) { super(s); }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeCharacterConstant extends CodeConstant {
    	CodeCharacterConstant(String s) { super(s); }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeIntegerConstant extends CodeConstant {
    	CodeIntegerConstant(String s) { super(s); }
    	public CodeIntegerConstant(int i) {
    	    super("");
    	    _const_val = new Integer(i);
    	}
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    static public class CodeFloatingConstant extends CodeConstant {
    	CodeFloatingConstant(String s) { super(s); }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeEnumerationConstant extends CodeConstant {
    	CodeEnumerationConstant(String s) { super(s); }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeDotDotDot extends CodeDeclaration {
    	CodeDotDotDot(CToken k) {
    		super(null, (List<CodeDeclarator>) null);
    		copyPosition(k);
    	}
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Specifiers
    //
    ///////////////////////////////////////////////////////////////////////////////
    
    static public class CodeSpecifierStorage extends CodeSpecifier {
    	public CTokenSpecifier _spec;
    	
    	CodeSpecifierStorage(CTokenSpecifier spec) { 
    	    _spec = spec;
    	    copyPosition(spec);
    	}
    	
    	// public CTokenSpecifier spec() { return _spec; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	public boolean isTypedef() { return _spec.isTypedef(); }
    }
    static public class CodeSpecifierQualifier extends CodeSpecifier {
    	public CTokenSpecifier _spec;
    	
    	CodeSpecifierQualifier(CTokenSpecifier spec) { 
    	    _spec = spec; 
    	    copyPosition(spec);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeSpecifierType extends CodeSpecifier {
    	public CTokenSpecifier _spec;
    	
    	CodeSpecifierType(CTokenSpecifier spec) { 
    	    _spec = spec; 
    	    copyPosition(spec);
	    }
    	
    	public boolean isTypedef() { return _spec.isTypedef(); }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeSpecifierTypedefName extends CodeSpecifier {
    	public CodeId _id;
    	
    	CodeSpecifierTypedefName(CodeId id) { 
    	    _id = id; 
    	    copyPosition(id);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    static public abstract class CodeSpecifierStructUnionEnum extends CodeSpecifier {
    	public CodeId       _optId;
    	public List<? extends Code> _optParts;  // List<CodeDeclaration> or List<CodeEnumerator>
    	
    	CodeSpecifierStructUnionEnum(
    		CodeId       optId, 
    		List<? extends Code> optParts) 
    	{ 
    	    _optId = optId; _optParts = optParts;
    	    copyPosition(optId);
    	    copyPosition(optParts);
	    }
    	
    	// public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeSpecifierStruct extends CodeSpecifierStructUnionEnum {
    	CodeSpecifierStruct(CodeId optId, List<CodeDeclaration> optDecls)
    	{ super(optId, optDecls); }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeSpecifierUnion extends CodeSpecifierStructUnionEnum {
    	CodeSpecifierUnion(CodeId optId, List<CodeDeclaration> optDecls)
    	{ super(optId, optDecls); }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeSpecifierEnum extends CodeSpecifierStructUnionEnum {
    	CodeSpecifierEnum(CodeId optId, List<CodeEnumerator> optEnums)
    	{ super(optId, optEnums); }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeEnumerator extends Code {
    	public CodeId _id;
    	public CodeExpr       _optValue;
    	
    	CodeEnumerator(CodeId id) 
    	{ 
    	    _id = id; _optValue = null; 
    	    copyPosition(id);
	    }
    	CodeEnumerator(CodeId id, CodeExpr value) 
    	{ 
    	    _id = id; _optValue = value; 
    	    copyPosition(id);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Declarators
    //
    ///////////////////////////////////////////////////////////////////////////////
    
    static public class CodeDeclaratorArray extends CodeDeclarator {
    	public CodeDeclarator  _optAr;
    	public CodeExpr        _optIndex;
    	
    	CodeDeclaratorArray(CodeDeclarator optAr, CodeExpr optIndex) 
    	{ 
    	    _optAr = optAr; _optIndex = optIndex; 
    	    copyPosition(optAr);
	        copyPosition(optIndex);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	
    	public CodeId getOptId() {
    		return _optAr == null ? null : _optAr.getOptId();
    	}
    	public CodeDeclaratorFunction getOptFunction() {
    		return null;
    	}
    }
    static public class CodeDeclaratorFunction extends CodeDeclarator {
    	public CodeDeclarator       _optFn;
    	public List<? extends Code> _argl;  // Either List<CodeDeclaration> or List<CodeIdentifier>
    	
    	CodeDeclaratorFunction(CodeDeclarator optFn, List<? extends Code> argl) 
    	{ 
    	    _optFn = optFn; _argl = argl; 
    	    copyPosition(optFn);
    	    copyPosition(argl);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	
    	public CodeId getOptId() {
    		return _optFn == null ? null : _optFn.getOptId();
    	}
    	public CodeDeclaratorFunction getOptFunction() {
    		return this;
    	}
    }
    static public class CodeDeclaratorInit extends CodeDeclarator {
    	public CodeDeclarator  _dtor;
    	public CodeInitializer _initializer;
    	
    	CodeDeclaratorInit(CodeDeclarator dtor, CodeInitializer initializer)
    	{ _dtor = dtor; _initializer = initializer; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	
    	public CodeId getOptId() { 
    		return _dtor.getOptId(); 
    	}
    	public CodeDeclaratorFunction getOptFunction() {
    		// Cannot have initializer on function.
    		return null;
    	}
    }
    static public class CodeDeclaratorPointer extends CodeDeclarator {
    	public CodePointerStar _star;
    	public CodeDeclarator  _optPointee;
    	
    	CodeDeclaratorPointer(CodePointerStar star, CodeDeclarator optPointee) 
    	{ _star = star; _optPointee = optPointee; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	
    	public CodeId getOptId() {
    		return _optPointee == null ? null : _optPointee.getOptId();
    	}
    	public CodeDeclaratorFunction getOptFunction() {
    		return _optPointee == null ? null : _optPointee.getOptFunction();
    	}
    }
    static public class CodeDeclaratorWidth extends CodeDeclarator {
    	public CodeDeclarator _dtor;
    	public CodeExpr       _width;
    	
    	CodeDeclaratorWidth(CodeDeclarator dtor, CodeExpr width) 
    	{ _dtor = dtor; _width = width; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	
    	public CodeId getOptId() {
    		return _dtor.getOptId();
    	}
    	public CodeDeclaratorFunction getOptFunction() {
    		// Cannot have width on function.
    		return null;
    	}
    }
    static public class CodeDeclaratorId extends CodeDeclarator {
    	public CodeId _id;
    	
    	CodeDeclaratorId(CodeId id) { 
    	    _id = id;
    	    copyPosition(id);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    	
    	public CodeId getOptId() {
    		return _id;
    	}
    	
    	public CodeDeclaratorFunction getOptFunction() {
    		return null;
    	}
    }
    
    static public class CodeDeclaratorParen extends CodeDeclarator {
        public CodeDeclarator _decl;
        
        CodeDeclaratorParen(CodeDeclarator decl) {
            _decl = decl;
            copyPosition(decl);
        }
        
        public void acceptVisitor(CodeVisitor v) { v.visit(this); }
        
        public CodeId getOptId() {
            return _decl.getOptId();
        }
        
        public CodeDeclaratorFunction getOptFunction() {
            return _decl.getOptFunction();
        }
    }
    
    static public class CodePointerStar extends Code {
    	public List<CodeSpecifier>   _lspec;
    	public CodePointerStar       _optStar;
    	
    	CodePointerStar(
    		List<CodeSpecifier> lspec, 
    		CodePointerStar optStar) 
    	{ _lspec = lspec; _optStar = optStar; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    static public abstract class CodeInitializer extends Code {
    }
    static public class CodeInitializerValue extends CodeInitializer {
    	public CodeExpr _value;
    	
    	CodeInitializerValue(CodeExpr value) { _value = value; }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeInitializerList extends CodeInitializer {
    	public List<CodeInitializer> _list;
    	
    	CodeInitializerList(List<CodeInitializer> list) { _list = list; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Statements
    //
    ///////////////////////////////////////////////////////////////////////////////
    
    static public class CodeStatBreak extends CodeStat {
    	CodeStatBreak() { } 
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatCase extends CodeStat {
    	public CodeExpr _value;
    	public CodeStat _stat;
    	
    	CodeStatCase(CodeExpr value, CodeStat stat) 
    	{ _value = value; _stat = stat; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatCompound extends CodeStat {
    	public List<CodeDeclaration> _ldecl;
    	public List<CodeStat> 	   _lstat;
    	//CStaticEnv             _scope = null; // TODO
    	
    	CodeStatCompound(List<CodeDeclaration> ldecl, List<CodeStat> lstat)
    	{ 
    	    _ldecl = ldecl; 
    	    _lstat = lstat;
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatContinue extends CodeStat {
    	CodeStatContinue() { } 
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatDefault extends CodeStat {
    	public CodeStat _stat;
    	
    	CodeStatDefault(CodeStat stat) { _stat = stat; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatDo extends CodeStat {
    	public CodeExpr _test;
    	public CodeStat _stat;
    	
    	CodeStatDo(CodeExpr test, CodeStat stat) 
    	{ _test = test; _stat = stat; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatExpression extends CodeStat {
    	public CodeExpr _optExpr;
    	
    	CodeStatExpression(CodeExpr optExpr) { 
    	    _optExpr = optExpr; 
    	    copyPosition(optExpr);
	    }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatFor extends CodeStat {
    	public CodeExpr _optInit, _optTest, _optStep;
    	public CodeStat _stat;
    	
    	CodeStatFor(CodeExpr optInit, CodeExpr optTest, CodeExpr optStep, 
    			     CodeStat stat) 
    	{ _optInit = optInit; _optTest = optTest; _optStep = optStep; _stat = stat; }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatGoto extends CodeStat {
    	public CodeId _label;
    	
    	CodeStatGoto(CodeId label) { _label = label; } 
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatIf extends CodeStat {
    	public CodeExpr _test;
    	public CodeStat _thstat;
    	public CodeStat _optElstat;
    	
    	CodeStatIf(CodeExpr test, CodeStat thstat, CodeStat optElstat) 
    	{ 
    	    _test = test; 
    	    _thstat = thstat; 
    	    _optElstat = optElstat;
	    }   
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatLabeled extends CodeStat {
    	public CodeId _label;
    	public CodeStat   _stat;
    	public boolean is_used = false;
    	
    	CodeStatLabeled(CodeId label, CodeStat stat) 
    	{ 
    	    _label = label; 
    	    _stat = stat; 
    	    copyPosition(label);
	    }
    
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatReturn extends CodeStat {
    	public CodeExpr _optExpr;
    	
    	CodeStatReturn(CodeExpr optExpr) { 
    	    _optExpr = optExpr;
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatSwitch extends CodeStat {
    	public CodeExpr _expr;
    	public CodeStat _stat;
    	
    	CodeStatSwitch(CodeExpr expr, CodeStat stat) 
    	{ 
    	    _expr = expr; 
    	    _stat = stat; 
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeStatWhile extends CodeStat {
    	public CodeExpr _test;
    	public CodeStat _stat;
    	
    	CodeStatWhile(CodeExpr test, CodeStat stat)
    	{ 
    	    _test = test; 
    	    _stat = stat;
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    ///////////////////////////////////////////////////////////////////////////////
    //
    // Expressions
    //
    ///////////////////////////////////////////////////////////////////////////////
    
    static public class CodeExprAssignment extends CodeExpr {
    	public CTokenOperator _op;
    	public CodeExpr      _a, _b;
    	
    	CodeExprAssignment(CTokenOperator op, CodeExpr a, CodeExpr b) 
    	{ 
    	    _op = op; 
    	    _a = a; 
    	    _b = b; 
    	    copyPosition(a);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprCast extends CodeExpr {
    	public CodeTypeName _typename;
    	public CodeExpr     _expr;
    	
    	CodeExprCast(CodeTypeName typename, CodeExpr expr) 
    	{ 
    	    _typename = typename; 
    	    _expr = expr; 
    	    copyPosition(typename);
	    }
    	
    	// a cast we know that will work
    	public CodeExprCast(CType dest_type, CodeExpr expr) {
    	    _expr = expr;
    	    _type = dest_type;
    	}
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprConditional extends CodeExpr {
    	public CodeExpr _test, _thexpr, _elexpr;
    	
    	CodeExprConditional(CodeExpr test, CodeExpr thexpr, CodeExpr elexpr) 
    	{ 
    	    _test = test; 
    	    _thexpr = thexpr; 
    	    _elexpr = elexpr; 
    	    copyPosition(test);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprInfix extends CodeExpr {
    	public CTokenOperator _op;
    	public CodeExpr      _a, _b;
    	
    	CodeExprInfix(CTokenOperator op, CodeExpr a, CodeExpr b) 
    	{ 
	        _op = op; 
	        _a = a; 
	        _b = b; 
	        copyPosition(a);
        }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprParen extends CodeExpr {
    	public CodeExpr _expr;
    	
    	CodeExprParen(CodeExpr expr) { 
    	    _expr = expr;
    	    copyPosition(expr);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprPostfix extends CodeExpr {
    	public CTokenOperator _op;
    	public CodeExpr      _a;
    	
    	CodeExprPostfix(CTokenOperator op, CodeExpr a)	
    	{
    	    _op = op; 
    	    _a = a; 
    	    copyPosition(a);
	        /*System.out.println("This is a trial");*/ // TODO
	    } 
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprPrefix extends CodeExpr {
    	public CTokenOperator _op;
    	public CodeExpr      _a;
    	
    	CodeExprPrefix(CTokenOperator op, CodeExpr a)	
    	{ 
    	    _op = op; 
    	    _a = a;
    	    copyPosition(op);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprId extends CodeExpr {
    	public CodeId _id;
    	
    	CodeExprId(CodeId id) { 
    	    _id = id;
    	    copyPosition(id);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprSizeofValue extends CodeExpr {
    	public CodeExpr _expr;
    	
    	CodeExprSizeofValue(CodeExpr expr) { 
    	    _expr = expr;
    	    copyPosition(expr);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprSizeofType extends CodeExpr {
    	public CodeTypeName _tname;
    	
    	CodeExprSizeofType(CodeTypeName tname) { 
    	    _tname = tname;
    	    copyPosition(tname);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprCall extends CodeExpr {
    	public CodeExpr       _fun;
    	public List<CodeExpr> _argl;
    	
    	CodeExprCall(CodeExpr fun, List<CodeExpr> argl) 
    	{ 
    	    _fun = fun; 
    	    _argl = argl;
    	    copyPosition(fun);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprSubscript extends CodeExpr {
    	public CodeExpr _arr;
    	public CodeExpr _idx;
    	
    	CodeExprSubscript(CodeExpr arr, CodeExpr idx)
    	{ 
    	    _arr = arr; 
    	    _idx = idx; 
    	    copyPosition(arr);
	    }
    	
    	public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprField extends CodeExpr {
        public CodeExpr       _ob;
        public CodeId         _id;
    
        CodeExprField(CodeExpr ob, CodeId id) {
            _ob = ob;
            _id = id;
            copyPosition(ob);
        }
        public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
    static public class CodeExprPointsTo extends CodeExpr {
        public CodeExpr   _ptr;
        public CodeId     _id;
    
        CodeExprPointsTo(CodeExpr ptr, CodeId id) {
            _ptr = ptr;
            _id  = id;
            copyPosition(ptr);
        }
        public void acceptVisitor(CodeVisitor v) { v.visit(this); }
    }
}
