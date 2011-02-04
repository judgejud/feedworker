package org.feedworker.client.frontend.panel;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.feedworker.client.ApplicationSettings;
import org.jfacility.javax.swing.Swing;

/**
 *
 * @author luca
 */
public class jpSetting extends jpAbstract {
    private final Dimension INTERNALSETTINGS = new Dimension(500, 430);
    private final String[] timeout = new String[]{"3", "6", "9", "12", "15", "18"};
    private final String[] minuti = new String[]{"3", "6", "10", "15", "20", "30", "45"};
    private static jpSetting jpanel = null;
    protected JComboBox jcbMinuti, jcbLookFeel, jcbTimeout;
    protected JLabel jlDataAggiornamento;
    protected JRadioButton jrbDirLocal, jrbDirSamba, jrbDownAuto, jrbDownManual;
    protected JCheckBox jcbAudioRss, jcbAudioSub, jcbAdvancedDownload, jcbRunIconized,
            jcbDownloadMyitasaStartup, jcbMail, jcbPaneSubDest, jcbPaneLog, 
            jcbPaneSetting, jcbPaneSearchSubItasa;
    private JButton jbSaveSettings, jbAnnullaSettings;
    protected JButton jbDestSub, jbDestTorrent, jbCheckItasa;
    protected JTextField jtfDestSub, jtfSambaDomain, jtfSambaIP, jtfSambaDir,
            jtfSambaUser, jtfRssItasa, jtfRssMyItasa, jtfRssSubsf,
            jtfDestTorrent, jtfItasaUser, jtfRssMySubsf, jtfMailTo, jtfMailSmtp;
    protected JPasswordField jpfSamba, jpfItasa;
    private ButtonGroup bgLocalSamba, bgDownItasa;
    protected ApplicationSettings prop;

   
    protected jpSetting() {
        super();
        setName("Settings");
        prop = proxy.getSettings();
        initComponents();
        initializePanel();
        initializeButtons();
    }

    public static jpSetting getPanel() {
        if (jpanel == null) {
            jpanel = new jpSetting();
        }
        return jpanel;
    }
    
