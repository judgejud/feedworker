package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.JFrameEventIconDate;
import org.feedworker.client.frontend.events.JFrameEventIconDateListener;
import org.feedworker.client.frontend.events.JFrameEventOperation;
import org.feedworker.client.frontend.events.JFrameEventOperationListener;
import org.feedworker.client.frontend.panel.jpItasa;
import org.feedworker.client.frontend.panel.jpSubsfactory;
import org.feedworker.client.frontend.panel.jpTorrent;
import org.feedworker.client.frontend.panel.jpSubtitleDest;
import org.feedworker.client.frontend.panel.jpCalendar;
import org.feedworker.client.frontend.panel.jpSetting;
import org.feedworker.client.frontend.panel.jpStatusBar;

import org.jfacility.java.awt.AWT;
import org.jfacility.javax.swing.Swing;

/**Gui base per java 1.5
 * 
 * @author luca judge
 */
public class jfMain extends JFrame implements WindowListener, 
                                            JFrameEventIconDateListener, 
                                            JFrameEventOperationListener {

    private final Dimension SCREEN_SIZE = new Dimension(1024, 768);
    private final Dimension TAB_SIZE = new Dimension(1024, 580);
    protected jpSetting settingsJP;
    protected JTabbedPane mainJTP;
    protected jtpLog logJTP;
    protected Mediator proxy = Mediator.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private jpItasa itasaJP;
    private jpTorrent torrentJP;
    private jpSubsfactory subsfactoryJP;
    private jdResultSearchTv resultSearchTvJD = jdResultSearchTv.getDialog();
    private jdProgressBarImport progressBar;
    private jpStatusBar statusBar;

    /** Costruttore */
    public jfMain() {
        super();
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
        settingsJP = jpSetting.getPanel();

        if (prop.isItasaOption()) {
            itasaJP = jpItasa.getPanel();
            mainJTP.addTab("Itasa", itasaJP);
        }
        if (prop.isSubsfactoryOption()) {
            subsfactoryJP = jpSubsfactory.getPanel();
            mainJTP.addTab("Subsfactory", subsfactoryJP);
        }
        if (prop.isTorrentOption()) {
            torrentJP = jpTorrent.getPanel();
            mainJTP.addTab("Torrent", torrentJP);
        }
        mainJTP.addTab("Subtitle Destination", jpSubtitleDest.getPanel());
        mainJTP.addTab("Calendar", jpCalendar.getPanel());
        mainJTP.addTab("Settings", settingsJP);

        logJTP = new jtpLog();
        mainJTP.addTab("Log", new JScrollPane(logJTP));
        
        statusBar = new jpStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        if (prop.isApplicationFirstTimeUsed()) {
            mainJTP.setSelectedComponent(settingsJP);
            changeEnabledButton(false);
            logJTP.appendOK("Benvenuto al primo utilizzo.");
            logJTP.appendAlert("Per poter usare il client, "
                    + "devi configurare le impostazioni presenti nella specifica sezione");
        } else {
            settingsJP.settingsValue();
            logJTP.appendOK("Ciao " + prop.getItasaUsername()+ ", impostazioni caricate.");
        }
        pack();
    }

    public void initializeSysTray() throws URISyntaxException {
        setVisible(true);
        setState(Frame.ICONIFIED);
    }

    /** inizializza la barra di menù */
    private void initializeMenuBar() {
        JMenuBar applicationJMB = new JMenuBar();

        JMenu fileJM = new JMenu(" Operazioni ");
        applicationJMB.add(fileJM);

        JMenuItem clearLogJMI = new JMenuItem(" Pulisci log ");
        clearLogJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logJTP.setText(null);
            }
        });
        fileJM.add(clearLogJMI);

        JMenuItem bruteRefreshJMI = new JMenuItem(" Forza aggiornamento RSS");
        bruteRefreshJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.bruteRefresh();
            }
        });
        fileJM.add(bruteRefreshJMI);

        JMenuItem restartJMI = new JMenuItem(" Riavvio ");
        restartJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (requestClose("riavviare"))
                    proxy.restartApplication(settingsJP.getDataAggiornamento());
            }
        });
        fileJM.add(restartJMI);

        JMenuItem jmiBackup = new JMenuItem(" Backup impostazioni ");
        jmiBackup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.invokeBackup(jfMain.this);
            }
        });
        fileJM.add(jmiBackup);

        JMenuItem closeJMI = new JMenuItem(" Esci ");
        closeJMI.addActionListener(new ActionListener() {
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
        JMenuItem jmiPrintTomorrow = new JMenuItem(" Stampa domani ");
        JMenuItem jmiPrintYesterday = new JMenuItem(" Stampa ieri ");
        jmiPrintToday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printDay(0);
            }
        });
        jmiPrintTomorrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printDay(1);
            }
        });
        jmiPrintYesterday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printDay(-1);
            }
        });

        jmSerial.add(jmiPrintToday);
        jmSerial.add(jmiPrintTomorrow);
        jmSerial.add(jmiPrintYesterday);

        JMenu nasJM = new JMenu(" NAS ");
        JMenuItem videoMoveJMI = new JMenuItem(" Video move ");
        JMenuItem taskStatusJMI = new JMenuItem(" Task status ");
        JMenuItem deleteCompletedTaskJMI = new JMenuItem(
                " Delete completed task ");
        videoMoveJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoMoveVideo();
            }
        });
        taskStatusJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoStatus();
            }
        });
        deleteCompletedTaskJMI.addActionListener(new ActionListener() {
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
        helpSubtitleRoleJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(), createPopupRuleHelp(),
                        "Come creare le regole per i sottotitoli",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpJM.add(helpSubtitleRoleJMI);

        JMenuItem helpSystemInfoJMI = new JMenuItem(" Informazioni Sistema ");
        helpSystemInfoJMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(), createPopupSystemInfo(),
                        "Informazioni di Sistema",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpJM.add(helpSystemInfoJMI);

        JMenuItem jmiHelpInfoFeedColor = new JMenuItem(" Legenda Colori Feed ");
        jmiHelpInfoFeedColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(), createInfoFeedColor(),
                        "Legenda Colori",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpJM.add(jmiHelpInfoFeedColor);
        // Install the menu bar in the frame
        setJMenuBar(applicationJMB);
    }

    private JPanel createInfoFeedColor(){
        JPanel jpanel = new JPanel();
        Dimension dim = new Dimension(300, 200);
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Colore ciano: versione dei sub normale");
        jtaHelp.append("\nColore rosso: versione dei sub 720p");
        jtaHelp.append("\nColore marrone: versione dei sub dvdrip");
        jtaHelp.append("\nColore viola: versione dei sub bluray");
        //jtaHelp.append("\nColore ciano: versione dei sub normale");
        //jtaHelp.append("\nColore ciano: versione dei sub normale");
        jpanel.add(jtaHelp);
        return jpanel;
    }

    /**inizializza il pannello per l'help regola sub
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
        jtaHelp.append("Serie: inserire il nome del file scaricato sostituendo i \".\" con lo spazio");
        jtaHelp.append("\nesempio: Lost.s06e06.sub.itasa.srt -->lost (n.b. no spazio)");
        jtaHelp.append("\nesempio: Spartacus.Blood.And.Sand.s01e06.sub.itasa.srt -->"
                + "spartacus blood and sand");
        jtaHelp.append("\n\nStagione: immettere il numero della stagione per la quale si vuole "
                + "creare la regola");
        jtaHelp.append("\nad eccezione degli anime/cartoon dove la stagione molto probabilmente è "
                + "unica e quindi 1");
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

    private JPanel createPopupSystemInfo(){
        JPanel jpanel = new JPanel();
        JTable jtable = new JTable(proxy.getModelSystemInfo());
        jtable.setRowSelectionAllowed(false);
        jtable.getTableHeader().setReorderingAllowed(false);
        jtable.setPreferredSize(new Dimension(500,145));
        Swing.setTableDimensionLockColumn(jtable, 0, 120);
        jpanel.add(jtable);
        jpanel.setVisible(true);
        return jpanel;
    }

    /** chiude l'applicazione */
    protected void applicationClose() {
        String temp = settingsJP.getDataAggiornamento();
        this.dispose();
        proxy.closeApp(temp);
    }
    
    private boolean requestClose(String oper){
        int returnCode = JOptionPane.showConfirmDialog(this,
                "Sei sicuro di "+oper+"?", "Info", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (returnCode == 1)
            return false;
        return true;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (requestClose("uscire"))
            applicationClose();
    }

    @Override
    public void objReceived(JFrameEventIconDate evt) {
        if (evt.getDate() != null)
            settingsJP.setDataAggiornamento(evt.getDate());
    }

    @Override
    public void objReceived(final JFrameEventOperation evt) {
        if (evt.getOperaz() != null) {
            if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationEnableButton()))
                changeEnabledButton(true);
            else if (evt.getOperaz().equalsIgnoreCase(proxy.getSearchTV()))
                resultSearchTvJD.setVisible(true);
            else if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationFocus()))
                requestFocus();
            else if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationImportShow())){
                progressBar = new jdProgressBarImport(this, evt.getMax());
                AWT.centerComponent(progressBar, this);
            } else if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationImportIncrement())){
            	progressBar.setProgress(evt.getMax());
            }
        }
    }

    /**risponde all'evento di cambiare lo stato (abilitato/disabilitato) dei
     * bottoni
     *
     * @param e stato
     */
    protected void changeEnabledButton(boolean e) {
        if (prop.isItasaOption())
            itasaJP.setButtonEnabled(e);
        if (prop.isSubsfactoryOption())
            subsfactoryJP.setEnableButton(e);
        if (prop.isTorrentOption())
            torrentJP.setButtonEnabled(e);
    }

    /** inizializza i listener per l'ascolto */
    private void initListeners() {
        addWindowListener(this);
        proxy.setTextPaneListener(logJTP);
        proxy.setFrameListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
} // end class