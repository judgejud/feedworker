package org.jrss2sub.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface MyTextPaneEventListener extends EventListener{
    public void objReceived(MyTextPaneEvent evt);
}