package org.feedworker.client.frontend.table;

//IMPORT JAVA
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;
import org.jfacility.javax.swing.Swing;
/**
 *
 * @author luca
 */
public class tableReminder extends JTable implements TableEventListener {
    // PRIVATE FINAL VARIABLE
    private final Font font = new Font("Arial", Font.PLAIN, 10);
    private final String colSubtitle = "Sottotitolo";
    private final String[] columnNames = {"Data", colSubtitle, "Select"};    

    /**
     *
     * @param name
     */
    public tableReminder(String name) {
        super();
        setName(name);
        setFont(font);
        DefaultTableModel dtm = new DefaultTableModel(null, columnNames) {
            Class[] types = new Class[]{String.class, String.class, Boolean.class};
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
        setRowSelectionAllowed(false);
        getTableHeader().setReorderingAllowed(false);
        Swing.setTableDimensionLockColumn(this, 0, 110);
        Swing.setTableDimensionLockColumn(this, 2, 50);
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
            //String titleCol = (String) this.getColumnModel().getColumn(3).getHeaderValue();
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            int size = evt.getArray().size();
            int start = dtm.getRowCount();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i+start, evt.getArray().get(i));
            this.getColumn(colSubtitle).setCellRenderer(new labelCellColorRenderer());
        }
    }

    public void removeAllRows() {
        DefaultTableModel dtm = (DefaultTableModel) getModel();
        int size = dtm.getRowCount();
        for (int i = 0; i < size; i++) {
            dtm.removeRow(0);
        }
    }
}