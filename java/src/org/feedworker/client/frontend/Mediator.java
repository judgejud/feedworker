package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.swt.SWTException;
import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.Kernel;
import org.feedworker.client.RssParser;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.client.frontend.events.MyJFrameEventListener;
import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.client.frontend.events.TableRssEventListener;
import org.feedworker.client.frontend.events.TableXmlEventListener;
import org.feedworker.util.Common;
import org.feedworker.util.KeyRule;
import org.feedworker.util.ManageException;
import org.feedworker.util.Quality;
import org.feedworker.util.ValueRule;

import org.jfacility.Awt;
import org.jfacility.java.lang.Lang;
import org.opensanskrit.application.UnableRestartApplicationException;

import com.sun.syndication.io.FeedException;
import javax.swing.table.DefaultTableModel;
import org.jfacility.java.lang.MySystem;

/**
 * Classe mediatrice tra gui e kernel, detta anche kernel della gui.
 * 
 * @author luca
 */
public class Mediator {
    private final String INCOMING_FEED_ICON_FILE_NAME = "IncomingFeedIcon.png";

    private static Mediator proxy = null;

    private Kernel core = Kernel.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private List listenerTextPane = new ArrayList();
    private List listenerJFrame = new ArrayList();
    private ManageException error = ManageException.getIstance();

    /**
     * Restituisce l'istanza attiva del Mediator se non esiste la crea
     *
     * @return Mediator
     */
    public static Mediator getIstance() {
        if (proxy == null) {
            proxy = new Mediator();
        }
        return proxy;
    }

    /**
     * Restituisce il testo itasa
     *
     * @return itasa
     */
    String getItasa() {
        return core.ITASA;
    }

    /**
     * Restituisce il testo myitasa
     *
     * @return myitasa
     */
    String getMyItasa() {
        return core.MYITASA;
    }

    /**
     * Restituisce il testo subsfactory
     *
     * @return subsfactory
     */
    String getSubsf() {
        return core.SUBSF;
    }

    String getMySubsf() {
        return core.MYSUBSF;
    }

    /**
     * Restituisce il testo eztv
     *
     * @return eztv
     */
    String getEztv() {
        return core.EZTV;
    }

    /**
     * Restituisce il testo btchat
     *
     * @return btchat
     */
    String getBtchat() {
        return core.BTCHAT;
    }

    /**
     * Pulisce la tabella specificata dai check
     *
     * @param jt
     *            tabella
     */
    void cleanSelect(JTable jt) {
        for (int i = 0; i < jt.getRowCount(); i++) {
            jt.setValueAt(false, i, 3);
        }
    }

    /**
     * Copia nella clipboard i link torrent selezionati
     *
     * @param jt1
     *            tabella1
     * @param jt2
     *            tabella2
     */
    void copyLinkTorrent(JTable jt1, JTable jt2) {
        String text = "";
        for (int i = 0; i < jt1.getRowCount(); i++) {
            if (jt1.getValueAt(i, 3) == Boolean.TRUE) {
                text += jt1.getValueAt(i, 0).toString() + "\n";
                jt1.setValueAt(false, i, 3);
            }
        }
        for (int i = 0; i < jt2.getRowCount(); i++) {
            if (jt2.getValueAt(i, 3) == Boolean.TRUE) {
                text += jt2.getValueAt(i, 0).toString() + "\n";
                jt2.setValueAt(false, i, 3);
            }
        }
        if (!text.equalsIgnoreCase("")) {
            Awt.setClipboard(text);
            fireNewTextPaneEvent("link copiati nella clipboard",
                    MyTextPaneEvent.OK);
        }
    }

    /**verifica le tabelle se sono flaggate per i download e invoca il kernel
     * coi link per il loro download
     *
     * @param jt1
     *            tabella1
     * @param jt2
     *            tabella2
     * @param itasa
     *            tabelle itasa
     */
    void downloadSub(JTable jt1, JTable jt2, boolean itasa) {
        ArrayList<String> alLinks = new ArrayList<String>();
        alLinks = addLinks(jt1);
        if (jt2 != null) {
            alLinks.addAll(addLinks(jt2));
        }
        if (alLinks.size() > 0) {
            core.downloadSub(alLinks, itasa);
        } else {
            String temp = "dalle tabelle";
            if (!itasa) {
                temp = "dalla tabella";
            }
            printAlert("Selezionare almeno un rigo " + temp);
        }
    }

