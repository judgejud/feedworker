package org.feedworker.client.frontend;

import java.awt.Font;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;
import org.jfacility.javax.swing.Swing;

/**
 *
 * @author luca
 */
class jtCalendar extends JTable implements TableEventListener{
    private final String[] nameCols = {"ID","Serie", "Stato", "Giorno", "Last Ep", 
                            "Titolo", "Data", "Next Ep", "Titolo", "Data" };
    private final Font font = new Font("Arial", Font.PLAIN, 10);

    public jtCalendar(String nome){
        super();
        setName(nome);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {

            Class[] types = new Class[]{String.class, String.class, String.class,
                String.class, String.class, String.class, Date.class,
                String.class, String.class, Date.class};

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
        getTableHeader().setReorderingAllowed(false);
        setFont(font);
        Swing.tableSorter(this);
        Swing.setTableDimensionLockColumn(this, 0, 40);
        Swing.setTableDimensionLockColumn(this, 2, 100);
        Swing.setTableDimensionLockColumn(this, 3, 70);
        Swing.setTableDimensionLockColumn(this, 4, 70);
        Swing.setTableDimensionLockColumn(this, 6, 65);
        Swing.setTableDimensionLockColumn(this, 7, 75);
        Swing.setTableDimensionLockColumn(this, 9, 65);
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            if (evt.isAddRows()){
                int size = evt.getArray().size();
                int start = dtm.getRowCount();
                for (int i = 0; i < size; i++)
                    dtm.insertRow(i+start, evt.getArray().get(i));
            } else {
                Long[] rows = (Long[]) evt.getArray().get(0);
                for (int i = 0; i < rows.length; i++){
                    int row = rows[i].intValue()-1;
                    dtm.removeRow(convertRowIndexToModel(row));
                }
            }
        }
    }
}