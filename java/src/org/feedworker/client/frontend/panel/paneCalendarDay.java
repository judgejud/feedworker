package org.feedworker.client.frontend.panel;

import java.util.ArrayList;
import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.component.canvasCalendar;
import org.feedworker.client.frontend.events.CanvasEvent;
import org.feedworker.client.frontend.events.CanvasEventListener;

/**
 *
 * @author luca judge
 */
public class paneCalendarDay extends paneAbstract implements CanvasEventListener{
    
    private static paneCalendarDay jpanel = null;
    
    private paneCalendarDay(){
        super("CalendarDay");
        initializePanel();
        initializeButtons();
        GuiCore.getInstance().setCanvasListener(this);
    }

    public static paneCalendarDay getPanel(){
        if (jpanel==null)
            jpanel = new paneCalendarDay();
        return jpanel;
    }

    @Override
    void initializePanel() {}

    @Override
    void initializeButtons() {
        remove(jpAction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void objReceived(CanvasEvent evt) {
        ArrayList<String> date = (ArrayList<String>) evt.getArray()[0];
        ArrayList<ArrayList<String>> shows = (ArrayList<ArrayList<String>>) evt.getArray()[1];
        jpCenter.add(new canvasCalendar(date, shows));
    }
}