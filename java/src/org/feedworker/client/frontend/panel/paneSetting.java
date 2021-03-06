package org.feedworker.client.frontend.panel;
//IMPORT JAVA
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.feedworker.client.ApplicationSettings;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.jfacility.javax.swing.Swing;

/**
 *
 * @author luca
 */
public class paneSetting extends paneAbstract {
    private final String[] timeout = new String[]{"3", "6", "9", "12", "15", "18"};
    private final String[] minuti = new String[]{"3", "6", "10", "15", "20", "30", "45"};
    private static paneSetting jpanel = null;
    private JComboBox jcbMinuti, jcbTimeout;
    private JRadioButton jrbDirLocal, jrbDirSamba, jrbDownAuto, jrbDownManual;
    private JCheckBox jcbDestination, jcbRunIconized, jcbDownloadMyitasaStartup, 
            jcbReminder, jcbPaneSubDest, jcbPaneLog, jcbPaneSetting, 
            jcbPaneSearchSubItasa, jcbPaneReminder, jcbPaneTorrent, jcbPaneCalendar, 
            jcbTorrent;
    private JButton jbSaveSettings, jbAnnullaSettings, jbDestSub, jbDestTorrent, 
            jbCheckItasaApi, jbCheckItasaLogin,  jbCheckSamba;
    private JTextField jtfDestSub, jtfSambaDomain, jtfSambaIP, jtfSambaDir,
            jtfSambaUser, jtfRssItasa, jtfRssMyItasa, jtfRssSubsf,
            jtfDestTorrent, jtfItasaUser, jtfRssMySubsf, jtfMailTo, jtfMailSmtp,
            jtfGoogleUser, jtfGoogleCalendar;
    private JPasswordField jpfSamba, jpfItasa, jpfGoogle;
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
        if (prop.isSubsfactoryOption())
            tpcWest.add(initTaskPaneSubsfactory());
                
        JXTaskPaneContainer tpcEast = new JXTaskPaneContainer();
        tpcEast.add(initTaskPaneItalianSubs());
        tpcEast.add(initTaskPaneAlert());
        tpcEast.add(initTaskPaneTorrent());

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
        jbSaveSettings = new JButton("Salva");
        jbSaveSettings.setBorder(BORDER);
        jbAnnullaSettings = new JButton("Annulla");
        jbAnnullaSettings.setBorder(BORDER);
        jbSaveSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                saveSettings();
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
        jcbMinuti = new JComboBox(new DefaultComboBoxModel(minuti));
        jcbMinuti.setSelectedIndex(2);
        jcbTimeout = new JComboBox(new DefaultComboBoxModel(timeout));
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
        
        JPanel temp = new JPanel();
        temp.add(new JLabel("Aggiorna RSS"));
        temp.add(jcbMinuti);
        temp.add(new JLabel("minuti"));
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
        jbCheckSamba = new JButton(" Test connessione Samba ");
        jbCheckSamba.setBorder(BORDER);
        jbCheckSamba.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbCheckSambaMouseClicked();
            }
        });
        
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
        jtfRssItasa = new JTextField(25);
        jtfRssMyItasa = new JTextField(25);
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
        jbCheckItasaApi = new JButton("Check login API");
        jbCheckItasaApi.setBorder(BORDER);
        jbCheckItasaApi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.checkLoginItasaApi(jtfItasaUser.getText(), 
                        jpfItasa.getPassword());
            }
        });
        
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
        
        task.add(jbCheckItasaApi);
        return task;
    }

    /** inizializzo il pannello settaggi torrent */
    private JXTaskPane initTaskPaneTorrent() {
        jcbTorrent = new JCheckBox("Abilita feed torrent");
        jtfDestTorrent = new JTextField(25);
        jbDestTorrent = new JButton("Seleziona directory");
        jbDestTorrent.setToolTipText("Seleziona la directory per i "
                + "download dei .torrent");
        jbDestTorrent.setBorder(BORDER);
        jbDestTorrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestTorrentMouseClicked();
            }
        });
        JPanel temp = new JPanel();
        temp.add(jtfDestTorrent);
        temp.add(jbDestTorrent);
        
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
    
    private JXTaskPane initTaskPaneVisibilePane(){
        jcbPaneCalendar = new JCheckBox("Calendar");
        jcbPaneLog = new JCheckBox("Log");
        jcbPaneReminder = new JCheckBox("Reminder");
        jcbPaneSearchSubItasa = new JCheckBox("Search Subtitle Itasa");
        jcbPaneSetting = new JCheckBox("Settings");
        jcbPaneSubDest = new JCheckBox("Subtitle Destination");
        jcbPaneTorrent = new JCheckBox("Torrent");
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Visible Pane at Startup");
        task.setCollapsed(true);
        
        task.add(jcbPaneCalendar);
        task.add(jcbPaneLog);
        task.add(jcbPaneReminder);
        task.add(jcbPaneSearchSubItasa);
        task.add(jcbPaneSetting);
        task.add(jcbPaneSubDest);
        task.add(jcbPaneTorrent);
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
        jtfRssItasa.setText(prop.getItasaFeedURL());
        jtfRssMyItasa.setText(prop.getMyitasaFeedURL());
        jtfItasaUser.setText(prop.getItasaUsername());
        jpfItasa.setText(prop.getItasaPassword());
        jrbDownAuto.setSelected(prop.isAutoDownloadMyItasa());
        jrbDownManual.setSelected(!prop.isAutoDownloadMyItasa());
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
        jcbPaneCalendar.setSelected(prop.isEnablePaneCalendar());
        jcbPaneLog.setSelected(prop.isEnablePaneLog());
        jcbPaneReminder.setSelected(prop.isEnablePaneReminder());
        jcbPaneSearchSubItasa.setSelected(prop.isEnablePaneSearchSubItasa());
        jcbPaneSetting.setSelected(prop.isEnablePaneSetting());
        jcbPaneSubDest.setSelected(prop.isEnablePaneSubDestination());
        jcbPaneTorrent.setSelected(prop.isEnablePaneTorrent());
    }
    
    private void jbCheckSambaMouseClicked(){
        //TODO
    }

    private void saveSettings() {
        String previousLookAndFeel = prop.getApplicationLookAndFeel();

        boolean save = proxy.saveSettings(jrbDirLocal.isSelected(), jtfDestSub.getText(),
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
                jcbTorrent.isSelected());
        /*
        if (save && 
            !previousLookAndFeel.equalsIgnoreCase(jcbLookFeel.getSelectedItem().toString())) {
            int returnCode = JOptionPane.showConfirmDialog(this,
                    "Per applicare il nuovo Look & Feel è necessario riavviare "
                    + proxy.getNameApp()
                    + ". \nPremi Si per riavviare, No altrimenti",
                    "Info", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (returnCode == 1)
                return;
            proxy.restartApplication();
        }
         */
    }
}