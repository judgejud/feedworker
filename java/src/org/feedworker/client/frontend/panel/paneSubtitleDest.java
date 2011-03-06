package org.feedworker.client.frontend.panel;

//IMPORT JAVA
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

import org.feedworker.client.frontend.table.tableSubtitleDest;
import org.jfacility.javax.swing.Swing;

/**
 * 
 * 
 * @author luca
 */
public class paneSubtitleDest extends paneAbstract {

    private static paneSubtitleDest jpanel = null;
    private tableSubtitleDest jtable;

    public static paneSubtitleDest getPanel() {
        if (jpanel == null)
            jpanel = new paneSubtitleDest();
        return jpanel;
    }

    private paneSubtitleDest() {
        super("Subtitle Destination");
        initializePanel();
        initializeButtons();
        initListeners();
    }

    @Override
    void initializePanel() {
        jtable = new tableSubtitleDest(proxy.getNameTableSubtitleDest());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        jpCenter.add(jScrollTable1);
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
                jtable.removeAllRows();
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

        int x = 0;
        jpAction.add(jbAddRow, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbRemoveRow, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbRemoveAll, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbSaveRole, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbAddDir, gbcAction);
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
        if (row > -1){
            row = jtable.convertRowIndexToModel(row);
            ((DefaultTableModel) jtable.getModel()).removeRow(row);
        }
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
}