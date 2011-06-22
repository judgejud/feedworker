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
import javax.swing.JOptionPane;
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
    protected JComboBox jcbMinuti, jcbLookFeel, jcbTimeout;
    protected JLabel jlDataAggiornamento;
    protected JRadioButton jrbDirLocal, jrbDirSamba, jrbDownAuto, jrbDownManual;
    protected JCheckBox jcbAudioRss, jcbAudioSub, jcbDestination, jcbRunIconized,
            jcbDownloadMyitasaStartup, jcbMail, jcbReminder, jcbPaneSubDest, jcbPaneLog, 
            jcbPaneSetting, jcbPaneSearchSubItasa, jcbPaneReminder, jcbPaneTorrent, 
            jcbGoogleSMS;
    private JButton jbSaveSettings, jbAnnullaSettings;
    protected JButton jbDestSub, jbDestTorrent, jbCheckItasa, jbCheckSamba;
    protected JTextField jtfDestSub, jtfSambaDomain, jtfSambaIP, jtfSambaDir,
            jtfSambaUser, jtfRssItasa, jtfRssMyItasa, jtfRssSubsf,
            jtfDestTorrent, jtfItasaUser, jtfRssMySubsf, jtfMailTo, jtfMailSmtp,
            jtfGoogleUser;
    protected JPasswordField jpfSamba, jpfItasa, jpfGoogle;
    private ButtonGroup bgLocalSamba, bgDownItasa;
    protected ApplicationSettings prop;

   
    protected paneSetting() {
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
                //TODO
            }
        });
        jbAnnullaSettings.setEnabled(false);

        jpAction.add(jbAnnullaSettings,gbcAction);
        gbcAction.gridx = 1;
        jpAction.add(jbSaveSettings, gbcAction);
    }
    
    private JXTaskPane initTaskPaneGeneral() {
        jcbMinuti = new JComboBox(new DefaultComboBoxModel(minuti));
        jcbMinuti.setSelectedIndex(2);
        jcbLookFeel = new JComboBox(new DefaultComboBoxModel(proxy.getAvailableLAF()));
        jcbTimeout = new JComboBox(new DefaultComboBoxModel(timeout));
        jcbTimeout.setSelectedIndex(2);
        jlDataAggiornamento = new JLabel();
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
        jbCheckItasa = new JButton("Check login");
        jbCheckItasa.setBorder(BORDER);
        jbCheckItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.checkLoginItasa(jtfItasaUser.getText(), 
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
        
        task.add(jbCheckItasa);
        return task;
    }

    /** inizializzo il pannello settaggi torrent */
    private JXTaskPane initTaskPaneTorrent() {
        jtfDestTorrent = new JTextField(20);
        jbDestTorrent = new JButton("Seleziona");
        jbDestTorrent.setBorder(BORDER);
        jbDestTorrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestTorrentMouseClicked();
            }
        });
        
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Torrent");
        task.setCollapsed(true);
        
        task.add(jbDestTorrent);

        task.add(jtfDestTorrent);
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
        jcbAudioRss = new JCheckBox("Avviso audio rss");
        jcbAudioSub = new JCheckBox("Avviso audio sub");
        jcbAudioSub.setEnabled(false);
        jcbMail = new JCheckBox("Avviso sub mail");
        jcbMail.setEnabled(false);
        jtfMailTo = new JTextField(20);
        jtfMailSmtp = new JTextField(20);
        
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
        jcbPaneSubDest = new JCheckBox("Subtitle Destination");
        jcbPaneSetting = new JCheckBox("Settings");
        jcbPaneLog = new JCheckBox("Log");
        jcbPaneSearchSubItasa = new JCheckBox("Search Subtitle Itasa");
        jcbPaneReminder = new JCheckBox("Reminder");
        
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
        if (jbDestTorrent.isEnabled()) {
            String title = "Seleziona la Directory dove si vogliono salvare i torrent";
            String dir = Swing.getDirectory(this, title);
            if (dir != null)
                jtfDestTorrent.setText(dir);
        }
    }
    
    public void settingFirstLookFeel(){
        jcbLookFeel.setSelectedItem(prop.getApplicationLookAndFeel());
    }

    public void settingsValue() {
        settingsGeneralValue();
        settingsSambaValue();
        settingsAlertValue();
        settingsItasaValue();
        settingsPaneVisibleValue();
        if (prop.isSubsfactoryOption())
            settingsSubsfactoryValue();
        if (prop.isTorrentOption())
            settingsTorrentValue();
    }

    /* Popola le impostazioni con il properties caricato */
    private void settingsGeneralValue() {
        jtfDestSub.setText(prop.getSubtitleDestinationFolder());
        jcbMinuti.setSelectedItem(prop.getRefreshInterval());
        jcbLookFeel.setSelectedItem(prop.getApplicationLookAndFeel());
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
        jtfDestTorrent.setText(prop.getTorrentDestinationFolder());
    }

    private void settingsSubsfactoryValue() {
        jtfRssSubsf.setText(prop.getSubsfactoryFeedURL());
        jtfRssMySubsf.setText(prop.getMySubsfactoryFeedUrl());
    }
   
    private void settingsAlertValue(){
        jcbAudioRss.setSelected(prop.isEnabledAdvisorAudioRss());
        jcbAudioSub.setSelected(prop.isEnabledAdvisorAudioSub());
        jcbMail.setSelected(prop.isEnabledAdvisorMail());
        jtfMailTo.setText(prop.getMailTO());
        jtfMailSmtp.setText(prop.getMailSMTP());
    }
    
    private void settingsPaneVisibleValue(){
        jcbPaneLog.setSelected(prop.isEnablePaneLog());
        jcbPaneSearchSubItasa.setSelected(prop.isEnablePaneSearchSubItasa());
        jcbPaneSetting.setSelected(prop.isEnablePaneSetting());
        jcbPaneSubDest.setSelected(prop.isEnablePaneSubDestination());
        jcbPaneReminder.setSelected(prop.isEnablePaneReminder());
    }

    public void setDataAggiornamento(String data) {
        jlDataAggiornamento.setText(data);
    }

    public String getDataAggiornamento() {
        return jlDataAggiornamento.getText();
    }
    
    private void jbCheckSambaMouseClicked(){
        //TODO
    }

    private void saveSettings() {
        String previousLookAndFeel = prop.getApplicationLookAndFeel();

        boolean save = proxy.saveSettings(jrbDirLocal.isSelected(), jtfDestSub.getText(),
                jtfSambaDomain.getText(), jtfSambaIP.getText(), jtfSambaDir.getText(), 
                jtfSambaUser.getText(), new String(jpfSamba.getPassword()),
                jcbMinuti.getSelectedItem().toString(), jcbLookFeel.getSelectedItem().toString(),
                jcbTimeout.getSelectedItem().toString(),
                jcbDestination.isSelected(), jcbRunIconized.isSelected(),
                jtfRssItasa.getText(), jtfRssMyItasa.getText(), jtfItasaUser.getText(), 
                new String(jpfItasa.getPassword()), jrbDownAuto.isSelected(),
                jcbDownloadMyitasaStartup.isSelected(), jtfRssSubsf.getText(), 
                jtfRssMySubsf.getText(), jtfDestTorrent.getText(), jcbAudioRss.isSelected(), 
                jcbAudioSub.isSelected(), jcbMail.isSelected(), jtfMailTo.getText(),
                jtfMailSmtp.getText(), jcbPaneLog.isSelected(), 
                jcbPaneSearchSubItasa.isSelected(), jcbPaneSetting.isSelected(), 
                jcbPaneSubDest.isSelected(), jcbPaneReminder.isSelected(), 
                jcbReminder.isSelected());

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
            proxy.restartApplication(jlDataAggiornamento.getText());
        }
    }
}