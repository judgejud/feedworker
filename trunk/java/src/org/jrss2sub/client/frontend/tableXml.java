package org.jrss2sub.client.frontend;
//IMPORT JAVA
import java.awt.Component;
import java.awt.Font;
//IMPORT JAVAX
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
//IMPORT JRSS2SUB
import org.jrss2sub.client.frontend.events.TableXmlEvent;
import org.jrss2sub.client.frontend.events.TableXmlEventListener;
//IMPORT MYUTILS
import org.lp.myUtils.Swing;
/**
 *
 * @author luca
 */
class tableXml extends JTable implements TableXmlEventListener{
    private final String[] nameCols = {"Serie", "Stagione", "Versione", "Destinazione"};
    private final String[] itemsCombo = Mediator.getIstance().getElemEnum();
    private final Font font = new Font("Arial", Font.PLAIN, 10);
    /**Costruttore*/
    public tableXml() {
        super();
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols);
        setModel(dtm);

        getTableHeader().setReorderingAllowed(false);

        Swing.setTableDimensionLockColumn(this, 0, 270);
        Swing.setTableDimensionLockColumn(this, 1, 55);
        Swing.setTableDimensionLockColumn(this, 2, 60);
        Swing.setTableDimensionLockColumn(this, 3, 270);
        // Set the combobox editor on the 1st visible column
        TableColumn col = getColumnModel().getColumn(2);
        col.setCellEditor(new MyComboBoxEditor(itemsCombo));
        // If the cell should appear like a combobox in its
        // non-editing state, also set the combobox renderer
        col.setCellRenderer(new MyComboBoxRenderer(itemsCombo));
        setFont(font);
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
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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

    class MyComboBoxEditor extends DefaultCellEditor {
        public MyComboBoxEditor(String[] items) {
            super(new JComboBox(items));
        }
    }
}