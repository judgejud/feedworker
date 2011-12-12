package org.feedworker.client.frontend.panel;

/**
 *
 * @author Administrator
 */
public class paneBlog extends paneAbstract{
    
    private static paneBlog jpanel = null;
    
    private paneBlog() {
        super("Blog");
        initializePanel();
        initializeButtons();
    }
    
    public static paneBlog getPanel(){
        if (jpanel==null)
            jpanel = new paneBlog();
        return jpanel;
    }

    @Override
    void initializePanel() {
        
    }

    @Override
    void initializeButtons() {
        remove(jpAction);
    }
}