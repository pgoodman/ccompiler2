
package com.pag.diag;

import java.util.Collections;
import java.util.Vector;

import com.pag.diag.Message;

import com.smwatt.comp.SourcePosition;

/**
 * Report out diagnostic messages.
 * @author petergoodman
 */
public class Reporter {
    
    /**
     * Represents a stored diagnostic report.
     */
    private static class Report implements Comparable<Report> {
        
        final public SourcePosition position;
        final public String message;
        
        public Report(SourcePosition pos, String msg) {
            position = pos;
            message = msg;
        }
        
        public int compareTo(Report o) {
            return position.compareTo(o.position);
        }
    }
    
    /**
     * Stored reports for later.
     */
    static private Vector<Report> reports = new Vector<Report>();
    static private boolean found_error = false;
    
    /**
     * Was an error reported?
     */
    static public boolean errorReported() {
        return found_error;
    }
    
    /**
     * Format a report and return the formatted string.
     * @param msg
     * @param pos
     * @param args
     * @return
     */
    static private String makeReport(Message msg, SourcePosition pos, Object[] args) {
        
        if(Type.ERROR == msg.type || Type.RECOVERABLE == msg.type) {
            found_error = true;
        }
        
        String format = msg.format;
        
        for(Object arg : args) {
            format = format.replaceFirst("%%", arg.toString());
        }
        
        return msg.type + ": " + (null != pos ? pos.toString() : "") + "\n    " + format + "\n\n";
    }
    
    /**
     * Immediately outputs a formatted report.
     */
    static public void reportNow(Message msg, SourcePosition pos, Object ... args) {
        System.out.print(makeReport(msg, pos, args));
    }
    
    /**
     * Creates and stores a formatted report for later output.
     */
    static public void reportLater(Message msg, SourcePosition pos, Object ... args) {
        reports.add(new Report(pos, makeReport(msg, pos, args)));
    }
    
    /**
     * Flush and output all stored reports in sorted order.
     */
    static public void flush() {
        Collections.sort(reports);
        for(Report report : reports) {
            System.out.print(report.message);
        }
        reports.clear();
    }
}
