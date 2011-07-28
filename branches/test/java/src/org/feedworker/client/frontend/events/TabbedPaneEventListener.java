package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author Administrator
 */
public interface TabbedPaneEventListener extends EventListener {
    /** intercetta l'evento table e lo gestisce */
    public void objReceived(TabbedPaneEvent evt);
}