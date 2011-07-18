package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 * 
 * @author luca
 */
public interface TableEventListener extends EventListener {
    /** intercetta l'evento table e lo gestisce */
    public void objReceived(TableEvent evt);
}