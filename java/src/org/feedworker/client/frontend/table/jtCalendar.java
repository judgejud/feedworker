package org.feedworker.client.frontend.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
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
public class jtCalendar extends JTable implements TableEventListener{
    private final String[] nameCols = {"ID tvrage", "ID Itasa", "ID Tvdb", "Serie", 
                                        "Stato", "Giorno", "Last Ep", "Titolo", 
                                        "Data", "Next Ep", "Titolo", "Data" };
    private final Font font = new Font("Arial", Font.PLAIN, 10);

    public jtCalendar(String nome){
        super();
        setName(nome);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {

            Class[] types = new Class[]{String.class, String.class, String.class,
                            String.class, String.class, String.class, String.class, 
                            String.class, Date.class, String.class, String.class, 
                            Date.class};

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
        lockColumns();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                Mediator.getIstance().printStatusBar("righe selezionate: "+ 
                                                        getSelectedRowCount());
            }
        });
    }
    
    private void lockColumns(){
        Swing.setTableDimensionLockColumn(this, 0, -1); //id tvrage
        Swing.setTableDimensionLockColumn(this, 1, -1); //id itasa
        Swing.setTableDimensionLockColumn(this, 2, -1); //id tvdb
        Swing.setTableDimensionLockColumn(this, 4, 100);
        Swing.setTableDimensionLockColumn(this, 5, 70);
        Swing.setTableDimensionLockColumn(this, 6, 70);
        Swing.setTableDimensionLockColumn(this, 8, 65);
        Swing.setTableDimensionLockColumn(this, 9, 75);
        Swing.setTableDimensionLockColumn(this, 11, 65);
    }

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            String titleCol = (String) this.getColumnModel().getColumn(3).getHeaderValue();
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            int size = evt.getArray().size();
            int start = dtm.getRowCount();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i+start, evt.getArray().get(i));
            getColumn(titleCol).setCellRenderer(new ColorRenderer());
        }
    }
    
    class ColorRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent (JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column){
            setText(value.toString());
            setFont(font);
            setOpaque(true);
            
            String text = (String) table.getValueAt(row, 4);
            if(text.equalsIgnoreCase("final season") || 
                    text.equalsIgnoreCase("canceled/ended"))
                setBackground(Color.red);
            else 
                setBackground(table.getBackground());
            Color back = getBackground();
            if (isSelected){
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(back);
                setForeground(Color.black);
            }
            repaint();
            return this;
        }
    }
}