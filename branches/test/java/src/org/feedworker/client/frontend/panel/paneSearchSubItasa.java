package org.feedworker.client.frontend.panel;

import javax.swing.JPanel;

/**
 *
 * @author luca
 */
public class paneSearchSubItasa extends JPanel{
    private JPanel jpParams;
    
    public paneSearchSubItasa(){
        super();
        setName("Search sub Itasa");
        initPaneParams();
    }
    
    private void initPaneParams(){
        jpParams = new JPanel();
    }

}