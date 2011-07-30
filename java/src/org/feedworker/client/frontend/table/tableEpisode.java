package org.feedworker.client.frontend.table;

import javax.swing.table.DefaultTableModel;
import org.feedworker.client.frontend.events.TableEvent;
import org.jfacility.javax.swing.Swing;

/**
 *
 * @author Administrator
 */
public class tableEpisode extends tableAbstract{
    // PRIVATE FINAL VARIABLE
    private final String[] columnNames = {"Num ep", "Num ep season", "Data", "Titolo"};    
    
    public tableEpisode(String name) {
        super(name);        
        DefaultTableModel dtm = new DefaultTableModel(null, columnNames) {
            Class[] types = new Class[]{String.class, String.class, String.class, String.class};
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
    }
    
    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            for (int i = 0; i < evt.getArray().size(); i++)
                dtm.insertRow(i, evt.getArray().get(i));
        }
    }

    @Override
    protected void lockColumns() {
        Swing.setTableDimensionLockColumn(this, 0, 60);
        Swing.setTableDimensionLockColumn(this, 1, 95);
        Swing.setTableDimensionLockColumn(this, 2, 90);
    }    
}