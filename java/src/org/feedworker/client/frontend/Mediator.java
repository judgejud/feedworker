package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.JFrameEventIconDateListener;
import org.feedworker.client.frontend.events.JFrameEventOperationListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.client.frontend.table.tableSubtitleDest;
import org.feedworker.core.Kernel;
import org.feedworker.core.ManageListener;
import org.feedworker.core.RssParser;
import org.feedworker.exception.ManageException;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;

import org.jfacility.java.awt.AWT;
import org.jfacility.java.lang.JVM;
import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.SystemProperty;
import org.jfacility.javax.swing.Swing;

import com.sun.syndication.io.FeedException;

/**
 * Classe mediatrice tra gui e kernel, detta anche kernel della gui.
 * 
 * @author luca
 */
public class Mediator {

    private final String INCOMING_FEED_ICON_FILE_NAME = "IncomingFeedIcon.png";
    private final String ENABLE_BUTTON = "enableButton";
    private final FileNameExtensionFilter fnfeZIP =
            new FileNameExtensionFilter("ZIP file", "zip");
    private static Mediator proxy = null;
    private Kernel core = Kernel.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();

    /**
     * Restituisce l'istanza attiva del Mediator se non esiste la crea
     *
     * @return Mediator
     */
    public static Mediator getIstance() {
        if (proxy == null)
            proxy = new Mediator();
        return proxy;
    }
    
    boolean isJava6(){
        return new JVM().isOrLater(16);
    }

    //TODO: ripristinare col getbuildernumber
    String getTitle() {
        return getApplicationName() + " revision "
                //+ FeedWorkerClient.getApplication().getBuildNumber() + " by "
                + "324 by "
                + FeedWorkerClient.getApplication().getAuthor();
    }

    /**Restituisce il testo itasa
     *
     * @return itasa
     */
    public String getItasa() {
        return core.ITASA;
    }

    /**Restituisce il testo myitasa
     *
     * @return myitasa
     */
    public String getMyItasa() {
        return core.MYITASA;
    }

    /**Restituisce il testo subsfactory
     *
     * @return subsfactory
     */
    public String getSubsf() {
        return core.SUBSF;
    }

    public String getMySubsf() {
        return core.MYSUBSF;
    }

    /**
     * Restituisce il testo eztv
     *
     * @return eztv
     */
    public String getEztv() {
        return core.EZTV;
    }

    /**Restituisce il testo btchat
     *
     * @return btchat
     */
    public String getBtchat() {
        return core.BTCHAT;
    }

    public String getNameTableSubtitleDest() {
        return core.SUBTITLE_DEST;
    }

    public String getNameTableCalendar() {
        return core.CALENDAR;
    }

    String getSearchTV() {
        return core.SEARCH_TV;
    }

    String getOperationFocus() {
        return core.OPERATION_FOCUS;
    }

    String getOperationProgressShow() {
        return core.OPERATION_PROGRESS_SHOW;
    }

    String getOperationProgressIncrement() {
        return core.OPERATION_PROGRESS_INCREMENT;
    }

    String getOperationEnableButton() {
        return ENABLE_BUTTON;
    }

    /**Pulisce la tabella specificata dai check
     *
     * @param jt
     *            tabella
     */
    public void cleanSelect(JTable jt, int col) {
        for (int i = 0; i < jt.getRowCount(); i++) {
            jt.setValueAt(false, i, col);
        }
    }

    /** Copia nella clipboard i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    public void copyLinkTorrent(JTable jt1, JTable jt2) {
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
            AWT.setClipboard(text);
            printOk("link copiati nella clipboard");
        }
    }

    /**verifica le tabelle se sono flaggate per i download e invoca il kernel
     * coi link per il loro download
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     * @param itasa tabelle itasa
     */
    public void downloadSub(JTable jt1, JTable jt2, boolean itasa) {
        ArrayList<String> alLinks = new ArrayList<String>();
        alLinks = addLinks(jt1);
        alLinks.addAll(addLinks(jt2));
        if (alLinks.size() > 0)
            core.downloadSub(alLinks, itasa);
        else {
            String temp = "dalle tabelle";
            if (!itasa)
                temp = "dalla tabella";
            printAlert("Selezionare almeno un rigo " + temp);
        }
    }

