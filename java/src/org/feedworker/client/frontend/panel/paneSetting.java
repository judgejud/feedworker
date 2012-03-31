package org.feedworker.client.frontend.panel;
//IMPORT JAVA
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.feedworker.client.ApplicationSettings;

import org.jfacility.javax.swing.Swing;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
/**
 *
 * @author luca
 */
public class paneSetting extends paneAbstract {
    
    private static paneSetting jpanel = null;
    private JComboBox jcbMinuti, jcbTimeout;
    private JRadioButton jrbDirLocal, jrbDirSamba, jrbDownAuto, jrbDownManual;
    private JCheckBox jcbDestination, jcbRunIconized, jcbDownloadMyitasaStartup, 
            jcbReminder, jcbPaneSubDest, jcbPaneLog, jcbPaneSetting, 
            jcbPaneSearchSubItasa, jcbPaneReminder, jcbPaneTorrent, jcbPaneCalendar, 
            jcbPaneShow, jcbTorrent, jcbPaneBlog, jcbItasaBlog, jcbItasaPM, 
            jcbPaneCalendarDay, jcbCalendarDay, jcbPaneIrc, jcbItasaRss, jcbMyItasaRss,
            jcbItasaNews, jcbShowNoDuplicateAll, jcbShowNoDuplicateSingle;
    private JButton jbDestSub;
    private JTextField jtfDestSub, jtfSambaDomain, jtfSambaIP, jtfSambaDir,
            jtfSambaUser, jtfRssItasa, jtfRssMyItasa, jtfRssSubsf, 
            jtfDestTorrent, jtfItasaUser, jtfRssMySubsf, jtfMailTo, jtfMailSmtp,
            jtfGoogleUser, jtfGoogleCalendar, jtfIrcServer, jtfIrcNick;
    private JPasswordField jpfSamba, jpfItasa, jpfGoogle, jpfIrc;
    private ButtonGroup bgLocalSamba, bgDownItasa;
    private ApplicationSettings prop;

    private paneSetting() {
        super("Settings");
        prop = proxy.getSettings();
        initializePanel();
        initializeButtons();
    }

    public static paneSetting getPanel() {
        if (jpanel == null)
            jpanel = new paneSetting();
        return jpanel;
    }
    
