package org.feedworker.client.frontend.table;

import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.jfacility.javax.swing.Swing;
/**
 *
 * @author luca
 */
public class tableResultSearchSub extends tableAbstract{
    private final String[] nameCols = {"ID", "Nome", "Versione", "Download"};

    public tableResultSearchSub(String name){
        super(name);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {
            Class[] types = new Class[]{Integer.class, String.class, String.class,
                Boolean.class};
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
        super.setMouseClickBoolean(3, 0);
    }
    
    @Override
    protected void lockColumns(){
        Swing.setTableDimensionLockColumn(this, 0, 100);
        Swing.setTableDimensionLockColumn(this, 2, 100);
        Swing.setTableDimensionLockColumn(this, 3, 100);
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            dtm.setRowCount(0);
            int size = evt.getArraySubtitle().size();
            for (int i = 0; i < size; i++){
                dtm.insertRow(i, evt.getArraySubtitle().get(i).toArrayIdNameVersion());
                dtm.setValueAt(false, i, 3);
            }
        }
    }
}