package org.feedworker.client.frontend;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;

import org.jfacility.javax.swing.Swing;
/**
 *
 * @author luca
 */
public class jtResultSearchTv extends JTable implements TableEventListener{
    private final String[] nameCols = {"ID","Serie", "Link", "Stagione", "Status", "Giorno"};
    private final Font font = new Font("Arial", Font.PLAIN, 10);

    public jtResultSearchTv(String name){
        super();
        setName(name);
        setPreferredSize(new Dimension(600, 290));
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {
            Class[] types = new Class[]{String.class, String.class, String.class,
                String.class, String.class, String.class};
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
        Swing.setTableDimensionLockColumn(this, 0, 80);
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            dtm.setRowCount(0);
            int size = evt.getArray().size();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i, evt.getArray().get(i));
        }
    }
}