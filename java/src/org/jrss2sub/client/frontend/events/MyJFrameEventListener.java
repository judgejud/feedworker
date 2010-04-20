package org.jrss2sub.client.frontend.events;
//IMPORT JAVA
import java.util.EventListener;
/**Interfaccia per eventi alla jframe
 *
 * @author luca
 */
public interface MyJFrameEventListener extends EventListener{
    /**intercetta l'evento myjframe e lo gestisce*/
    public void objReceived(MyJFrameEvent evt);
}