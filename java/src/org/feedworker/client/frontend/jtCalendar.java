package org.feedworker.client.frontend;

import javax.swing.JTable;
import org.feedworker.client.frontend.events.TableCalendarEvent;
import org.feedworker.client.frontend.events.TableCalendarEventListener;

/**
 *
 * @author luca
 */
class jtCalendar extends JTable implements TableCalendarEventListener{




    @Override
    public void objReceived(TableCalendarEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}