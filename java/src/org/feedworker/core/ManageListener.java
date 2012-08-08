package org.feedworker.core;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import org.feedworker.client.frontend.events.*;
import org.feedworker.object.Subtitle;

/**
 *
 * @author luca
 */
public class ManageListener {

    private static List<EventListener> listenerTable = new ArrayList<EventListener>();
    private static List<EventListener> listenerFrame = new ArrayList<EventListener>();
    private static List<EventListener> listenerTextPane = new ArrayList<EventListener>();
    private static List<EventListener> listenerStatusBar = new ArrayList<EventListener>();
    private static List<EventListener> listenerComboBox = new ArrayList<EventListener>();
    private static List<EventListener> listenerEditorPane = new ArrayList<EventListener>();
    private static List<EventListener> listenerList = new ArrayList<EventListener>();
    private static List<EventListener> listenerTabbedPane = new ArrayList<EventListener>();
    private static List<EventListener> listenerCanvas = new ArrayList<EventListener>();

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
    
    public static synchronized void addCanvasEventListener(
                                                    CanvasEventListener listener) {
        listenerCanvas.add(listener);
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
    
    public static synchronized void fireFrameEvent(Object from, boolean _icontray, String msg) {
        FrameEvent event = new FrameEvent(from, _icontray, msg);
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
    
    public static synchronized void fireEditorPaneEvent(Object from, String html, 
                                                        String title, String rev, String dest) {
        EditorPaneEvent event = new EditorPaneEvent(from, html, title, rev, dest);
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
    
    public static synchronized void fireListEvent(Object from, String dest, ArrayList<Object[]> array) {
        ListEvent event = new ListEvent(from, dest, array);
        Iterator listeners = listenerList.iterator();
        while (listeners.hasNext()) {
            ListEventListener myel = (ListEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireListIrcEvent(Object from, String dest, String oper, String nick) {
        ListEvent event = new ListEvent(from, dest, oper, nick);
        Iterator listeners = listenerList.iterator();
        while (listeners.hasNext()) {
            ListEventListener myel = (ListEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireListIrcEvent(Object from, String dest, String oper, Object[] nicks) {
        ListEvent event = new ListEvent(from, dest, oper, nicks);
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
    
    public static synchronized void fireTabbedPaneEvent(Object from, String name, String dest) {
        TabbedPaneEvent event = new TabbedPaneEvent(from, name, dest);
        Iterator listeners = listenerTabbedPane.iterator();
        while (listeners.hasNext()) {
            TabbedPaneEventListener myel = (TabbedPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
    
    public static synchronized void fireCanvasEvent(Object from, Object[] array) {
        CanvasEvent event = new CanvasEvent(from, array);
        Iterator listeners = listenerCanvas.iterator();
        while (listeners.hasNext()) {
            CanvasEventListener myel = (CanvasEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
}