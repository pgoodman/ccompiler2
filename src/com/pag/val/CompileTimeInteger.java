package com.pag.val;

public class CompileTimeInteger implements CompileTimeValue {
    
    // non-final so that we might slot in a value.
    public int value;
    
    static private CompileTimeInteger cast(CompileTimeValue other) {
        return (CompileTimeInteger) other;
    }
    
    static private CompileTimeValue make(int value) {
        return new CompileTimeInteger(value);
    }
    
    public CompileTimeInteger(int i) {
        value = i;
    }
    
    public CompileTimeValue add(CompileTimeValue other) {
        return make(value + cast(other).value);
    }

    public CompileTimeValue bit_and(CompileTimeValue other) {
        return make(value & cast(other).value);
    }

    public CompileTimeValue bit_left_shift(CompileTimeValue other) {
        return make(value << cast(other).value);
    }

    public CompileTimeValue bit_not() {
        return make(~value);
    }

    public CompileTimeValue bit_or(CompileTimeValue other) {
        return make(value | cast(other).value);
    }

    public CompileTimeValue bit_right_shift(CompileTimeValue other) {
        return make(value >> cast(other).value);
    }

    public CompileTimeValue bit_xor(CompileTimeValue other) {
        return make(value ^ cast(other).value);
    }

    public CompileTimeValue divide(CompileTimeValue other) {
        CompileTimeInteger o = cast(other);
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
        CompileTimeInteger o = cast(other);
        if(0 == o.value) {
            return null;
        }
        return make(value % o.value);
    }

    public CompileTimeValue multiply(CompileTimeValue other) {
        return make(value * cast(other).value);
    }

    public CompileTimeValue negative() {
        return make(-1 * value);
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
        return Integer.toString(value);
    }
    
}
