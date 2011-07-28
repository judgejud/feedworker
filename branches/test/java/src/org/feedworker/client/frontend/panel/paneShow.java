package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.feedworker.client.frontend.events.ComboboxEvent;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEvent;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.jfacility.javax.swing.ButtonTabComponent;

/**TODO: lista ordinata e senza duplicati,
 * @author luca
 */
public class paneShow extends paneAbstract implements ComboboxEventListener, 
                                    EditorPaneEventListener, ListEventListener {
    
    private static paneShow jpanel = null;
    private JList jlSeries;
    private JTabbedPane jtpShows;
    private JComboBox jcbShows;
    private DefaultListModel listModel;
    
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
        
        JButton jbSave = new JButton("Salva Lista");
        jbSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (listModel.getSize()>0)
                    proxy.saveList(listModel.toArray());
            }
        });
        
        JButton jbRemove = new JButton("Rimuovi selezionato");
        jbRemove.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int index = jlSeries.getSelectedIndex();
                if (index>0)
                    listModel.remove(index);
            }
        });
        
        listModel = new DefaultListModel();
        jlSeries = new JList(listModel){
            // This method is called as the cursor moves within the list.
            @Override
            public String getToolTipText(MouseEvent evt) {
                return getModel().getElementAt(locationToIndex(evt.getPoint())).toString();
            }
        };
        jlSeries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Double-click
                if (evt.getClickCount() == 2) 
                    newTabShow(jlSeries.getSelectedValue().toString());
            }
        });
        
        JXTaskPaneContainer tpcWest = new JXTaskPaneContainer();
        JXTaskPane taskMySeries = new JXTaskPane();
        
        taskMySeries.setTitle("My Series");
        taskMySeries.setCollapsed(false);
        taskMySeries.add(jbImport);
        taskMySeries.add(jbSave);
        taskMySeries.add(jbRemove);
        taskMySeries.add(jlSeries);
        
        tpcWest.add(taskMySeries);
        
        JScrollPane jspLeft = new JScrollPane(tpcWest);
        jspLeft.setPreferredSize(new Dimension(200,700));
        jtpShows = new JTabbedPane();
        JPanel pane = new JPanel(new BorderLayout());
        pane.add(jspLeft, BorderLayout.WEST);
        pane.add(jtpShows, BorderLayout.CENTER);
        jpCenter.add(pane);
    }

    @Override
    void initializeButtons() {
        jcbShows = new JComboBox();
        
        JButton jbAdd = new JButton(" Add to my series ");
        jbAdd.setBorder(BORDER);
        jbAdd.setToolTipText("Aggiunge il rigo selezionato alle mie serie");
        jbAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                listModel.add(0, jcbShows.getSelectedItem());                
            }
        });
        
        JButton jbSee = new JButton(" Visualizza serie ");
        jbSee.setBorder(BORDER);
        jbSee.setToolTipText("Visualizza info serie selezionata");
        jbSee.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jcbShows.getSelectedIndex()>-1)
                    newTabShow(jcbShows.getSelectedItem());
            }
        });
        
        JButton jbClose = new JButton(" Close all tabs ");
        jbClose.setBorder(BORDER);
        jbClose.setToolTipText("Chiude tutti i tab aperti");
        jbClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jtpShows.removeAll();
            }
        });
        
        int x=0;
        jpAction.add(jcbShows, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbSee, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbAdd, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbClose, gbcAction);
    }
    
    private void newTabShow(Object name){
        tabInternalShow pane = (tabInternalShow) core.addNewTabShow(name);
        if (!jtpShows.isAncestorOf(pane)) {
            jtpShows.addTab(name.toString(), pane);
            jtpShows.setTabComponentAt(jtpShows.getTabCount() - 1,
                    new ButtonTabComponent(jtpShows));
            proxy.infoShow(name.toString());
        }
        jtpShows.setSelectedComponent(pane);
    }

    @Override
    public void objReceived(ListEvent evt) {
        for (int i=0; i<evt.getArray().length; i++)
            listModel.addElement(evt.getArray()[i]);
    }

    @Override
    public void objReceived(ComboboxEvent evt) {
        for (int i=0; i<evt.getArray().length; i++)
            jcbShows.addItem(evt.getArray()[i]);
        jcbShows.addItem(null);
        jcbShows.setSelectedItem(null);
    }

    @Override
    public void objReceived(EditorPaneEvent evt) {
        tabInternalShow pane = (tabInternalShow) core.addNewTabShow(evt.getDest());
        if (evt.getTable().equals("show"))
            pane.setHtmlShow(evt.getHtml());
        else if (evt.getTable().equals("actors"))
            pane.setHtmlActors(evt.getHtml());
    }
} //end class