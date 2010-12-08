package org.feedworker.client.frontend.events;

import java.util.EventListener;

/**
 *
 * @author luca
 */
public interface JFrameEventOperationListener extends EventListener {
    /** intercetta l'evento myjframe e lo gestisce */
    public void objReceived(JFrameEventOperation evt);
}