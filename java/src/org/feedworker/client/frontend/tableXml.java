package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
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
class tableXml extends JTable implements TableXmlEventListener {

    private final String[] nameCols = {"Serie", "Stagione", "Versione",
        "Destinazione", "Stato", "Giorno", "Rename"};
    private final String[] itemsCombo = Mediator.getIstance().getElemEnum();
    private final String[] itemsComboSettimana = {"", "Domenica", "Lunedì",
        "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"};
    private final String[] itemsComboStato = {"", "In corso", "Sospeso",
        "In attesa", "In arrivo", "Season Finale", "Series finale"};
    private final Font font = new Font("Arial", Font.PLAIN, 10);

    /** Costruttore */
    public tableXml() {
        super();
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
        for (int i=0; i<nameCols.length; i++)
            getColumnModel().getColumn(i).setHeaderRenderer(new MyTableHeaderRenderer());

        getColumnModel().getColumn(0).setMinWidth(70);
        Swing.setTableDimensionLockColumn(this, 1, 75);
        Swing.setTableDimensionLockColumn(this, 2, 75);
        getColumnModel().getColumn(3).setMinWidth(100);
        Swing.setTableDimensionLockColumn(this, 4, 90);
        Swing.setTableDimensionLockColumn(this, 5, 75);
        Swing.setTableDimensionLockColumn(this, 6, 70);

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
    public void objReceived(TableXmlEvent evt) {
        DefaultTableModel dtm = (DefaultTableModel) getModel();
        int size = evt.getObj().size();
        for (int i = 0; i < size; i++) {
            dtm.insertRow(i, evt.getObj().get(i));
        }
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
            String text = value.toString();
            setText(text);
            setToolTipText(Swing.getTextToolTip(table, column, this, text));
            setFont(font);
            setOpaque(true);
            this.repaint();
            return this;
        }
    } // end class JLabelRenderer

    class MyTableHeaderRenderer extends JLabel implements TableCellRenderer {
        // This method is called each time a column header
        // using this renderer needs to be rendered.
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
            // 'value' is column header value of column 'vColIndex'
            // rowIndex is always -1
            // isSelected is always false
            // hasFocus is always false
            // Configure the component with the specified value
            setText(value.toString());
            setHorizontalAlignment(CENTER);
            setBorder(new LineBorder(Color.black,1));
            // Set tool tip if desired setToolTipText((String)value);
            // Since the renderer is a component, return itself
            return this;
        }
        // The following methods override the defaults for performance reasons
        @Override
        public void validate() {}
        @Override
        public void revalidate() {}
        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
    }
}