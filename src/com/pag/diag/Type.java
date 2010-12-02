package com.pag.diag;

public enum Type {
    WARNING         ("Warning"),
    NOTE            ("Note"),
    FATAL           ("Fatal Error"),
    RECOVERABLE     ("Recoverable Error");
    
    public final String format;
    
    Type(String fmt) {
        format = fmt;
    }
    
    public String toString() {
        return format;
    }
}
