package org.feedworker.client.frontend.events;

import java.util.EventListener;
/**
 *
 * @author luca judge
 */
public interface CanvasEventListener extends EventListener {
    /** intercetta l'evento editorpane e lo gestisce */
    public void objReceived(CanvasEvent evt);
}