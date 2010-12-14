package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author luca
 */
public class JFrameEventOperation extends EventObject{
    private String operaz;
    private int max;

    public JFrameEventOperation(Object source, String oper) {
        super(source);
        operaz = oper;
    }
    public JFrameEventOperation(Object source, String oper, int num) {
        super(source);
        operaz = oper;
        max = num;
    }

    public String getOperaz() {
        return operaz;
    }

    public int getMax() {
        return max;
    }
}