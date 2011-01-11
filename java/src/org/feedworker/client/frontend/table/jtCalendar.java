package org.feedworker.client.frontend.table;

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
public class jtCalendar extends JTable implements TableEventListener{
    private final String[] nameCols = {"ID tvrage", "ID ITasa", "ID Tvdb", "Serie", "Stato", 
                "Giorno", "Last Ep", "Titolo", "Data", "Next Ep", "Titolo", "Data" };
    private final Font font = new Font("Arial", Font.PLAIN, 10);

    public jtCalendar(String nome){
        super();
        setName(nome);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {

            Class[] types = new Class[]{String.class, String.class, String.class,
                            String.class, String.class, String.class, String.class, 
                            String.class, Date.class, String.class, String.class, 
                            Date.class};

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
        lockColumns();
    }
    
    private void lockColumns(){
        Swing.setTableDimensionLockColumn(this, 0, -1); //id tvrage
        Swing.setTableDimensionLockColumn(this, 1, -1); //id itasa
        Swing.setTableDimensionLockColumn(this, 2, -1); //id tvdb
        Swing.setTableDimensionLockColumn(this, 4, 100);
        Swing.setTableDimensionLockColumn(this, 5, 70);
        Swing.setTableDimensionLockColumn(this, 6, 70);
        Swing.setTableDimensionLockColumn(this, 8, 65);
        Swing.setTableDimensionLockColumn(this, 9, 75);
        Swing.setTableDimensionLockColumn(this, 11, 65);
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            int size = evt.getArray().size();
            int start = dtm.getRowCount();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i+start, evt.getArray().get(i));
        }
    }
}