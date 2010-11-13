package org.feedworker.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAdapter extends RuntimeException {

    private final String stackTrace;
    public Exception originalException;

    public ExceptionAdapter(Exception e) {
        super(e.toString());
        originalException = e;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        stackTrace = sw.toString();
    }
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
    @Override
    public void printStackTrace(java.io.PrintStream s) {
        synchronized (s) {
            s.print(getClass().getName() + ": ");
            s.print(stackTrace);
        }
    }
    @Override
    public void printStackTrace(java.io.PrintWriter s) {
        synchronized (s) {
            s.print(getClass().getName() + ": ");
            s.print(stackTrace);
        }
    }

    public void rethrow() throws Exception {
        throw originalException;
    }
}