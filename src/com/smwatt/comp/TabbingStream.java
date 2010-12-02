//////////////////////////////////////////////////////////////////////////////
//
// TabbingStream
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.io.PrintStream;

public class TabbingStream {
	PrintStream	_out;
	
	//-- Parameters
	int			_width     = 80;    // output width, in characters

	boolean     _doRealTabs= false; // really output tab characters?
	int			_tabSize   = 8;	    // tab stop setting, in characters
	String		_tabStr    = "        ";
	
	boolean     _doWrap    = true;  // wrap on overflow?
	int         _wrapExtraIndent=1; // Extra wrap indent, in tab stops.
	
	//-- State
	int			_indent    = 0;     // current indent, in tab stops
	int			_column    = 0;	    // current column, in characters

	TabbingStream(PrintStream out)     { _out = out; }
	
	
	//-- Methods defining the page layout ----------------------------
	public int     getWidth()          { return _width; }
	public boolean getDoRealTabs()     { return _doRealTabs; }
	public int     getTabSize()        { return _tabSize; }
	public boolean getDoWrap()         { return _doWrap; }
	public int     getWrapExtraIndent(){ return _wrapExtraIndent; }

	
	public void setWidth(int width) {
		_width = width; 
	}
	public void setDoRealTabs(boolean doRealTabs) {
		_doRealTabs = doRealTabs;
		if (doRealTabs) 
			_tabStr = "\t";
		else
			setTabSize(_tabSize);
	}
	public void setTabSize(int tabSize) {
		_tabSize = tabSize;
		
		// If not using real tabs, make a string with the
		// necessary number of spaces.
		if (!_doRealTabs) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tabSize; i++) sb.append(' ');
			_tabStr = sb.toString();
		}
	}
	public void setDoWrap(boolean doWrap) {
		_doWrap = doWrap;
	}
	public void setWrapExtraIndent(int wrapExtraIndent) {
		_wrapExtraIndent = wrapExtraIndent;
	}

	
	//-- Methods affecting the current position ---------------------
	public int  getCurrentIndent()    { return _indent; }
	public int  getCurrentColumn()    { return _column; }
	public int  getWidthRemaining()   { return _width - _column; }
	public void incIndent()           { _indent++; }
	public void decIndent()           { _indent--; }
	
	/**
	 * Tab over to the given tab stop number.  If already past 
	 * the requested tab stop, then a single space is printed.
	 * 
	 * @param stopNo
	 */
	public void tabTo(int stopNo) {
		int tabsPast = _column/_tabSize;
		
		// If past the requested tab stop, print one space.
		if (tabsPast > stopNo) {
			_out.print(" ");
			_column++;
			return;
		}
		//If using spaces, then fill out any partial tab stop.
		if (!_doRealTabs && tabsPast < stopNo) {
			while (_column % _tabSize != 0) {
				_out.print(" ");
				_column++;
			}
			tabsPast = _column/_tabSize;
		}
		// Output the needed number of tabs.
		while (tabsPast < stopNo) {
			_out.print(_tabStr);
			tabsPast++;
		}
		_column = tabsPast * _tabSize;	
	}

	//-- Newlines and indenting -------------------------------------
	/**
	 * Output a newline.
	 */
	public void println() {
		_out.println();
		_column = 0;
	}
	/**
	 * Output a newline and indent to the current margin.
	 */
	public void printlnIndent() {
		println();
		tabTo(_indent);
	}
	/**
	 * Output a newline, then indent to the given absolute tab stop.
	 */
	public void printlnIndent(int stopNo) {
		println();
		tabTo(stopNo);
	}
	/**
	 * Output a newline, then indent to the given relative tab stop.
	 */
	public void printlnIndentRelative(int stopNoRelative) {
		println();
		tabTo(_indent + stopNoRelative);
	}
	
	//-- Output strings ----------------------------------------------
	public void print(String s) {
		int l = s.length();
		// If it doesn't fit, but would by wrapping, then wrap.
		if (_doWrap && 
			l >  _width - _column &&          // doesn't fit
			l <= _width - (_indent + _wrapExtraIndent) * _tabSize)
		{
			printlnIndentRelative(_wrapExtraIndent);
		}
		_out.print(s);
		_column += l;
	}
	public void print(StringBuilder b) {
		print(b.toString());
	}
}
