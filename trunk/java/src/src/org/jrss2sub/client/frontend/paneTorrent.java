package org.jrss2sub.client.frontend;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 *
 * @author luca
 */
class paneTorrent extends paneAbstract{
    private static paneTorrent jpanel = null;
    private JButton jbDown, jbCopyLinks, jbClean, jbFireNas;
    private tableRss jtTorrent1, jtTorrent2; 
        
    private paneTorrent(){
        super();
        initPane();
        initButtons();
        proxy.setTableRssListener(jtTorrent1);
        proxy.setTableRssListener(jtTorrent2);
    }

    static paneTorrent getPanel(){
        if (jpanel==null)
            jpanel = new paneTorrent();
        return jpanel;
    }

    @Override
    void initPane() {        
        jtTorrent1 = new tableRss(proxy.getEztv());
        jtTorrent1.setTitleDescriptionColumn("Descrizione Torrent EZTV");
        JScrollPane jsp1 = new JScrollPane(jtTorrent1);
        jsp1.setPreferredSize(TABLESCROLLSIZE);
        jsp1.setAutoscrolls(true);
        add(jsp1, BorderLayout.WEST);

        jtTorrent2 = new tableRss(proxy.getBtchat());
        jtTorrent2.setTitleDescriptionColumn("Descrizione Torrent BTCHAT");
        JScrollPane jsp2 = new JScrollPane(jtTorrent2);
        jsp2.setPreferredSize(TABLESCROLLSIZE);
        jsp2.setAutoscrolls(true);
        add(jsp2, BorderLayout.EAST);
    }

    @Override
    void initButtons() {
        jbDown = new JButton(" Download ");
        jbDown.setToolTipText("Scarica i .torrent selezionati");
        jbDown.setBorder(BORDER);
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDownMouseClicked();
            }
        });

        jbCopyLinks = new JButton(" Copia links ");
        jbCopyLinks.setToolTipText("Copia i link dei torrent selezionati nella clipboard");
        jbCopyLinks.setBorder(BORDER);
        jbCopyLinks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbCopyLinksMouseClicked();
            }
        });

        jbClean = new JButton(" Pulisci ");
        jbClean.setToolTipText("Pulisce le righe selezionate");
        jbClean.setBorder(BORDER);        
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.cleanSelect(jtTorrent1);
                proxy.cleanSelect(jtTorrent2);
            }
        });

        jbFireNas = new JButton(" invia Nas ");
        jbFireNas.setToolTipText("invia i link dei torrent selezionati alla download station NAS");
        jbFireNas.setBorder(BORDER);
        jbFireNas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbFireLinksMouseClicked();
            }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = BUTTONSPACEINSETS;
        jpButton.add(jbDown, gbc);
        gbc.gridx = 1;
        jpButton.add(jbCopyLinks, gbc);
        gbc.gridx = 2;
        jpButton.add(jbClean, gbc);
        gbc.gridx = 3;
        jpButton.add(jbFireNas, gbc);
        add(jpButton, BorderLayout.NORTH);
    }

    private void jbDownMouseClicked(){
        if (jbDown.isEnabled())
            proxy.downloadTorrent(jtTorrent1, jtTorrent2);
    }

    private void jbCopyLinksMouseClicked(){
        if (jbCopyLinks.isEnabled())
            proxy.copyLinkTorrent(jtTorrent1, jtTorrent2);
    }

    private void jbFireLinksMouseClicked(){
        if (jbFireNas.isEnabled())
            proxy.fireTorrentToNas(jtTorrent1, jtTorrent2);
    }

    void setButtonEnabled(boolean e){
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
        jbCopyLinks.setEnabled(e);
    }
}