package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.feedworker.client.frontend.table.tableReminder;

/**
 *
 * @author luca
 */
public class paneReminder extends paneAbstract{
    
    private static paneReminder jpanel = null;
    private tableReminder jtable;

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
        proxy.setTableListener(jtable);
        setVisible(true);
    }

    @Override
    void initializePanel() {
        jtable = new tableReminder("Reminder");
        JScrollPane jsp = new JScrollPane(jtable);
        this.add(jsp, BorderLayout.WEST);
    }

    @Override
    void initializeButtons() {
        
    }

}