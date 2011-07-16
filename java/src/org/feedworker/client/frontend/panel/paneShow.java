package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author luca
 */
public class paneShow extends paneAbstract{
    
    private static paneShow jpanel = null;
    private JEditorPane jepShow, jepActors;
    private JList jlSeries;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
    }
    
    public static paneShow getPanel(){
        if (jpanel==null)
            jpanel = new paneShow();
        return jpanel;
    }

    @Override
    void initializePanel() {
        
        
        JButton jbImport = new JButton("importa da myItasa");
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //TODO
            }
        });
        String[] temp = {"90210", "Alias"};
        jlSeries = new JList(temp);
        jlSeries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jepShow.setText(proxy.test(jlSeries.getSelectedValue().toString()));
            }
        });
        //JXList jxl = new JXList(temp);
        
        JXTaskPaneContainer tpcWest = new JXTaskPaneContainer();
        JXTaskPane task = new JXTaskPane();
        task.setTitle("My Series");
        task.setCollapsed(true);
        task.add(jbImport);
        task.add(jlSeries);
        //task.add(jxl);
        
        tpcWest.add(task);
        
        
        //TODO add list
        jepShow = new JEditorPane();
        jepShow.setContentType("text/html");
        jepShow.setOpaque(false);
        jepShow.setEditable(false);
        
        
        JXTaskPane taskShow = new JXTaskPane();
        taskShow.setTitle("Info Show");
        taskShow.setCollapsed(true);
        taskShow.add(jepShow);
        
        JXTaskPane taskSeasons = new JXTaskPane();
        taskSeasons.setTitle("Info Seasons");
        taskSeasons.setCollapsed(true);
        
        JXTaskPane taskActors = new JXTaskPane();
        taskActors.setTitle("Info Actors");
        taskActors.setCollapsed(true);
        
        JXTaskPaneContainer tpcEast = new JXTaskPaneContainer();
        tpcEast.add(taskShow);
        tpcEast.add(taskSeasons);
        tpcEast.add(taskActors);
        
        JScrollPane jspLeft = new JScrollPane(tpcWest);
        JScrollPane jspCenter = new JScrollPane(tpcEast);
        JPanel pane = new JPanel(new BorderLayout());
        pane.add(jspLeft, BorderLayout.WEST);
        pane.add(jspCenter, BorderLayout.CENTER);
        jpCenter.add(pane);
    }

    @Override
    void initializeButtons() {
        //TODO add checkbox & import myitasa
        JButton jb = new JButton("test");
        jpAction.add(jb);
        jb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //jepShow.setText(proxy.test());
            }
        });
        JButton jb1 = new JButton("print html");
        jpAction.add(jb1);
        jb1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                System.out.println(jepShow.getText());
            }
        });
        
    }
}