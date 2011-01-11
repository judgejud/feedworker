package org.feedworker.client.frontend.panel;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * 
 * @author luca
 */
public class jpSettingEnhanced extends jpSetting {
    private static jpSettingEnhanced jpanel = null;
    
    private jpSettingEnhanced() {
        super();
    }

    public static jpSettingEnhanced getPanel() {
        if (jpanel == null) {
            jpanel = new jpSettingEnhanced();
        }
        return jpanel;
    }
    
    @Override
    void initializePanel() {
        JXTaskPaneContainer tpcWest = new JXTaskPaneContainer();
        tpcWest.add(initTaskPaneGeneral());
        tpcWest.add(initTaskPaneSamba());
        if (prop.isTorrentOption())
            tpcWest.add(initTaskPaneTorrent());
        
        JXTaskPaneContainer tpcEast = new JXTaskPaneContainer();
        tpcEast.add(initTaskPaneItalianSubs());
        tpcEast.add(initTaskPaneAlert());
        if (prop.isSubsfactoryOption())
            tpcEast.add(initTaskPaneSubsfactory());

        JScrollPane jspWest = new JScrollPane(tpcWest);
        jspWest.setPreferredSize(TABLE_SCROLL_SIZE);
        jspWest.setAutoscrolls(true);
        
        JScrollPane jspEast = new JScrollPane(tpcEast);
        jspEast.setPreferredSize(TABLE_SCROLL_SIZE);
        jspEast.setAutoscrolls(true);
        
        jpCenter.add(jspWest);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jspEast);
        add(jpCenter, BorderLayout.CENTER);

        this.setVisible(true);
    }

    private JXTaskPane initTaskPaneGeneral() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("General setting");
        task.setCollapsed(true);
        
        JPanel temp = new JPanel();
        temp.add(new JLabel("Aggiorna RSS"));
        temp.add(jcbMinuti);
        temp.add(new JLabel("minuti"));
        task.add(temp);

        temp = new JPanel();
        temp.add(new JLabel("Ultimo aggiornamento: "));
        temp.add(jlDataAggiornamento);
        task.add(temp);

        temp = new JPanel();
        temp.add(new JLabel("Destinazione Sub"));
        temp.add(jrbDirLocal);
        temp.add(jrbDirSamba);
        task.add(temp);

        task.add(jcbAdvancedDownload);

        temp = new JPanel();
        JLabel jlLocal = new JLabel("Percorso locale standard");
        jlLocal.setForeground(Color.magenta);
        temp.add(jlLocal);
        temp.add(jbDestSub);
        task.add(temp);
        task.add(jtfDestSub);
        
        temp = new JPanel();
        temp.add(new JLabel("Look&Feel"));
        temp.add(jcbLookFeel);
        task.add(temp);

        temp = new JPanel();
        temp.add(new JLabel("Timeout"));
        temp.add(jcbTimeout);
        temp.add(new JLabel("secondi"));
        task.add(temp);
        
        task.add(jcbRunIconized);
        
        return task;
    }
    
    private JXTaskPane initTaskPaneSamba(){
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Samba/jcifs setting");
        task.setCollapsed(true);
        
        JLabel jlDomain = new JLabel("Samba Dominio");
        jlDomain.setForeground(Color.blue);
        task.add(jlDomain);
        
        task.add(jtfSambaDomain);
        
        JLabel jlSip = new JLabel("Samba IP");
        jlSip.setForeground(Color.blue);
        task.add(jlSip);
        
        task.add(jtfSambaIP);

        JLabel jlSdir = new JLabel("Samba cartella condivisa");
        jlSdir.setForeground(Color.blue);
        task.add(jlSdir);

        task.add(jtfSambaDir);

        JLabel jlSuser = new JLabel("Samba Username");
        jlSuser.setForeground(Color.blue);
        task.add(jlSuser);

        task.add(jtfSambaUser);

        JLabel jlSpwd = new JLabel("Samba Password");
        jlSpwd.setForeground(Color.blue);
        task.add(jlSpwd);
        
        task.add(jpfSamba);
        return task;
    }

    /** inizializza il pannello dei settaggi itasa */
    private JXTaskPane initTaskPaneItalianSubs() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("ItalianSubs setting");
        task.setCollapsed(true);

        JLabel jlItasa = new JLabel("RSS Itasa");
        jlItasa.setForeground(Color.magenta);
        task.add(jlItasa);
        task.add(jtfRssItasa);

        JLabel jlMyItasa = new JLabel("RSS myItasa");
        jlMyItasa.setForeground(Color.magenta);
        task.add(jlMyItasa);
        task.add(jtfRssMyItasa);

        
        task.add(new JLabel("myItasa download sub"));
        task.add(jrbDownAuto);
        task.add(jrbDownManual);

        task.add(jcbDownloadMyitasaStartup);

        JLabel jlIuser = new JLabel("Username");
        jlIuser.setForeground(Color.red);
        task.add(jlIuser);
        task.add(jtfItasaUser);

        JLabel jlIpwd = new JLabel("Password");
        jlIpwd.setForeground(Color.red);
        task.add(jlIpwd);
        task.add(jpfItasa);
        
        task.add(jbCheckItasa);
        return task;
    }

    /** inizializzo il pannello settaggi torrent */
    private JXTaskPane initTaskPaneTorrent() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Torrent setting");
        task.setCollapsed(true);
        
        task.add(jbDestTorrent);

        task.add(jtfDestTorrent);
        return task;
    }

    /** inizializzo il pannello settaggi subsfactory */
    private JXTaskPane initTaskPaneSubsfactory() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Subsfactory setting");
        task.setCollapsed(true);
        
        task.add(new JLabel("Indirizzo RSS Subsfactory"));
        task.add(jtfRssSubsf);
        
        task.add(new JLabel("Indirizzo RSS Subsf personalizzato"));
        task.add(jtfRssMySubsf);
        return task;
    }
    
    private JXTaskPane initTaskPaneAlert(){
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Alert setting");
        task.setCollapsed(true);
        
        task.add(jcbAudioRss);
        
        task.add(jcbAudioSub);
        
        task.add(jcbMail);

        task.add(new JLabel("mail TO"));
        task.add(jtfMailTo);
        
        task.add(new JLabel("SMTP server"));
        task.add(jtfMailSmtp);
        return task;
    }
}