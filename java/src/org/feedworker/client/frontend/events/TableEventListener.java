package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 * 
 * @author luca
 */
public interface TableEventListener extends EventListener {
    public void objReceived(TableEvent evt);
}