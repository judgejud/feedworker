package org.feedworker.client.frontend.component;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
/**
 *
 * @author luca judge
 */
public class taskpaneNews extends JScrollPane{
    private JEditorPane jepNews;
    private JXTaskPane taskNews, taskSubtitle;
    private JXTaskPaneContainer container;
    
    public taskpaneNews() {
        super();
        //setName(name);
        setPreferredSize(new Dimension(1000, 450));
        container = new JXTaskPaneContainer();
        setViewportView(container);
        
        jepNews = new JEditorPane();
        jepNews.setContentType("text/html");
        jepNews.setBackground(Color.LIGHT_GRAY);
        jepNews.setForeground(Color.BLACK);
        jepNews.setOpaque(false);
        jepNews.setEditable(false);

        taskNews = new JXTaskPane();
        taskNews.setTitle("Info News");
        taskNews.setCollapsed(true);
        taskNews.add(jepNews);

        taskSubtitle = new JXTaskPane();
        taskSubtitle.setTitle("Sottotitoli");
        taskSubtitle.setCollapsed(true);
        
        container.add(taskNews);
        container.add(taskSubtitle);
    }
    
    public void setHtmlNews(String html){
        jepNews.setText(html);
    }
    
    public void reset(){
        jepNews.setText(null);
    }
}