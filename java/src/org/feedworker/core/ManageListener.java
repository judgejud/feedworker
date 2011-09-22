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
    private static List listenerEditorPane = new ArrayList();
    private static List listenerList = new ArrayList();
    private static List listenerTabbedPane = new ArrayList();

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
    
    public static synchronized void addEditorPaneEventListener(
                                                EditorPaneEventListener listener) {
        listenerEditorPane.add(listener);
    }
    
    public static synchronized void addListEventListener(
                                                    ListEventListener listener) {
        listenerList.add(listener);
    }
    
    public static synchronized void addTabbedPaneEventListener(
                                                    TabbedPaneEventListener listener) {
        listenerTabbedPane.add(listener);
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
    
    public static synchronized void fireFrameEvent(Object from, String[][] array) {
        FrameEvent event = new FrameEvent(from, array);
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
    
    public static synchronized void fireEditorPaneEvent(Object from, String html, 
                                                        String dest, String table) {
        EditorPaneEvent event = new EditorPaneEvent(from, html, dest, table);
        Iterator listeners = listenerEditorPane.iterator();
        while (listeners.hasNext()) {
            EditorPaneEventListener myel = (EditorPaneEventListener) listeners.next();
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
    
    public static synchronized void fireListEvent(Object from, String dest, Object[][] array) {
        ListEvent event = new ListEvent(from, dest, array);
        Iterator listeners = listenerList.iterator();
        while (listeners.hasNext()) {
            ListEventListener myel = (ListEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireTabbedPaneEvent(Object from, ArrayList<String> array, 
                                                                    String dest) {
        TabbedPaneEvent event = new TabbedPaneEvent(from, array, dest);
        Iterator listeners = listenerTabbedPane.iterator();
        while (listeners.hasNext()) {
            TabbedPaneEventListener myel = (TabbedPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
}