    public void downloadTorrent(JTable jt1, JTable jt2) {
        if (Lang.verifyTextNotNull(prop.getTorrentDestinationFolder())) {
            ArrayList<String> alLinks = addLinks(jt1);
            alLinks.addAll(addLinks(jt2));
            if (alLinks.size() > 0)
                core.downloadTorrent(alLinks);
            else
                printAlert("Selezionare almeno un rigo dalle tabelle");
        } else {
            printAlert("Non posso salvare perchè non hai specificato "
                    + "una cartella dove scaricare i file.torrent");
        }
    }

    void runRss() {
        core.runRss();
    }

    /**chiama nel kernel la chiusura applicazione
     * 
     * @param date 
     */
    void closeApp(String date) {
        core.closeApp(date, false);
    }

    public void setTableListener(TableEventListener listener) {
        ManageListener.addTableEventListener(listener);
    }

    public void setTextPaneListener(TextPaneEventListener listener) {
        ManageListener.addTextPaneEventListener(listener);
    }
    
    public void setStatusBarListener(StatusBarEventListener listener) {
        ManageListener.addStatusBarEventListener(listener);
    }

    void setFrameIconDateListener(JFrameEventIconDateListener listener) {
        ManageListener.addJFrameEventIconDateListener(listener);
    }
    
