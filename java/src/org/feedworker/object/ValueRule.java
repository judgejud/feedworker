package org.feedworker.object;

/**
 * 
 * @author luca
 */
public class ValueRule {

    private String path, operation;

    public ValueRule(String path, String operation) {
        this.path = path;
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public String getPath() {
        return path;
    }
}