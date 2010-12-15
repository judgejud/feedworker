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
        JButton jbAddRow = new JButton(" Aggiungi Serie ");
        jbAddRow.setToolTipText("Aggiungi riga/serie alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" Rimuove Serie ");
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });

        JButton jbRefresh = new JButton(" Aggiorna informazioni ");
        jbRefresh.setToolTipText("Aggiorna le informazioni sulle serie");
        jbRefresh.setBorder(BORDER);
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshCalendarMouseClicked();
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

        actionJP.add(jbAddRow, gbc);
        gbc.gridx = 1;
        actionJP.add(jbRemoveRow, gbc);
        gbc.gridx = 2;
        actionJP.add(jbRefresh, gbc);
        gbc.gridx = 3;
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
            Object[] value = new Object[10];
            for (int i=0; i<10; i++)
                value[i] = jtable.getValueAt(row, i);
            proxy.removeCalendar(value);
            ((DefaultTableModel) jtable.getModel()).removeRow(row);
        }
    }

    private void jbRefreshCalendarMouseClicked() {

    }
}