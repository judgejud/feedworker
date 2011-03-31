package org.feedworker.client.frontend.panel;
//IMPORT JAVA
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
public class paneSettingEnhanced extends paneSetting {
    private static paneSettingEnhanced jpanel = null;
    
    private paneSettingEnhanced() {
        super();
    }

    public static paneSettingEnhanced getPanel() {
        if (jpanel == null)
            jpanel = new paneSettingEnhanced();
        return jpanel;
    }
    
    @Override
    void initializePanel() {
        JXTaskPaneContainer tpcWest = new JXTaskPaneContainer();
        tpcWest.add(initTaskPaneGeneral());
        tpcWest.add(initTaskPaneSamba());
        tpcWest.add(initTaskPaneVisibilePane());
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
    }

    private JXTaskPane initTaskPaneGeneral() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("General");
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

        temp = new JPanel();
        JLabel jlLocal = new JLabel("Percorso locale standard");
        jlLocal.setForeground(Color.magenta);
        temp.add(jlLocal);
        temp.add(jbDestSub);
        task.add(temp);
        task.add(jtfDestSub);
        
        task.add(jcbDestination);
        
        task.add(jcbReminder);
        
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
        task.setTitle("Samba/jcifs");
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
        
        task.add(jbCheckSamba);
        
        return task;
    }

    /** inizializza il pannello dei settaggi itasa */
    private JXTaskPane initTaskPaneItalianSubs() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("ItalianSubs");
        task.setCollapsed(true);

        JLabel jlItasa = new JLabel("RSS Itasa");
        jlItasa.setForeground(Color.magenta);
        task.add(jlItasa);
        task.add(jtfRssItasa);

        JLabel jlMyItasa = new JLabel("RSS myItasa");
        jlMyItasa.setForeground(Color.magenta);
        task.add(jlMyItasa);
        task.add(jtfRssMyItasa);

        JPanel temp = new JPanel();
        temp.add(new JLabel("myItasa download sub"));
        temp.add(jrbDownAuto);
        temp.add(jrbDownManual);
        task.add(temp);

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
        task.setTitle("Torrent");
        task.setCollapsed(true);
        
        task.add(jbDestTorrent);

        task.add(jtfDestTorrent);
        return task;
    }

    /** inizializzo il pannello settaggi subsfactory */
    private JXTaskPane initTaskPaneSubsfactory() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Subsfactory");
        task.setCollapsed(true);
        
        task.add(new JLabel("Indirizzo RSS Subsfactory"));
        task.add(jtfRssSubsf);
        
        task.add(new JLabel("Indirizzo RSS Subsf personalizzato"));
        task.add(jtfRssMySubsf);
        return task;
    }
    
    private JXTaskPane initTaskPaneAlert(){
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Alert");
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
    
    private JXTaskPane initTaskPaneVisibilePane(){
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Visible Pane at Startup");
        task.setCollapsed(true);
        
        task.add(jcbPaneLog);
        task.add(jcbPaneSearchSubItasa);
        task.add(jcbPaneReminder);
        task.add(jcbPaneSetting);
        task.add(jcbPaneSubDest);
        return task;
    }
}