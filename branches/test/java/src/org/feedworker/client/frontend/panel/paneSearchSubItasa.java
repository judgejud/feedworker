package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author luca
 */
public class paneSearchSubItasa extends paneAbstract{
    private static paneSearchSubItasa jpanel = null;
    
    private JComboBox jcbShow, jcbVersion;
    private JCheckBox jcbComplete;
    private JTextField jtfSeason, jtfEpisode;
    
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
        jcbShow.addItem(null);
        jcbShow.setSelectedItem(null);
        jcbVersion = new JComboBox(proxy.getQualityEnum());
        jcbVersion.removeItemAt(jcbVersion.getItemCount()-1);
        jcbComplete = new JCheckBox("Stagione completa");
        jtfSeason = new JTextField(3);
        jtfEpisode = new JTextField(3);
        
        JButton jbSearch = new JButton("Ricerca");
        jbSearch.setBorder(BORDER);
        jbSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbSearchMouseClicked();
            }
        });
        JButton jbClean = new JButton(" Pulisci ");
        jbClean.setBorder(BORDER);
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbCleanMouseClicked();
            }
        });
        
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
        
        gbc.gridx = ++x;
        jpAction.add(jcbComplete, gbc);
        
        gbc.gridx = ++x;
        jpAction.add(new JLabel("Stagione"), gbc);
        gbc.gridx = ++x;
        jpAction.add(jtfSeason, gbc);
        
        gbc.gridx = ++x;
        jpAction.add(new JLabel("Episodio"), gbc);
        gbc.gridx = ++x;
        jpAction.add(jtfEpisode, gbc);
        
        gbc.gridx = ++x;
        jpAction.add(jbSearch, gbc);
        gbc.gridx = ++x;
        jpAction.add(jbClean, gbc);
        
        add(jpAction, BorderLayout.NORTH);
    }
    
    private void jbCleanMouseClicked(){
        jcbShow.setSelectedItem(null);
        jcbVersion.setSelectedIndex(0);
        jcbComplete.setSelected(false);
        jtfSeason.setText(null);
        jtfEpisode.setText(null);
    }
    
    private void jbSearchMouseClicked(){
        if (jcbShow.getSelectedItem()!=null){
            proxy.searchSubItasa(jcbShow.getSelectedItem(), jcbVersion.getSelectedItem(), 
                                    jcbComplete.isSelected(), jtfSeason.getText(),
                                    jtfEpisode.getText());
        } else
            proxy.printAlert("Lo show è obbligatorio per la ricerca");
    }
}