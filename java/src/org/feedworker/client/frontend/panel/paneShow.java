package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.jfacility.javax.swing.ButtonTabComponent;

/**
 *
 * @author luca
 */
public class paneShow extends paneAbstract implements ListEventListener{
    
    private static paneShow jpanel = null;
    private JList jlSeries;
    private JTabbedPane jtpShows;
    
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
        core.setListListener(this);
    }
    
    public static paneShow getPanel(){
        if (jpanel==null)
            jpanel = new paneShow();
        return jpanel;
    }

    @Override
    void initializePanel() {
        JButton jbImport = new JButton("Importa da myItasa");
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.importNameListFromMyItasa();
            }
        });
        jlSeries = new JList();
        
        jlSeries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                newTabShow(jlSeries.getSelectedValue().toString());
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
    void initializeButtons() {}
    
    private void newTabShow(String name){
        JScrollPane jsp = new JScrollPane(core.addNewTabShow(name));
        if (!jtpShows.isAncestorOf(jsp)) {
            jtpShows.addTab(name, jsp);
            jtpShows.setTabComponentAt(jtpShows.getTabCount() - 1,
                    new ButtonTabComponent(jtpShows));
        }
        jtpShows.setSelectedComponent(jsp);
    }

    @Override
    public void objReceived(ListEvent evt) {
        jlSeries.setListData(evt.getArray());
    }
} //end class