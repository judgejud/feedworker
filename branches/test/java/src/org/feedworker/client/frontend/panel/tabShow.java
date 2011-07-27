package org.feedworker.client.frontend.panel;

import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author Administrator
 */
public class tabShow extends JScrollPane{
    private JEditorPane jepShow, jepActors;
    private JXTaskPane taskShow, taskSeasons, taskActors;
    private JXTaskPaneContainer container;
    
    public tabShow(String name) {
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

        container.add(taskShow);
        container.add(taskSeasons);
        container.add(taskActors);
    }
    
    public void setHtmlShow(String html){
        jepShow.setText(html);
    }
    
    public void setHtmlActors(String html){        
        jepActors.setText(html);
    }
}