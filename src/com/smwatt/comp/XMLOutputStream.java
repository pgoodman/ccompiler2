//////////////////////////////////////////////////////////////////////////////
//
// XMLOutputStream
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

public class XMLOutputStream {
	
	private class StateStack {
		boolean    _inOpenTag = true;
		String     _tag;
		StateStack _prev;
		StateStack(String tag, StateStack prev) 
		{ _tag = tag; _prev = prev; }
	}
	
	private StateStack 		_ss = null;
	private TabbingStream 	_tout;
	
	XMLOutputStream(TabbingStream tout) { _tout = tout; }
	
	void openElement(String tag) {
		if (_ss != null && _ss._inOpenTag) beginContent();
		_ss = new StateStack(tag, _ss);
		_tout.printlnIndent();
		_tout.print("<"+tag);
	}
	void beginContent() {
		_tout.print(">");
		_ss._inOpenTag = false;
		_tout.incIndent();
	}
	void endElement() {
		if (_ss._inOpenTag) {
			_tout.print("/>");
		}
		else {
			_tout.decIndent();
			_tout.printlnIndent();
			_tout.print("</"+_ss._tag+">");
		}
		_ss = _ss._prev;
	}
	
	void putAttribute(String name, String value) {
		_tout.print(" "+name+"='"+value+"'");
	}
	void putAttribute(String name, int value) {
		_tout.print(" "+name+"='"+value+"'");
	}
	void putAttribute(String name, boolean value) {
		_tout.print(" "+name+"='"+value+"'");
	}
	
	void putNonZeroAttribute(String name, int value) {
		if (value != 0) putAttribute(name, value);
	}
	void putTrueAttribute(String name, boolean value) {
		if (value)      putAttribute(name, "true");
	}
	
	void putText(String text) {
		if (_ss._inOpenTag) beginContent();
		_tout.print(text);
	}
}
