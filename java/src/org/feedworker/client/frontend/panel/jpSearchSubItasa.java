package org.feedworker.client.frontend.panel;

import javax.swing.JPanel;

/**
 *
 * @author luca
 */
public class jpSearchSubItasa extends JPanel{
    private JPanel jpParams;
    
    public jpSearchSubItasa(){
        super();
        setName("Search sub Itasa");
        initPaneParams();
    }
    
    private void initPaneParams(){
        jpParams = new JPanel();
    }

}