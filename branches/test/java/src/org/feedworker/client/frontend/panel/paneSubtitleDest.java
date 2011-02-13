package org.feedworker.client.frontend.panel;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.table.jtSubtitleDest;
import org.jfacility.javax.swing.Swing;

/**
 * 
 * 
 * @author luca
 */
public class paneSubtitleDest extends paneAbstract {

    private static paneSubtitleDest jpanel = null;
    private jtSubtitleDest jtable;

    public static paneSubtitleDest getPanel() {
        if (jpanel == null)
            jpanel = new paneSubtitleDest();
        return jpanel;
    }

    private paneSubtitleDest() {
        super();
        setName("Subtitle Destination");
        initializePanel();
        initializeButtons();
        initListeners();
        setVisible(true);
    }

    @Override
    void initializePanel() {
        jtable = new jtSubtitleDest(proxy.getNameTableSubtitleDest());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.CENTER);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(" + ");
        jbAddRow.setToolTipText("Aggiungi riga alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });
        
        JButton jbRemoveAll = new JButton(" Remove All ");
        jbRemoveAll.setToolTipText("Rimuove tutte le serie dalla tabella");
        jbRemoveAll.setBorder(BORDER);
        jbRemoveAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveAllRowsMouseClick();
            }
        });

        JButton jbSaveRole = new JButton(" Salva ");
        jbSaveRole.setToolTipText("Salva impostazioni");
        jbSaveRole.setBorder(BORDER);
        jbSaveRole.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbSaveRoleMouseClicked();
            }
        });

        JButton jbAddDir = new JButton(" Destinazione ");
        jbAddDir.setToolTipText("");
        jbAddDir.setBorder(BORDER);
        jbAddDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddDirMouseClicked();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        int x = -1;
        gbc.gridx = ++x;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        jpAction.add(jbAddRow, gbc);
        gbc.gridx = ++x;
        jpAction.add(jbRemoveRow, gbc);
        gbc.gridx = ++x;
        jpAction.add(jbRemoveAll, gbc);
        gbc.gridx = ++x;
        jpAction.add(jbSaveRole, gbc);
        gbc.gridx = ++x;
        jpAction.add(jbAddDir, gbc);

        add(jpAction, BorderLayout.NORTH);
    }
    
    private void initListeners(){
        jtable.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
		// Left mouse click
		if (SwingUtilities.isRightMouseButton(e)){
                    // get the coordinates of the mouse click
                    final Point p = e.getPoint();
                    if (jtable.columnAtPoint(p)==3){
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem jmiOpenFolder = new JMenuItem("Apri cartella");
                        jmiOpenFolder.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                jmiOpenFolderClicked(p);
                            }
                        });
                        popupMenu.add(jmiOpenFolder);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
		}
            }
        });
        proxy.setTableListener(jtable);
    }

    private void jbAddRowMouseClicked() {
        DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
        dtm.insertRow(0, new Object[]{null, 1, "normale", null, null, null, false});
    }

    private void jbSaveRoleMouseClicked() {
        //TODO passare il model
        proxy.saveRules(jtable);
    }

    private void jbRemoveRowMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1)
            ((DefaultTableModel) jtable.getModel()).removeRow(jtable.convertRowIndexToModel(row));
    }

    private void jbAddDirMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1) {
            String dir = Swing.getDirectory(this, "");
            if (dir != null) {
                DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
                dtm.setValueAt(dir, row, 3);
            }
        }
    }
    
    private void jmiOpenFolderClicked(Point p){
        int row = jtable.rowAtPoint(p);
        proxy.openFolder(jtable.getValueAt(row, 3).toString());
    }
    
    private void jbRemoveAllRowsMouseClick(){
        ((DefaultTableModel) jtable.getModel()).setRowCount(0);
    }
}