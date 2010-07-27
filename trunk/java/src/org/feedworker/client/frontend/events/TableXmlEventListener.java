package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface TableXmlEventListener extends EventListener{
    public void objReceived(TableXmlEvent evt);
}