    @Override
    void initializePanel() {
        JXTaskPaneContainer tpcWest = new JXTaskPaneContainer();
        tpcWest.add(initTaskPaneGeneral());
        tpcWest.add(initTaskPaneSamba());
        tpcWest.add(initTaskPaneVisibilePane());
        tpcWest.add(initTaskPaneShow());
        tpcWest.add(initTaskPaneIrc());
        
        JXTaskPaneContainer tpcEast = new JXTaskPaneContainer();
        tpcEast.add(initTaskPaneItalianSubs());
        if (prop.isSubsfactoryOption())
            tpcEast.add(initTaskPaneSubsfactory());
        tpcEast.add(initTaskPaneTorrent());
        tpcEast.add(initTaskPaneAlert());
        

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

    @Override
    void initializeButtons() {
        JButton jbSaveSettings = new JButton(core.getIconSave());
        jbSaveSettings.setToolTipText("Salva");
        jbSaveSettings.setBorder(BORDER);
        JButton jbAnnullaSettings = new JButton(core.getIconUndo());
        jbAnnullaSettings.setToolTipText("Annulla");
        jbAnnullaSettings.setBorder(BORDER);
        
        jbSaveSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                core.saveSettings(jrbDirLocal.isSelected(), jtfDestSub.getText(),
                jtfSambaDomain.getText(), jtfSambaIP.getText(), jtfSambaDir.getText(), 
                jtfSambaUser.getText(), new String(jpfSamba.getPassword()),
                jcbMinuti.getSelectedItem().toString(), 
                jcbTimeout.getSelectedItem().toString(),
                jcbDestination.isSelected(), jcbRunIconized.isSelected(),
                jtfRssItasa.getText(), jtfRssMyItasa.getText(), jtfItasaUser.getText(), 
                new String(jpfItasa.getPassword()), jrbDownAuto.isSelected(),
                jcbDownloadMyitasaStartup.isSelected(), jtfRssSubsf.getText(), 
                jtfRssMySubsf.getText(), jtfDestTorrent.getText(), jtfMailTo.getText(),
                jtfMailSmtp.getText(), jcbPaneLog.isSelected(), 
                jcbPaneSearchSubItasa.isSelected(), jcbPaneSetting.isSelected(), 
                jcbPaneSubDest.isSelected(), jcbPaneReminder.isSelected(), 
                jcbReminder.isSelected(), jtfGoogleUser.getText(), 
                new String(jpfGoogle.getPassword()), jtfGoogleCalendar.getText(), 
                jcbPaneTorrent.isSelected(), jcbPaneCalendar.isSelected(), 
                jcbTorrent.isSelected(), jcbPaneShow.isSelected(), jcbItasaBlog.isSelected(), 
                jcbPaneBlog.isSelected(), jcbItasaPM.isSelected(), 
                jcbPaneCalendarDay.isSelected(), jcbCalendarDay.isSelected(), jcbPaneIrc.isSelected(),
                jtfIrcNick.getText(), new String(jpfIrc.getPassword()), jcbItasaRss.isSelected(), 
                jcbMyItasaRss.isSelected(), jcbItasaNews.isSelected(), 
                jcbShowNoDuplicateAll.isSelected(), jcbShowNoDuplicateSingle.isSelected());
            }
        });
        
        jbAnnullaSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                settingsValue();
            }
        });
        
        jpAction.add(jbAnnullaSettings,gbcAction);
        gbcAction.gridx = 1;
        jpAction.add(jbSaveSettings, gbcAction);
    }
    
    private JXTaskPane initTaskPaneGeneral() {
        String[] time = new String[]{"2", "4", "6", "8", "10", "15", "20"};
        DefaultComboBoxModel modelTime = new DefaultComboBoxModel(time);
        DefaultComboBoxModel modelMin = new DefaultComboBoxModel(time);
        jcbMinuti = new JComboBox(modelMin);
        jcbMinuti.setSelectedIndex(2);
        jcbTimeout = new JComboBox(modelTime);
        jcbTimeout.setSelectedIndex(2);
        jrbDirLocal = new JRadioButton("HD locale");
        jrbDirLocal.setSelected(true);
        jrbDirSamba = new JRadioButton("HD Samba");
        jcbDestination = new JCheckBox("Abilita Destinazione personalizzata");
        jcbReminder = new JCheckBox("Abilita Reminder");
        jtfDestSub = new JTextField(20);
        jbDestSub = new JButton("Seleziona");
        jbDestSub.setBorder(BORDER);
        jbDestSub.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestSubMouseClicked();
            }
        });
        jcbRunIconized = new JCheckBox("Avvio iconizzato");
        bgLocalSamba = new ButtonGroup();
        bgLocalSamba.add(jrbDirLocal);
        bgLocalSamba.add(jrbDirSamba);
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("General");
        task.setCollapsed(true);
        
        JXPanel temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(new JLabel("Aggiorna RSS"));
        temp.add(jcbMinuti);
        temp.add(new JLabel("minuti"));
        task.add(temp);

        temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(new JLabel("Destinazione Sub"));
        temp.add(jrbDirLocal);
        temp.add(jrbDirSamba);
        task.add(temp);

        temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jlLocal = new JLabel("Percorso locale standard");
        temp.add(jlLocal);
        temp.add(jbDestSub);
        task.add(temp);
        task.add(jtfDestSub);
        
        task.add(jcbDestination);
        
        task.add(jcbReminder);

        temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(new JLabel("Timeout"));
        temp.add(jcbTimeout);
        temp.add(new JLabel("secondi"));
        task.add(temp);
        
        task.add(jcbRunIconized);
        return task;
    }
    
    private JXTaskPane initTaskPaneSamba(){
        jtfSambaDomain = new JTextField(20);
        jtfSambaIP = new JTextField(20);
        jtfSambaDir = new JTextField(20);
        jtfSambaUser = new JTextField(20);
        jpfSamba = new JPasswordField(20);
        JButton jbCheckSamba = new JButton(" Test connessione Samba ");
        jbCheckSamba.setBorder(BORDER);
        jbCheckSamba.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.checkSamba(jtfSambaDomain.getText(), jtfSambaDir.getText(), 
                    jtfSambaIP.getText(), jtfSambaUser.getText(), jpfSamba.getPassword());
            }
        });
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Samba/jcifs");
        task.setCollapsed(true);
        
        JLabel jlDomain = new JLabel("Samba Dominio");
        task.add(jlDomain);
        task.add(jtfSambaDomain);
        
        JLabel jlSip = new JLabel("Samba IP");
        task.add(jlSip);
        task.add(jtfSambaIP);

        JLabel jlSdir = new JLabel("Samba cartella condivisa");
        task.add(jlSdir);
        task.add(jtfSambaDir);

        JLabel jlSuser = new JLabel("Samba Username");
        task.add(jlSuser);
        task.add(jtfSambaUser);

        task.add(new JLabel("Samba Password"));
        task.add(jpfSamba);
        
        task.add(jbCheckSamba);
        
        return task;
    }

    /** inizializza il pannello dei settaggi itasa */
    private JXTaskPane initTaskPaneItalianSubs() {
        jcbItasaNews = new JCheckBox("Abilita news Itasa");
        jcbItasaRss = new JCheckBox("Abilita rss Itasa");
        jtfRssItasa = new JTextField(25);
        jtfRssItasa.setToolTipText("Immettere l'url per il feed rss Itasa");
        jcbMyItasaRss = new JCheckBox("Abilita rss myItasa");
        jtfRssMyItasa = new JTextField(25);
        jtfRssMyItasa.setToolTipText("Immettere l'url per il feed rss myItasa");
        jrbDownAuto = new JRadioButton("Automatico");
        jrbDownAuto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                settingItasaDownloadStartup();
            }
        });
        jrbDownManual = new JRadioButton("Manuale");
        jrbDownManual.setSelected(true);
        jrbDownManual.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                settingItasaDownloadStartup();
            }
        });
        bgDownItasa = new ButtonGroup();
        bgDownItasa.add(jrbDownAuto);
        bgDownItasa.add(jrbDownManual);
        jcbDownloadMyitasaStartup = new JCheckBox("download automatico dei nuovi "
                                                    + "sub myItasa all'avvio");
        jtfItasaUser = new JTextField(20);
        jpfItasa = new JPasswordField(20);
        
        JButton jbCheckItasaLogin = new JButton("Check login");
        jbCheckItasaLogin.setBorder(BORDER);
        jbCheckItasaLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.checkLoginItasa(jtfItasaUser.getText(), 
                        jpfItasa.getPassword());
            }
        });
        
        JButton jbCheckItasaApi = new JButton("Check login API");
        jbCheckItasaApi.setBorder(BORDER);
        jbCheckItasaApi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.checkLoginItasaApi(jtfItasaUser.getText(), 
                        jpfItasa.getPassword());
            }
        });
        
        JButton jbCheckItasaPM = new JButton("Check login Forum");
        jbCheckItasaPM.setBorder(BORDER);
        jbCheckItasaPM.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.checkLoginItasaPM(jtfItasaUser.getText(), 
                        jpfItasa.getPassword());
            }
        });
        
        jcbItasaBlog = new JCheckBox("Abilita Blog*");
        jcbItasaBlog.setToolTipText("Questa funzionalità richiede java 1.7");
        if (!core.isJava17()){
            jcbItasaBlog.setSelected(false);
            jcbItasaBlog.setEnabled(false);
        }
        
        jcbItasaPM = new JCheckBox("Abilita controllo messaggi privati forum");
        
        jcbCalendarDay = new JCheckBox("Abilita Calendario giornaliero");
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("ItalianSubs");
        task.setCollapsed(true);
        
        task.add(jcbItasaNews);
        task.add(jcbItasaRss);
        task.add(jtfRssItasa);
        task.add(jcbMyItasaRss);
        task.add(jtfRssMyItasa);

        JXPanel temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(new JLabel("myItasa download sub"));
        temp.add(jrbDownAuto);
        temp.add(jrbDownManual);
        task.add(temp);

        task.add(jcbDownloadMyitasaStartup);
        
        task.add(jcbItasaBlog);
        task.add(jcbItasaPM);
        task.add(jcbCalendarDay);

        task.add(new JLabel("Username"));
        task.add(jtfItasaUser);

        task.add(new JLabel("Password"));
        task.add(jpfItasa);
        
        temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(jbCheckItasaLogin);
        temp.add(jbCheckItasaApi);
        temp.add(jbCheckItasaPM);
        task.add(temp);
        
        return task;
    }

    /** inizializzo il pannello settaggi torrent */
    private JXTaskPane initTaskPaneTorrent() {
        jcbTorrent = new JCheckBox("Abilita feed torrent");
        jtfDestTorrent = new JTextField(25);
        JButton jbDestTorrent = new JButton("Seleziona directory");
        jbDestTorrent.setToolTipText("Seleziona la directory per i "
                + "download dei .torrent");
        jbDestTorrent.setBorder(BORDER);
        jbDestTorrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestTorrentMouseClicked();
            }
        });
        JXPanel temp = new JXPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(jbDestTorrent);
        temp.add(jtfDestTorrent);
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Torrent");
        task.setCollapsed(true);
        task.add(jcbTorrent);
        task.add(temp);
        return task;
    }

    /** inizializzo il pannello settaggi subsfactory */
    private JXTaskPane initTaskPaneSubsfactory() {
        jtfRssSubsf = new JTextField(25);
        jtfRssMySubsf = new JTextField(25);
        
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
        jtfMailTo = new JTextField(20);
        jtfMailTo.setToolTipText("Immettere l'indirizzo e-mail a cui si vogliono "
                                + "inviare le notifiche");
        jtfMailSmtp = new JTextField(20);
        jtfMailSmtp.setToolTipText("Immettere l'indirizzo del server di posta SMTP "
                + "della propria connessione internet per spedire le mail ");
        jtfGoogleUser = new JTextField(20);
        jtfGoogleUser.setToolTipText("Immettere l'username google completo di indirizzo "
                + "per poter fruire degli sms tramite google calendar");
        jtfGoogleCalendar = new JTextField(20);
        jtfGoogleCalendar.setToolTipText("Immettere l'username google completo di indirizzo "
                + "per poter fruire degli sms tramite google calendar");
        jpfGoogle = new JPasswordField(20);
        jpfGoogle.setToolTipText("Immettere la password dell'account google");
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Alert");
        task.setCollapsed(true);
        
        task.add(new JLabel("mail TO"));
        task.add(jtfMailTo);
        
        task.add(new JLabel("SMTP server"));
        task.add(jtfMailSmtp);
        
        task.add(new JLabel("Account google"));
        task.add(jtfGoogleUser);
        
        task.add(new JLabel("Password"));
        task.add(jpfGoogle);
        
        task.add(new JLabel("Calendario google da usare"));
        task.add(jtfGoogleCalendar);
        
        return task;
    }
    
    private JXTaskPane initTaskPaneIrc(){
        //jtfIrcServer = new JTextField();
        jtfIrcNick = new JTextField();
        jpfIrc = new JPasswordField();
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("IRC");
        task.setCollapsed(true);
        
        //task.add(new JLabel("Server irc"));
        //task.add(jtfIrcServer);
        task.add(new JLabel("Nickname"));
        task.add(jtfIrcNick);
        task.add(new JLabel("Password"));
        task.add(jpfIrc);
        
        return task;
    }
    
    private JXTaskPane initTaskPaneVisibilePane(){
        jcbPaneBlog = new JCheckBox("Blog");
        jcbPaneCalendarDay = new JCheckBox("Calendar Day");
        jcbPaneCalendar = new JCheckBox("Calendar Show");
        jcbPaneIrc = new JCheckBox("Irc"); 
        jcbPaneLog = new JCheckBox("Log");
        jcbPaneReminder = new JCheckBox("Reminder");
        jcbPaneSearchSubItasa = new JCheckBox("Search Subtitle Itasa");
        jcbPaneSetting = new JCheckBox("Setting");
        jcbPaneShow = new JCheckBox("Show");
        jcbPaneSubDest = new JCheckBox("Subtitle Destination");
        jcbPaneTorrent = new JCheckBox("Torrent");
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Visible Pane at Startup");
        task.setCollapsed(true);
        task.add(jcbPaneBlog);
        task.add(jcbPaneCalendarDay);
        task.add(jcbPaneCalendar);
        task.add(jcbPaneIrc);
        task.add(jcbPaneLog);
        task.add(jcbPaneReminder);
        task.add(jcbPaneSearchSubItasa);
        task.add(jcbPaneSetting);
        task.add(jcbPaneShow);
        task.add(jcbPaneSubDest);
        task.add(jcbPaneTorrent);
        return task;
    }
    
    private JXTaskPane initTaskPaneShow(){
        jcbShowNoDuplicateAll = new JCheckBox("Abilita controllo globale per i duplicati");
        jcbShowNoDuplicateSingle = new JCheckBox("Abilita controllo singola categoria per i duplicati");
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Show");
        task.setCollapsed(true);
        task.add(jcbShowNoDuplicateAll);
        task.add(jcbShowNoDuplicateSingle);
        return task;
    }

    /** Evento click select cartella */
    private void jbDestSubMouseClicked() {
        if (jbDestSub.isEnabled()) {
            String title = "Seleziona la Directory dove si vogliono salvare i sub";
            String dir = Swing.getDirectory(this, title);
            if (dir != null)
                jtfDestSub.setText(dir);
        }
    }

    private void jbDestTorrentMouseClicked() {
        String title = "Seleziona la Directory dove si vogliono salvare i torrent";
        String dir = Swing.getDirectory(this, title);
        if (dir != null)
            jtfDestTorrent.setText(dir);
    }

    public void settingsValue() {
        settingsGeneralValue();
        settingsSambaValue();
        settingsAlertValue();
        settingsItasaValue();
        settingsPaneVisibleValue();
        settingsTorrentValue();
        settingsIrcValue();
        settingsShowValue();
        if (prop.isSubsfactoryOption())
            settingsSubsfactoryValue();
    }

    /* Popola le impostazioni con il properties caricato */
    private void settingsGeneralValue() {
        jtfDestSub.setText(prop.getSubtitleDestinationFolder());
        jcbMinuti.setSelectedItem(prop.getRefreshInterval());
        jrbDirLocal.setSelected(prop.isLocalFolder());
        jrbDirSamba.setSelected(!prop.isLocalFolder());
        jcbTimeout.setSelectedItem(prop.getHttpTimeout());
        jcbDestination.setSelected(prop.isEnabledAdvancedDownload());
        jcbRunIconized.setSelected(prop.isEnabledIconizedRun());
        jcbReminder.setSelected(prop.isReminderOption());
    }
    
    private void settingsSambaValue() {
        jtfSambaDomain.setText(prop.getCifsShareDomain());
        jtfSambaDir.setText(prop.getCifsSharePath());
        jtfSambaIP.setText(prop.getCifsShareLocation());
        jtfSambaUser.setText(prop.getCifsShareUsername());
        jpfSamba.setText(prop.getCifsSharePassword());
    }

    private void settingsItasaValue() {
        jcbItasaNews.setSelected(prop.isItasaNews());
        jtfRssItasa.setText(prop.getItasaFeedURL());
        jtfRssMyItasa.setText(prop.getMyitasaFeedURL());
        jtfItasaUser.setText(prop.getItasaUsername());
        jpfItasa.setText(prop.getItasaPassword());
        jrbDownAuto.setSelected(prop.isAutoDownloadMyItasa());
        jrbDownManual.setSelected(!prop.isAutoDownloadMyItasa());
        jcbItasaBlog.setSelected(prop.isItasaBlog());
        jcbItasaPM.setSelected(prop.isItasaPM());
        jcbCalendarDay.setSelected(prop.isCalendarDay());
        settingItasaDownloadStartup();
    }
    
    private void settingItasaDownloadStartup(){
        if (jrbDownManual.isSelected()){
            jcbDownloadMyitasaStartup.setEnabled(false);
            jcbDownloadMyitasaStartup.setSelected(false);
        } else {
            jcbDownloadMyitasaStartup.setEnabled(true);
            jcbDownloadMyitasaStartup.setSelected(prop.isAutoLoadDownloadMyItasa());
        }
    }

    private void settingsTorrentValue() {
        jcbTorrent.setSelected(prop.isTorrentOption());
        jtfDestTorrent.setText(prop.getTorrentDestinationFolder());
    }
    
    private void settingsIrcValue() {
        //jtfIrcServer.setText("irc.azzurra.org");
        //jtfIrcServer.setEnabled(false);
        jtfIrcNick.setText(prop.getIrcNick());
        jpfIrc.setText(prop.getIrcPwd());
    }
    
    private void settingsShowValue() {
        jcbShowNoDuplicateAll.setSelected(prop.isShowNoDuplicateAll());
        jcbShowNoDuplicateSingle.setSelected(prop.isShowNoDuplicateSingle());
    }

    private void settingsSubsfactoryValue() {
        jtfRssSubsf.setText(prop.getSubsfactoryFeedURL());
        jtfRssMySubsf.setText(prop.getMySubsfactoryFeedUrl());
    }
   
    private void settingsAlertValue(){
        jtfMailTo.setText(prop.getMailTO());
        jtfMailSmtp.setText(prop.getMailSMTP());
        jtfGoogleUser.setText(prop.getGoogleUser());
        jtfGoogleCalendar.setText(prop.getGoogleCalendar());
        jpfGoogle.setText(prop.getGooglePwd());
    }
    
    private void settingsPaneVisibleValue(){
        jcbPaneBlog.setSelected(prop.isEnablePaneBlog());
        jcbPaneCalendarDay.setSelected(prop.isEnablePaneCalendarDay());
        jcbPaneCalendar.setSelected(prop.isEnablePaneCalendar());
        jcbPaneIrc.setSelected(prop.isEnablePaneIrc());
        jcbPaneLog.setSelected(prop.isEnablePaneLog());
        jcbPaneReminder.setSelected(prop.isEnablePaneReminder());
        jcbPaneSearchSubItasa.setSelected(prop.isEnablePaneSearchSubItasa());
        jcbPaneSetting.setSelected(prop.isEnablePaneSetting());
        jcbPaneShow.setSelected(prop.isEnablePaneShow());
        jcbPaneSubDest.setSelected(prop.isEnablePaneSubDestination());
        jcbPaneTorrent.setSelected(prop.isEnablePaneTorrent());
    }
}