    void downloadTorrent(JTable jt1, JTable jt2) {
        if (Lang.verifyTextNotNull(prop.getTorrentDestinationFolder())) {
            ArrayList<String> alLinks = addLinks(jt1);
            alLinks.addAll(addLinks(jt2));
            if (alLinks.size() > 0) {
                core.downloadTorrent(alLinks);
            } else {
                printAlert("Selezionare almeno un rigo dalle tabelle");
            }
        } else {
            printAlert("Non posso salvare perchè non hai specificato "
                    + "una cartella dove scaricare i file.torrent");
        }
    }

    void runRss() {
        core.runRss();
    }

    void closeApp(String date) {
        core.closeApp(date);
    }

    void setTableRssListener(TableRssEventListener listener) {
        core.addTableRssEventListener(listener);
    }

    void setTableXmlListener(TableXmlEventListener listener) {
        core.addTableXmlEventListener(listener);
    }

    void setTextPaneListener(MyTextPaneEventListener listener) {
        core.addMyTextPaneEventListener(listener);
        core.setDownloadThreadListener(listener);
        ManageException.getIstance().addMyTextPaneEventListener(listener);
        addMyTextPaneEventListener(listener);

    }

    void setFrameListener(MyJFrameEventListener listener) {
        core.addMyJFrameEventListener(listener);
        addMyJFrameEventListener(listener);
    }

