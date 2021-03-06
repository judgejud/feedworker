package org.feedworker.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.feedworker.client.frontend.events.*;
import org.feedworker.object.Subtitle;

/**
 *
 * @author luca
 */
public class ManageListener {

    private static List listenerTable = new ArrayList();
    private static List listenerFrame = new ArrayList();
    private static List listenerTextPane = new ArrayList();
    private static List listenerStatusBar = new ArrayList();
    private static List listenerComboBox = new ArrayList();

    public static synchronized void addTableEventListener(
                                                    TableEventListener listener) {
        listenerTable.add(listener);
    }
    
    public static synchronized void addFrameEventListener(FrameEventListener listener) {
        listenerFrame.add(listener);
    }
    
    public static synchronized void addTextPaneEventListener(
                                                TextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }
    
    public static synchronized void addStatusBarEventListener(
                                                StatusBarEventListener listener) {
        listenerStatusBar.add(listener);
    }
    
    public static synchronized void addComboBoxEventListener(
                                                ComboboxEventListener listener) {
        listenerComboBox.add(listener);
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
    
    public static synchronized void fireTableEvent(Object from, String dest, 
                                                        ArrayList<Subtitle> alObj) {
        TableEvent event = new TableEvent(from, dest, alObj);
        Iterator listeners = listenerTable.iterator();
        while (listeners.hasNext()) {
            TableEventListener myel = (TableEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireFrameEvent(Object from, boolean _icontray) {
        FrameEvent event = new FrameEvent(from, _icontray);
        Iterator listeners = listenerFrame.iterator();
        while (listeners.hasNext()) {
            FrameEventListener myel = (FrameEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public static synchronized void fireFrameEvent(Object from, String oper) {
        FrameEvent event = new FrameEvent(from, oper);
        Iterator listeners = listenerFrame.iterator();
        while (listeners.hasNext()) {
            FrameEventListener myel = (FrameEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public static synchronized void fireFrameEvent(Object from, String oper, int max) {
        FrameEvent event = new FrameEvent(from, oper, max);
        Iterator listeners = listenerFrame.iterator();
        while (listeners.hasNext()) {
            FrameEventListener myel = (FrameEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public static synchronized void fireTextPaneEvent(Object from, String msg, 
            String type, boolean statusbar) {
        TextPaneEvent event = new TextPaneEvent(from, msg, type);
        Iterator listeners = listenerTextPane.iterator();
        while (listeners.hasNext()) {
            TextPaneEventListener myel = (TextPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
        if (statusbar)
            fireStatusBarEvent(from, msg);
    }
    
    public static synchronized void fireStatusBarEvent(Object from, String msg) {
        StatusBarEvent event = new StatusBarEvent(from, msg);
        Iterator listeners = listenerStatusBar.iterator();
        while (listeners.hasNext()) {
            StatusBarEventListener myel = (StatusBarEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireComboBoxEvent(Object from, Object[] array) {
        ComboboxEvent event = new ComboboxEvent(from, array);
        Iterator listeners = listenerComboBox.iterator();
        while (listeners.hasNext()) {
            ComboboxEventListener myel = (ComboboxEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
}
/*
    public static synchronized void removeMyTextPaneEventListener(
                                                    TextPaneEventListener listener) {
        listenerTP.remove(listener);
    }
*/