package org.feedworker.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.feedworker.util.Common;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
/**
 *
 * @author luca judge
 */
public class IcsParser {
    
    private Calendar calendar;
    
    public IcsParser(FileInputStream fis) throws ParserException, IOException{
        calendar = new CalendarBuilder().build(fis);
    }

    public Object[] getData(){
        ArrayList<String> date = new ArrayList<String>();
        ArrayList<String> shows = new ArrayList<String>();
        ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
        String show = null;
        Date newDay = null, oldDay=null;
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            if (component.getName().equals("VEVENT")){
                for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                    Property property = (Property) j.next();
                    if (property.getName().equals("SUMMARY"))
                        show = property.getValue();
                    else if (property.getName().equals("DTSTART")){
                        try {
                            newDay = Common.stringIcsToDate(property.getValue());
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        shows.add(show);
                        break;
                    }
                }
                if (!newDay.equals(oldDay)){
                    date.add(Common.dateIcsToString(newDay));
                    matrix.add(shows);
                    oldDay = newDay;
                    shows = new ArrayList<String>();
                }
            }
        }
        return new Object[]{date, matrix};
    }
}