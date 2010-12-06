package com.pag.val;

/**
 * Represents a compile-time arithmetic value.
 * 
 * @author petergoodman
 *
 */
public interface CompileTimeValue {
    
    public CompileTimeValue add(CompileTimeValue other);
    public CompileTimeValue subtract(CompileTimeValue other);
    public CompileTimeValue multiply(CompileTimeValue other);
    public CompileTimeValue divide(CompileTimeValue other);
    public CompileTimeValue modulo(CompileTimeValue other);
    public CompileTimeValue positive();
    public CompileTimeValue negative();
    
    public CompileTimeValue log_and(CompileTimeValue other);
    public CompileTimeValue log_or(CompileTimeValue other);
    public CompileTimeValue log_not();
    
    public CompileTimeValue bit_xor(CompileTimeValue other);
    public CompileTimeValue bit_and(CompileTimeValue other);
    public CompileTimeValue bit_or(CompileTimeValue other);
    public CompileTimeValue bit_not();
    
    public CompileTimeValue bit_left_shift(CompileTimeValue other);
    public CompileTimeValue bit_right_shift(CompileTimeValue other);
    
    public CompileTimeValue less_than(CompileTimeValue other);
    public CompileTimeValue greater_than(CompileTimeValue other);
    public CompileTimeValue equals(CompileTimeValue other);
    public CompileTimeValue not_equals(CompileTimeValue other);
    public CompileTimeValue less_than_equal(CompileTimeValue other);
    public CompileTimeValue greater_than_equal(CompileTimeValue other);
    
    public CompileTimeValue dereference();
}
