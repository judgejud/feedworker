package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.table.jtRss;
/**
 * 
 * @author luca
 */
public class paneTorrent extends paneAbstract {

    private static paneTorrent jpanel = null;
    private JButton jbDown, jbCopyLinks, jbClean, jbFireNas;
    private jtRss jtTorrent1, jtTorrent2;

    private paneTorrent() {
        super();
        setName("Torrent");
        initializePanel();
        initializeButtons();
        proxy.setTableListener(jtTorrent1);
        proxy.setTableListener(jtTorrent2);
    }

    public static paneTorrent getPanel() {
        if (jpanel == null) {
            jpanel = new paneTorrent();
        }
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtTorrent1 = new jtRss(proxy.getEztv());
        jtTorrent1.setTitleDescriptionColumn("Descrizione Torrent EZTV");
        JScrollPane jsp1 = new JScrollPane(jtTorrent1);
        jsp1.setPreferredSize(TABLE_SCROLL_SIZE);
        jsp1.setAutoscrolls(true);

        jtTorrent2 = new jtRss(proxy.getBtchat());
        jtTorrent2.setTitleDescriptionColumn("Descrizione Torrent BTCHAT");
        JScrollPane jsp2 = new JScrollPane(jtTorrent2);
        jsp2.setPreferredSize(TABLE_SCROLL_SIZE);
        jsp2.setAutoscrolls(true);
        
        jpCenter.add(jsp1);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jsp2);
        add(jpCenter, BorderLayout.CENTER);
    }

    @Override
    void initializeButtons() {
        jbDown = new JButton(" Download ");
        jbDown.setToolTipText("Scarica i .torrent selezionati");
        jbDown.setBorder(BORDER);
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jbDown.isEnabled())
                    proxy.downloadTorrent(jtTorrent1, jtTorrent2);
            }
        });

        jbCopyLinks = new JButton(" Copia links ");
        jbCopyLinks.setToolTipText("Copia i link dei torrent selezionati nella "
                                + "clipboard");
        jbCopyLinks.setBorder(BORDER);
        jbCopyLinks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jbCopyLinks.isEnabled())
                    proxy.copyLinkTorrent(jtTorrent1, jtTorrent2);
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
        jbFireNas.setToolTipText("invia i link dei torrent selezionati alla "
                            + "download station NAS"); 
        jbFireNas.setBorder(BORDER);
        jbFireNas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jbFireNas.isEnabled())
                    proxy.fireTorrentToNas(jtTorrent1, jtTorrent2);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = BUTTON_SPACE_INSETS;
        jpAction.add(jbDown, gbc);
        gbc.gridx = 1;
        jpAction.add(jbCopyLinks, gbc);
        gbc.gridx = 2;
        jpAction.add(jbClean, gbc);
        gbc.gridx = 3;
        jpAction.add(jbFireNas, gbc);
        add(jpAction, BorderLayout.NORTH);
    }

    public void setButtonEnabled(boolean e) {
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
        jbCopyLinks.setEnabled(e);
    }
}
