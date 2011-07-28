package org.feedworker.client.frontend.panel;

import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author Administrator
 */
public class tabInternalShow extends JScrollPane{
    private JEditorPane jepShow, jepActors;
    private JXTaskPane taskShow, taskSeasons, taskActors;
    private JXTaskPaneContainer container;
    private JTabbedPane tab;
    
    public tabInternalShow(String name) {
        super();
        setName(name);
        setPreferredSize(new Dimension(700, 600));
        container = new JXTaskPaneContainer();
        setViewportView(container);
        
        jepShow = new JEditorPane();
        jepShow.setContentType("text/html");
        jepShow.setOpaque(false);
        jepShow.setEditable(false);
        
        jepActors = new JEditorPane();
        jepActors.setContentType("text/html");
        jepActors.setOpaque(false);
        jepActors.setEditable(false);
        
        tab = new JTabbedPane();

        taskShow = new JXTaskPane();
        taskShow.setTitle("Info Show");
        taskShow.add(jepShow);

        taskActors = new JXTaskPane();
        taskActors.setTitle("Info Actors");
        taskActors.setCollapsed(true);
        taskActors.add(jepActors);
        
        taskSeasons = new JXTaskPane();
        taskSeasons.setTitle("Episode List");
        taskSeasons.setCollapsed(true);
        taskSeasons.add(tab);

        container.add(taskShow);
        container.add(taskActors);
        container.add(taskSeasons);
    }
    
    public void setHtmlShow(String html){
        jepShow.setText(html);
    }
    
    public void setHtmlActors(String html){        
        jepActors.setText(html);
    }
}