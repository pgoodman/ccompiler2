package com.pag.diag;

public enum Type {
    WARNING         ("Warning"),
    NOTE            ("Note"),
    ERROR           ("Error"),
    RECOVERABLE     ("Recoverable Error"),
    BUG             ("Bug");
    
    public final String format;
    
    Type(String fmt) {
        format = fmt;
    }
    
    public String toString() {
        return format;
    }
}
