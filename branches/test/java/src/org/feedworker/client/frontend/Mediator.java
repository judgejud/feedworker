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
                + "406 by "
                + FeedWorkerClient.getApplication().getAuthor();
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

    void writeSettings() {
        core.writeProp();
    }
}