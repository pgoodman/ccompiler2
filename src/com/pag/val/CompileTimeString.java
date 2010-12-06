package com.pag.val;

public class CompileTimeString implements CompileTimeValue {
    
    final public String value;
    
    public CompileTimeString(String s) {
        value = s;
    }
    
    public CompileTimeValue add(CompileTimeValue other) {
        if(!(other instanceof CompileTimeInteger)) {
            return null;
        }
        CompileTimeInteger ii = (CompileTimeInteger) other;
        
        if(ii.value >= value.length()) {
            return null;
        }
        
        return new CompileTimeString(value.substring(ii.value));
    }

    public CompileTimeValue bit_and(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue bit_left_shift(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue bit_not() {
        return null;
    }

    public CompileTimeValue bit_or(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue bit_right_shift(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue bit_xor(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue divide(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue greater_than_equal(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue greater_than(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue less_than(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue less_than_equal(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue log_and(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue log_not() {
        return null;
    }

    public CompileTimeValue log_or(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue modulo(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue multiply(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue negative() {
        return null;
    }

    public CompileTimeValue positive() {
        return this;
    }

    public CompileTimeValue subtract(CompileTimeValue other) {
        return null;
    }
    
    public CompileTimeValue equals(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue not_equals(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue dereference() {
        return new CompileTimeInteger((int) value.charAt(0));
    }
    
    public String toString() {
        return value;
    }
}
