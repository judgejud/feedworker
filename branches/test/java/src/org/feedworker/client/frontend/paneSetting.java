/*TODO: vedere se riesce ad ereditare da paneAbstract,
senza però fare danni con i panel dei setting che vanno posizionati EAST
 */
package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.jfacility.javax.swing.Swing;

/**
 * 
 * @author luca
 */
public class paneSetting extends JPanel {

    private final Dimension TABBEDSIZE = new Dimension(1024, 540);
    private final Dimension INTERNALSETTINGS = new Dimension(500, 430);
    private final SoftBevelBorder SBBORDER = new SoftBevelBorder(
            BevelBorder.RAISED);
    private static paneSetting jpanel = null;
    private JTree jtSettings;
    private JPanel jpSettingGlobal, jpSettingItasa, jpSettingSubsf,
            jpSettingTorrent;
    private JComboBox jcbMinuti, jcbLookFeel, jcbTimeout, jcbFont;
    private JLabel jlDataAggiornamento;
    private JRadioButton jrbDirLocal, jrbDirSamba, jrbDownAuto, jrbDownManual;
    private JCheckBox jcbAudio, jcbAdvancedDest, jcbRunIconized,
            jcbRunAtStartup, jcbDownloadMyitasaStartup;
    private JButton jbDestSub, jbSaveSettings, jbAnnullaSettings,
            jbDestTorrent;
    private JTextField jtfDestSub, jtfSambaDomain, jtfSambaIP, jtfSambaDir,
            jtfSambaUser, jtfRssItasa, jtfRssMyItasa, jtfRssSubsf,
            jtfDestTorrent, jtfItasaUser, jtfRssMySubsf;
    private JPasswordField jpfSamba, jpfItasa;
    private ButtonGroup bgLocalSamba, bgDownItasa;
    private Mediator proxy = Mediator.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();

    private paneSetting() {
        super(new GridBagLayout());
        setPreferredSize(TABBEDSIZE);
        initSettings();
    }

    public static paneSetting getPanel() {
        if (jpanel == null) {
            jpanel = new paneSetting();
        }
        return jpanel;
    }

    private void initSettings() {
        initTree();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(jtSettings, gbc);

        initPanelSettingsGlobal();
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.weightx = 1;
        add(jpSettingGlobal, gbc);
        if (prop.hasItasaOption()) {
            initPanelSettingsItasa();
            add(jpSettingItasa, gbc);
        }
        if (prop.hasSubsfactoryOption()) {
            initPanelSettingsSubsf();
            add(jpSettingSubsf, gbc);
        }
        if (prop.hasTorrentOption()) {
            initPanelSettingsTorrent();
            add(jpSettingTorrent, gbc);
        }

        jbSaveSettings = new JButton("Salva");
        jbSaveSettings.setBorder(SBBORDER);
        jbAnnullaSettings = new JButton("Annulla");
        jbAnnullaSettings.setBorder(SBBORDER);
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

        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(jbAnnullaSettings, gbc);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 2;
        add(jbSaveSettings, gbc);
        setVisible(true);
    }

