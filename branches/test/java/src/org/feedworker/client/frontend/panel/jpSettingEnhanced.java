package org.feedworker.client.frontend.panel;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
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
import org.jfacility.javax.swing.Swing;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * 
 * @author luca
 */
public class jpSettingEnhanced extends jpAbstract {
    private final String[] timeout = new String[]{"3", "6", "9", "12", "15", "18"};
    private static jpSettingEnhanced jpanel = null;
    private JComboBox jcbMinuti, jcbLookFeel, jcbTimeout;
    private JLabel jlDataAggiornamento;
    private JRadioButton jrbDirLocal, jrbDirSamba, jrbDownAuto, jrbDownManual;
    private JCheckBox jcbAudioRss, jcbAudioSub, jcbAdvancedDownload, jcbRunIconized,
            jcbDownloadMyitasaStartup, jcbMail;
    private JButton jbDestSub, jbSaveSettings, jbAnnullaSettings,
            jbDestTorrent, jbCheckItasa;
    private JTextField jtfDestSub, jtfSambaDomain, jtfSambaIP, jtfSambaDir,
            jtfSambaUser, jtfRssItasa, jtfRssMyItasa, jtfRssSubsf,
            jtfDestTorrent, jtfItasaUser, jtfRssMySubsf, jtfMailTo, jtfMailSmtp;
    private JPasswordField jpfSamba, jpfItasa;
    private ButtonGroup bgLocalSamba, bgDownItasa;
    private ApplicationSettings prop;
    
    private jpSettingEnhanced() {
        super();
        prop = proxy.getSettings();
        initializePanel();
        initializeButtons();
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
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        jpAction.add(jbSaveSettings, gbc);
        gbc.gridx = 1;
        jpAction.add(jbAnnullaSettings, gbc);
        this.add(jpAction, BorderLayout.SOUTH);
    }

    private JXTaskPane initTaskPaneGeneral() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("General setting");
        
        JPanel temp = new JPanel();
        temp.add(new JLabel("Aggiorna RSS"));
        DefaultComboBoxModel dcbmM = new DefaultComboBoxModel(new String[]{
                    "3", "6", "10", "15", "20", "30", "45"});
        jcbMinuti = new JComboBox(dcbmM);
        jcbMinuti.setSelectedIndex(2);
        temp.add(jcbMinuti);
        temp.add(new JLabel("minuti"));
        task.add(temp);

        temp = new JPanel();
        temp.add(new JLabel("Ultimo aggiornamento: "));
        jlDataAggiornamento = new JLabel();
        temp.add(jlDataAggiornamento);
        task.add(temp);

        task.add(new JLabel("Destinazione Sub"));
        temp = new JPanel();
        jrbDirLocal = new JRadioButton("HD locale");
        jrbDirLocal.setSelected(true);
        temp.add(jrbDirLocal);
        jrbDirSamba = new JRadioButton("HD Samba");
        temp.add(jrbDirSamba);
        task.add(temp);

        jcbAdvancedDownload = new JCheckBox("Download avanzato");
        task.add(jcbAdvancedDownload);

