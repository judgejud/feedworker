package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.feedworker.client.frontend.jtpLog;
/**
 *
 * @author luca
 */
public class jpLog extends jpAbstract{
    
    private jtpLog logWest, logEast;
    
    public jpLog(){
        super();
        initializePanel();
        proxy.setTextPaneListener(logWest);
        proxy.setTextPaneListener(logEast);
        setVisible(true);
    }

    @Override
    void initializePanel() {
        logWest = new jtpLog(true);
        logEast = new jtpLog(false);
        JScrollPane jspWest = new JScrollPane(logWest);
        JScrollPane jspEast = new JScrollPane(logEast);
        jspWest.setPreferredSize(TABLE_SCROLL_SIZE);
        jspWest.setAutoscrolls(true);
        jspEast.setPreferredSize(TABLE_SCROLL_SIZE);
        jspEast.setAutoscrolls(true);
        
        jpCenter.add(jspWest);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jspEast);
        add(jpCenter, BorderLayout.CENTER);
    }

    @Override
    void initializeButtons() {}
}