package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.table.tableRss;
/**
 * 
 * @author luca
 */
public class paneTorrent extends paneAbstract {

    private static paneTorrent jpanel = null;
    private JButton jbDown, jbCopyLinks, jbClean, jbFireNas;
    private tableRss jtTorrent1, jtTorrent2;

    private paneTorrent() {
        super("Torrent");
        initializePanel();
        initializeButtons();
        core.setTableListener(jtTorrent1);
        core.setTableListener(jtTorrent2);
    }

    public static paneTorrent getPanel() {
        if (jpanel == null)
            jpanel = new paneTorrent();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtTorrent1 = new tableRss(proxy.getEztv());
        jtTorrent1.setTitleDescriptionColumn("Descrizione Torrent EZTV");
        JScrollPane jsp1 = new JScrollPane(jtTorrent1);
        jsp1.setPreferredSize(TABLE_SCROLL_SIZE);
        jsp1.setAutoscrolls(true);

        jtTorrent2 = new tableRss(proxy.getBtchat());
        jtTorrent2.setTitleDescriptionColumn("Descrizione Torrent BTCHAT");
        JScrollPane jsp2 = new JScrollPane(jtTorrent2);
        jsp2.setPreferredSize(TABLE_SCROLL_SIZE);
        jsp2.setAutoscrolls(true);
        
        jpCenter.add(jsp1);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jsp2);
    }

    @Override
    void initializeButtons() {
        jbDown = new JButton(core.getIconDownload());
        jbDown.setToolTipText("Scarica i .torrent selezionati");
        jbDown.setBorder(BORDER);
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jbDown.isEnabled()){
                    core.downloadTorrent(jtTorrent1, jtTorrent2);
                    cleanTables();
                }
            }
        });

        jbCopyLinks = new JButton(core.getIconCopy());
        jbCopyLinks.setToolTipText("Copia i link dei torrent selezionati nella "
                                + "clipboard");
        jbCopyLinks.setBorder(BORDER);
        jbCopyLinks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jbCopyLinks.isEnabled())
                    core.copyLinkTorrent(jtTorrent1, jtTorrent2);
            }
        });

        jbClean = new JButton(core.getIconClean1());
        jbClean.setToolTipText("Pulisce le righe selezionate");
        jbClean.setBorder(BORDER);
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                cleanTables();
            }
        });

        jbFireNas = new JButton(core.getIconNas());
        jbFireNas.setToolTipText("invia i link dei torrent selezionati alla "
                            + "download station NAS"); 
        jbFireNas.setBorder(BORDER);
        jbFireNas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (jbFireNas.isEnabled())
                     core.fireTorrentToNas(jtTorrent1, jtTorrent2);
            }
        });
        
        jpAction.add(jbDown, gbcAction);
        gbcAction.gridx = 1;
        jpAction.add(jbCopyLinks, gbcAction);
        gbcAction.gridx = 2;
        jpAction.add(jbClean, gbcAction);
        gbcAction.gridx = 3;
        jpAction.add(jbFireNas, gbcAction);
    }

    public void setButtonEnabled(boolean e) {
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
        jbCopyLinks.setEnabled(e);
    }
    
    private void cleanTables(){
        core.cleanSelect(jtTorrent1,3);
        core.cleanSelect(jtTorrent2,3);
    }
}