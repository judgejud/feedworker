package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface StatusBarEventListener extends EventListener {
        /** intercetta l'evento statusbar e lo gestisce */
    public void objReceived(StatusBarEvent evt);
}