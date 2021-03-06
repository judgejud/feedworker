package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 * 
 * @author luca
 */
public class FrameEvent extends EventObject {

    private String operaz;
    private int max;
    private boolean icontray;

    public FrameEvent(Object source, String oper) {
        super(source);
        operaz = oper;
    }

    public FrameEvent(Object source, String oper, int num) {
        super(source);
        operaz = oper;
        max = num;
    }
    
    /**Costruttore
     *
     * @param source oggetto sorgente
     * @param icontray stato dell'icontray
     */
    public FrameEvent(Object source, boolean icontray) {
        super(source);
        this.icontray = icontray;
    }

    public boolean isIcontray() {
        return icontray;
    }

    public String getOperaz() {
        return operaz;
    }

    public int getMax() {
        return max;
    }
}