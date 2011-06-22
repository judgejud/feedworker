/**
 * http://www.danwalmsley.com/tag/sms-java-google-calendar-api-atom-app-service-monitoring/
 * http://code.google.com/intl/it/apis/calendar/data/2.0/developers_guide_java.html
 */
package org.feedworker.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Reminder.Method;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class GCalNotifierSms {
    
    /**
    * TimeZone offset (in hours)
    * event start offset (in minutes)
    * event end offset (in minutes)
    * title
    * description
    */
    public static void main(String[] args) {
        String user = "judgejud@gmail.com";
        String password = "pincopallino";
        String calendarName = "judge judge";
        String url_https = "https://www.google.com/calendar/feeds/default/allcalendars/full";
        long minutes = 60*1000;
        try {
            // Create a CalenderService and authenticate
            CalendarService myService = new CalendarService("GCal Event Notifier");
            myService.setUserCredentials(user, password);
            // Send the request and print the response
            URL metafeedUrl = new URL(url_https);
            CalendarFeed resultFeed = myService.getFeed(metafeedUrl, CalendarFeed.class);
            // Get a list of all entries
            List entries = resultFeed.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                CalendarEntry entry = (CalendarEntry) entries.get(i);
                String currCalendarName = entry.getTitle().getPlainText();
                if (currCalendarName.equals(calendarName)) {
                    Long tzOffset = 2 * 60 * minutes;
                    Long startOffset = 30 * minutes;
                    Long endOffset = 60 * minutes;
                    String title = "test";
                    String description = "ciao";
                    sendDowntimeAlert(myService, entry, title, description, 
                            startOffset, endOffset, tzOffset);
                    break;
                }
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendDowntimeAlert(CalendarService myService, CalendarEntry entry, 
                                            String title, String description, 
                                            Long startOffset, Long endOffset, Long tzOffset) 
                                        throws IOException, ServiceException {
        String postUrlString = entry.getLink("alternate", "application/atom+xml").getHref();
        //was: "http://www.google.com/calendar/feeds/jo@gmail.com/private/full"
        URL postUrl = new URL(postUrlString);

        CalendarEventEntry myEntry = new CalendarEventEntry();

        myEntry.setTitle(new PlainTextConstruct(title));
        myEntry.setContent(new PlainTextConstruct(description));

        Date now = new Date();

        Date startDate = new Date(now.getTime()+startOffset);
        Date endDate = new Date(now.getTime()+endOffset);

        DateTime startTime = new DateTime(startDate.getTime()+tzOffset);

        DateTime endTime = new DateTime(endDate.getTime()+tzOffset);

        When eventTimes = new When();
        eventTimes.setStartTime(startTime);
        eventTimes.setEndTime(endTime);
        myEntry.addTime(eventTimes);

        // Send the request and receive the response:
        CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
        for(When when : insertedEntry.getTimes()) {
            System.out.println("When: "+when.getStartTime()+" to "+when.getEndTime());
        }

        //5 minute reminder
        Reminder reminder = new Reminder();
        reminder.setMinutes(10);
        reminder.setMethod(Method.SMS);
        insertedEntry.getReminder().add(reminder);
        insertedEntry.update();
    }
}