        temp = new JPanel();
        JLabel jlLocal = new JLabel("Percorso locale standard");
        jlLocal.setForeground(Color.magenta);
        temp.add(jlLocal);
        jbDestSub = new JButton("Seleziona");
        jbDestSub.setBorder(BORDER);
        jbDestSub.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestSubMouseClicked();
            }
        });
        temp.add(jbDestSub);
        task.add(temp);
        jtfDestSub = new JTextField(20);
        task.add(jtfDestSub);
        
        task.add(new JLabel("Look&Feel"));
        jcbLookFeel = new JComboBox(new DefaultComboBoxModel(proxy.getAvailableLAF()));
        task.add(jcbLookFeel);

        temp = new JPanel();
        temp.add(new JLabel("Timeout"));
        jcbTimeout = new JComboBox(new DefaultComboBoxModel(timeout));
        jcbTimeout.setSelectedIndex(2);
        temp.add(jcbTimeout);
        temp.add(new JLabel("secondi"));
        task.add(temp);
        
        jcbRunIconized = new JCheckBox("Avvio iconizzato");
        task.add(jcbRunIconized);
        
        bgLocalSamba = new ButtonGroup();
        bgLocalSamba.add(jrbDirLocal);
        bgLocalSamba.add(jrbDirSamba);
        
        return task;
    }
    
    private JXTaskPane initTaskPaneSamba(){
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Samba/jcifs setting");
        
        JLabel jlDomain = new JLabel("Samba Dominio");
        jlDomain.setForeground(Color.blue);
        task.add(jlDomain);
        
        jtfSambaDomain = new JTextField();
        jtfSambaDomain.setColumns(20);
        task.add(jtfSambaDomain);
        
        JLabel jlSip = new JLabel("Samba IP");
        jlSip.setForeground(Color.blue);
        task.add(jlSip);
        
        jtfSambaIP = new JTextField();
        jtfSambaIP.setColumns(20);
        task.add(jtfSambaIP);

        JLabel jlSdir = new JLabel("Samba cartella condivisa");
        jlSdir.setForeground(Color.blue);
        task.add(jlSdir);

        jtfSambaDir = new JTextField(20);
        task.add(jtfSambaDir);

        JLabel jlSuser = new JLabel("Samba Username");
        jlSuser.setForeground(Color.blue);
        task.add(jlSuser);

        jtfSambaUser = new JTextField(20);
        task.add(jtfSambaUser);

        JLabel jlSpwd = new JLabel("Samba Password");
        jlSpwd.setForeground(Color.blue);
        task.add(jlSpwd);
        
        jpfSamba = new JPasswordField(20);
        task.add(jpfSamba);
        return task;
    }

    /** inizializza il pannello dei settaggi itasa */
    private JXTaskPane initTaskPaneItalianSubs() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("ItalianSubs setting");

        JLabel jlItasa = new JLabel("RSS Itasa");
        jlItasa.setForeground(Color.magenta);
        task.add(jlItasa);
        jtfRssItasa = new JTextField(25);
        task.add(jtfRssItasa);

        JLabel jlMyItasa = new JLabel("RSS myItasa");
        jlMyItasa.setForeground(Color.magenta);
        task.add(jlMyItasa);
        jtfRssMyItasa = new JTextField(25);
        task.add(jtfRssMyItasa);

        
        task.add(new JLabel("myItasa download sub"));
        jrbDownAuto = new JRadioButton("Automatico");
        jrbDownAuto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                settingItasaDownloadStartup();
            }
        });
        task.add(jrbDownAuto);
        jrbDownManual = new JRadioButton("Manuale");
        jrbDownManual.setSelected(true);
        jrbDownManual.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                settingItasaDownloadStartup();
            }
        });
        task.add(jrbDownManual);

        bgDownItasa = new ButtonGroup();
        bgDownItasa.add(jrbDownAuto);
        bgDownItasa.add(jrbDownManual);

        jcbDownloadMyitasaStartup = new JCheckBox("myItasa download automatico avvio");
        task.add(jcbDownloadMyitasaStartup);

        JLabel jlIuser = new JLabel("Username");
        jlIuser.setForeground(Color.red);
        task.add(jlIuser);
        jtfItasaUser = new JTextField(20);
        task.add(jtfItasaUser);

        JLabel jlIpwd = new JLabel("Password");
        jlIpwd.setForeground(Color.red);
        task.add(jlIpwd);
        jpfItasa = new JPasswordField(20);
        task.add(jpfItasa);
        
        jbCheckItasa = new JButton("Check login");
        jbCheckItasa.setBorder(BORDER);
        jbCheckItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //TODO
            }
        });
        task.add(jbCheckItasa);
        return task;
    }

    /** inizializzo il pannello settaggi torrent */
    private JXTaskPane initTaskPaneTorrent() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Torrent setting");
        
        jbDestTorrent = new JButton("Destinazione Torrent");
        jbDestTorrent.setBorder(BORDER);
        jbDestTorrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestTorrentMouseClicked();
            }
        });
        task.add(jbDestTorrent);

        jtfDestTorrent = new JTextField(20);
        task.add(jtfDestTorrent);
        return task;
    }

    /** inizializzo il pannello settaggi subsfactory */
    private JXTaskPane initTaskPaneSubsfactory() {
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Subsfactory setting");
        
        task.add(new JLabel("Indirizzo RSS Subsfactory"));
        jtfRssSubsf = new JTextField(25);
        task.add(jtfRssSubsf);
        
        task.add(new JLabel("Indirizzo RSS Subsf personalizzato"));
        jtfRssMySubsf = new JTextField(25);
        task.add(jtfRssMySubsf);
        return task;
    }
    
    private JXTaskPane initTaskPaneAlert(){
        JXTaskPane task = new JXTaskPane();
        task.setTitle("Alert setting");
        
        jcbAudioRss = new JCheckBox("Avviso audio rss");
        task.add(jcbAudioRss);
        
        jcbAudioSub = new JCheckBox("Avviso audio sub");
        task.add(jcbAudioSub);
        
        jcbMail = new JCheckBox("Avviso sub mail");
        task.add(jcbMail);

        task.add(new JLabel("mail TO"));
        jtfMailTo = new JTextField(20);
        task.add(jtfMailTo);
        
        task.add(new JLabel("SMTP server"));
        jtfMailSmtp = new JTextField(20);
        task.add(jtfMailSmtp);
        return task;
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
        if (prop.isSubsfactoryOption())
            settingsSubsfValue();
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

    private void settingsSubsfValue() {
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
                jtfMailSmtp.getText());

        if (!previousLookAndFeel.equalsIgnoreCase(prop.getApplicationLookAndFeel())) {
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