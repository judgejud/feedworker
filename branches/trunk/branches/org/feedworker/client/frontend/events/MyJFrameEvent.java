package org.feedworker.client.frontend.events;
//IMPORT JAVA
import java.util.EventObject;
/**Classe evento/i per la jframe
 *
 * @author luca
 */
public class MyJFrameEvent extends EventObject{
    //VARIABLES PRIVATE
    private boolean icontray;
    private String date;
    private String operaz;
    /**Costruttore
     *
     * @param source oggetto sorgente
     * @param icontray stato dell'icontray
     * @param date data ultimo aggiornamento
     */
    public MyJFrameEvent(Object source, boolean icontray, String date) {
        super(source);
        this.icontray = icontray;
        this.date = date;
    }

    public MyJFrameEvent(Object source, String oper){
        super(source);
        operaz = oper;
    }

    public String getDate() {
        return date;
    }

    public boolean isIcontray() {
        return icontray;
    }

    public String getOperaz(){
        return operaz;
    }
}