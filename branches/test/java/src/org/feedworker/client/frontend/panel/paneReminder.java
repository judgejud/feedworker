package org.feedworker.client.frontend.panel;

/**
 *
 * @author luca
 */
public class paneReminder extends paneAbstract{
    
    private static paneReminder jpanel = null;
    //private jtSubtitleDest jtable;

    public static paneReminder getPanel() {
        if (jpanel == null)
            jpanel = new paneReminder();
        return jpanel;
    }

    private paneReminder() {
        super();
        setName("Reminder");
        initializePanel();
        initializeButtons();
        //initListeners();
        setVisible(true);
    }

    @Override
    void initializePanel() {
        
    }

    @Override
    void initializeButtons() {
        
    }

}