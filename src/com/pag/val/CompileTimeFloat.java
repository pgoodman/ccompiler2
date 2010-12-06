package com.pag.val;

public class CompileTimeFloat implements CompileTimeValue {
    
    final public double value;
    
    static private CompileTimeFloat cast(CompileTimeValue other) {
        return (CompileTimeFloat) other;
    }
    
    static private CompileTimeValue make(double value) {
        return new CompileTimeFloat(value);
    }
    
    public CompileTimeFloat(double i) {
        value = i;
    }
    
    public CompileTimeValue add(CompileTimeValue other) {
        return make(value + cast(other).value);
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
        CompileTimeFloat o = cast(other);
        if(0 == o.value) {
            return null;
        }
        return make(value / o.value);
    }

    public CompileTimeValue greater_than_equal(CompileTimeValue other) {
        return make(value >= cast(other).value ? 1 : 0);
    }

    public CompileTimeValue greater_than(CompileTimeValue other) {
        return make(value > cast(other).value ? 1 : 0);
    }

    public CompileTimeValue less_than(CompileTimeValue other) {
        return make(value < cast(other).value ? 1 : 0);
    }

    public CompileTimeValue less_than_equal(CompileTimeValue other) {
        return make(value <= cast(other).value ? 1 : 0);
    }

    public CompileTimeValue log_and(CompileTimeValue other) {
        return make(2 == (value + cast(other).value) ? 1 : 0);
    }

    public CompileTimeValue log_not() {
        return make(value == 0 ? 1 : 0);
    }

    public CompileTimeValue log_or(CompileTimeValue other) {
        return make(0 < (value + cast(other).value) ? 1 : 0);
    }

    public CompileTimeValue modulo(CompileTimeValue other) {
        return null;
    }

    public CompileTimeValue multiply(CompileTimeValue other) {
        return make(value * cast(other).value);
    }

    public CompileTimeValue negative() {
        return make(-1.0 * value);
    }

    public CompileTimeValue positive() {
        return this;
    }

    public CompileTimeValue subtract(CompileTimeValue other) {
        return make(value - cast(other).value);
    }
    
    public CompileTimeValue equals(CompileTimeValue other) {
        return make(value == cast(other).value ? 1 : 0);
    }

    public CompileTimeValue not_equals(CompileTimeValue other) {
        return make(value == cast(other).value ? 0 : 1);
    }

    public CompileTimeValue dereference() {
        return null;
    }
    
    public String toString() {
        return Double.toString(value);
    }
}
