package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.feedworker.client.frontend.Mediator;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jfacility.javax.swing.ButtonTabComponent;

/**
 *
 * @author luca
 */
public class paneShow extends paneAbstract{
    
    private static paneShow jpanel = null;
    private JList jlSeries;
    private JTabbedPane jtpShows;
    private TreeMap<String, JPanel> map;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
        map = new TreeMap<String, JPanel>();
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
                newTabShow(jlSeries.getSelectedValue().toString());
                //TODO
                //jepShow.setText(proxy.test(jlSeries.getSelectedValue().toString()));
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
        
        JScrollPane jspLeft = new JScrollPane(tpcWest);
        JPanel pane = new JPanel(new BorderLayout());
        jtpShows = new JTabbedPane();
        pane.add(jspLeft, BorderLayout.WEST);
        pane.add(jtpShows, BorderLayout.CENTER);
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
                //(System.out.println(jepShow.getText());
            }
        });
    }
    
    private void newTabShow(String name){
        tabShow temp;
        if (!map.containsKey(name)){
            temp = new tabShow(name);
            map.put(name, temp);
        } else 
           temp = (tabShow) map.get(name);
        JScrollPane jsp = new JScrollPane(temp);
        if (!jtpShows.isAncestorOf(jsp)) {
            jtpShows.addTab(name, jsp);
            jtpShows.setTabComponentAt(jtpShows.getTabCount() - 1,
                    new ButtonTabComponent(jtpShows));
        }
        jtpShows.setSelectedComponent(jsp);
    }
    
    
} //end class
class tabShow extends JXTaskPaneContainer{
        private JEditorPane jepShow, jepActors;
        JXTaskPane taskShow, taskSeasons, taskActors;

        public tabShow(String name) {
            super();
            setName(name);
            jepShow = new JEditorPane();
            jepShow.setContentType("text/html");
            jepShow.setOpaque(false);
            jepShow.setEditable(false);
            
            taskShow = new JXTaskPane();
            taskShow.setTitle("Info Show");
            taskShow.setCollapsed(true);
            taskShow.add(jepShow);

            taskSeasons = new JXTaskPane();
            taskSeasons.setTitle("Info Seasons");
            taskSeasons.setCollapsed(true);

            taskActors = new JXTaskPane();
            taskActors.setTitle("Info Actors");
            taskActors.setCollapsed(true);
            
            add(taskShow);
            add(taskSeasons);
            add(taskActors);
            
            jepShow.setText(Mediator.getIstance().test(name));
        }
    }// END private class tabShow