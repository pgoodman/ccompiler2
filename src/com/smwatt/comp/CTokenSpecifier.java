package com.smwatt.comp;

public class CTokenSpecifier extends CToken { 
    CTokenSpecifier(int type) { super(type); }
    CTokenSpecifier(int type, String fname, int line, int col) { super(type, fname, line, col); }
}