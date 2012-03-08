package org.feedworker.client.frontend.table;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.object.Subtitle;

import org.jfacility.javax.swing.Swing;
/**
 *
 * @author luca
 */
public class tableResultSearchSub extends tableAbstract{
    private final String[] nameCols = {"ID", "Nome", "Versione", "Nome file", 
                                        "Dimensione", "Download"};

    public tableResultSearchSub(String name){
        super(name);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {
            Class[] types = new Class[]{Integer.class, String.class, String.class,
                String.class, String.class, Boolean.class};
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
        super.setMouseClickBoolean(nameCols.length-1, 0);
    }
    
    @Override
    protected void lockColumns(){
        Swing.setTableDimensionLockColumn(this, 0, 45);
        Swing.setTableDimensionLockColumn(this, 2, 70);
        Swing.setTableDimensionLockColumn(this, 4, 90);
        Swing.setTableDimensionLockColumn(this, 5, 80);
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            //dtm.setRowCount(0);
            ArrayList<Subtitle> array = evt.getArraySubtitle();
            for (int i = 0; i < array.size(); i++){
                dtm.insertRow(i, array.get(i).toArray());
                dtm.setValueAt(false, i, nameCols.length-1);
            }
        }
    }
}