package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.table.tableRss;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
/**
 * 
 * @author luca
 */
public class paneTorrent extends paneAbstract {

    private static paneTorrent jpanel = null;
    private JButton jbDown, jbCopyLinks, jbClean, jbFireNas;
    private tableRss[] jtTorrent;
    private ApplicationSettings prop;
    private JXTaskPaneContainer tpcWest, tpcEast;

    private paneTorrent() {
        super("Torrent");
        prop = proxy.getSettings();
        initializePanel();
        initializeButtons();
        //core.setTableListener(jtTorrent1);
        //core.setTableListener(jtTorrent2);
    }

    public static paneTorrent getPanel() {
        if (jpanel == null)
            jpanel = new paneTorrent();
        return jpanel;
    }

    @Override
    void initializePanel() {
        if (prop.getTorrentCount()>0){
            tpcWest = new JXTaskPaneContainer();
            JScrollPane jspWest = new JScrollPane(tpcWest);
            jspWest.setPreferredSize(TABLE_SCROLL_SIZE);
            jspWest.setAutoscrolls(true);
            jpCenter.add(jspWest);
            if (prop.getTorrentCount()>1){
                tpcEast = new JXTaskPaneContainer();
                JScrollPane jspEast = new JScrollPane(tpcEast);
                jspEast.setPreferredSize(TABLE_SCROLL_SIZE);
                jspEast.setAutoscrolls(true);
                jpCenter.add(RIGID_AREA);
                jpCenter.add(jspEast);
            }
            initializeTables();
        }
    }
    
    private void initializeTables(){
        JXTaskPane[] jtp = new JXTaskPane[prop.getTorrentCount()];
        jtTorrent = new tableRss[prop.getTorrentCount()];
        boolean east = false;
        int count=-1;
        if (prop.isTorrentEztvOption()){
            jtp[++count] = new JXTaskPane();
            jtTorrent[count] = new tableRss(proxy.getEztv());
            jtp[count].add(jtTorrent[count]);
            jtp[count].setTitle(proxy.getEztv());
            tpcWest.add(jtp[count]);
            east=true;
        }
        if (prop.isTorrentBtchatOption()){
            jtp[++count] = new JXTaskPane();
            jtTorrent[count] = new tableRss(proxy.getBtchat());
            jtp[count].add(jtTorrent[count]);
            jtp[count].setTitle(proxy.getBtchat());
            if (east) {
                tpcEast.add(jtp[count]);
                east=false;
            } else {
                tpcWest.add(jtp[count]);
                east=true;
            }
        }
        if (prop.isTorrentKarmorraOption()){
            jtp[++count] = new JXTaskPane();
            jtTorrent[count] = new tableRss(proxy.getKarmorra());
            jtp[count].add(jtTorrent[count]);
            jtp[count].setTitle(proxy.getKarmorra());
            if (east) {
                tpcEast.add(jtp[count]);
                east=false;
            } else {
                tpcWest.add(jtp[count]);
                east=true;
            }
        }
        if (prop.isTorrentMyKarmorraOption()){
            jtp[++count] = new JXTaskPane();
            jtTorrent[count] = new tableRss(proxy.getMyKarmorra());
            jtp[count].add(jtTorrent[count]);
            jtp[count].setTitle(proxy.getMyKarmorra());
            if (east) {
                tpcEast.add(jtp[count]);
                east=false;
            } else {
                tpcWest.add(jtp[count]);
                east=true;
            }
        }
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
                    //core.downloadTorrent(jtTorrent);
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
                //TODO
                //if (jbCopyLinks.isEnabled())
                    //core.copyLinkTorrent(jtTorrent);
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
                //TODO
                //if (jbFireNas.isEnabled())
                //     core.fireTorrentToNas(jtTorrent);
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
        for (int i=0; i<jtTorrent.length; i++)
            core.cleanSelect(jtTorrent[i],3);
    }
}