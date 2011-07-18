package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author Administrator
 */
public class EditorPaneEvent extends EventObject{
    private String html;

    public EditorPaneEvent(Object source, String text) {
        super(source);
        html = text;
    }

    public String getHtml() {
        return html;
    }
}