package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.feedworker.client.frontend.events.TableXmlEvent;
import org.feedworker.client.frontend.events.TableXmlEventListener;
import org.jfacility.swing.ComboBoxEditor;
import org.jfacility.swing.Swing;
/**
 *
 * @author luca
 */
class tableXml extends JTable implements TableXmlEventListener{
    private final String[] nameCols = {"Serie", "Stagione", "Versione", "Destinazione", "Stato",
                                        "Giorno", "Rename"};
    private final String[] itemsCombo = Mediator.getIstance().getElemEnum();
    private final String[] itemsComboSettimana = {"", "Domenica", "Lunedì", "Martedì",
                                                "Mercoledì", "Giovedì", "Venerdì", "Sabato"};
    private final String[] itemsComboStato = {"","In corso", "Sospeso", "In attesa", "In arrivo",
                                            "Season Finale", "Series finale"};
    private final Font font = new Font("Arial", Font.PLAIN, 10);
    /**Costruttore*/
    public tableXml() {
        super();
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols){
            Class[] types = new Class[]{String.class, Integer.class, String.class, String.class,
                String.class, String.class, Boolean.class};
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        setModel(dtm);

        getTableHeader().setReorderingAllowed(false);
        
        Swing.setTableDimensionLockColumn(this, 1, 55);
        Swing.setTableDimensionLockColumn(this, 2, 60);
        Swing.setTableDimensionLockColumn(this, 4, 90);
        Swing.setTableDimensionLockColumn(this, 5, 75);
        Swing.setTableDimensionLockColumn(this, 6, 55);

        setComboColumn(2, itemsCombo);
        setComboColumn(4, itemsComboStato);
        setComboColumn(5, itemsComboSettimana);
        
        setFont(font);
    }

    private void setComboColumn(int num, String[] items){
        // Set the combobox editor on column
        TableColumn col = getColumnModel().getColumn(num);
        col.setCellEditor(new ComboBoxEditor(items));
        // If the cell should appear like a combobox in its
        // non-editing state, also set the combobox renderer
        col.setCellRenderer(new MyComboBoxRenderer(items));
    }
    @Override
    public void objReceived(TableXmlEvent evt) {
        DefaultTableModel dtm = (DefaultTableModel) getModel();
        int size = evt.getObj().size();
        for (int i = 0; i < size; i++)
            dtm.insertRow(i, evt.getObj().get(i));
        repaint();
        validate();
    }

    class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
        private final Font font = new Font("Arial", Font.PLAIN, 10);
        public MyComboBoxRenderer(String[] items) {
            super(items);
            setFont(font);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
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
    }
}