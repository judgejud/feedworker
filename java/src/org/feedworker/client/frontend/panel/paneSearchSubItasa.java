package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.feedworker.client.frontend.table.tableResultSearchSub;

/**
 *
 * @author luca
 */
public class paneSearchSubItasa extends paneAbstract{
    private static paneSearchSubItasa jpanel = null;
    
    private JComboBox jcbShow, jcbVersion;
    private JCheckBox jcbComplete;
    private JTextField jtfSeason, jtfEpisode;
    private tableResultSearchSub jtable;
    
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
        //TODO: cambiare null col nome
        jtable = new tableResultSearchSub(null);
        JScrollPane jsp = new JScrollPane(jtable);
        JPanel jpTemp = new JPanel(new BorderLayout());
        jpTemp.add(jsp, BorderLayout.CENTER);
        JPanel jpButton = new JPanel();
        jpTemp.add(jpButton, BorderLayout.SOUTH);
        jpCenter.add(jpTemp, BorderLayout.CENTER);
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
        
        int x = 0;
        jpAction.add(new JLabel("Show"), gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jcbShow, gbcAction); 
        
        gbcAction.gridx = ++x;
        jpAction.add(new JLabel("Versione"), gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jcbVersion, gbcAction);
        
        gbcAction.gridx = ++x;
        jpAction.add(jcbComplete, gbcAction);
        
        gbcAction.gridx = ++x;
        jpAction.add(new JLabel("Stagione"), gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jtfSeason, gbcAction);
        
        gbcAction.gridx = ++x;
        jpAction.add(new JLabel("Episodio"), gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jtfEpisode, gbcAction);
        
        gbcAction.gridx = ++x;
        jpAction.add(jbSearch, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbClean, gbcAction);
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