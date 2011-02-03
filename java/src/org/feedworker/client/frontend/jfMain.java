package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.JFrameEventIconDate;
import org.feedworker.client.frontend.events.JFrameEventIconDateListener;
import org.feedworker.client.frontend.events.JFrameEventOperation;
import org.feedworker.client.frontend.events.JFrameEventOperationListener;
import org.feedworker.client.frontend.panel.jpCalendar;
import org.feedworker.client.frontend.panel.jpItasa;
import org.feedworker.client.frontend.panel.jpLog;
import org.feedworker.client.frontend.panel.jpSearchSubItasa;
import org.feedworker.client.frontend.panel.jpSetting;
import org.feedworker.client.frontend.panel.jpSettingEnhanced;
import org.feedworker.client.frontend.panel.jpStatusBar;
import org.feedworker.client.frontend.panel.jpSubsfactory;
import org.feedworker.client.frontend.panel.jpSubtitleDest;
import org.feedworker.client.frontend.panel.jpTorrent;

import org.jfacility.java.awt.AWT;
import org.jfacility.javax.swing.ButtonTabComponent;

import org.opensanskrit.widget.ProgressDialog;
import org.opensanskrit.widget.ProgressEvent;
import org.opensanskrit.widget.ProgressListener;
import org.opensanskrit.widget.SystemInfoDialog;

/**GUI
 * 
 * @author luca judge
 */
