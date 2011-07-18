package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import org.feedworker.client.frontend.events.ComboboxEvent;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEvent;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;


import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.jfacility.javax.swing.ButtonTabComponent;

/**
 *
 * @author luca
 */
public class paneShow extends paneAbstract implements ComboboxEventListener, 
                                    EditorPaneEventListener, ListEventListener {
    
    private static paneShow jpanel = null;
    private JList jlSeries;
    private JTabbedPane jtpShows;
    private JComboBox jcbShows;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
        core.setListListener(this);
        core.setComboboxListener(this);
        core.setEditorPaneListener(this);
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
        
        jcbShows = new JComboBox();
        JButton jbAdd = new JButton("Add to my series");
        jbAdd.setToolTipText("Aggiunge il rigo selezionato alle mie serie");
        jbAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //TODO java.lang.ClassCastException: 
                //javax.swing.JList$3 cannot be cast to javax.swing.DefaultListModel
                ((DefaultListModel) jlSeries.getModel()).add(0, jcbShows.getSelectedItem());
            }
        });
        
        JButton jbSee = new JButton("Visualizza serie");
        jbSee.setToolTipText("Visualizza info serie selezionata");
        jbSee.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jcbShows.getSelectedIndex()>-1)
                    newTabShow(jcbShows.getSelectedItem());
            }
        });
        
        JXTaskPaneContainer tpcWest = new JXTaskPaneContainer();
        //tpcWest.setPreferredSize(new Dimension(200, 800));
        JXTaskPane taskMySeries = new JXTaskPane();
        taskMySeries.setTitle("My Series");
        taskMySeries.setCollapsed(true);
        taskMySeries.add(jbImport);
        taskMySeries.add(jlSeries);
        //task.add(jxl);
        JXTaskPane taskAdd = new JXTaskPane();
        taskAdd.setTitle("List Series");
        taskAdd.setCollapsed(true);
        taskAdd.add(jbAdd);
        taskAdd.add(jbSee);
        taskAdd.add(jcbShows);
        
        tpcWest.add(taskMySeries);
        tpcWest.add(taskAdd);
        
        JScrollPane jspLeft = new JScrollPane(tpcWest);
        JPanel pane = new JPanel(new BorderLayout());
        jtpShows = new JTabbedPane();
        pane.add(jspLeft, BorderLayout.WEST);
        pane.add(jtpShows, BorderLayout.CENTER);
        jpCenter.add(pane);
    }

    @Override
    void initializeButtons() {}
    
    private void newTabShow(Object name){
        JScrollPane jsp = new JScrollPane(core.addNewTabShow(name));
        if (!jtpShows.isAncestorOf(jsp)) {
            jtpShows.addTab(name.toString(), jsp);
            jtpShows.setTabComponentAt(jtpShows.getTabCount() - 1,
                    new ButtonTabComponent(jtpShows));
        }
        jtpShows.setSelectedComponent(jsp);
    }

    @Override
    public void objReceived(ListEvent evt) {
        jlSeries.setListData(evt.getArray());
    }

    @Override
    public void objReceived(ComboboxEvent evt) {
        Object[] array = evt.getArray(); 
        for (int i=0; i<array.length; i++)
            jcbShows.addItem(array[i]);
        jcbShows.addItem(null);
        jcbShows.setSelectedItem(null);
    }

    @Override
    public void objReceived(EditorPaneEvent evt) {
        
    }
} //end class