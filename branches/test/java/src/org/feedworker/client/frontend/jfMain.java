package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URISyntaxException;
import javax.swing.ButtonGroup;

import javax.swing.JCheckBoxMenuItem;
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
import org.feedworker.client.frontend.events.FrameEvent;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.panel.*;

import org.jfacility.java.awt.AWT;
import org.jfacility.javax.swing.ButtonTabComponent;

import org.opensanskrit.widget.ProgressDialog;
import org.opensanskrit.widget.ProgressEvent;
import org.opensanskrit.widget.interfaces.ProgressListener;
import org.opensanskrit.widget.SystemInfoDialog;

/**GUI
 * 
 * @author luca judge
 */
public class jfMain extends JFrame implements WindowListener, FrameEventListener {
    //VARIABLES PRIVATE FINAL
    private final Dimension SCREEN_SIZE = new Dimension(1024, 768);
    private final Dimension TAB_SIZE = new Dimension(1024, 580);
    //VARIABLES PRIVATE
    private JTabbedPane mainJTP;
    private Mediator proxy;
    private ApplicationSettings prop;
    //private Panels
    private paneSetting jpSettings;
    private paneSubtitleDest jpDestination;
    private paneItasa jpItasa;
    private paneTorrent jpTorrent;
    private paneSubsfactory jpSubsfactory;
    private paneLog jpLog;
    private paneSearchSubItasa jpSearch;
    private paneStatusBar statusBar;
    private paneReminder jpReminder;
    private paneCalendar jpCalendar;
    private jdResultSearchTv resultSearchTvJD;
    private ProgressDialog progressBar;
    private EnhancedSystemTray systemTray;

