package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.jtpLog;
/**
 *
 * @author luca
 */
public class paneLog extends paneAbstract{
    
    private jtpLog logWest, logEast;
    
    public paneLog(){
        super("Log");
        initializePanel();
        initializeButtons();
        core.setTextPaneListener(logEast);
        core.setTextPaneListener(logWest);
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
    }

    @Override
    void initializeButtons() {
        JButton jbCleanWest = new JButton(core.getIconClean1());
        jbCleanWest.setToolTipText("Pulisci log sinistro");
        jbCleanWest.setBorder(BORDER);
        jbCleanWest.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                logWest.setText(null);
            }
        });
        
        JButton jbCleanEast = new JButton(core.getIconClean2());
        jbCleanEast.setToolTipText("Pulisci log destro");
        jbCleanEast.setBorder(BORDER);
        jbCleanEast.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                logEast.setText(null);
            }
        });
        
        jpAction.add(jbCleanWest,gbcAction);
        gbcAction.gridx = 1;
        jpAction.add(jbCleanEast,gbcAction);
    }    
}