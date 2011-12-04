package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 * 
 * @author luca
 */
public class FrameEvent extends EventObject {

    private String operaz, msg;
    private int max;
    private boolean icontray;
    private String[][] menu;

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
    public FrameEvent(Object source, boolean icontray, String msg) {
        super(source);
        this.icontray = icontray;
        this.msg = msg;
    }

    public FrameEvent(Object o, String[][] menu) {
        super(o);
        this.menu = menu;
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

    public String[][] getMenu() {
        return menu;
    }

    public String getMsg() {
        return msg;
    }
}