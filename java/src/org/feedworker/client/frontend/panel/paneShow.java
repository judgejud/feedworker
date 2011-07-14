package org.feedworker.client.frontend.panel;

/**
 *
 * @author luca
 */
public class paneShow extends paneAbstract{
    
    private static paneShow jpanel = null;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
    }
    
    public static paneShow getPanel(){
        if (jpanel==null)
            jpanel = new paneShow();
        return jpanel;
    }

    @Override
    void initializePanel() {
        
    }

    @Override
    void initializeButtons() {
        
    }
}