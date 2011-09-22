package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import org.feedworker.client.frontend.events.ComboboxEvent;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEvent;
import org.feedworker.client.frontend.events.EditorPaneEventListener;

import org.jfacility.javax.swing.ButtonTabComponent;

/**TODO: lista ordinata e senza duplicati,
 * @author luca
 */
public class paneShow extends paneAbstract implements ComboboxEventListener, 
                                                        EditorPaneEventListener{
    
    private static paneShow jpanel = null;
    private JTabbedPane jtpShows, jtpList;
    private JComboBox jcbShows;
    private JPopupMenu menu = new JPopupMenu("Popup");
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
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
        JButton jbImport = new JButton(core.getIconImport2());
        jbImport.setBorder(BORDER);
        jbImport.setToolTipText("Importa da myItasa");
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                importFromMyItasa();
            }
        });
        
        JButton jbSave = new JButton(core.getIconSave());
        jbSave.setBorder(BORDER);
        jbSave.setToolTipText("Salva Lista");
        jbSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //if (jlSeries.getSizeModel()>0)
                    //proxy.saveList(jlSeries.getArrayModel());
            }
        });
        
        JButton jbRemove = new JButton(core.getIconRemove());
        jbRemove.setBorder(BORDER);
        jbRemove.setToolTipText("Rimuovi selezionato");
        jbRemove.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //jlSeries.removeSelectedItem();
            }
        });
        
        jtpList = new JTabbedPane();
        jtpList.setPreferredSize(new Dimension(190, 850));
        jtpList.setMinimumSize(new Dimension(190, 600));
        
        /*
        jlSeries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) // Double-click 
                    newTabShow(jlSeries.getListSelectedValue(), false);
            }
            @Override
            public void mousePressed(MouseEvent ev) {
                if (ev.isPopupTrigger())
                    menu.show(ev.getComponent(), ev.getX(), ev.getY());
            }
            @Override
            public void mouseReleased(MouseEvent ev) {
                if (ev.isPopupTrigger())
                    menu.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        });
        */
        JMenuItem item = new JMenuItem("Test1");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu item Test1");
            }
        });
        menu.add(item);

        item = new JMenuItem("Test2");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Menu item Test2");
            }
        });
        menu.add(item);
        
        JPanel jpWest = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int x=-1;
        gbc.gridx = ++x;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        jpWest.add(jbImport, gbc);
        gbc.gridx = ++x;
        jpWest.add(jbSave, gbc);
        gbc.gridx = ++x;
        jpWest.add(jbRemove, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        jpWest.add(jtpList, gbc);
        
        jtpShows = new JTabbedPane();
        
        JPanel pane = new JPanel(new BorderLayout());
        
        pane.add(jpWest, BorderLayout.WEST);
        pane.add(jtpShows, BorderLayout.CENTER);
        jpCenter.add(pane);
    }

    @Override
    void initializeButtons() {
        jcbShows = new JComboBox();
        jcbShows.setMaximumRowCount(20);
        
        JButton jbAdd = new JButton(core.getIconAdd());
        jbAdd.setBorder(BORDER);
        jbAdd.setToolTipText("Aggiunge il rigo selezionato alle mie serie");
        jbAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jtpShows.getSelectedIndex()>-1)
                    proxy.requestSingleAddList(jtpList.getSelectedComponent().getName(), 
                                                    jcbShows.getSelectedItem());
            }
        });
        
        JButton jbSee = new JButton(core.getIconSee());
        jbSee.setBorder(BORDER);
        jbSee.setToolTipText("Visualizza info serie selezionata");
        jbSee.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jcbShows.getSelectedIndex()>-1)
                    newTabShow(jcbShows.getSelectedItem(), false);
            }
        });
        
        JButton jbRefresh = new JButton(core.getIconRefresh2());
        jbRefresh.setBorder(BORDER);
        jbRefresh.setToolTipText("Aggiorna i dati del tab attivo");
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jtpShows.getSelectedIndex()>-1)
                    newTabShow(jtpShows.getSelectedComponent().getName(), true);
            }
        });
        
        JButton jbBrowse = new JButton(core.getIconWWW());
        jbBrowse.setBorder(BORDER);
        jbBrowse.setToolTipText("Visualizza la scheda itasa del tab attivo nel browser");
        jbBrowse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jtpShows.getSelectedIndex()>-1)
                    proxy.openFormTV(jtpShows.getSelectedComponent().getName());
            }
        });
        
        JButton jbClose = new JButton(core.getIconClose());
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
        jpAction.add(jbRefresh, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbBrowse, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbClose, gbcAction);
    }
    
    private void newTabShow(Object name, boolean refresh){
        tabShowInfo pane;
        if (!refresh){
            pane = core.addNewTabShow(name);
            if (!jtpShows.isAncestorOf(pane)) {
                jtpShows.addTab(name.toString(), pane);
                jtpShows.setTabComponentAt(jtpShows.getTabCount() - 1,
                        new ButtonTabComponent(jtpShows));
            }
        } else
            pane = core.refreshTabShow(name);
        jtpShows.setSelectedComponent(pane);
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
        tabShowInfo pane = (tabShowInfo) core.addNewTabShow(evt.getDest());
        if (evt.getTable().equals("show"))
            pane.setHtmlShow(evt.getHtml());
        else if (evt.getTable().equals("actors"))
            pane.setHtmlActors(evt.getHtml());
    }
    
    private void importFromMyItasa() {
        tabShowList pane = core.addNewTabListMyItasa("myItasa");
        if (pane!=null)
            jtpList.addTab(pane.getName(), pane);
    }
} //end class