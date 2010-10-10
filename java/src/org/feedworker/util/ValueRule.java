package org.feedworker.util;
/**
 *
 * @author luca
 */
public class ValueRule {
    private String path;
    private String day;
    private String status;
    private boolean rename;

    public ValueRule(String path, String day, String status, boolean rename) {
        this.path = path;
        this.day = day;
        this.status = status;
        this.rename = rename;
    }

    public String getDay() {
        return day;
    }

    public String getPath() {
        return path;
    }

    public boolean isRename() {
        return rename;
    }

    public String getStatus() {
        return status;
    }
}