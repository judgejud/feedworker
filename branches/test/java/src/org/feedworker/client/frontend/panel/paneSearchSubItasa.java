package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 *
 * @author luca
 */
public class paneSearchSubItasa extends paneAbstract{
    private static paneSearchSubItasa jpanel = null;
    
    private JComboBox jcbShow, jcbVersion;
    
    private paneSearchSubItasa(){
        super("Search sub Itasa");
        initializePanel();
        initializeButtons();
    }
    
    public static paneSearchSubItasa getPanel(){
        if (jpanel==null)
            jpanel = new paneSearchSubItasa();
        return jpanel;
    }

    @Override
    void initializePanel() {
        
    }

    @Override
    void initializeButtons() {
        jcbShow = new JComboBox(proxy.showList());
        jcbVersion = new JComboBox(proxy.getQualityEnum());
        
        GridBagConstraints gbc = new GridBagConstraints();
        int x = -1;
        gbc.gridx = ++x;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = BUTTON_SPACE_INSETS;
        
        jpAction.add(new JLabel("Show"), gbc);
        gbc.gridx = ++x;
        jpAction.add(jcbShow, gbc); 
        
        gbc.gridx = ++x;
        jpAction.add(new JLabel("Versione"), gbc);
        gbc.gridx = ++x;
        jpAction.add(jcbVersion, gbc);
        
        add(jpAction, BorderLayout.NORTH);
    }
}