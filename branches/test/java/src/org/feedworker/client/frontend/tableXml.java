package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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

        Swing.setTableDimensionLockColumn(this, 1, 60);
        Swing.setTableDimensionLockColumn(this, 2, 60);
        Swing.setTableDimensionLockColumn(this, 4, 90);
        Swing.setTableDimensionLockColumn(this, 5, 75);
        Swing.setTableDimensionLockColumn(this, 6, 55);

        setComboColumn(2, itemsCombo);
        setComboColumn(4, itemsComboStato);
        setComboColumn(5, itemsComboSettimana);

        setFont(font);

        setAutoCreateColumnsFromModel(false);
        getTableHeader().addMouseListener(new ColumnHeaderListener());
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
    }

    // This comparator is used to sort vectors of data
    class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        @Override
        public int compare(Object a, Object b) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);
            // Treat empty strains like nulls
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
            // Sort nulls so they appear last, regardless // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable)o1).compareTo(o2);
                } else {
                    return ((Comparable)o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    }

    class ColumnHeaderListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            JTable table = ((JTableHeader)evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();
            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            //int mColIndex = table.convertColumnIndexToModel(vColIndex);
            // Return if not clicked on any column header
            if (vColIndex == -1) { return; }
            else if(vColIndex == 0 || vColIndex == 4 || vColIndex == 5){
                // Sort all the rows in descending order based on the
                // values in the clicked column of the model
                sortAllRowsBy((DefaultTableModel) table.getModel(), vColIndex, false);
            }
            /*
            // Determine if mouse was clicked between column heads
            Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
            if (vColIndex == 0) {
                headerRect.width -= 3; // Hard-coded constant
            } else {
                headerRect.grow(-3, 0); // Hard-coded constant
            }
            if (!headerRect.contains(evt.getX(), evt.getY())) {
                
                // Mouse was clicked between column heads
                // vColIndex is the column head closest to the click
                // vLeftColIndex is the column head to the left of the click
                int vLeftColIndex = vColIndex;
                if (evt.getX() < headerRect.x) {
                    vLeftColIndex--;
                }
            }*/
        }
        // Regardless of sort order (ascending or descending), null values always appear last.
        // colIndex specifies a column in model.
        private void sortAllRowsBy(DefaultTableModel model, int colIndex, boolean ascending) {
            Vector data = model.getDataVector();
            Collections.sort(data, new ColumnSorter(colIndex, ascending));
            model.fireTableStructureChanged();
        }
    }
}