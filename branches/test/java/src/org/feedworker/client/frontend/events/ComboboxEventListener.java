package org.feedworker.client.frontend.events;
//IMPORT JAVA
import java.util.EventListener;

/**Interfaccia per eventi alla jframe
 * 
 * @author luca
 */
public interface ComboboxEventListener extends EventListener {
    /** intercetta l'evento combobox e lo gestisce */
    public void objReceived(ComboboxEvent evt);
}