package org.feedworker.client.frontend.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.feedworker.client.frontend.Mediator;
import org.feedworker.client.frontend.events.TableEvent;

import org.jfacility.javax.swing.Swing;

/**
 *
 * @author luca
 */
public class tableCalendar extends tableAbstract{
    private final String[] nameCols = {"ID tvrage", "Serie", "Stato", "Giorno", 
                                        "Last Ep", "Titolo", "Data", 
                                        "Next Ep", "Titolo", "Data" };

    public tableCalendar(String nome){
        super(nome);
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {

            Class[] types = new Class[]{String.class, String.class, Integer.class, 
                            String.class, String.class, String.class, Date.class, 
                            String.class, String.class, Date.class};

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
        setRowSelectionAllowed(true);
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

    @Override
    public void objReceived(TableEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            int size = evt.getArray().size();
            int start = dtm.getRowCount();
            for (int i = 0; i < size; i++)
                dtm.insertRow(i+start, evt.getArray().get(i));
            getColumn(nameCols[1]).setCellRenderer(new ColorRenderer());
        }
    }

    @Override
    protected void lockColumns() {
        Swing.setTableDimensionLockColumn(this, 0, -1); //id tvrage
        Swing.setTableDimensionLockColumn(this, 2, -1); //stato
        Swing.setTableDimensionLockColumn(this, 3, 70);
        Swing.setTableDimensionLockColumn(this, 4, 65); //last ep
        Swing.setTableDimensionLockColumn(this, 6, 85); //data last
        Swing.setTableDimensionLockColumn(this, 7, 65); //next ep
        Swing.setTableDimensionLockColumn(this, 9, 85); //data next
    }
    
    class ColorRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent (JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column){
            setText(value.toString());
            setFont(font);
            setOpaque(true);
            int status; 
            try{
                String text = table.getValueAt(row, 2).toString();
                status = (Integer.parseInt(text));
            } catch (NumberFormatException npe){
                status=-1;
            }
            if (status==0){
                setBackground(Color.red);
                setToolTipText("Serie cancellata/terminata");
            } else if (status==1){
                setBackground(Color.orange);
                setToolTipText("Ultima stagione");
            } else if (status==2){
                setBackground(Color.green);
                setToolTipText("Nuova serie");
            } else {
                setBackground(table.getBackground());
                setToolTipText(null);
            }
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