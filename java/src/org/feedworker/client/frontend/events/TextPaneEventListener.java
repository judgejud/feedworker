package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 * 
 * @author luca
 */
public interface TextPaneEventListener extends EventListener {
    /** intercetta l'evento textpane e lo gestisce */
    public void objReceived(TextPaneEvent evt);
}