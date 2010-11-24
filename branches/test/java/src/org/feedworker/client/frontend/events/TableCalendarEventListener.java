package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface TableCalendarEventListener extends EventListener{
    public void objReceived(TableCalendarEvent evt);
}