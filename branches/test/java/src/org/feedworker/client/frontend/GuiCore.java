package org.feedworker.client.frontend;

import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.feedworker.client.frontend.events.ComboboxEventListener;

import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.client.frontend.panel.tabInternalShow;
import org.feedworker.core.ManageListener;

import org.jfacility.java.lang.Lang;
/**
 *
 * @author Administrator
 */
public class GuiCore {
    private static GuiCore core = null;
    
    private Mediator proxy = Mediator.getIstance();
    private TreeMap<Object, JScrollPane> mapPaneShows = 
                                    new TreeMap<Object, JScrollPane>();
    
    public static GuiCore getInstance(){
        if (core==null)
            core = new GuiCore();
        return core;
    }
    
    public void setTableListener(TableEventListener listener) {
        ManageListener.addTableEventListener(listener);
    }

    public void setTextPaneListener(TextPaneEventListener listener) {
        ManageListener.addTextPaneEventListener(listener);
    }
    
    public void setStatusBarListener(StatusBarEventListener listener) {
        ManageListener.addStatusBarEventListener(listener);
    }

    void setFrameListener(FrameEventListener listener) {
        ManageListener.addFrameEventListener(listener);
    }
    
    public void setComboboxListener(ComboboxEventListener listener) {
        ManageListener.addComboBoxEventListener(listener);
    }
    
    public void setListListener(ListEventListener listener) {
        ManageListener.addListEventListener(listener);
    }
    
    public void setEditorPaneListener(EditorPaneEventListener listener) {
        ManageListener.addEditorPaneEventListener(listener);
    }
    
    public void addNewSerial(){
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
    }
    
    public JScrollPane addNewTabShow(Object name){
        tabInternalShow pane;
        if (!mapPaneShows.containsKey(name)){
            pane = new tabInternalShow(name.toString());
            mapPaneShows.put(name, pane);
        } else 
           pane = (tabInternalShow) mapPaneShows.get(name);
        return pane;
    }
}