package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.feedworker.client.frontend.jtpLog;
/**
 *
 * @author luca
 */
public class paneLog extends paneAbstract{
    
    private jtpLog logWest, logEast;
    
    public paneLog(){
        super();
        setName("Log");
        initializePanel();
        proxy.setTextPaneListener(logEast);
        proxy.setTextPaneListener(logWest);
        setVisible(true);
    }

    @Override
    void initializePanel() {
        logWest = new jtpLog(true);
        JScrollPane jspWest = new JScrollPane(logWest);
        jspWest.setPreferredSize(TABLE_SCROLL_SIZE);
        jspWest.setAutoscrolls(true);
        jpCenter.add(jspWest);
        
        jpCenter.add(RIGID_AREA);
        
        logEast = new jtpLog(false);
        JScrollPane jspEast = new JScrollPane(logEast);
        jspEast.setPreferredSize(TABLE_SCROLL_SIZE);
        jspEast.setAutoscrolls(true);
        jpCenter.add(jspEast);
        
        add(jpCenter, BorderLayout.CENTER);
    }
    
    public void cleanLogs(){
        logEast.setText(null);
        logWest.setText(null);
    }

    @Override
    void initializeButtons() {}
}