package org.feedworker.client.frontend.table;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableEvent;
import org.feedworker.client.frontend.events.TableEventListener;

/**
 *
 * @author luca
 */
abstract class tableAbstract extends JTable implements TableEventListener {

    protected final Font font = new Font("Arial", Font.PLAIN, 11);

    public tableAbstract(String name) {
        super();
        setName(name);
        setFont(font);
        setFocusable(false);
        setRowSelectionAllowed(false);
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    abstract public void objReceived(TableEvent evt);

    abstract protected void lockColumns();

    public void removeAllRows() {
        ((DefaultTableModel) getModel()).setRowCount(0);
    }
    
    protected void setMouseClickBoolean(final int col, final int min_col){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (getSelectedColumn() >= min_col) {
                    int row = getSelectedRow();
                    if (Boolean.parseBoolean(getValueAt(row, col).toString()) == true)
                        setValueAt(false, row, col);
                    else
                        setValueAt(true, row, col);
                }
            }
        });
    }
}