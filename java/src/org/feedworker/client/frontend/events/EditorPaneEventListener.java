package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author Administrator
 */
public interface EditorPaneEventListener extends EventListener {
    /** intercetta l'evento editorpane e lo gestisce */
    public void objReceived(EditorPaneEvent evt);
}