    /**Restituisce i nodi per la jtree Settings
     *
     * @return nodi jtree
     */
    DefaultMutableTreeNode getTreeNode() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Settings");
        root.add(new DefaultMutableTreeNode("General"));
        //root.add(new DefaultMutableTreeNode("Tabs"));
        if (prop.isItasaOption()) {
            root.add(new DefaultMutableTreeNode("Itasa"));
        }
        if (prop.isSubsfactoryOption()) {
            root.add(new DefaultMutableTreeNode("Subsfactory"));
        }
        if (prop.isTorrentOption()) {
            root.add(new DefaultMutableTreeNode("Torrent"));
        }
        return root;
    }

    void restartRss() {
        core.stopAndRestartTimer();
    }

    /**
     * testa la validità rss
     *
     * @param link
     *            rss
     * @return booleano che verifica la validità
     */
    boolean testRss(String link, String from) throws MalformedURLException {
        boolean passed = false;
        new URL(link);
        try {
            new RssParser(link);
            passed = true;
        } catch (FeedException ex) {
            error.launch(ex, getClass(), from);
        } catch (IllegalArgumentException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        return passed;
    }

    boolean testSamba(String ip, String dir, String domain, String user,
            String pwd) {
        return core.testSamba(ip, dir, dir, user, pwd);
    }

    void saveRules(tableXml jtable) {
        boolean _break = false;
        TreeMap<KeyRule, ValueRule> temp = new TreeMap<KeyRule, ValueRule>();
        for (int i = 0; i < jtable.getRowCount(); i++) {
            String name = ((String) jtable.getValueAt(i, 0));
            String season = jtable.getValueAt(i, 1).toString();
            String quality = (String) jtable.getValueAt(i, 2);
            String path = (String) jtable.getValueAt(i, 3);
            String status = (String) jtable.getValueAt(i, 4);
            String day = (String) jtable.getValueAt(i, 5);
            boolean rename = Boolean.parseBoolean(jtable.getValueAt(i, 6).toString());

            if (Lang.verifyTextNotNull(name)) {
                if (Lang.verifyTextNotNull(path)) {
                    try {
                        if (Lang.verifyTextNotNull(season)) {
                            int s = Lang.stringToInt(season);
                            season = Lang.intToString(s);
                        } else {
                            printAlert("Riga: " + i
                                    + " immettere un numero alla stagione");
                            _break = true;
                            break;
                        }
                        KeyRule key = new KeyRule(name, season, quality);
                        ValueRule value = new ValueRule(path, day, status,
                                rename);
                        if (!temp.containsKey(key)) {
                            temp.put(key, value);
                        } else {
                            printAlert("Riga: "
                                    + i
                                    + " trovato duplicato, si prega di correggerlo");
                            _break = true;
                            break;
                        }
                    } catch (NumberFormatException ex) {
                        error.launch(ex, getClass(), Lang.intToString(i));
                        _break = true;
                        break;
                    }
                } else {
                    printAlert("Riga: " + i
                            + " immettere la destinazione per la regola/sub");
                    _break = true;
                    break;
                }
            } else {
                printAlert("Riga: " + i
                        + " immettere il nome della regola/sub/serie");
                _break = true;
                break;
            }
        }
        if (!_break) {
            core.saveMap(temp);
        }
    }

    /**
     * Aggiunge i link corrispondenti al true della colonna download
     * nell'arraylist
     *
     * @param jt
     *            jtable su cui operare
     * @return Arraylist di stringhe
     */
    ArrayList<String> addLinks(JTable jt) {
        ArrayList<String> alLinks = new ArrayList<String>();
        for (int i = 0; i < jt.getRowCount(); i++) {
            if (jt.getValueAt(i, 3) == Boolean.TRUE) {
                alLinks.add(jt.getValueAt(i, 0).toString());
            }
        }
        return alLinks;
    }

    String[] getElemEnum() {
        return core.getQuality();
    }

    void synoMoveVideo() {
        core.synoMoveVideo();
    }

    void synoStatus() {
        core.synoStatus();
    }

    /**verifica impostazioni torrent
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveTorrent(String text) {
        if (!Lang.verifyTextNotNull(text)) {
            printAlert("Avviso: Non immettendo la Destinazione dei Torrent non potrai "
                    + "scaricare .torrent");
        }
        return true;
    }

    /**verifica impostazioni subsf
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveSubsf(String subsf, String mySubsf) {
        boolean check = true;
        if (!Lang.verifyTextNotNull(subsf) && !Lang.verifyTextNotNull(mySubsf))
            printAlert("Avviso: Non immettendo link RSS Subsfactory non potrai usare i feed"
                    + " Subsfactory");
        else {
            try {
                if (Lang.verifyTextNotNull(subsf))
                    check = testRss(subsf, "subsfactory");
                if (check && Lang.verifyTextNotNull(mySubsf))
                        check = testRss(mySubsf, "mysubsfactory");
            } catch (MalformedURLException e) {
                error.launch(e, getClass(), "subsfactory");
                check = false;
            }
        }
        return check;
    }

    /**verifica impostazioni itasa
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveItasa(String itasa, String myitasa, String user, String pwd) {
        boolean check = true;
        try {
            if (!Lang.verifyTextNotNull(itasa)
                    && !Lang.verifyTextNotNull(myitasa)) {
                printAlert("Avviso: Non immettendo link RSS itasa e/o myitasa non potrai "
                        + "usare i feed italiansubs");
            } else {
                if (Lang.verifyTextNotNull(itasa))
                    check = testRss(itasa, "itasa");
                if (check) {
                    if (Lang.verifyTextNotNull(myitasa)) {
                        check = testRss(myitasa, "myitasa");
                    }
                    if (check) {
                        if (!Lang.verifyTextNotNull(user)) {
                            printAlert("Avviso: senza Username Itasa non potrai scaricare i "
                                    + "subs");
                        } else if (!Lang.verifyTextNotNull(new String(pwd))) {
                            printAlert("Avviso: senza Password Itasa non potrai scaricare i "
                                    + "subs");
                        }
                    }
                }
            }
        } catch (MalformedURLException ex) {
            error.launch(ex, getClass(), "Itasa");
            check = false;
        }
        return check;
    }

    boolean checkSaveGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd) {
        boolean check = false;
        if (dirLocal) {
            if (!Lang.verifyTextNotNull(destSub)) {
                printAlert("INPUT OBBLIGATORIO: La Destinazione Locale non può essere vuota.");
            } else {
                check = true;
            }
        } else { // SAMBA selected
            if (!Lang.verifyTextNotNull(sambaDomain)) {
                printAlert("INPUT OBBLIGATORIO: Il Dominio Samba non può essere vuoto.");
            } else if (!Lang.verifyTextNotNull(sambaIP)) {
                printAlert("INPUT OBBLIGATORIO: L'ip Samba non può essere vuoto.");
            } else if (!Lang.verifyTextNotNull(sambaDir)) {
                printAlert("INPUT OBBLIGATORIO: La cartella condivisa Samba non può essere "
                        + "vuota.");
            } else if (!Lang.verifyTextNotNull(sambaUser)) {
                printAlert("INPUT OBBLIGATORIO: L'utente Samba non può essere vuoto.");
            } else if (!Lang.verifyTextNotNull(sambaPwd)) {
                printAlert("INPUT OBBLIGATORIO: La password Samba non può essere vuota.");
            } else if (!proxy.testSamba(sambaIP, sambaDir, sambaDomain,
                    sambaUser, sambaPwd)) {
                printAlert("Impossibile connettermi al server/dir condivisa Samba");
            } else {
                check = true;
            }
        }
        return check;
    }

    void saveSettings(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String laf, boolean audio, String timeout,
            boolean advancedDest, boolean runIconized, String itasa,
            String myitasa, String user, String pwd, boolean autoMyitasa,
            boolean autoLoadMyItasa,
            String subsf, String mySubsf, String torrent) {
        String oldLF = prop.getApplicationLookAndFeel();
        String oldMin = prop.getRefreshInterval();
        boolean first = prop.isApplicationFirstTimeUsed();
        boolean oldAD = prop.isEnabledCustomDestinationFolder();
        boolean save = false;
        if (checkSaveGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                sambaUser, sambaPwd)) {
            save = true;
            if (prop.isItasaOption() && save)
                save = checkSaveItasa(itasa, myitasa, user, pwd);
            if (prop.isSubsfactoryOption() && save)
                save = checkSaveSubsf(subsf, mySubsf);
            if (prop.isTorrentOption() && save)
                checkSaveTorrent(torrent);
        }
        if (save) {
            setPropGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                    sambaUser, sambaPwd, time, laf, audio, timeout,
                    advancedDest, runIconized);
            setPropItasa(itasa, myitasa, user, pwd, autoMyitasa, autoLoadMyItasa);
            prop.setSubsfactoryFeedURL(subsf);
            prop.setMySubsfactoryFeedUrl(mySubsf);
            prop.setTorrentDestinationFolder(torrent);
            core.writeProp();
            if (!oldLF.equals(prop.getApplicationLookAndFeel())) {
                printAlert("Il Look&Feel selezionato sarà disponibile al riavvio del client.");
            }
            if (oldAD != prop.isEnabledCustomDestinationFolder()) {
                if (prop.isEnabledCustomDestinationFolder()) {
                    fireNewJFrameEvent("ADD_PANE_RULEZ");
                } else {
                    fireNewJFrameEvent("REMOVE_PANE_RULEZ");
                }
            }
            if (!prop.isApplicationFirstTimeUsed() && first) {
                fireNewJFrameEvent("ENABLED_BUTTON");
                runRss();
            } else {
                if (Lang.verifyTextNotNull(oldMin)
                        && !oldMin.equalsIgnoreCase(prop.getRefreshInterval())) {
                    restartRss();
                }
            }
            fireNewTextPaneEvent(
                    "Impostazioni salvate in " + prop.getSettingsFilename(),
                    MyTextPaneEvent.OK);
        }
    }

    private void setPropGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd, String time, String laf,
            boolean audio, String timeout, boolean advancedDest,
            boolean runIconized) {
        prop.setLocalFolder(dirLocal);
        prop.setSubtitleDestinationFolder(destSub);
        prop.setRefreshInterval(time);
        prop.setApplicationLookAndFeel(laf);
        prop.setEnableAudioAdvisor(audio);
        prop.setCifsShareDomain(sambaDomain);
        prop.setCifsShareLocation(sambaIP);
        prop.setCifsSharePath(sambaDir);
        prop.setCifsShareUsername(sambaUser);
        prop.setCifsSharePassword(sambaPwd);
        prop.setHttpTimeout(timeout);
        prop.setEnableCustomDestinationFolder(advancedDest);
        prop.setEnableIconizedRun(runIconized);
    }

    private void setPropItasa(String itasa, String myitasa, String user,
            String pwd, boolean auto, boolean autoload) {
        prop.setItasaFeedURL(itasa);
        prop.setMyitasaFeedURL(myitasa);
        prop.setItasaUsername(user);
        prop.setItasaPassword(pwd);
        prop.setAutoDownloadMyItasa(auto);
        prop.setAutoLoadDownloadMyItasa(autoload);
    }

    void bruteRefresh() {
        core.bruteRefreshRSS();
    }

    /**
     * Invia alla download station del nas i link torrent selezionati
     *
     * @param jt1
     *            tabella1
     * @param jt2
     *            tabella2
     */
    void fireTorrentToNas(JTable jt1, JTable jt2) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < jt1.getRowCount(); i++) {
            if (jt1.getValueAt(i, 3) == Boolean.TRUE) {
                al.add(jt1.getValueAt(i, 0).toString());
                jt1.setValueAt(false, i, 3);
            }
        }
        for (int i = 0; i < jt2.getRowCount(); i++) {
            if (jt2.getValueAt(i, 3) == Boolean.TRUE) {
                al.add(jt2.getValueAt(i, 0).toString());
                jt2.setValueAt(false, i, 3);
            }
        }
        if (al.size() > 0) {
            core.synoDownloadRedirectory(al);
        }
    }

    void synoClearFinish() {
        core.synoClearFinish();
    }

    Color searchVersion(String text) {
        Color col = Color.cyan;
        String[] temp = text.split(" ");
        String version = temp[temp.length - 1].toLowerCase();
        if (version.equals(Quality.FORM_1080p.toString()))
            col = Color.blue;
        else if (version.equals(Quality.FORM_1080i.toString()))
            col = Color.orange;
        else if (version.equals(Quality.FORM_720p.toString()))
            col = Color.red;
        else if (version.equals(Quality.DVDRIP.toString()))
            col = new Color(183, 65, 14);
        else if (version.equals(Quality.HR.toString()))
            col = Color.green;
        else if (version.equals(Quality.BLURAY.toString()))
            col = Color.magenta;
        return col;
    }

    void printError(URISyntaxException e) {
        ManageException.getIstance().launch(e, null);
    }

    void printError(Exception e) {
        ManageException.getIstance().launch(e);
    }

    void printError(SWTException e) {
        ManageException.getIstance().launch(e, EnhancedMainJF.class);
    }

    private void printAlert(String msg) {
        fireNewTextPaneEvent(msg, MyTextPaneEvent.ALERT);
    }

    // This methods allows classes to register for MyEvents
    public synchronized void addMyTextPaneEventListener(
            MyTextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }

    // This methods allows classes to unregister for MyEvents
    public synchronized void removeMyTextPaneEventListener(
            MyTextPaneEventListener listener) {
        listenerTextPane.remove(listener);
    }

    private synchronized void fireNewTextPaneEvent(String msg, String type) {
        MyTextPaneEvent event = new MyTextPaneEvent(this, msg, type);
        Iterator listeners = listenerTextPane.iterator();
        while (listeners.hasNext()) {
            MyTextPaneEventListener myel = (MyTextPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    public synchronized void addMyJFrameEventListener(
            MyJFrameEventListener listener) {
        listenerJFrame.add(listener);
    }

    public synchronized void removeMyJFrameEventListener(
            MyJFrameEventListener listener) {
        listenerJFrame.remove(listener);
    }

    private synchronized void fireNewJFrameEvent(String oper) {
        MyJFrameEvent event = new MyJFrameEvent(this, oper);
        Iterator listeners = listenerJFrame.iterator();
        while (listeners.hasNext()) {
            MyJFrameEventListener myel = (MyJFrameEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    void restartApplication() {
        try {
            FeedWorkerClient.getApplication().restart();
        } catch (UnableRestartApplicationException e) {
            e.printStackTrace();
        }
    }

    String getNameApp() {
        return FeedWorkerClient.getApplication().getName();
    }

    Object[] getAvailableLAF() {
        return FeedWorkerClient.getApplication().getAvailableLookAndFeel().toArray();
    }

    ApplicationSettings getSettings(){
        return ApplicationSettings.getIstance();
    }

    Image getIncomingFeedIcon(){
        return Common.getResourceIcon(INCOMING_FEED_ICON_FILE_NAME);
    }

    Image getApplicationIcon(){
        return FeedWorkerClient.getApplication().getIcon();
    }

    String getApplicationName(){
        return FeedWorkerClient.getApplication().getName();
    }

    String getTitle(){
        return getApplicationName() + " build "
                + FeedWorkerClient.getApplication().getBuildNumber() + " by "
                + FeedWorkerClient.getApplication().getAuthor();
    }

    String[] getDaysOfWeek(){
        return core.daysOfWeek;
    }

    TableModel getModelSystemInfo() {
        DefaultTableModel dtm = new DefaultTableModel(null, new String[]{"Informazione", "Valore"}) {

            Class[] types = new Class[]{String.class, String.class};

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        dtm.addRow(new String[]{"Java version", MySystem.getJavaVersion()});
        dtm.addRow(new String[]{"Java vendor", MySystem.getJavaVendor()});
        dtm.addRow(new String[]{"Java Home", MySystem.getJavaHome()});
        dtm.addRow(new String[]{"Sistema Operativo", MySystem.getOsName()});
        dtm.addRow(new String[]{"Versione SO", MySystem.getOsVersion()});
        dtm.addRow(new String[]{"Architettura SO", MySystem.getOsArchitecture()});
        dtm.addRow(new String[]{"Directory attuale", MySystem.getUserDir()});
        dtm.addRow(new String[]{"File regole", "rules.xml"});
        dtm.addRow(new String[]{"File impostazioni", "settings.properties"});
        return dtm;
    }
}