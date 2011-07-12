package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface StatusBarEventListener extends EventListener {
    public void objReceived(StatusBarEvent evt);
}