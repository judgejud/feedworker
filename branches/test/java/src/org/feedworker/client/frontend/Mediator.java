package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.Kernel;
import org.feedworker.core.ManageListener;
import org.feedworker.core.RssParser;
import org.feedworker.exception.ManageException;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;

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

    /**Restituisce l'istanza attiva del Mediator se non esiste la crea
     *
     * @return Mediator
     */
    public static Mediator getIstance() {
        if (proxy == null)
            proxy = new Mediator();
        return proxy;
    }

    //TODO: ripristinare col getbuildernumber
    String getTitle() {
        return getApplicationName() + " revision "
                //+ FeedWorkerClient.getApplication().getBuildNumber() + " by "
                + "405 by "
                + FeedWorkerClient.getApplication().getAuthor();
    }
    
    ApplicationSettings getProperties(){
        return prop;
    }
    
    ManageException getError(){
        return error;
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
    
    public String getNameTableSearchSub() {
        return core.TABLE_SEARCH_SUB;
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

    void runRss() {
        core.runRss(true);
    }

    /**chiama nel kernel la chiusura applicazione
     * 
     */
    void closeApp() {
        core.closeApp(false);
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

    public String[] getQualityEnum() {
        return Quality.toArray();
    }
    /**verifica impostazioni torrent
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveTorrent(String text) {
        if (!Lang.verifyTextNotNull(text))
            printAlert("Avviso: Non immettendo la Destinazione dei Torrent non potrai "
                    + "scaricare .torrent");
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

    public boolean saveSettings(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String timeout,
            boolean advancedDownload, boolean runIconized, String itasa,
            String myitasa, String user, String pwd, boolean autoMyitasa,
            boolean autoLoadMyItasa, String subsf, String mySubsf, String torrentDest,
            String mailTO,  String smtp, boolean paneLog, boolean paneSearch, 
            boolean paneSetting, boolean paneSubDest, boolean paneReminder, 
            boolean reminder, String googleUser, String googlePwd, String googleCalendar, 
            boolean paneTorrent, boolean paneCalendar, boolean torrentOption, 
            boolean paneShow) {
                
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
                checkSaveTorrent(torrentDest);
        }
        if (save) {
            setPropGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                    sambaUser, sambaPwd, time, timeout, 
                    advancedDownload, runIconized, reminder);
            setPropItasa(itasa, myitasa, user, pwd, autoMyitasa, autoLoadMyItasa);
            setPropSubsf(subsf, mySubsf);
            setPropTorrent(torrentDest, torrentOption);
            setPropAdvisor(mailTO, smtp, googleUser, googlePwd, googleCalendar);
            setPropVisiblePane(paneLog, paneSearch, paneSetting, paneSubDest, 
                            paneReminder, paneTorrent, paneCalendar, paneShow);
            core.writeProp();
            if (!oldLF.equals(prop.getApplicationLookAndFeel())) {
                printAlert("Il Look&Feel selezionato sarà disponibile al riavvio "
                                                                + "del client.");
            }
            if (!prop.isApplicationFirstTimeUsed() && first) {
                ManageListener.fireFrameEvent(this, ENABLE_BUTTON);
                runRss();
            } else {
                if (Lang.verifyTextNotNull(oldMin)
                        && !oldMin.equalsIgnoreCase(prop.getRefreshInterval())) {
                    restartRss();
                }
            }
            printOk("Impostazioni salvate in " + prop.getSettingsFilename());
        }
        return save;
    }

    private void setPropGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd, String time,
            String timeout, boolean advancedDownload,
            boolean runIconized, boolean reminder) {
        prop.setLocalFolder(dirLocal);
        prop.setSubtitleDestinationFolder(destSub);
        prop.setRefreshInterval(time);
        prop.setCifsShareDomain(sambaDomain);
        prop.setCifsShareLocation(sambaIP);
        prop.setCifsSharePath(sambaDir);
        prop.setCifsShareUsername(sambaUser);
        prop.setCifsSharePassword(sambaPwd);
        prop.setHttpTimeout(timeout);
        prop.setEnableAdvancedDownload(advancedDownload);
        prop.setEnableIconizedRun(runIconized);
        prop.setReminderOption(reminder);
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
    
    private void setPropAdvisor(String mailTO, String smtp, String googleUser,
            String googlePwd, String googleCalendar){
        prop.setMailTO(mailTO);
        prop.setMailSMTP(smtp);
        prop.setGoogleUser(googleUser);
        prop.setGooglePwd(googlePwd);
        prop.setGoogleCalendar(googleCalendar);
    }
    
    private void setPropVisiblePane(boolean log, boolean search, boolean setting, 
                                    boolean subdest, boolean reminder, boolean torrent,
                                    boolean calendar, boolean show){
        prop.setEnablePaneCalendar(calendar);
        prop.setEnablePaneLog(log);
        prop.setEnablePaneSearchSubItasa(search);
        prop.setEnablePaneSetting(setting);
        prop.setEnablePaneSubDestination(subdest);
        prop.setEnablePaneReminder(reminder);
        prop.setEnablePaneTorrent(torrent);
        prop.setEnablePaneShow(show);
    }
    
    private void setPropTorrent(String dest, boolean option){
        prop.setTorrentDestinationFolder(dest);
        prop.setTorrentOption(option);
    }

    void bruteRefresh() {
        core.bruteRefreshRSS();
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

    public void restartApplication() {
        core.closeApp(true);
    }

    public String getNameApp() {
        return FeedWorkerClient.getApplication().getName();
    }

    public String[] getAvailableLAF() {
        return FeedWorkerClient.getApplication().getLookAndFeelInstance().getAvailableLAF();
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
        if (name != null)
            core.backup(name);
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
    
    public void checkLoginItasaApi(String user, char[] password) {
        core.checkLoginItasa(user,new String(password));
    }

    public void removeReminders(ArrayList<Integer> numbers) {
        core.removeReminders(numbers);
    }

    public void refreshSingleCalendar(Object value) {
        core.refreshSingleCalendar(value.toString());
    }

    public void calendarImportTVFromMyItasa() {
        core.calendarImportNameTvFromMyItasa();
    }

    public void undoLastRemoveReminder() {
        
    }
    
    void printLastDate() {
        printOk("Data & ora ultimo aggiornamento: "+prop.getLastDateTimeRefresh());
    }
    
    void changeRuntimeLaf(String laf, JFrame parent){
        core.setWriteLaf(laf, parent);
    }

    public void checkSamba(String domain, String dir, String ip, String user, 
            char[] password) {
        if (core.testSamba(ip, dir, domain, user, new String(password)))
            printOk("Test connessione samba ok");
    }

    public void infoShow(String name) {
        core.requestInfoShow(name);
    }

    public void importNameListFromMyItasa() {
        core.listImportNameTvFromMyItasa();
    }

    public void saveList(Object[] toArray) {
        core.saveList(toArray);
    }

    public void requestSingleAddList(Object selectedItem) {
        core.requestSingleAddList(selectedItem);
    }

    void openWebsite(String url) {
        core.openWebsite(url);
    }

    void downloadSub(ArrayList<String> alLinks, boolean itasa, boolean id) {
        core.downloadSub(alLinks, itasa, id);
    }

    void downloadTorrent(ArrayList<String> array) {
        core.downloadTorrent(array);
    }

    void saveMap(TreeMap<KeyRule, ValueRule> map) {
        core.saveMap(map);
    }

    void synoDownloadRedirectory(ArrayList<String> array) {
        core.synoDownloadRedirectory(array);
    }

    public void openFormTV(String name) {
        core.openItasaID(name);
    }

    void setPropNotify(int i, boolean value) {
        core.setPropNotify(i, value);
    }

    void searchSubItasa(Object show, Object version, boolean complete, 
                                                String season, String episode) {
        core.searchSubItasa(show, version, complete, season, episode);
    }
}