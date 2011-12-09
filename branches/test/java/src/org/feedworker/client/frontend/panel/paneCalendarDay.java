package org.feedworker.client.frontend.panel;

import org.feedworker.client.frontend.component.canvasCalendar;

/**
 *
 * @author luca judge
 */
public class paneCalendarDay extends paneAbstract{
    
    private static paneCalendarDay jpanel = null;
    
    private paneCalendarDay(){
        super("CalendarDay");
        //TODO
        initializePanel();
        initializeButtons();
    }

    public static paneCalendarDay getPanel(){
        if (jpanel==null)
            jpanel = new paneCalendarDay();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jpCenter.add(new canvasCalendar());
    }

    @Override
    void initializeButtons() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}