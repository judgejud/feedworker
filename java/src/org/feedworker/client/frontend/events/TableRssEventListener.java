package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface TableRssEventListener extends EventListener {
    public void objReceived(TableRssEvent evt);
}