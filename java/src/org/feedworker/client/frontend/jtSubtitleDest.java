package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;
import org.jfacility.javax.swing.ComboBoxEditor;
import org.jfacility.javax.swing.Swing;

/**
 * 
 * @author luca
 */
class jtSubtitleDest extends JTable implements TableEventListener {
    private Mediator proxy = Mediator.getIstance();
    private final String[] nameCols = {"Serie", "Stagione", "Versione",
        "Destinazione", "Stato", "Giorno", "Rename"};
    private final String[] itemsCombo = proxy.getElemEnum();
    private final String[] itemsComboSettimana = proxy.getDaysOfWeek();
    private final String[] itemsComboStato = {"", "In corso", "Sospeso",
        "In attesa", "In arrivo", "Season Finale", "Series finale"};
    private final Font font = new Font("Arial", Font.PLAIN, 10);

    /** Costruttore */
    public jtSubtitleDest(String name) {
        super();
        setName(name);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {

            Class[] types = new Class[]{String.class, Integer.class,
                String.class, String.class, String.class, String.class,
                Boolean.class};

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        setModel(dtm);

        getTableHeader().setReorderingAllowed(false);

        getColumnModel().getColumn(0).setMinWidth(70); //serie
        Swing.setTableDimensionLockColumn(this, 1, 80); //stagione
        Swing.setTableDimensionLockColumn(this, 2, 80); //versione
        getColumnModel().getColumn(3).setMinWidth(100); //destinazione
        Swing.setTableDimensionLockColumn(this, 4, 90); //stato
        Swing.setTableDimensionLockColumn(this, 5, 75); //giorno
        Swing.setTableDimensionLockColumn(this, 6, 75); //rename

        setComboColumn(2, itemsCombo);
        setComboColumn(4, itemsComboStato);
        setComboColumn(5, itemsComboSettimana);

        setFont(font);
        Swing.tableSorter(this);
        this.getColumn(nameCols[0]).setCellRenderer(new JLabelTitleRenderer());
        this.getColumn(nameCols[3]).setCellRenderer(new JLabelTitleRenderer());
    }

    private void setComboColumn(int num, String[] items) {
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
        private final Font font = new Font("Arial", Font.PLAIN, 10);

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

    class JLabelTitleRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            String text;
            if (value!=null)
                text = value.toString();
            else
                text = "";
            setText(text);
            setToolTipText(Swing.getTextToolTip(table, column, this, text));
            setFont(font);
            setOpaque(true);
            this.repaint();
            return this;
        }
    } // end class JLabelRenderer
}