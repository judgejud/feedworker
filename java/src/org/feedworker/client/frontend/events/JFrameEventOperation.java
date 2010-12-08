package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author luca
 */
public class JFrameEventOperation extends EventObject{
    private String operaz;

    public JFrameEventOperation(Object source, String oper) {
        super(source);
        operaz = oper;
    }

    public String getOperaz() {
        return operaz;
    }
}