package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.jfacility.java.lang.Lang;
/**
 *
 * @author luca
 */
class jpCalendar extends jpAbstract{

    private static jpCalendar jpanel = null;
    private jtCalendar jtable;

    private jpCalendar(){
        super();
        initializePanel();
        initializeButtons();
    }

    public static jpCalendar getPanel(){
        if (jpanel==null)
            jpanel = new jpCalendar();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtable = new jtCalendar(proxy.getNameTableCalendar());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.CENTER);
        proxy.setTableListener(jtable);
        setVisible(true);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(" + ");
        jbAddRow.setToolTipText("Aggiungi riga/serie alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
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
                jbRemoveAllRows();
            }
        });

        JButton jbRefresh = new JButton(" Aggiorna ");
        jbRefresh.setToolTipText("Aggiorna le informazioni sulle serie");
        jbRefresh.setBorder(BORDER);
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.refreshCalendar();
            }
        });

        JButton jbImport = new JButton(" Importa ");
        jbImport.setToolTipText("Importa dai nomi serie di subtitle destination");
        jbImport.setBorder(BORDER);
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.importFromSubDest();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int x=1;
        actionJP.add(jbAddRow, gbc);
        gbc.gridx = x++;
        actionJP.add(jbRemoveRow, gbc);
        gbc.gridx = x++;
        actionJP.add(jbRemoveAll, gbc);
        gbc.gridx = x++;
        actionJP.add(jbRefresh, gbc);
        gbc.gridx = x++;
        actionJP.add(jbImport, gbc);
        
        add(actionJP, BorderLayout.NORTH);
    }

    private void jbAddRowMouseClicked() {
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
    }

    private void jbRemoveRowMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1){
            row = jtable.convertRowIndexToModel(row);
            proxy.removeSingleShowCalendar(row, jtable.getValueAt(row, 0));
            ((DefaultTableModel) jtable.getModel()).removeRow(row);
        }
    }
    
    private void jbRemoveAllRows(){
        proxy.removeAllShowCalendar();
        ((DefaultTableModel) jtable.getModel()).setRowCount(0);
    }
}