    /** Costruttore */
    public jfMain() {
        super();
        proxy = Mediator.getIstance();
        prop = proxy.getSettings();
        systemTray = EnhancedSystemTray.getInstance(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(SCREEN_SIZE);
        setMinimumSize(SCREEN_SIZE);
        setTitle(proxy.getTitle());
        setIconImage(proxy.getApplicationIcon());
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
        
        jpCalendar = paneCalendar.getPanel();
        jpItasa = paneItasa.getPanel();
        jpLog = new paneLog();
        jpReminder = paneReminder.getPanel();
        jpSearch = paneSearchSubItasa.getPanel();
        jpSettings = paneSetting.getPanel();
        jpDestination = paneSubtitleDest.getPanel();
        jpTorrent = paneTorrent.getPanel();
        
        mainJTP.addTab("Itasa", jpItasa);
        if (prop.isSubsfactoryOption()) {
            jpSubsfactory = paneSubsfactory.getPanel();
            mainJTP.addTab("Subsfactory", jpSubsfactory);
        }
        if (prop.isEnablePaneTorrent())
            checkAddTab(jpTorrent, false);
        if (prop.isEnablePaneCalendar())
            checkAddTab(jpCalendar, false);
        if (prop.isEnablePaneSearchSubItasa())
            checkAddTab(jpSearch, false);
        if (prop.isEnablePaneReminder())
            checkAddTab(jpReminder, false);
        if (prop.isEnablePaneSubDestination())
            checkAddTab(jpDestination, false);
        if (prop.isEnablePaneSetting())
            checkAddTab(jpSettings, false);
        if (prop.isEnablePaneLog())
            checkAddTab(jpLog, false);

        statusBar = new paneStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        if (prop.isApplicationFirstTimeUsed()) {
            jpSettings.settingFirstLookFeel();
            checkAddTab(jpSettings,true);
            changeEnabledButton(false);
            proxy.printAlert("Benvenuto al primo utilizzo :) Per poter usare il client, "
                    + "devi configurare le impostazioni");
        } else {
            jpSettings.settingsValue();
            proxy.printOk("Ciao " + prop.getItasaUsername()
                    + ", impostazioni caricate.");
        }
        resultSearchTvJD = jdResultSearchTv.getDialog();
        pack();
    }

    public void initializeSystemTray() throws URISyntaxException {
        if (systemTray != null)
            systemTray.showSystemTray();
        else
            setVisible(true);
    }

    /** inizializza la barra di menù */
    private void initializeMenuBar() {
        JMenuBar applicationJMB = new JMenuBar();
        
        JMenu fileJM = new JMenu(" Operazioni ");
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
                if (requestClose("riavviare"))
                    proxy.restartApplication();
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
        
        JMenuItem jmiPrintLastDateRefresh = new JMenuItem(" Stampa data ultimo aggiornamento");
        jmiPrintLastDateRefresh.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.printLastDate();
            }
        });
        fileJM.add(jmiPrintLastDateRefresh);

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
        
        applicationJMB.add(fileJM);
        applicationJMB.add(jMenuSerial()); //Stampa tf
        applicationJMB.add(jMenuWindowTab());//Visualizza pannelli
        applicationJMB.add(jMenuNas()); //NAS
        applicationJMB.add(jMenuLAF()); //LAF
        applicationJMB.add(jMenuNotify()); //NOTIFICHE
        applicationJMB.add(jMenuHelp()); //HELP
        // Install the menu bar in the frame
        setJMenuBar(applicationJMB);
    }
    
    private JMenu jMenuSerial(){
        JMenu jmSerial = new JMenu(" Serial tv ");
        
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
        
        return jmSerial;
    }
    
    private JMenu jMenuWindowTab(){
        JMenu jmWindowTab = new JMenu(" Visualizza ");
        
        JMenuItem jmiWindowSubDest = new JMenuItem(" Subtitle Destination ");
        jmiWindowSubDest.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpDestination,true);
            }
        });
        
        JMenuItem jmiWindowSetting = new JMenuItem(" Settings ");
        jmiWindowSetting.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpSettings, true);
            }
        });
        
        JMenuItem jmiWindowLog = new JMenuItem(" Log ");
        jmiWindowLog.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpLog, true);
            }
        });
        
        JMenuItem jmiWindowReminder = new JMenuItem(" Reminder ");
        jmiWindowReminder.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpReminder, true);
            }
        });
        
        JMenuItem jmiWindowSearch = new JMenuItem(" Search Subtitle ");
        jmiWindowSearch.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpSearch, true);
            }
        });
        
        JMenuItem jmiWindowCalendar = new JMenuItem(" Calendar ");
        jmiWindowCalendar.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpCalendar, true);
            }
        });
        
        JMenuItem jmiWindowTorrent = new JMenuItem(" Torrent ");
        jmiWindowTorrent.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAddTab(jpTorrent, true);
            }
        });
        
        jmWindowTab.add(jmiWindowCalendar);
        jmWindowTab.add(jmiWindowLog);
        jmWindowTab.add(jmiWindowReminder);
        jmWindowTab.add(jmiWindowSearch);
        jmWindowTab.add(jmiWindowSetting);
        jmWindowTab.add(jmiWindowSubDest);
        jmWindowTab.add(jmiWindowTorrent);
        
        return jmWindowTab;
    }
    
    private JMenu jMenuHelp(){
        JMenu helpJM = new JMenu(" Help ");

        JMenuItem subtitleRule = new JMenuItem(" Regola Sottotitolo ");
        JMenuItem systemInfo = new JMenuItem(" Informazioni Sistema ");
        JMenuItem infoFeedColor = new JMenuItem(" Legenda Colori Feed ");
        JMenuItem googleCalendarSMS = new JMenuItem(" Google Calendar SMS ");
        JMenuItem calendarImport = new JMenuItem(" Importazione Calendario ");
        
        subtitleRule.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(),
                        createPopupRuleHelp(),
                        "Come creare le regole per i sottotitoli",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        systemInfo.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSystemInfo();
            }
        });

        infoFeedColor.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(),
                        createPopupInfoFeedColor(), "Legenda Colori",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        googleCalendarSMS.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(),
                        createPopupGoogleCalSMS(), "Uso del servizio sms tramite "
                        + "Google Calendar",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        calendarImport.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getParent(),
                        createPopupCalendarImport(), "Importazione Calendario",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        helpJM.add(subtitleRule);
        helpJM.add(systemInfo);
        helpJM.add(infoFeedColor);
        helpJM.add(googleCalendarSMS);
        helpJM.add(calendarImport);
        
        return helpJM;
    }
    
    private JMenu jMenuNas(){
        JMenu menu = new JMenu(" NAS ");
        JMenuItem videoMoveJMI = new JMenuItem(" Video move ");
        videoMoveJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoMoveVideo();
            }
        });
        
        JMenuItem taskStatusJMI = new JMenuItem(" Task status ");
        taskStatusJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoStatus();
            }
        });
        
        JMenuItem deleteCompletedTaskJMI = new JMenuItem(
                " Delete completed task ");
        deleteCompletedTaskJMI.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.synoClearFinish();
            }
        });
        menu.add(videoMoveJMI);
        menu.add(taskStatusJMI);
        menu.add(deleteCompletedTaskJMI);
        return menu;
    }
    
    private JMenu jMenuLAF(){
        JMenu menu = new JMenu(" Look & Feel ");
        String[] laf = proxy.getAvailableLAF();
        JCheckBoxMenuItem[] menuItems = new JCheckBoxMenuItem[laf.length];
        ButtonGroup bg = new ButtonGroup();
        for (int i=0; i<laf.length; i++){
            menuItems[i] = new JCheckBoxMenuItem(laf[i]);
            if (laf[i].equals(prop.getApplicationLookAndFeel()))
                menuItems[i].setSelected(true);
            menuItems[i].addActionListener(new ActionListener()  {
                @Override
                public void actionPerformed(ActionEvent e) {
                    proxy.changeRuntimeLaf(e.getActionCommand(),jfMain.this);
                }
            });
            bg.add(menuItems[i]);
            menu.add(menuItems[i]);
        }
        return menu;
    }
  
    private JMenu jMenuNotify(){
        JMenu jmNotify = new JMenu(" Notifiche ");
        
        JCheckBoxMenuItem jcbmiNotifyAudioRss = new JCheckBoxMenuItem(" Rss audio ");
        jcbmiNotifyAudioRss.setToolTipText("Abilita la notifica audio dei nuovi feed rss");
        jcbmiNotifyAudioRss.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.invertMenuCheck(0, ((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });
        jmNotify.add(jcbmiNotifyAudioRss);
        
        JCheckBoxMenuItem jcbmiNotifyAudioSub = new JCheckBoxMenuItem(" Sub audio");
        jcbmiNotifyAudioSub.setToolTipText("Abilita la notifica audio dei sub "
                                        + "scaricati in automatico");
        jcbmiNotifyAudioSub.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.invertMenuCheck(1, ((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });
        jmNotify.add(jcbmiNotifyAudioSub);
        
        JCheckBoxMenuItem jcbmiNotifyMail = new JCheckBoxMenuItem(" Sub e-mail ");
        jcbmiNotifyMail.setToolTipText("Abilita la notifica per posta dei sub "
                                        + "scaricati in automatico");
        jcbmiNotifyMail.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.invertMenuCheck(2, ((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });
        jmNotify.add(jcbmiNotifyMail);
        
        JCheckBoxMenuItem jcbmiNotifySms = new JCheckBoxMenuItem(" Sub sms ");
        jcbmiNotifySms.setToolTipText("Abilita la notifica per sms tramite google "
                                        + "calendar dei sub scaricati in automatico ");
        jcbmiNotifySms.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                proxy.invertMenuCheck(3, ((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });
        jmNotify.add(jcbmiNotifySms);
        
        jcbmiNotifyAudioRss.setSelected(prop.isEnableNotifyAudioRss());
        jcbmiNotifyAudioSub.setSelected(prop.isEnableNotifyAudioSub());
        jcbmiNotifyMail.setSelected(prop.isEnableNotifyMail());
        jcbmiNotifySms.setSelected(prop.isEnableNotifySms());
        
        return jmNotify;
    }
    
    private JPanel createPopupGoogleCalSMS() {
        JPanel jpanel = new JPanel();
        Dimension dim = new Dimension(550, 300);
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Inanzitutto bisogna avere un accoung google per poter "
                + "fruire del servizio");
        jtaHelp.append("\nCollegarsi a google calendar al seguente indirizzo "
                + "http://www.google.com/calendar ");
        jtaHelp.append("\nIn automatico avrete due calendari a disposizione, "
                + "uno \"vostro\" e un \"google task\" \n(che al momento ignoro cosa sia).");
        jtaHelp.append("\nAndate nelle impostazioni, a configurazione cellulare, qui "
                + "immetterete i dati del vostro cellulare \ne il codice di notifica "
                + "una volta ricevuto.");
        jtaHelp.append("\nNei settings dovrete immettere preferibilmente il nome "
                + "del calendario \"vostro\"");
        jpanel.add(jtaHelp);
        return jpanel;
    }
    
    private JPanel createPopupCalendarImport() {
        JPanel jpanel = new JPanel();
        Dimension dim = new Dimension(550, 300);
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Quando si importano le serie nel calendario è consigliato "
                + "fare un controllo dei nomi.\nSe nel caso sono presenti nomi di "
                + "serie sconosciute basta cancellarle e fare la ricerca\n manuale"
                + "con il nome giusto ed inserirla.\nN.B. Sarebbe consigliato"
                + " controllare i nomi delle serie remake con stesso nome.");
        jpanel.add(jtaHelp);
        return jpanel;
    }

    private JPanel createPopupInfoFeedColor() {
        JPanel jpanel = new JPanel();
        Dimension dim = new Dimension(300, 200);
        jpanel.setPreferredSize(dim);
        jpanel.setVisible(true);
        JTextArea jtaHelp = new JTextArea();
        jtaHelp.setPreferredSize(dim);
        jtaHelp.append("Colore ciano: sub per la versione normale"
                + "\nColore rosso: sub per la versione 720"
                + "\nColore blu: sub per la versione 1080p"
                + "\nColore arancione: sub per la versione 1080i"
                + "\nColore marrone: sub per la versione dvdrip"
                + "\nColore bianco: sub per la versione WEB-DL"
                + "\nColore verde: sub per la versione HR"
                + "\nColore nero: sub per la versione BRRIP"
                + "\nColore grigio: sub per la versione BDRIP"
                + "\nColore viola: sub per la versione bluray");
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
        jtaHelp.append("\n\nQualità: \n- * = tutte le versioni dei sub; \n- normale = sub "
                + "qualità standard;");
        jtaHelp.append("\n- 720p = versione 720p;\n- 1080p = versione 1080p; \n- dvdrip = "
                + "versione dvdrip");
        jtaHelp.append("\n- bluray = versione bluray;\n- hr = versione hr;");
        jtaHelp.append("\n- \\ = se avete già impostato una regola per una versione e "
                + "volete che le altre versioni vadino \nin un'altra cartella basterà "
                + "selezionare questa opzione \"differenziale\".");
        jtaHelp.append("\n\nPercorso: specificare il percorso dove desiderate che "
                + "vi estragga il sub.");
        jpanel.add(jtaHelp);
        return jpanel;
    }

    private void showSystemInfo() {
        SystemInfoDialog sid = new SystemInfoDialog(this, proxy.getPropertiesInfo());
        AWT.centerComponent(sid, this);
        sid.setVisible(true);
    }

    /** chiude l'applicazione */
    protected void applicationClose() {
        this.dispose();
        proxy.closeApp();
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
            if (requestClose("uscire"))
                applicationClose();
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
    public void objReceived(final FrameEvent evt) {
        if (evt.getOperaz() != null) {
            if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationEnableButton()))
                changeEnabledButton(true);
            else if (evt.getOperaz().equalsIgnoreCase(proxy.getSearchTV()))
                resultSearchTvJD.setVisible(true);
            else if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationFocus()))
                requestFocus();
            else if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationProgressShow())) {
                String msg = "Operazione in corso...";
                progressBar = new ProgressDialog(this, msg, evt.getMax());
                AWT.centerComponent(progressBar, this);
                progressBar.addProgressListener(new ProgressListener()  {
                    @Override
                    public void objReceived(ProgressEvent pe) {
                        proxy.stopImportRefresh();
                    }
                });
            } else if (evt.getOperaz().equalsIgnoreCase(proxy.getOperationProgressIncrement())) {
                progressBar.setProgress(evt.getMax());
            }
        }
        if ((evt.isIcontray()) && (!this.isVisible()))
            systemTray.notifyIncomingFeed();
    }

    /**
     * risponde all'evento di cambiare lo stato (abilitato/disabilitato) dei
     * bottoni
     * 
     * @param e
     *            stato
     */
    protected void changeEnabledButton(boolean e) {
        jpItasa.setButtonEnabled(e);
        if (prop.isSubsfactoryOption())
            jpSubsfactory.setEnableButton(e);
        if (prop.isTorrentOption())
            jpTorrent.setButtonEnabled(e);
    }

    /**verifica se il pannello non è presente e lo aggiunge stabilendo la visibilità
     * 
     * @param pane
     * @param visible 
     */
    private void checkAddTab(JPanel pane, boolean visible) {
        if (!mainJTP.isAncestorOf(pane)) {
            mainJTP.addTab(pane.getName(), pane);
            mainJTP.setTabComponentAt(mainJTP.getTabCount() - 1,
                    new ButtonTabComponent(mainJTP));
            if (visible)
                mainJTP.setSelectedComponent(pane);
        }
    }
    
    /** inizializza i listener per l'ascolto */
    private void initListeners() {
        addWindowListener(this);
        proxy.setFrameListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {    }
    @Override
    public void windowClosed(WindowEvent e) {    }
    @Override
    public void windowIconified(WindowEvent e) {    }
    @Override
    public void windowDeiconified(WindowEvent e) {    }
    @Override
    public void windowActivated(WindowEvent e) {    }
    @Override
    public void windowDeactivated(WindowEvent e) {    }
} // end class