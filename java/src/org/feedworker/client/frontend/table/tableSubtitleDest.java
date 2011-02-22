package org.feedworker.client.frontend.table;

//IMPORT JAVA
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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
        "Destinazione",  "Rename", "Delete"};
    private final String[] itemsCombo = Mediator.getIstance().getQualityEnum();

    /** Costruttore */
    public tableSubtitleDest(String name) {
        super(name);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {
            Class[] types = new Class[]{String.class, Integer.class,
                String.class, String.class, Boolean.class, Boolean.class};
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        setModel(dtm);
        setComboBoxColumn(2, itemsCombo);
        setRowSelectionAllowed(true);
        lockColumns();
    }
    
    @Override
    protected void lockColumns(){
        int c = -1;
        getColumnModel().getColumn(++c).setMinWidth(70); //serie
        Swing.setTableDimensionLockColumn(this, ++c, 80); //stagione
        Swing.setTableDimensionLockColumn(this, ++c, 80); //versione
        getColumnModel().getColumn(++c).setMinWidth(100); //destinazione
        Swing.setTableDimensionLockColumn(this, ++c, 75); //rename
        Swing.setTableDimensionLockColumn(this, ++c, 65); //delete
    }

    private void setComboBoxColumn(int num, String[] items) {
        // Set the combobox editor on column
        TableColumn col = getColumnModel().getColumn(num);
        col.setCellEditor(new ComboBoxEditor(items));
        // If the cell should appear like a combobox in its
        // non-editing state, also set the combobox renderer
        col.setCellRenderer(new MyComboBoxRenderer(items));
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

    class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public MyComboBoxRenderer(String[] items) {
            super(items);
            setFont(font);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            // Select the current value
            setSelectedItem(value);
            return this;
        }
    } //END CLASS MyComboBoxRenderer
}