public class jfMain extends JFrame implements WindowListener,
                    JFrameEventIconDateListener, JFrameEventOperationListener {
    //VARIABLES PRIVATE FINAL
    private final Dimension SCREEN_SIZE = new Dimension(1024, 768);
    private final Dimension TAB_SIZE = new Dimension(1024, 580);
    //VARIABLES PRIVATE
    private jpSetting jpSettings;
    private jpSubtitleDest jpDestination;
    private JTabbedPane mainJTP;
    private Mediator proxy = Mediator.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private jpItasa itasaJP;
    private jpTorrent torrentJP;
    private jpSubsfactory subsfactoryJP;
    private jpLog logJP;
    private jpSearchSubItasa jpSearch;
    private jpStatusBar statusBar;
    private jdResultSearchTv resultSearchTvJD = jdResultSearchTv.getDialog();
    private ProgressDialog progressBar;
    private EnhancedSystemTray systemTray;

    /** Costruttore */
    public jfMain() {
        super();
        systemTray = EnhancedSystemTray.getInstance(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(SCREEN_SIZE);
        this.setMinimumSize(SCREEN_SIZE);
        this.setTitle(proxy.getTitle());
        this.setIconImage(proxy.getApplicationIcon());
        initializeMenuBar();
        initializeComponents();
        initListeners();
    }

    /** Inizializzo i componenti per la GUI */
    private void initializeComponents() {
        this.setLayout(new BorderLayout());
        mainJTP = new JTabbedPane();
        mainJTP.setPreferredSize(TAB_SIZE);
        this.add(mainJTP, BorderLayout.CENTER);
        itasaJP = jpItasa.getPanel();
        mainJTP.addTab("Itasa", itasaJP);
        jpSearch = new jpSearchSubItasa();
        mainJTP.addTab("Search sub itasa", jpSearch);

        if (prop.isSubsfactoryOption()) {
            subsfactoryJP = jpSubsfactory.getPanel();
            mainJTP.addTab("Subsfactory", subsfactoryJP);
        }
        if (prop.isTorrentOption()) {
            torrentJP = jpTorrent.getPanel();
            mainJTP.addTab("Torrent", torrentJP);
        }
        mainJTP.addTab("Calendar", jpCalendar.getPanel());
        
        jpDestination = jpSubtitleDest.getPanel();
        mainJTP.addTab("Subtitle Destination", jpDestination);
        mainJTP.setTabComponentAt(mainJTP.getTabCount() - 1,
                                        new ButtonTabComponent(mainJTP));
        
        if (proxy.isJava6()) {
            jpSettings = jpSettingEnhanced.getPanel();
        } else {
            jpSettings = jpSetting.getPanel();
        }
        mainJTP.addTab("Settings", jpSettings);
        mainJTP.setTabComponentAt(mainJTP.getTabCount() - 1,
                new ButtonTabComponent(mainJTP));
        
        logJP = new jpLog();
        mainJTP.addTab("Log", logJP);
        mainJTP.setTabComponentAt(mainJTP.getTabCount() - 1,
                new ButtonTabComponent(mainJTP));

        statusBar = new jpStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        if (prop.isApplicationFirstTimeUsed()) {
            mainJTP.setSelectedComponent(jpSettings);
            changeEnabledButton(false);
            proxy.printOk("Benvenuto al primo utilizzo.");
            proxy.printAlert("Per poter usare il client, devi configurare le "
                    + "impostazioni presenti nella specifica sezione");
        } else {
            jpSettings.settingsValue();
            proxy.printOk("Ciao " + prop.getItasaUsername()
                    + ", impostazioni caricate.");
        }
        pack();
    }

    public void initializeSystemTray() throws URISyntaxException {
        if (systemTray != null) {
            systemTray.showSystemTray();
        } else {
            setVisible(true);
        }
    }

    /** inizializza la barra di menù */
    private void initializeMenuBar() {
        JMenuBar applicationJMB = new JMenuBar();

        JMenu fileJM = new JMenu(" Operazioni ");
        applicationJMB.add(fileJM);

        JMenuItem clearLogJMI = new JMenuItem(" Pulisci log ");
        clearLogJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                logJP.cleanLogs();
            }
        });
        fileJM.add(clearLogJMI);

        JMenuItem bruteRefreshJMI = new JMenuItem(" Forza aggiornamento RSS");
        bruteRefreshJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.bruteRefresh();
            }
        });
        fileJM.add(bruteRefreshJMI);

        JMenuItem restartJMI = new JMenuItem(" Riavvio ");
        restartJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (requestClose("riavviare")) {
                    proxy.restartApplication(jpSettings.getDataAggiornamento());
                }
            }
        });
        fileJM.add(restartJMI);

        JMenuItem jmiBackup = new JMenuItem(" Backup impostazioni ");
        jmiBackup.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.invokeBackup(jfMain.this);
            }
        });
        fileJM.add(jmiBackup);

        JMenuItem closeJMI = new JMenuItem(" Esci ");
        closeJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowEvent we = new WindowEvent(jfMain.this,
                        WindowEvent.WINDOW_CLOSING);
                we.setSource(new JMenuItem());
                jfMain.this.dispatchEvent(we);
            }
        });
        fileJM.add(closeJMI);

        JMenu jmSerial = new JMenu(" Serial tv ");
        applicationJMB.add(jmSerial);

        JMenuItem jmiPrintToday = new JMenuItem(" Stampa oggi ");        
        jmiPrintToday.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printDay(0);
            }
        });
        
        JMenuItem jmiPrintTomorrow = new JMenuItem(" Stampa domani ");        
        jmiPrintTomorrow.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printDay(1);
            }
        });
        
        JMenuItem jmiPrintYesterday = new JMenuItem(" Stampa ieri ");
        jmiPrintYesterday.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printDay(-1);
            }
        });

        jmSerial.add(jmiPrintToday);
        jmSerial.add(jmiPrintTomorrow);
        jmSerial.add(jmiPrintYesterday);

        JMenu jmWindowTab = new JMenu(" Visualizza ");
        applicationJMB.add(jmWindowTab);
        
        JMenuItem jmiWindowSubDest = new JMenuItem(" Subtitle Destination ");
        jmiWindowSubDest.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpDestination);
            }
        });
        
        JMenuItem jmiWindowSetting = new JMenuItem(" Settings ");
        jmiWindowSetting.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpSettings);
            }
        });
        
        JMenuItem jmiWindowLog = new JMenuItem(" Log ");
        jmiWindowLog.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(logJP);
            }
        });
        
        jmWindowTab.add(jmiWindowSubDest);
        jmWindowTab.add(jmiWindowSetting);
        jmWindowTab.add(jmiWindowLog);

        JMenu nasJM = new JMenu(" NAS ");
        JMenuItem videoMoveJMI = new JMenuItem(" Video move ");
        JMenuItem taskStatusJMI = new JMenuItem(" Task status ");
        JMenuItem deleteCompletedTaskJMI = new JMenuItem(
                " Delete completed task ");
        videoMoveJMI.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoMoveVideo();
            }
        });
        taskStatusJMI.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoStatus();
            }
        });
        deleteCompletedTaskJMI.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoClearFinish();
            }
        });
        nasJM.add(videoMoveJMI);
        nasJM.add(taskStatusJMI);
        nasJM.add(deleteCompletedTaskJMI);
        applicationJMB.add(nasJM);

        JMenu helpJM = new JMenu(" Help ");
        applicationJMB.add(helpJM);

        JMenuItem helpSubtitleRoleJMI = new JMenuItem(" Regola Sottotitolo ");
        helpSubtitleRoleJMI.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(),
                        createPopupRuleHelp(),
                        "Come creare le regole per i sottotitoli",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpJM.add(helpSubtitleRoleJMI);

        JMenuItem helpSystemInfoJMI = new JMenuItem(" Informazioni Sistema ");
        helpSystemInfoJMI.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                showSystemInfo();
            }
        });
        helpJM.add(helpSystemInfoJMI);

        JMenuItem jmiHelpInfoFeedColor = new JMenuItem(" Legenda Colori Feed ");
        jmiHelpInfoFeedColor.addActionListener(new ActionListener()  {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(),
                        createInfoFeedColor(), "Legenda Colori",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpJM.add(jmiHelpInfoFeedColor);
        // Install the menu bar in the frame
        setJMenuBar(applicationJMB);
    }

    private JPanel createInfoFeedColor() {
        JPanel jpanel = new JPanel();
        Dimension dim = new Dimension(300, 200);
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Colore ciano: versione dei sub normale"
                + "\nColore rosso: versione dei sub 720p"
                + "\nColore marrone: versione dei sub dvdrip");
        jtaHelp.append("\nColore viola: versione dei sub bluray");
        // jtaHelp.append("\nColore ciano: versione dei sub normale");
        // jtaHelp.append("\nColore ciano: versione dei sub normale");
        jpanel.add(jtaHelp);
        return jpanel;
    }

    /**
     * inizializza il pannello per l'help regola sub
     * 
     * @return pannello helprolesub
     */
    private JPanel createPopupRuleHelp() {
        JPanel jpanel = new JPanel();
        Dimension dim = new Dimension(550, 300);
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Serie: inserire il nome del file scaricato sostituendo i "
                + "\".\" con lo spazio \nesempio: Lost.s06e06.sub.itasa.srt -->lost "
                + "(n.b. no spazio) \nesempio: Spartacus.Blood.And.Sand.s01e06.sub."
                + "itasa.srt -->spartacus blood and sand");
        jtaHelp.append("\n\nStagione: immettere il numero della stagione per la "
                + "quale si vuole creare la regola\nad eccezione degli anime/cartoon "
                + "dove la stagione molto probabilmente è unica e quindi 1");
        jtaHelp.append("\n\nQualità: * = tutte le versioni dei sub ; normale = sub qualità standard");
        jtaHelp.append("\n720p = versione 720p; 1080p = versione 1080p; dvdrip = versione dvdrip");
        jtaHelp.append("\nbluray = versione bluray; hr = versione hr");
        jtaHelp.append("\n\\ = se avete già impostato una regola per una versione e volete che le "
                + "altre versioni");
        jtaHelp.append("\nvadino in un'altra cartella basterà selezionare questa opzione "
                + "\"differenziale\"");
        jtaHelp.append("\n\nPercorso: specificare il percorso dove desiderate che vi estragga il sub");
        jpanel.add(jtaHelp);
        return jpanel;
    }

    private void showSystemInfo() {
        SystemInfoDialog sid = new SystemInfoDialog(this,
                proxy.getPropertiesInfo());
        AWT.centerComponent(sid, this);
        sid.setVisible(true);
    }

    /** chiude l'applicazione */
    protected void applicationClose() {
        String temp = jpSettings.getDataAggiornamento();
        this.dispose();
        proxy.closeApp(temp);
    }

    private boolean requestClose(String oper) {
        int returnCode = JOptionPane.showConfirmDialog(this, "Sei sicuro di "
                + oper + "?", "Info", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (returnCode == 1) {
            return false;
        }
        return true;
    }

    @Override
    public void windowClosing(WindowEvent we) {
        if (we.getSource() instanceof JMenuItem) {
            if (requestClose("uscire")) {
                applicationClose();
            }
        } else {
            setVisible(false);
            try {
                initializeSystemTray();
            } catch (Exception e) {
                proxy.printError(e);
                setVisible(true);
            }
        }
    }

    @Override
    public void objReceived(JFrameEventIconDate evt) {
        if ((evt.isIcontray()) && (!this.isVisible()))
            systemTray.notifyIncomingFeed();
        
        if (evt.getDate() != null)
            jpSettings.setDataAggiornamento(evt.getDate());
    }

    @Override
    public void objReceived(final JFrameEventOperation evt) {
        if (evt.getOperaz() != null) {
            if (evt.getOperaz().equalsIgnoreCase(
                    proxy.getOperationEnableButton())) {
                changeEnabledButton(true);
            } else if (evt.getOperaz().equalsIgnoreCase(proxy.getSearchTV())) {
                resultSearchTvJD.setVisible(true);
            } else if (evt.getOperaz().equalsIgnoreCase(
                    proxy.getOperationFocus())) {
                requestFocus();
            } else if (evt.getOperaz().equalsIgnoreCase(
                    proxy.getOperationProgressShow())) {
                progressBar = new ProgressDialog(this,
                        "Operazione in corso...", evt.getMax());
                AWT.centerComponent(progressBar, this);
                progressBar.addProgressListener(new ProgressListener()  {

                    @Override
                    public void objReceived(ProgressEvent evt) {
                        proxy.stopImport();
                    }
                });
            } else if (evt.getOperaz().equalsIgnoreCase(
                    proxy.getOperationProgressIncrement())) {
                progressBar.setProgress(evt.getMax());
            }
        }
    }

    /**
     * risponde all'evento di cambiare lo stato (abilitato/disabilitato) dei
     * bottoni
     * 
     * @param e
     *            stato
     */
    protected void changeEnabledButton(boolean e) {
        itasaJP.setButtonEnabled(e);
        if (prop.isSubsfactoryOption()) {
            subsfactoryJP.setEnableButton(e);
        }
        if (prop.isTorrentOption()) {
            torrentJP.setButtonEnabled(e);
        }
    }

    private void checkAddTab(JPanel pane) {
        if (!mainJTP.isAncestorOf(pane)) {
            mainJTP.addTab(pane.getName(), pane);
            mainJTP.setTabComponentAt(mainJTP.getTabCount() - 1,
                    new ButtonTabComponent(mainJTP));
        }
    }

    /** inizializza i listener per l'ascolto */
    private void initListeners() {
        addWindowListener(this);
        proxy.setFrameIconDateListener(this);
        proxy.setFrameOperationListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
} // end class