    void setFrameOperationListener(JFrameEventOperationListener listener) {
        ManageListener.addJFrameEventOperationListener(listener);
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

    public void saveRules(tableSubtitleDest jtable) {
        boolean _break = false;
        TreeMap<KeyRule, ValueRule> temp = new TreeMap<KeyRule, ValueRule>();
        for (int r = 0; r < jtable.getRowCount(); r++) {
            int c = -1;
            String name = ((String) jtable.getValueAt(r, ++c));
            String season = jtable.getValueAt(r, ++c).toString();
            String quality = (String) jtable.getValueAt(r, ++c);
            String path = (String) jtable.getValueAt(r, ++c);
            boolean rename = false, delete = false;
            try {
                rename = Boolean.parseBoolean(jtable.getValueAt(r, ++c).toString());
            } catch (NullPointerException e) {
            }
            try {
                delete = Boolean.parseBoolean(jtable.getValueAt(r, ++c).toString());
            } catch (NullPointerException e) {
            }
            if (rename && delete) {
                printAlert("Riga: " + r
                        + " non possono coesistere entrambi i flag true di rename e delete");
                _break = true;
                break;
            } else {
                if (Lang.verifyTextNotNull(name)) {
                    if (delete || Lang.verifyTextNotNull(path)) {
                        try {
                            if (Lang.verifyTextNotNull(season)) {
                                int s = Lang.stringToInt(season);
                                season = Lang.intToString(s);
                            } else {
                                printAlert("Riga: " + r
                                        + " immettere un numero alla stagione");
                                _break = true;
                                break;
                            }
                            KeyRule key = new KeyRule(name, season, quality);
                            ValueRule value = new ValueRule(path, rename, delete);
                            if (!temp.containsKey(key)) {
                                temp.put(key, value);
                            } else {
                                printAlert("Riga: " + r
                                        + " trovato duplicato, si prega di correggerlo");
                                _break = true;
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            error.launch(ex, getClass(), Lang.intToString(r));
                            _break = true;
                            break;
                        }
                    } else {
                        printAlert("Riga: " + r
                                + " immettere la destinazione per la regola/sub");
                        _break = true;
                        break;
                    }
                } else {
                    printAlert("Riga: " + r
                            + " immettere il nome della regola/sub/serie");
                    _break = true;
                    break;
                }
            }
        } //end for
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

    public String[] getQualityEnum() {
        return Quality.toArray();
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
        if (!Lang.verifyTextNotNull(subsf) && !Lang.verifyTextNotNull(mySubsf)) {
            printAlert("Avviso: Non immettendo link RSS Subsfactory non potrai usare i feed"
                    + " Subsfactory");
        } else {
            try {
                if (Lang.verifyTextNotNull(subsf)) {
                    check = testRss(subsf, "subsfactory");
                }
                if (check && Lang.verifyTextNotNull(mySubsf)) {
                    check = testRss(mySubsf, "mysubsfactory");
                }
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
                if (Lang.verifyTextNotNull(itasa)) {
                    check = testRss(itasa, "itasa");
                }
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
                printAlert("INPUT OBBLIGATORIO: La Destinazione Locale non può "
                                                                + "essere vuota.");
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

    public void saveSettings(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String laf, String timeout,
            boolean advancedDownload, boolean runIconized, String itasa,
            String myitasa, String user, String pwd, boolean autoMyitasa,
            boolean autoLoadMyItasa, String subsf, String mySubsf, String torrent,
            boolean audioRss, boolean audioSub, boolean mail, String mailTO, 
            String smtp, boolean paneLog, boolean paneSearch, boolean paneSetting,
            boolean paneSubDest, boolean paneReminder) {
                
        String oldLF = prop.getApplicationLookAndFeel();
        String oldMin = prop.getRefreshInterval();
        boolean first = prop.isApplicationFirstTimeUsed();
        boolean save = false;
        if (checkSaveGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                sambaUser, sambaPwd)) {
            save = true;
            if (save)
                save = checkSaveItasa(itasa, myitasa, user, pwd);
            if (prop.isSubsfactoryOption() && save)
                save = checkSaveSubsf(subsf, mySubsf);
            if (prop.isTorrentOption() && save)
                checkSaveTorrent(torrent);
        }
        if (save) {
            setPropGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                    sambaUser, sambaPwd, time, laf, timeout, 
                    advancedDownload, runIconized);
            setPropItasa(itasa, myitasa, user, pwd, autoMyitasa, autoLoadMyItasa);
            setPropSubsf(subsf, mySubsf);
            prop.setTorrentDestinationFolder(torrent);
            setPropAdvisor(audioRss, audioSub, mail, mailTO, smtp);
            setPropVisiblePane(paneLog, paneSearch, paneSetting, paneSubDest, paneReminder);
            core.writeProp();
            if (!oldLF.equals(prop.getApplicationLookAndFeel())) {
                printAlert("Il Look&Feel selezionato sarà disponibile al riavvio "
                                                                + "del client.");
            }
            if (!prop.isApplicationFirstTimeUsed() && first) {
                ManageListener.fireJFrameEventOperation(this, ENABLE_BUTTON);
                runRss();
            } else {
                if (Lang.verifyTextNotNull(oldMin)
                        && !oldMin.equalsIgnoreCase(prop.getRefreshInterval())) {
                    restartRss();
                }
            }
            printOk("Impostazioni salvate in " + prop.getSettingsFilename());
        }
    }

    private void setPropGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd, String time, String laf,
            String timeout, boolean advancedDownload,
            boolean runIconized) {
        prop.setLocalFolder(dirLocal);
        prop.setSubtitleDestinationFolder(destSub);
        prop.setRefreshInterval(time);
        prop.setApplicationLookAndFeel(laf);
        prop.setCifsShareDomain(sambaDomain);
        prop.setCifsShareLocation(sambaIP);
        prop.setCifsSharePath(sambaDir);
        prop.setCifsShareUsername(sambaUser);
        prop.setCifsSharePassword(sambaPwd);
        prop.setHttpTimeout(timeout);
        prop.setEnableAdvancedDownload(advancedDownload);
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
    
    private void setPropSubsf(String subsf, String mySubsf){
        prop.setSubsfactoryFeedURL(subsf);
        prop.setMySubsfactoryFeedUrl(mySubsf);
    }
    
    private void setPropAdvisor(boolean audioRss, boolean audioSub, boolean mail,
                                String mailTO, String smtp){
        prop.setEnableAdvisorAudioRss(audioRss);
        prop.setEnableAdvisorAudioSub(audioSub);
        prop.setEnableAdvisorMail(mail);
        prop.setMailTO(mailTO);
        prop.setMailSMTP(smtp);
    }
    
    private void setPropVisiblePane(boolean log, boolean search, boolean setting, 
                                    boolean subdest, boolean reminder){
        prop.setEnablePaneLog(log);
        prop.setEnablePaneSearchSubItasa(search);
        prop.setEnablePaneSetting(setting);
        prop.setEnablePaneSubDestination(subdest);
        prop.setEnablePaneReminder(reminder);
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
    public void fireTorrentToNas(JTable jt1, JTable jt2) {
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

    public Color searchVersion(String text) {
        Color col = Color.cyan;
        String[] temp = text.split(" ");
        String version = temp[temp.length - 1].toLowerCase();
        if (version.equals(Quality._1080p.toString())) {
            col = Color.blue;
        } else if (version.equals(Quality._1080i.toString())) {
            col = Color.orange;
        } else if (version.equals(Quality._720p.toString())) {
            col = Color.red;
        } else if (version.equals(Quality.DVDRIP.toString())) {
            col = new Color(183, 65, 14);
        } else if (version.equals(Quality.HR.toString())) {
            col = Color.green;
        } else if (version.equals(Quality.BLURAY.toString())) {
            col = Color.magenta;
        } else if (version.equals(Quality.WEB_DL.toString())) {
            col = Color.white;
        } else if (version.equals(Quality.BRRIP.toString())) {
            col = Color.black;
        } else if (version.equals(Quality.BDRIP.toString())) {
            col = Color.darkGray;
        }
        return col;
    }

    void printError(URISyntaxException e) {
        ManageException.getIstance().launch(e, this.getClass());
    }

    void printError(Exception e) {
        ManageException.getIstance().launch(e, this.getClass());
    }

    public void printAlert(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, true);
    }
    
    void printOk(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.OK, true);
    }
    
    public void printStatusBar(String msg){
        ManageListener.fireStatusBarEvent(this, msg);
    }

    public void restartApplication(String date) {
        core.closeApp(date, true);
    }

    public String getNameApp() {
        return FeedWorkerClient.getApplication().getName();
    }

    public String[] getAvailableLAF() {
        return FeedWorkerClient.getApplication().getIstanceLAF().getAvailableLAF();
    }

    public ApplicationSettings getSettings() {
        return ApplicationSettings.getIstance();
    }

    Image getIncomingFeedIcon() {
        return Common.getResourceIcon(INCOMING_FEED_ICON_FILE_NAME);
    }

    Image getApplicationIcon() {
        return FeedWorkerClient.getApplication().getIcon();
    }

    String getApplicationName() {
        return FeedWorkerClient.getApplication().getName();
    }

    ArrayList<String[]> getPropertiesInfo(){
        ArrayList<String[]> array = new ArrayList<String[]>();
        array.add(new String[]{"File calendario", "calendar.xml"});
        array.add(new String[]{"File impostazioni", prop.getSettingsFilename()});
        array.add(new String[]{"File regole", "rules.xml"});
        return array;
    }

    void printDay(int day) {
        core.searchDay(day);
    }

    void invokeBackup(Component parent) {
        String name = Swing.getFile(parent, "Creare il file zip per il backup",
                fnfeZIP, new File(SystemProperty.getUserDir() + File.separator));
        if (name != null) {
            core.backup(name);
        }
    }

    public void searchTV(String tv) {
        core.detailedSearchShow(tv);
    }

    void searchIdTv(ArrayList<Object[]> id) {
        core.searchIdTv(id);
    }

    public void importFromSubDest() {
        core.importTvFromDestSub();
    }

    public void removeSingleShowCalendar(int value, Object id) {
        core.removeShowTv(value, id);
    }

    public void removeAllShowCalendar() {
        core.removeAllShowTv();
    }

    public void refreshCalendar() {
        core.refreshCalendar();
    }

    public void openFolder(String dir) {
        core.openFolder(dir);
    }
    
    public void stopImportRefresh() {
    	core.stopImportRefreshCalendar();
    }
    
    public void checkLoginItasa(String user, char[] password) {
        core.checkLoginItasa(user,new String(password));
    }

    public void removeReminders(ArrayList<Integer> numbers) {
        core.removeReminders(numbers);
    }
    
    public Object[] showList(){
        return core.getShowNameList();
    }

    public void searchSubItasa(Object show, Object version, boolean complete, 
                                String season, String episode) {
        
    }
}