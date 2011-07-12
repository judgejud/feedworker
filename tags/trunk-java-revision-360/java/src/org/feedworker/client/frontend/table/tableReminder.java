package org.feedworker.client.frontend.table;

//IMPORT JAVA
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.jfacility.javax.swing.Swing;
/**
 *
 * @author luca
 */
public class tableReminder extends tableAbstract {
    // PRIVATE FINAL VARIABLE
    private final String[] columnNames = {"Data", "Sottotitolo", "Select"};    

    /**
     *
     * @param name
     */
    public tableReminder(String name) {
        super(name);
        
        DefaultTableModel dtm = new DefaultTableModel(null, columnNames) {
            Class[] types = new Class[]{Date.class, String.class, Boolean.class};
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        
        setModel(dtm);
        lockColumns();
        Swing.tableSorter(this);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = getSelectedRow();
                if (Boolean.parseBoolean(getValueAt(row, 2).toString()) == true)
                    setValueAt(false, row, 2);
                else
                    setValueAt(true, row, 2);
            }
        });
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            int size = evt.getArray().size();
            int start = dtm.getRowCount();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i+start, evt.getArray().get(i));
            getColumn(columnNames[1]).setCellRenderer(new labelCellColorRenderer());
        }
    }
    
    @Override
    protected void lockColumns(){
        Swing.setTableDimensionLockColumn(this, 0, 70);
        Swing.setTableDimensionLockColumn(this, 2, 50);
    }
}