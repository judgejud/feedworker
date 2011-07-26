package org.feedworker.client.frontend;

import java.awt.Dimension;
import java.util.TreeMap;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.core.ManageListener;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.jfacility.java.lang.Lang;
/**
 *
 * @author Administrator
 */
public class GuiCore {
    private static GuiCore core = null;
    
    private Mediator proxy = Mediator.getIstance();
    private TreeMap<Object, JXTaskPaneContainer> mapPaneShows = 
                                    new TreeMap<Object, JXTaskPaneContainer>();
    
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
    
    public JXTaskPaneContainer addNewTabShow(Object name){
        tabShow pane;
        if (!mapPaneShows.containsKey(name)){
            pane = new tabShow(name.toString());
            mapPaneShows.put(name, pane);
        } else 
           pane = (tabShow) mapPaneShows.get(name);
        return pane;
    }
}

class tabShow extends JXTaskPaneContainer{

    private JEditorPane jepShow, jepActors;
    private JXTaskPane taskShow, taskSeasons, taskActors;
    
    public tabShow(String name) {
        super();
        setName(name);
        setPreferredSize(new Dimension(500, 600));
        
        jepShow = new JEditorPane();
        jepShow.setContentType("text/html");
        jepShow.setOpaque(false);
        jepShow.setEditable(false);
        
        jepActors = new JEditorPane();
        jepActors.setContentType("text/html");
        jepActors.setOpaque(false);
        jepActors.setEditable(false);

        taskShow = new JXTaskPane();
        taskShow.setTitle("Info Show");
        taskShow.add(jepShow);

        taskSeasons = new JXTaskPane();
        taskSeasons.setTitle("Info Seasons");
        taskSeasons.setCollapsed(true);

        taskActors = new JXTaskPane();
        taskActors.setTitle("Info Actors");
        taskActors.setCollapsed(true);
        taskActors.add(jepActors);

        add(taskShow);
        add(taskSeasons);
        add(taskActors);

        jepShow.setText(Mediator.getIstance().test(name));
    }
}// END private class tabShow