package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import org.feedworker.client.frontend.Mediator;

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
    }

    @Override
    void initializePanel() {
        remove(jpCenter);
        jtable = new tableReminder("Reminder");
        jtable.setPreferredSize(new Dimension(500, 650));
        JScrollPane jsp = new JScrollPane(jtable);        
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jsp);
        jpCenter.add(RIGID_AREA);
        add(jpCenter, BorderLayout.WEST);
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
        jpAction.add(jbRemoveRow, gbcAction);
    }
    
    private void jbRemoveRowMouseClicked() {
        int rows = jtable.getRowCount();
        if (rows > 0){
            ArrayList<Integer> numbers = new ArrayList<Integer>();
            for (int i=rows-1; i>-1; i--){
                if (Boolean.parseBoolean(jtable.getValueAt(i, 2).toString())){
                    int row;
                    if (Mediator.getIstance().isJava6())
                        row = jtable.convertRowIndexToModel(i);
                    else
                        row = i;
                    numbers.add(row);
                    ((DefaultTableModel) jtable.getModel()).removeRow(row);
                }
            }
            proxy.removeReminders(numbers);
        }
    }
}