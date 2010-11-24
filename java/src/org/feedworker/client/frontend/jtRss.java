package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.feedworker.client.frontend.events.TableRssEvent;
import org.feedworker.client.frontend.events.TableRssEventsListener;
import org.jfacility.javax.swing.Swing;

/**
 * 
 * @author luca
 */
class jtRss extends JTable implements TableRssEventsListener {
    // PRIVATE FINAL VARIABLE

    private final String[] columnNames = {"link", "Data", "Sottotitolo",
        "Select"};
    private final int width = 500;
    // PRIVATE VARIABLE
    private int[] lastFeedSize = {0, 0};
    private Mediator proxy = Mediator.getIstance();

    /**
     *
     * @param name
     */
    public jtRss(String name) {
        super();
        setName(name);
        DefaultTableModel dtm = new DefaultTableModel(null, columnNames) {

            Class[] types = new Class[]{String.class, String.class,
                String.class, Boolean.class};

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

        Swing.setTableDimensionLockColumn(this, 0, -1);
        Swing.setTableDimensionLockColumn(this, 1, 110);
        Swing.setTableDimensionLockColumn(this, 2, 330);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                if (getSelectedColumn() >= 2) {
                    int row = getSelectedRow();
                    if (Boolean.parseBoolean(getValueAt(row, 3).toString()) == true) {
                        setValueAt(false, row, 3);
                    } else {
                        setValueAt(true, row, 3);
                    }
                }
            }
        });
    }

    @Override
    public void objReceived(TableRssEvent evt) {
        if (this.getName().equalsIgnoreCase(evt.getNameTableDest())) {
            DefaultTableModel dtm = (DefaultTableModel) getModel();
            String titleCol = (String) this.getColumnModel().getColumn(2).getHeaderValue();
            String dateCol = (String) this.getColumnModel().getColumn(1).getHeaderValue();
            int size = evt.getObjs().size();
            lastFeedSize[1] = lastFeedSize[0];
            lastFeedSize[0] = size;
            int lenght = dtm.getRowCount() + size;
            this.setPreferredSize(new Dimension(width, (16 * lenght)));
            for (int i = 0; i < size; i++) {
                dtm.insertRow(i, evt.getObjs().get(i));
            }
            this.getColumn(titleCol).setCellRenderer(new JLabelTitleRenderer());
            this.getColumn(dateCol).setCellRenderer(
                    new JLabelDateRenderer(lastFeedSize[0], lastFeedSize[1]));
        }
    }

    public void setTitleDescriptionColumn(String _name) {
        this.getColumnModel().getColumn(2).setHeaderValue(_name);
    }

    public void removeAllRows() {
        DefaultTableModel dtm = (DefaultTableModel) getModel();
        int size = dtm.getRowCount();
        for (int i = 0; i < size; i++) {
            dtm.removeRow(0);
        }
    }

    class JLabelDateRenderer extends JLabel implements TableCellRenderer {

        private final Font font = new Font("Arial", Font.PLAIN, 10);
        private int rowsize0, rowsize1;

        public JLabelDateRenderer(int rowsize0, int rowsize1) {
            this.rowsize0 = rowsize0;
            this.rowsize1 = rowsize1;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            // imposta il testo della cella
            String text = value.toString();
            setText(text);
            setFont(font);
            setOpaque(true);
            if (row < rowsize0) {
                setBackground(Color.orange);
            } else if (row >= rowsize0 && row < rowsize0 + rowsize1) {
                setBackground(Color.lightGray);
            } else {
                setBackground(Color.white);
            }
            this.repaint();
            return this;
        }
    } // end class JLabelRenderer

    /**
     * Classe che restituisce la jlabel della cella tabella con determinati
     * colori e font di testo ed eventuale tooltip se testo lungo
     */
    class JLabelTitleRenderer extends JLabel implements TableCellRenderer {

        private final Font font = new Font("Arial", Font.PLAIN, 10);

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
                    || back.equals(new Color(183, 65, 14))) {
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
