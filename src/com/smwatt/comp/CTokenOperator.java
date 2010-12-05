package com.smwatt.comp;

public class CTokenOperator  extends CToken {
    public CTokenOperator(int type) { super(type); }
    CTokenOperator(int type, String fname, int line, int col) { super(type, fname, line, col); }
}