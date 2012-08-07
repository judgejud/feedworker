package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author Administrator
 */
public class EditorPaneEvent extends EventObject{
    private String html, dest, table, title, rev;

    public EditorPaneEvent(Object o, String html, String dest, String table) {
        super(o);
        this.html = html;
        this.dest = dest;
        this.table = table;
    }
    
    public EditorPaneEvent(Object o, String html, String title, String rev, String dest) {
        super(o);
        this.html = html;
        this.dest = dest;
        this.title = title;
        this.rev = rev;
    }

    public String getTable() {
        return table;
    }

    public String getDest() {
        return dest;
    }

    public String getHtml() {
        return html;
    }

    public String getRev() {
        return rev;
    }

    public String getTitle() {
        return title;
    }
}