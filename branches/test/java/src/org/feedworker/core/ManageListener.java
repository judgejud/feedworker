package org.feedworker.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;

/**
 *
 * @author luca
 */
public class ManageListener {

    private static List listenerTable = new ArrayList();
    /**
     * Permette alla classe di registrarsi per l'evento tablerss
     * 
     * @param listener
     *            evento tablerss
     */
    public static synchronized void addTableEventListener(TableEventListener listener) {
        listenerTable.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento tablerss
     * 
     * @param listener
     *            evento tablerss
     */
    public static synchronized void removeTableEventListener(
            TableEventListener listener) {
        listenerTable.remove(listener);
    }

    public static synchronized void fireTableEvent(Object from, ArrayList<Object[]> alObj, 
            String dest) {
        TableEvent event = new TableEvent(from, alObj, dest);
        Iterator listeners = listenerTable.iterator();
        while (listeners.hasNext()) {
            TableEventListener myel = (TableEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireTableEvent(Object from, ArrayList<Object[]> alObj,
            String dest, boolean addRows) {
        TableEvent event = new TableEvent(from, alObj, dest, addRows);
        Iterator listeners = listenerTable.iterator();
        while (listeners.hasNext()) {
            TableEventListener myel = (TableEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

}