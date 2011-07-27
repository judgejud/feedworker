package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author Administrator
 */
public class EditorPaneEvent extends EventObject{
    private String html, dest, table;

    public EditorPaneEvent(Object o, String html, String dest, String table) {
        super(o);
        this.html = html;
        this.dest = dest;
        this.table = table;
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
}