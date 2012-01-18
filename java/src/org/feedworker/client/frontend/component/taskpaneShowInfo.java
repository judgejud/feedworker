package org.feedworker.client.frontend.component;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.events.TabbedPaneEvent;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;
import org.feedworker.client.frontend.table.tableEpisode;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author Administrator
 */
public class taskpaneShowInfo extends JScrollPane implements TabbedPaneEventListener{
    private JEditorPane jepShow, jepActors;
    private JXTaskPane taskShow, taskActors, taskEpisodes;
    private JXTaskPaneContainer container;
    private JTabbedPane tabEpisodes;
    private GuiCore core = GuiCore.getInstance();
    
    public taskpaneShowInfo(String name) {
        super();
        setName(name);
        setPreferredSize(new Dimension(700, 600));
        container = new JXTaskPaneContainer();
        setViewportView(container);
        
        jepShow = new JEditorPane();
        jepShow.setContentType("text/html");
        jepShow.setBackground(Color.LIGHT_GRAY);
        jepShow.setForeground(Color.BLACK);
        jepShow.setOpaque(false);
        jepShow.setEditable(false);
        
        jepActors = new JEditorPane();
        jepActors.setContentType("text/html");
        jepActors.setBackground(Color.LIGHT_GRAY);
        jepShow.setForeground(Color.BLACK);
        jepActors.setOpaque(false);
        jepActors.setEditable(false);
        
        tabEpisodes = new JTabbedPane();

        taskShow = new JXTaskPane();
        taskShow.setTitle("Info Show");
        taskShow.add(jepShow);

        taskActors = new JXTaskPane();
        taskActors.setTitle("Info Actors");
        taskActors.setCollapsed(true);
        taskActors.add(jepActors);
        
        taskEpisodes = new JXTaskPane();
        taskEpisodes.setTitle("Episode List");
        taskEpisodes.setCollapsed(true);
        taskEpisodes.add(tabEpisodes);

        container.add(taskShow);
        container.add(taskActors);
        container.add(taskEpisodes);
        
        core.setTabbedPaneListener(this);
    }
    
    public void setHtmlShow(String html){
        jepShow.setText(html);
    }
    
    public void setHtmlActors(String html){
        jepActors.setText(html);
    }
    
    public void reset(){
        jepShow.setText(null);
        jepActors.setText(null);
        tabEpisodes.removeAll();
    }

    @Override
    public void objReceived(TabbedPaneEvent evt) {
        if (evt.getDest().equalsIgnoreCase(this.getName())){
            for (int i=0; i<evt.getArray().size(); i++){
                String name = evt.getArray().get(i);
                tableEpisode table = new tableEpisode(this.getName() + name);
                tabEpisodes.addTab(name, new JScrollPane(table));
                core.setTableListener(table);
            }
        }
    }
}