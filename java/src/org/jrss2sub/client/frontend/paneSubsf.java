package org.jrss2sub.client.frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 *
 * @author luca
 */
class paneSubsf extends paneAbstract{
    private static paneSubsf jpanel = null;
    private JButton jbDown, jbClean;
    private tableRss jtable;

    private paneSubsf(){
        super();
        initPane();
        initButtons();
        proxy.setTableRssListener(jtable);
    }

    public static paneSubsf getPanel(){
        if (jpanel==null)
            jpanel = new paneSubsf();
        return jpanel;
    }

    @Override
    void initPane() {
        jtable = new tableRss(proxy.getSubsf());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setPreferredSize(TABLESCROLLSIZE);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.WEST);
        setVisible(true);
    }

    @Override
    void initButtons() {
        jbDown = new JButton(" Download ");
        jbDown.setToolTipText("Scarica i sub selezionati");
        jbDown.setBorder(BORDER);
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDownMouseClicked();
            }
        });

        jbClean = new JButton(" Pulisci ");
        jbClean.setToolTipText("Pulisce le righe selezionate");
        jbClean.setBorder(BORDER);        
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.cleanSelect(jtable);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = BUTTONSPACEINSETS;
        jpButton.add(jbDown, gbc);
        gbc.gridx = 1;
        jpButton.add(jbClean, gbc);
        gbc.gridwidth=3;
        gbc.gridx=2;
        JLabel jlTemp = new JLabel();
        jlTemp.setPreferredSize(new Dimension(800, 20));
        jpButton.add(jlTemp, gbc);
        add(jpButton, BorderLayout.NORTH);
    }

    private void jbDownMouseClicked(){
        if (jbDown.isEnabled())
            proxy.downloadSub(jtable, null, false);
    }

    public void setEnableButton(boolean e){
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
    }   
}