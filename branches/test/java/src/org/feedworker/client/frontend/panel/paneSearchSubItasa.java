package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ComboBoxModel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.feedworker.client.frontend.events.ComboboxEvent;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.table.tableResultSearchSub;

/**
 *
 * @author luca
 */
public class paneSearchSubItasa extends paneAbstract implements ComboboxEventListener{
    private static paneSearchSubItasa jpanel = null;
    
    private JComboBox jcbShow, jcbVersion;
    private JCheckBox jcbComplete;
    private JTextField jtfSeason, jtfEpisode;
    private tableResultSearchSub jtable;
    private JPanel jpButton;
    
    private paneSearchSubItasa(){
        super("Search sub Itasa");
        initializePanel();
        initializeButtons();
        core.setTableListener(jtable);
        core.setComboboxListener(this);
    }
    
    public static paneSearchSubItasa getPanel(){
        if (jpanel==null)
            jpanel = new paneSearchSubItasa();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtable = new tableResultSearchSub(proxy.getNameTableSearchSub());
        JScrollPane jsp = new JScrollPane(jtable);
        JPanel jpTemp = new JPanel(new BorderLayout());
        jpTemp.add(jsp, BorderLayout.CENTER);
        jpButton = new JPanel(new GridBagLayout());
        jpButton.setPreferredSize(new Dimension(1000, 30));
        jpTemp.add(jpButton, BorderLayout.NORTH);
        jpCenter.add(jpTemp);
    }

    @Override
    void initializeButtons() {
        jcbShow = new JComboBox();
        jcbVersion = new JComboBox(proxy.getQualityEnum());
        jcbVersion.removeItemAt(jcbVersion.getItemCount()-1);
        jcbComplete = new JCheckBox("Stagione completa");
        jtfSeason = new JTextField(3);
        jtfEpisode = new JTextField(3);
        
        JButton jbSearch = new JButton(" Ricerca ");
        jbSearch.setBorder(BORDER);
        jbSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbSearchMouseClicked();
            }
        });
        JButton jbReset = new JButton(" Reset ");
        jbReset.setBorder(BORDER);
        jbReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbResetMouseClicked();
            }
        });
        JButton jbDownload = new JButton(" Download ");
        jbDownload.setBorder(BORDER);
        jbDownload.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.downloadSub(jtable, null, true, true);
                proxy.cleanSelect(jtable,3);
            }
        });
        JButton jbPulisci = new JButton(" Pulisci ");
        jbPulisci.setBorder(BORDER);
        jbPulisci.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.cleanSelect(jtable, 3);
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
        jpAction.add(jbReset, gbcAction);
        
        gbcAction.gridx = 0;
        jpButton.add(jbDownload, gbcAction);
        gbcAction.gridx = 1;
        jpButton.add(jbPulisci, gbcAction);
    }
    
    private void jbResetMouseClicked(){
        jcbShow.setSelectedItem(null);
        jcbVersion.setSelectedIndex(0);
        jcbComplete.setSelected(false);
        jtfSeason.setText(null);
        jtfEpisode.setText(null);
    }
    
    private void jbSearchMouseClicked(){
        if (jcbShow.getSelectedItem()!=null){
            jtable.removeAllRows();
            proxy.searchSubItasa(jcbShow.getSelectedItem(), jcbVersion.getSelectedItem(), 
                                    jcbComplete.isSelected(), jtfSeason.getText(),
                                    jtfEpisode.getText());
        } else
            proxy.printAlert("Lo show è obbligatorio per la ricerca");
    }

    @Override
    public void objReceived(ComboboxEvent evt) {
        Object[] array = evt.getArray(); 
        for (int i=0; i<array.length; i++)
            jcbShow.addItem(array[i]);
        jcbShow.addItem(null);
        jcbShow.setSelectedItem(null);
    }
}