package org.feedworker.util;

/**
 * 
 * @author luca
 */
public class ValueRule {

    private String path;
    private boolean rename;
    private boolean delete;

    public ValueRule(String path, boolean ren, boolean del) {
        this.path = path;
        this.rename = ren;
        this.delete = del;
    }

    public String getPath() {
        return path;
    }

    public boolean isRename() {
        return rename;
    }

    public boolean isDelete() {
        return delete;
    }
}