    @Override
    void initializePanel() {
        JTabbedPane jtpSettings = new JTabbedPane(JTabbedPane.LEFT);
        jtpSettings.addTab("General", initPanelSettingsGlobal());
        jtpSettings.addTab("ItalianSubs", initPanelSettingsItasa());
        if (prop.isSubsfactoryOption())
            jtpSettings.addTab("Subsfactory", initPanelSettingsSubsf());
        if (prop.isTorrentOption())
            jtpSettings.addTab("Torrent", initPanelSettingsTorrent());
        jtpSettings.addTab("Alert", initPanelSettingsAlert());
        jtpSettings.addTab("Visible Panel", initPaneSettingsVisiblePanel());
       
        jpCenter.add(jtpSettings);
        add(jpCenter, BorderLayout.CENTER);
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
            }
        });

        jpAction = new JPanel(new FlowLayout());
        jpAction.add(jbAnnullaSettings);
        jpAction.add(jbSaveSettings);
        this.add(jpAction, BorderLayout.SOUTH);
    }
   
    private GridBagConstraints initGbc(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 1, 2, 1);
        return gbc;
    }

    private JPanel initPanelSettingsGlobal() {
        JPanel jpSettingGlobal = new JPanel(new GridBagLayout());
        jpSettingGlobal.setPreferredSize(INTERNALSETTINGS);

        GridBagConstraints gbc = initGbc();
        int y = 0;
        jpSettingGlobal.add(new JLabel("Aggiorna RSS"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jpSettingGlobal.add(jcbMinuti, gbc);

        gbc.gridx = 3;
        jpSettingGlobal.add(new JLabel("minuti"), gbc);

        ++y;
        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        jpSettingGlobal.add(new JLabel("Ultimo aggiornamento"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jpSettingGlobal.add(jlDataAggiornamento, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingGlobal.add(new JLabel("Destinazione Sub"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jpSettingGlobal.add(jrbDirLocal, gbc);

        gbc.gridx = 3;
        jpSettingGlobal.add(jrbDirSamba, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingGlobal.add(jcbAdvancedDownload, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlLocal = new JLabel("Percorso locale standard");
        jlLocal.setForeground(Color.magenta);
        jpSettingGlobal.add(jlLocal, gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jtfDestSub, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        jpSettingGlobal.add(jbDestSub, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlDomain = new JLabel("Samba Dominio");
        jlDomain.setForeground(Color.blue);
        jpSettingGlobal.add(jlDomain, gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jtfSambaDomain, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlSip = new JLabel("Samba IP");
        jlSip.setForeground(Color.blue);
        jpSettingGlobal.add(jlSip, gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jtfSambaIP, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlSdir = new JLabel("Samba cartella condivisa");
        jlSdir.setForeground(Color.blue);
        jpSettingGlobal.add(jlSdir, gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jtfSambaDir, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlSuser = new JLabel("Samba Username");
        jlSuser.setForeground(Color.blue);
        jpSettingGlobal.add(jlSuser, gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jtfSambaUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlSpwd = new JLabel("Samba Password");
        jlSpwd.setForeground(Color.blue);
        jpSettingGlobal.add(jlSpwd, gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jpfSamba, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(new JLabel("Look&Feel"), gbc);

        gbc.gridx = 2;
        jpSettingGlobal.add(jcbLookFeel, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(new JLabel("Timeout"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jpSettingGlobal.add(jcbTimeout, gbc);

        gbc.gridx = 3;
        jpSettingGlobal.add(new JLabel("secondi"), gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(jcbRunIconized, gbc);
        
        return jpSettingGlobal;
    }

    /** inizializza il pannello dei settaggi itasa */
    private JPanel initPanelSettingsItasa() {
        JPanel jpSettingItasa = new JPanel(new GridBagLayout());
        jpSettingItasa.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = initGbc();
        int y = 0;

        JLabel jlItasa = new JLabel("RSS Itasa:");
        jlItasa.setForeground(Color.magenta);
        jpSettingItasa.add(jlItasa, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 3;
        jpSettingItasa.add(jtfRssItasa, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlMyItasa = new JLabel("RSS myItasa:");
        jlMyItasa.setForeground(Color.magenta);
        jpSettingItasa.add(jlMyItasa, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 3;
        jpSettingItasa.add(jtfRssMyItasa, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingItasa.add(new JLabel("myItasa download sub"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jpSettingItasa.add(jrbDownAuto, gbc);

        gbc.gridx = 3;
        jpSettingItasa.add(jrbDownManual, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingItasa.add(jcbDownloadMyitasaStartup, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlIuser = new JLabel("Username");
        jlIuser.setForeground(Color.red);
        jpSettingItasa.add(jlIuser, gbc);

        gbc.gridx = 2;
        jpSettingItasa.add(jtfItasaUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlIpwd = new JLabel("Password");
        jlIpwd.setForeground(Color.red);
        jpSettingItasa.add(jlIpwd, gbc);

        gbc.gridx = 2;
        jpSettingItasa.add(jpfItasa, gbc);
        
        return jpSettingItasa;
    }

    /** inizializzo il pannello settaggi torrent */
    private JPanel initPanelSettingsTorrent() {
        JPanel jpSettingTorrent = new JPanel(new GridBagLayout());
        jpSettingTorrent.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = initGbc();
        int y = 0;

        jpSettingTorrent.add(new JLabel("Destinazione Torrent"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        jpSettingTorrent.add(jtfDestTorrent, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        jpSettingTorrent.add(jbDestTorrent, gbc);
        return jpSettingTorrent;
    }
    
    /** inizializzo il pannello settaggi subsfactory */
    private JPanel initPanelSettingsSubsf() {
        JPanel jpSettingSubsf = new JPanel(new GridBagLayout());
        jpSettingSubsf.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = initGbc();
        int y = 0;
        jpSettingSubsf.add(new JLabel("Indirizzo RSS Subsfactory: "), gbc);

        gbc.gridx = 2;
        jpSettingSubsf.add(jtfRssSubsf, gbc);
       
        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingSubsf.add(new JLabel("Indirizzo RSS Subsf personalizzato: "), gbc);

        gbc.gridx = 2;
        jpSettingSubsf.add(jtfRssMySubsf, gbc);
        
        return jpSettingSubsf;
    }
    
    private void initComponents(){
        //GENERAL
        jcbMinuti = new JComboBox(new DefaultComboBoxModel(minuti));
        jcbMinuti.setSelectedIndex(2);
        jcbLookFeel = new JComboBox(new DefaultComboBoxModel(proxy.getAvailableLAF()));
        jcbTimeout = new JComboBox(new DefaultComboBoxModel(timeout));
        jcbTimeout.setSelectedIndex(2);
        jlDataAggiornamento = new JLabel();
        jrbDirLocal = new JRadioButton("HD locale");
        jrbDirLocal.setSelected(true);
        jrbDirSamba = new JRadioButton("HD Samba");
        jcbAdvancedDownload = new JCheckBox("Download avanzato");
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
        //SAMBA
        jtfSambaDomain = new JTextField(20);
        jtfSambaIP = new JTextField(20);
        jtfSambaDir = new JTextField(20);
        jtfSambaUser = new JTextField(20);
        jpfSamba = new JPasswordField(20);
        //ITASA
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
        //TORRENT
        jtfDestTorrent = new JTextField(20);
        jbDestTorrent = new JButton("Seleziona");
        jbDestTorrent.setBorder(BORDER);
        jbDestTorrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestTorrentMouseClicked();
            }
        });
        //SUBSFACTORY
        jtfRssSubsf = new JTextField(25);
        jtfRssMySubsf = new JTextField(25);
        //ALERT
        jcbAudioRss = new JCheckBox("Avviso audio rss");
        jcbAudioSub = new JCheckBox("Avviso audio sub");
        jcbMail = new JCheckBox("Avviso sub mail");
        jtfMailTo = new JTextField(20);
        jtfMailSmtp = new JTextField(20);
        //VISIBLE PANEL
        jcbPaneSubDest = new JCheckBox("Subtitle Destination");
        jcbPaneSetting = new JCheckBox("Settings");
        jcbPaneLog = new JCheckBox("Log");
        jcbPaneSearchSubItasa = new JCheckBox("Search Subtitle Itasa");
    }
   
    private JPanel initPanelSettingsAlert(){
        JPanel jpSettingAlert = new JPanel(new GridBagLayout());
        jpSettingAlert.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = initGbc();
        int y = 0;
        jpSettingAlert.add(jcbAudioRss, gbc);
       
        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingAlert.add(jcbAudioSub, gbc);
       
        gbc.gridx = 0;
        gbc.gridy = ++y;        
        jpSettingAlert.add(jcbMail, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingAlert.add(new JLabel("mail TO"), gbc);
        gbc.gridx = 2;
        jpSettingAlert.add(jtfMailTo, gbc);
       
        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingAlert.add(new JLabel("SMTP server"), gbc);
        gbc.gridx = 2;
        jpSettingAlert.add(jtfMailSmtp, gbc);
        
        return jpSettingAlert;
    }
    
    private JPanel initPaneSettingsVisiblePanel() {
        JPanel jpSettingVP = new JPanel(new GridBagLayout());
        jpSettingVP.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = initGbc();
        int y = 0;
        
        return jpSettingVP;
    }

    /** Evento click select cartella */
    private void jbDestSubMouseClicked() {
        if (jbDestSub.isEnabled()) {
            String title = "Seleziona la Directory dove si vogliono salvare i sub";
            String dir = Swing.getDirectory(this, title);
            if (dir != null) {
                jtfDestSub.setText(dir);
            }
        }
    }

    private void jbDestTorrentMouseClicked() {
        if (jbDestTorrent.isEnabled()) {
            String title = "Seleziona la Directory dove si vogliono salvare i torrent";
            String dir = Swing.getDirectory(this, title);
            if (dir != null) {
                jtfDestTorrent.setText(dir);
            }
        }
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
        jcbAdvancedDownload.setSelected(prop.isEnabledAdvancedDownload());
        jcbRunIconized.setSelected(prop.isEnabledIconizedRun());
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
        jtfMailSmtp.setText(prop.getMailSmtp());
    }
    
    private void settingsPaneVisibleValue(){
        jcbPaneLog.setSelected(prop.isEnablePaneLog());
        jcbPaneSearchSubItasa.setSelected(prop.isEnablePaneSearchSubItasa());
        jcbPaneSetting.setSelected(prop.isEnablePaneSetting());
        jcbPaneSubDest.setSelected(prop.isEnablePaneSubDestination());
    }

    public void setDataAggiornamento(String data) {
        jlDataAggiornamento.setText(data);
    }

    public String getDataAggiornamento() {
        return jlDataAggiornamento.getText();
    }

    private void saveSettings() {
        String previousLookAndFeel = prop.getApplicationLookAndFeel();

        proxy.saveSettings(jrbDirLocal.isSelected(), jtfDestSub.getText(),
                jtfSambaDomain.getText(), jtfSambaIP.getText(), jtfSambaDir.getText(), 
                jtfSambaUser.getText(), new String(jpfSamba.getPassword()),
                jcbMinuti.getSelectedItem().toString(), jcbLookFeel.getSelectedItem().toString(),
                jcbTimeout.getSelectedItem().toString(),
                jcbAdvancedDownload.isSelected(), jcbRunIconized.isSelected(),
                jtfRssItasa.getText(), jtfRssMyItasa.getText(), jtfItasaUser.getText(), 
                new String(jpfItasa.getPassword()), jrbDownAuto.isSelected(),
                jcbDownloadMyitasaStartup.isSelected(), jtfRssSubsf.getText(), 
                jtfRssMySubsf.getText(), jtfDestTorrent.getText(), jcbAudioRss.isSelected(), 
                jcbAudioSub.isSelected(), jcbMail.isSelected(), jtfMailTo.getText(),
                jtfMailSmtp.getText(), jcbPaneLog.isSelected(), 
                jcbPaneSearchSubItasa.isSelected(), jcbPaneSetting.isSelected(), 
                jcbPaneSubDest.isSelected());

        if (!previousLookAndFeel.equalsIgnoreCase(jcbLookFeel.getSelectedItem().toString())) {
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