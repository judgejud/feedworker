package org.feedworker.client.frontend.table;

//IMPORT JAVA
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.Mediator;
import org.feedworker.client.frontend.events.TableEvent;

import org.jfacility.javax.swing.ComboBoxEditor;
import org.jfacility.javax.swing.Swing;

/**
 * 
 * @author luca
 */
public class tableSubtitleDest extends tableAbstract {
    private final String[] nameCols = {"Serie", "Stagione", "Versione",
        "Destinazione",  "Operazione"};
    private Mediator proxy = Mediator.getIstance();

    /** Costruttore */
    public tableSubtitleDest(String name) {
        super(name);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {
            Class[] types = new Class[]{String.class, Integer.class,
                String.class, String.class, String.class};
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return true;
            }
        };
        setModel(dtm);
        getColumnModel().getColumn(2).setCellEditor(new ComboBoxEditor(proxy.getQualityEnum()));
        getColumnModel().getColumn(4).setCellEditor(new ComboBoxEditor(proxy.getOperationEnum()));
        setFocusable(true);
        setRowSelectionAllowed(true);
        lockColumns();
        Swing.tableSorter(this);
    }
    
    @Override
    protected void lockColumns(){
        int c = -1;
        getColumnModel().getColumn(++c).setMinWidth(70); //serie
        Swing.setTableDimensionLockColumn(this, ++c, 80); //stagione
        Swing.setTableDimensionLockColumn(this, ++c, 80); //versione
        getColumnModel().getColumn(++c).setMinWidth(100); //destinazione
        getColumnModel().getColumn(++c).setMinWidth(60); //operazione
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            int size = evt.getArray().size();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i, evt.getArray().get(i));
        }
    }
}