    private void initTree() {
        jtSettings = new JTree(proxy.getTreeNode());
        jtSettings.setBackground(this.getBackground());
        jtSettings.setOpaque(false);
        jtSettings.setEditable(false);
        jtSettings.setBorder(SBBORDER);
        jtSettings.setPreferredSize(new Dimension(150, 430));
        jtSettings.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtSettings.setSelectionRow(1);

        jtSettings.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent evt) {
                valueChangedEventTreeNodeSelect(evt);
            }
        });
        jtSettings.setVisible(true);
    }

    private void initPanelSettingsGlobal() {
        jpSettingGlobal = new JPanel(new GridBagLayout());
        jpSettingGlobal.setPreferredSize(INTERNALSETTINGS);

        GridBagConstraints gbc = new GridBagConstraints();
        int y = -1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 1, 2, 1);
        gbc.gridheight = 2;

        jpSettingGlobal.add(new JLabel("Aggiorna RSS"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;

        DefaultComboBoxModel dcbmM = new DefaultComboBoxModel(new String[]{
                    "3", "6", "10", "15", "20", "30", "45"});
        jcbMinuti = new JComboBox(dcbmM);
        jcbMinuti.setSelectedIndex(2);
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
        jlDataAggiornamento = new JLabel();
        jpSettingGlobal.add(jlDataAggiornamento, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingGlobal.add(new JLabel("Destinazione Sub"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jrbDirLocal = new JRadioButton("HD locale");
        jrbDirLocal.setSelected(true);
        jpSettingGlobal.add(jrbDirLocal, gbc);

        gbc.gridx = 3;
        jrbDirSamba = new JRadioButton("HD Samba");
        jpSettingGlobal.add(jrbDirSamba, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingGlobal.add(new JLabel("Directory personalizzate"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jcbAdvancedDest = new JCheckBox("Abilitato");
        jpSettingGlobal.add(jcbAdvancedDest, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlLocal = new JLabel("Percorso locale standard");
        jlLocal.setForeground(Color.magenta);
        jpSettingGlobal.add(jlLocal, gbc);

        gbc.gridx = 2;
        jtfDestSub = new JTextField();
        jtfDestSub.setColumns(20);
        jpSettingGlobal.add(jtfDestSub, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        jbDestSub = new JButton("Seleziona");
        jbDestSub.setBorder(SBBORDER);
        jbDestSub.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestSubMouseClicked();
            }
        });
        jpSettingGlobal.add(jbDestSub, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlDomain = new JLabel("Samba Dominio");
        jlDomain.setForeground(Color.blue);
        jpSettingGlobal.add(jlDomain, gbc);

        gbc.gridx = 2;
        jtfSambaDomain = new JTextField();
        jtfSambaDomain.setColumns(20);
        jpSettingGlobal.add(jtfSambaDomain, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlSip = new JLabel("Samba IP");
        jlSip.setForeground(Color.blue);
        jpSettingGlobal.add(jlSip, gbc);

        gbc.gridx = 2;
        jtfSambaIP = new JTextField();
        jtfSambaIP.setColumns(20);
        jpSettingGlobal.add(jtfSambaIP, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlSdir = new JLabel("Samba cartella condivisa");
        jlSdir.setForeground(Color.blue);
        jpSettingGlobal.add(jlSdir, gbc);

        gbc.gridx = 2;
        jtfSambaDir = new JTextField();
        jtfSambaDir.setColumns(20);
        jpSettingGlobal.add(jtfSambaDir, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlSuser = new JLabel("Samba Username");
        jlSuser.setForeground(Color.blue);
        jpSettingGlobal.add(jlSuser, gbc);

        gbc.gridx = 2;
        jtfSambaUser = new JTextField();
        jtfSambaUser.setColumns(20);
        jpSettingGlobal.add(jtfSambaUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlSpwd = new JLabel("Samba Password");
        jlSpwd.setForeground(Color.blue);
        jpSettingGlobal.add(jlSpwd, gbc);

        gbc.gridx = 2;
        jpfSamba = new JPasswordField(20);
        jpSettingGlobal.add(jpfSamba, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(new JLabel("Look&Feel"), gbc);

        gbc.gridx = 2;
        DefaultComboBoxModel dcbmL = new DefaultComboBoxModel(FeedWorkerClient.getApplication().getAvailableLookAndFeel().toArray());
        jcbLookFeel = new JComboBox(dcbmL);
        jpSettingGlobal.add(jcbLookFeel, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(new JLabel("Avviso audio rss"), gbc);

        gbc.gridx = 2;
        jcbAudio = new JCheckBox("Abilitato");
        jpSettingGlobal.add(jcbAudio, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(new JLabel("Timeout"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        DefaultComboBoxModel dcbmT = new DefaultComboBoxModel(new String[]{
                    "3", "6", "9", "12", "15", "18"});
        jcbTimeout = new JComboBox(dcbmT);
        jcbTimeout.setSelectedIndex(2);
        jpSettingGlobal.add(jcbTimeout, gbc);

        gbc.gridx = 3;
        jpSettingGlobal.add(new JLabel("secondi"), gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        jpSettingGlobal.add(new JLabel("Avvio iconizzato"), gbc);

        gbc.gridx = 2;
        jcbRunIconized = new JCheckBox("Abilitato");
        jpSettingGlobal.add(jcbRunIconized, gbc);
        /*
         * gbc.gridx = 0; gbc.gridy = ++y; gbc.gridwidth = 2; JLabel jlFont =
         * new JLabel("Font da usare"); jpSettingGlobal.add(jlFont, gbc);
         *
         * gbc.gridx = 2; DefaultComboBoxModel dcbmF = new
         * DefaultComboBoxModel(Awt.getAvailableFont()); jcbFont = new
         * JComboBox(dcbmF); jcbFont.setEnabled(false);
         * jpSettingGlobal.add(jcbFont, gbc);
         */
        bgLocalSamba = new ButtonGroup();
        bgLocalSamba.add(jrbDirLocal);
        bgLocalSamba.add(jrbDirSamba);
    }

    /** inizializza il pannello dei settaggi itasa */
    private void initPanelSettingsItasa() {
        jpSettingItasa = new JPanel(new GridBagLayout());
        jpSettingItasa.setPreferredSize(INTERNALSETTINGS);
        int y = -1;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 1, 2, 1);

        JLabel jlItasa = new JLabel("RSS Itasa:");
        jlItasa.setForeground(Color.magenta);
        jpSettingItasa.add(jlItasa, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 3;
        jtfRssItasa = new JTextField();
        jtfRssItasa.setColumns(27);
        jpSettingItasa.add(jtfRssItasa, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlMyItasa = new JLabel("RSS myItasa:");
        jlMyItasa.setForeground(Color.magenta);
        jpSettingItasa.add(jlMyItasa, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 3;
        jtfRssMyItasa = new JTextField();
        jtfRssMyItasa.setColumns(27);
        jpSettingItasa.add(jtfRssMyItasa, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingItasa.add(new JLabel("myItasa download sub"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        jrbDownAuto = new JRadioButton("Automatico");
        jpSettingItasa.add(jrbDownAuto, gbc);

        gbc.gridx = 3;
        jrbDownManual = new JRadioButton("Manuale");
        jrbDownManual.setSelected(true);
        jpSettingItasa.add(jrbDownManual, gbc);

        bgDownItasa = new ButtonGroup();
        bgDownItasa.add(jrbDownAuto);
        bgDownItasa.add(jrbDownManual);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        jpSettingItasa.add(new JLabel("myItasa download automatico avvio"), gbc);

        gbc.gridx = 2;
        jcbDownloadMyitasaStartup = new JCheckBox("Abilitato");
        jpSettingGlobal.add(jcbDownloadMyitasaStartup, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        JLabel jlIuser = new JLabel("Username");
        jlIuser.setForeground(Color.red);
        jpSettingItasa.add(jlIuser, gbc);

        gbc.gridx = 2;
        jtfItasaUser = new JTextField();
        jtfItasaUser.setColumns(20);
        jpSettingItasa.add(jtfItasaUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++y;
        JLabel jlIpwd = new JLabel("Password");
        jlIpwd.setForeground(Color.red);
        jpSettingItasa.add(jlIpwd, gbc);

        gbc.gridx = 2;
        jpfItasa = new JPasswordField(20);
        jpSettingItasa.add(jpfItasa, gbc);

        jpSettingItasa.setVisible(false);
    }

    /** inizializzo il pannello settaggi torrent */
    private void initPanelSettingsTorrent() {
        jpSettingTorrent = new JPanel(new GridBagLayout());
        jpSettingTorrent.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = new GridBagConstraints();

        int y = -1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = ++y;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 1, 2, 1);

        jpSettingTorrent.add(new JLabel("Destinazione Torrent"), gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        jtfDestTorrent = new JTextField();
        jtfDestTorrent.setColumns(20);
        jpSettingTorrent.add(jtfDestTorrent, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        jbDestTorrent = new JButton("Seleziona");
        jbDestTorrent.setBorder(SBBORDER);
        jbDestTorrent.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDestTorrentMouseClicked();
            }
        });
        jpSettingTorrent.add(jbDestTorrent, gbc);

        jpSettingTorrent.setVisible(false);
    }

    /** inizializzo il pannello settaggi subsfactory */
    private void initPanelSettingsSubsf() {
        jpSettingSubsf = new JPanel(new GridBagLayout());
        jpSettingSubsf.setVisible(false);
        jpSettingSubsf.setPreferredSize(INTERNALSETTINGS);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        jpSettingSubsf.add(new JLabel("Indirizzo RSS Subsfactory: "), gbc);

        gbc.gridx = 1;
        jtfRssSubsf = new JTextField(25);
        jpSettingSubsf.add(jtfRssSubsf, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        jpSettingSubsf.add(new JLabel("Indirizzo RSS Subsf personalizzato: "), gbc);

        gbc.gridx = 1;
        jtfRssMySubsf = new JTextField(25);
        jpSettingSubsf.add(jtfRssMySubsf, gbc);
    }

    private void valueChangedEventTreeNodeSelect(TreeSelectionEvent evt) {
        // Get all nodes whose selection status has changed
        TreePath[] paths = evt.getPaths();
        // Iterate through all affected nodes
        for (int i = 0; i < paths.length; i++) {
            String temp = paths[i].toString();
            if (evt.isAddedPath(i) && (temp.length() > 10)) {
                String node = temp.substring(11, temp.length() - 1);
                if (node.equalsIgnoreCase("General")) {
                    if (prop.hasItasaOption()) {
                        jpSettingItasa.setVisible(false);
                    }
                    if (prop.hasSubsfactoryOption()) {
                        jpSettingSubsf.setVisible(false);
                    }
                    if (prop.hasTorrentOption()) {
                        jpSettingTorrent.setVisible(false);
                    }
                    jpSettingGlobal.setVisible(true);
                } else if (node.equalsIgnoreCase("Itasa")) {
                    jpSettingGlobal.setVisible(false);
                    if (prop.hasSubsfactoryOption()) {
                        jpSettingSubsf.setVisible(false);
                    }
                    if (prop.hasTorrentOption()) {
                        jpSettingTorrent.setVisible(false);
                    }
                    jpSettingItasa.setVisible(true);
                } else if (node.equalsIgnoreCase("Subsfactory")) {
                    jpSettingGlobal.setVisible(false);
                    if (prop.hasItasaOption()) {
                        jpSettingItasa.setVisible(false);
                    }
                    if (prop.hasTorrentOption()) {
                        jpSettingTorrent.setVisible(false);
                    }
                    jpSettingSubsf.setVisible(true);
                } else if (node.equalsIgnoreCase("Torrent")) {
                    jpSettingGlobal.setVisible(false);
                    if (prop.hasItasaOption()) {
                        jpSettingItasa.setVisible(false);
                    }
                    if (prop.hasSubsfactoryOption()) {
                        jpSettingSubsf.setVisible(false);
                    }
                    jpSettingTorrent.setVisible(true);
                }
            }
        }
    } // end valueChangedEventTreeNodeSelect

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
        settingsGlobalValue();
        if (prop.hasItasaOption()) {
            settingsItasaValue();
        }
        if (prop.hasSubsfactoryOption()) {
            settingsSubsfValue();
        }
        if (prop.hasTorrentOption()) {
            settingsTorrentValue();
        }
    }

    /* Popola le impostazioni con il properties caricato */
    private void settingsGlobalValue() {
        jtfDestSub.setText(prop.getSubtitleDestinationFolder());
        jcbMinuti.setSelectedItem(prop.getRefreshInterval());
        jcbLookFeel.setSelectedItem(prop.getApplicationLookAndFeel());
        jcbAudio.setSelected(prop.isEnabledAudioAdvisor());
        jrbDirLocal.setSelected(prop.isLocalFolder());
        jrbDirSamba.setSelected(!prop.isLocalFolder());
        jtfSambaDomain.setText(prop.getCifsShareDomain());
        jtfSambaDir.setText(prop.getCifsSharePath());
        jtfSambaIP.setText(prop.getCifsShareLocation());
        jtfSambaUser.setText(prop.getCifsShareUsername());
        jpfSamba.setText(prop.getCifsSharePassword());
        jcbTimeout.setSelectedItem(prop.getHttpTimeout());
        jcbAdvancedDest.setSelected(prop.isEnabledCustomDestinationFolder());
        jcbRunIconized.setSelected(prop.isEnabledIconizedRun());
    }

    private void settingsItasaValue() {
        jtfRssItasa.setText(prop.getItasaFeedURL());
        jtfRssMyItasa.setText(prop.getMyitasaFeedURL());
        jtfItasaUser.setText(prop.getItasaUsername());
        jpfItasa.setText(prop.getItasaPassword());
        jrbDownAuto.setSelected(prop.isAutoDownloadMyItasa());
        jrbDownManual.setSelected(!prop.isAutoDownloadMyItasa());
    }

    private void settingsTorrentValue() {
        jtfDestTorrent.setText(prop.getTorrentDestinationFolder());
    }

    private void settingsSubsfValue() {
        jtfRssSubsf.setText(prop.getSubsfactoryFeedURL());
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
                jcbAudio.isSelected(), jcbTimeout.getSelectedItem().toString(),
                jcbAdvancedDest.isSelected(), jcbRunIconized.isSelected(),
                jtfRssItasa.getText(), jtfRssMyItasa.getText(), jtfItasaUser.getText(), 
                new String(jpfItasa.getPassword()), jrbDownAuto.isSelected(),
                jtfRssSubsf.getText(), jtfRssMySubsf.getText(), jtfDestTorrent.getText());

        if (previousLookAndFeel != jcbLookFeel.getSelectedItem()) {
            int returnCode = JOptionPane.showConfirmDialog(this,
                    "Per applicare il nuovo Look & Feel è necessario riavviare "
                    + proxy.getNameApp()
                    + ". \nPremi Si per riavviare, No altrimenti",
                    "Info", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (returnCode == 1) {
                return;
            }
            proxy.restartApplication();
        }
    }
}
