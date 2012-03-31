package org.feedworker.client.frontend.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
        jpCenter.add(new JScrollPane(jtable));
    }

    @Override
    void initializeButtons() {
        jcbShow = new JComboBox();
        jcbShow.setMaximumRowCount(20);
        jcbVersion = new JComboBox(proxy.getQualityEnum());
        jcbVersion.removeItemAt(jcbVersion.getItemCount()-1);
        jcbComplete = new JCheckBox("Stagione completa");
        jtfSeason = new JTextField(3);
        jtfSeason.setMinimumSize(new Dimension(30,20));
        jtfEpisode = new JTextField(3);
        jtfEpisode.setMinimumSize(new Dimension(30,20));
        
        JButton jbSearch = new JButton(core.getIconSearch());
        jbSearch.setToolTipText("Ricerca");
        jbSearch.setBorder(BORDER);
        jbSearch.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbSearchMouseClicked();
            }
        });
        
        JButton jbReset = new JButton(core.getIconReset());
        jbReset.setToolTipText("Reset");
        jbReset.setBorder(BORDER);
        jbReset.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbResetMouseClicked();
            }
        });
        
        JButton jbDownload = new JButton(core.getIconDownload());
        jbDownload.setToolTipText("Download");
        jbDownload.setBorder(BORDER);
        jbDownload.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                core.downloadSub(jtable, null, true, true, jtable.getColumnCount()-1);
                core.cleanSelect(jtable, jtable.getColumnCount()-1);
            }
        });
        
        JButton jbPulisci = new JButton(core.getIconClean1());
        jbPulisci.setToolTipText("Pulisci");
        jbPulisci.setBorder(BORDER);
        jbPulisci.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                core.cleanSelect(jtable, jtable.getColumnCount()-1);
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
        
        gbcAction.gridx = ++x;
        jpAction.add(jbDownload, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbPulisci, gbcAction);
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
            core.searchSubItasa(jcbShow.getSelectedItem(), jcbVersion.getSelectedItem(), 
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