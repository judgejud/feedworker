package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.table.tableReminder;

/**
 *
 * @author luca
 */
public class paneReminder extends paneAbstract{
    
    private static paneReminder jpanel = null;
    private tableReminder jtable;

    public static paneReminder getPanel() {
        if (jpanel == null)
            jpanel = new paneReminder();
        return jpanel;
    }

    private paneReminder() {
        super("Reminder");
        initializePanel();
        initializeButtons();
        proxy.setTableListener(jtable);
        setVisible(true);
    }

    @Override
    void initializePanel() {
        jtable = new tableReminder("Reminder");
        JScrollPane jsp = new JScrollPane(jtable);
        
        jpCenter.add(jsp);
        jpCenter.add(RIGID_AREA);
        add(jpCenter, BorderLayout.CENTER);
    }

    @Override
    void initializeButtons() {
        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });
        jpAction.add(jbRemoveRow);
        add(jpAction, BorderLayout.NORTH);
    }
    
    private void jbRemoveRowMouseClicked() {
        int[] rows = jtable.getSelectedRows();
        if (rows.length > 0){
            for (int i=0; i<rows.length; i++){
                int row = jtable.convertRowIndexToModel(rows[i]);
                System.out.println(row); 
                //proxy.removeSingleShowCalendar(row, jtable.getValueAt(row, 0));
                ((DefaultTableModel) jtable.getModel()).removeRow(row);
            }
        }
    }
}