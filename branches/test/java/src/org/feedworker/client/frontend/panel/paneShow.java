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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import org.feedworker.client.frontend.component.listShow;
import org.feedworker.client.frontend.component.taskpaneShowInfo;
import org.feedworker.client.frontend.events.ComboboxEvent;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEvent;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.TabbedPaneEvent;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;

import org.jfacility.javax.swing.ButtonTabComponent;

/**TODO: lista ordinata
 * @author luca
 */
public class paneShow extends paneAbstract implements ComboboxEventListener, 
                                    EditorPaneEventListener, TabbedPaneEventListener{
    private static paneShow jpanel = null;
    private JTabbedPane jtpShows, jtpList;
    private JComboBox jcbShows;
    private JPopupMenu menu;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
        createPopupMenu();
        core.setComboboxListener(this);
        core.setEditorPaneListener(this);
        core.setTabbedPaneListener(this);
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
                if (jtpList.getSelectedIndex()>-1)
                    core.saveList(jtpList);
            }
        });
        
        JButton jbAddCategory = new JButton(core.getIconTabAdd());
        jbAddCategory.setBorder(BORDER);
        jbAddCategory.setToolTipText("Aggiungi categoria");
        jbAddCategory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                addCat();
            }
        });
        
        JButton jbRenameCategory = new JButton(core.getIconTabEdit());
        jbRenameCategory.setBorder(BORDER);
        jbRenameCategory.setToolTipText("Rinomina la categoria selezionata");
        jbRenameCategory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                renameCat();
            }
        });
        
        JButton jbRemoveCategory = new JButton(core.getIconTabDel());
        jbRemoveCategory.setBorder(BORDER);
        jbRemoveCategory.setToolTipText("Rimuove la categoria selezionata e le serie "
                + "presenti in essa");
        jbRemoveCategory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jtpList.getSelectedIndex()>-1 && 
                                    core.requestRemoveSeries(getParent(),false)==0)
                    jtpList.remove(jtpList.getSelectedComponent());
            }
        });
        
        jtpList = new JTabbedPane();
        jtpList.setPreferredSize(new Dimension(190, 850));
        jtpList.setMinimumSize(new Dimension(190, 600));
        
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
        jpWest.add(jbAddCategory, gbc);
        gbc.gridx = ++x;
        jpWest.add(jbRenameCategory, gbc);
        gbc.gridx = ++x;
        jpWest.add(jbRemoveCategory, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = ++x;
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
        jbAdd.setToolTipText("Aggiunge il rigo selezionato della combobox alla "
                            + "categoria attiva");
        jbAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jcbShows.getSelectedIndex()>-1 && jtpList.getSelectedIndex()>-1)
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
    
    private void createPopupMenu(){
        menu = new JPopupMenu("Popup");
        JMenuItem jmiRemove = new JMenuItem("Rimuovi");
        jmiRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((listShow)jtpList.getSelectedComponent()).removeSelectedItem();
            }
        });
        menu.add(jmiRemove);
    }
    
    private void createDynamicSubmenu(){
        if (menu.getComponentCount()>1)
            menu.remove(1);
        JMenu submenu = new JMenu("Sposta in");
        JMenuItem jmi = null;
        String name = jtpList.getSelectedComponent().getName();
        for (int i=0; i<jtpList.getTabCount(); i++){
            String tab = jtpList.getTitleAt(i);
            if (!name.equalsIgnoreCase(tab)){
                jmi = new JMenuItem(tab);
                jmi.setActionCommand(Integer.toString(i));
                jmi.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        listShow tab = (listShow)jtpList.getSelectedComponent();
                        Object[] clone = tab.getListCloneSelectedValue();
                        int j = Integer.parseInt(e.getActionCommand());
                        ((listShow)jtpList.getComponentAt(j)).addItem(clone);
                        tab.removeSelectedItem();
                    }
                });
                submenu.add(jmi);
            }
        }
        menu.add(submenu);
    }
    
    private void newTabShow(Object name, boolean refresh){
        taskpaneShowInfo pane;
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
        String t = evt.getTable();
        if (t!=null){
            taskpaneShowInfo pane = (taskpaneShowInfo) core.addNewTabShow(evt.getDest());
            if (t.equals("show"))
                pane.setHtmlShow(evt.getHtml());
            else if (t.equals("actors"))
                pane.setHtmlActors(evt.getHtml());
        }
    }
    
    private void importFromMyItasa() {
        if (jtpList.getSelectedIndex()>-1)
            proxy.importNameListFromMyItasa(jtpList.getSelectedComponent().getName());
        else
            proxy.printAlert("Selezionare/aggiungere una categoria per poter importare");
    }
    
    private void addCat(){
        String cat = JOptionPane.showInputDialog(this, "Dare un nome alla categoria/tab");
        if (cat!=null && core.checkTabListShow(cat)) {
            listShow pane = new listShow(cat);
            jtpList.addTab(pane.getName(), pane);
            pane.setListMouseListener(getListMouseAdapter(jtpList.getTabCount() - 1));
        }
    }
    
    private void renameCat(){
        if (jtpList.getSelectedIndex()>-1){
            String cat = JOptionPane.showInputDialog(this, "Dare un nome alla categoria/tab");
            if (cat!=null && core.checkTabListShow(cat)) {
                jtpList.setTitleAt(jtpList.getSelectedIndex(), cat);
                ((listShow)jtpList.getSelectedComponent()).rename(cat);
            }
        }
    }

    @Override
    public void objReceived(TabbedPaneEvent evt) {
        if (evt.getDest()==null){
            for (int i=0; i<evt.getArray().size(); i++){
                String cat = evt.getArray().get(i);
                core.checkTabListShow(cat);
                listShow pane = new listShow(cat);
                jtpList.addTab(pane.getName(), pane);
                pane.setListMouseListener(getListMouseAdapter(jtpList.getTabCount() - 1));
            }
        }
    }
    
    private MouseAdapter getListMouseAdapter(final int index){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                if (ev.getClickCount() == 2){ // Double-click 
                    listShow pane = (listShow) jtpList.getComponentAt(index);
                    newTabShow(pane.getListSelectedValue(), false);
                }
            }
            @Override
            public void mouseReleased(MouseEvent ev) {
                if (ev.isPopupTrigger()){
                    createDynamicSubmenu();
                    listShow pane = (listShow) jtpList.getComponentAt(index);
                    pane.setListPointSelection(ev.getPoint());
                    menu.show(ev.getComponent(), ev.getX(), ev.getY());
                }
            }
        };  
    }
} //end class