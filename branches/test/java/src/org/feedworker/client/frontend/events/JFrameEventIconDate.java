package org.feedworker.client.frontend.events;

//IMPORT JAVA
import java.util.EventObject;

/**
 * Classe evento/i per la jframe
 * 
 * @author luca
 */
public class JFrameEventIconDate extends EventObject {
    // VARIABLES PRIVATE
    private boolean icontray;
    private String date;

    /**Costruttore
     *
     * @param source oggetto sorgente
     * @param icontray stato dell'icontray
     * @param date data ultimo aggiornamento
     */
    public JFrameEventIconDate(Object source, boolean icontray, String date) {
        super(source);
        this.icontray = icontray;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public boolean isIcontray() {
        return icontray;
    }
}