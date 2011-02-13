package org.feedworker.client.frontend.table;

//IMPORT JAVA
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.feedworker.client.frontend.Mediator;
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
    private final String[] columnNames = {"Data", "Sottotitolo", "Select"};
    private final int width = 500;
    // PRIVATE VARIABLE
    private Mediator proxy = Mediator.getIstance();

    /**
     *
     * @param name
     */
    public tableReminder(String name) {
        super();
        setName(name);
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
        //TODO
    }

    public void removeAllRows() {
        DefaultTableModel dtm = (DefaultTableModel) getModel();
        int size = dtm.getRowCount();
        for (int i = 0; i < size; i++) {
            dtm.removeRow(0);
        }
    }    

    /**
     * Classe che restituisce la jlabel della cella tabella con determinati
     * colori e font di testo ed eventuale tooltip se testo lungo
     */
    class JLabelTitleRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            // imposta il testo della cella
            String text = value.toString();
            setText(text);
            setToolTipText(Swing.getTextToolTip(table, column, this, text));
            // imposta il font della cella
            setFont(font);

            setBackground(proxy.searchVersion(text));
            Color back = getBackground();
            if (back.equals(Color.blue) || back.equals(Color.red) 
                || back.equals(Color.black) || back.equals(new Color(183, 65, 14)) 
                || back.equals(Color.darkGray)) {        
                setForeground(Color.white);
            } else {
                setForeground(Color.black);
            }
            setOpaque(true);
            this.repaint();
            return this;
        }
    } // end class JLabelRenderer
}