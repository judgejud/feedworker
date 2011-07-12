package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 * 
 * @author luca
 */
public interface TextPaneEventListener extends EventListener {
	public void objReceived(TextPaneEvent evt);
}