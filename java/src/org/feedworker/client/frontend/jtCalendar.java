package org.feedworker.client.frontend;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.events.TableCalendarEvent;
import org.feedworker.client.frontend.events.TableCalendarEventListener;
import org.jfacility.javax.swing.Swing;

/**
 *
 * @author luca
 */
class jtCalendar extends JTable implements TableCalendarEventListener{    
    private final String[] nameCols = {"Serie", "Stato", "Giorno", "Last Episode", "Data",
                            "Titolo", "Next Episode", "Data", "Titolo"};
    private final Font font = new Font("Arial", Font.PLAIN, 10);
    private Mediator proxy = Mediator.getIstance();

    public jtCalendar(){
        super();
        DefaultTableModel dtm = new DefaultTableModel(null, nameCols) {

            Class[] types = new Class[]{String.class, String.class, String.class,
                String.class, String.class, String.class, String.class,
                String.class, String.class};

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        setModel(dtm);

        setFont(font);
        Swing.tableSorter(this);
    }

    @Override
    public void objReceived(TableCalendarEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}