package org.feedworker.client.frontend.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.feedworker.client.frontend.events.TableEvent;
import org.jfacility.javax.swing.Swing;
/**
 *
 * @author luca
 */
public class tableResultSearchTvRage extends tableAbstract{
    private final String[] nameCols = {"ID","Serie", "Stagione", "Status", "Giorno"};

    public tableResultSearchTvRage(String name){
        super(name);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {
            Class[] types = new Class[]{Integer.class, String.class, String.class,
                String.class, String.class};
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
        setRowSelectionAllowed(true);
        getColumn(nameCols[1]).setCellRenderer(new JLabelCellTextRenderer());
        Swing.tableSorter(this);
    }
    
    @Override
    protected void lockColumns(){
        Swing.setTableDimensionLockColumn(this, 0, 45);
        Swing.setTableDimensionLockColumn(this, 2, 80);
        Swing.setTableDimensionLockColumn(this, 3, 90);
        Swing.setTableDimensionLockColumn(this, 4, 70);
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

    class JLabelCellTextRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if (isSelected){
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else{
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            String text = value.toString();
            setText(text);
            setToolTipText(Swing.getTextToolTip(table, column, this, text));
            setFont(font);
            setOpaque(true);
            this.repaint();
            return this;
        }
    } // end class JLabelRenderer
}