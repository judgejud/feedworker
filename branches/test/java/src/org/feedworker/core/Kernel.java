package org.feedworker.core;
//IMPORT JAVA
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.http.HttpOther;
import org.feedworker.core.http.Https;
import org.feedworker.core.http.HttpItasa;
import org.feedworker.core.thread.DownloadThread;
import org.feedworker.core.thread.DownloadTorrentThread;
import org.feedworker.core.thread.IcsThread;
import org.feedworker.core.thread.RssBlogThread;
import org.feedworker.core.thread.RssThread;
import org.feedworker.core.thread.ShowThread;
import org.feedworker.exception.ItasaException;
import org.feedworker.exception.ManageException;
import org.feedworker.exception.TvrageException;
import org.feedworker.object.*;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.ResourceLocator;
import org.feedworker.util.Samba;
import org.feedworker.xml.*;

import org.jfacility.Io;
import org.jfacility.Util;
import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.SystemFileManager;

import org.opensanskrit.exception.NotAvailableLookAndFeelException;
import org.opensanskrit.exception.UnableRestartApplicationException;

import jcifs.smb.SmbException;

import org.apache.http.client.ClientProtocolException;
import org.apache.xmlrpc.XmlRpcException;

import org.feedworker.core.thread.NewsThread;
import org.jdom.JDOMException;

import org.xml.sax.SAXException;

/**Motore di Feedworker
 * 
 * @author luca
 */
public class Kernel implements PropertyChangeListener {
    // PUBLIC FINAL VARIABLES
    public final String ITASA = "Itasa";
    public final String SUBSF = "Subsf";
    public final String EZTV = "Eztv";
    public final String BTCHAT = "Btchat";
    public final String MYITASA = "MyItasa";
    public final String MYSUBSF = "MySubsf";
    public final String BLOG = "Blog";
    public final String ITASA_PM = "Itasa_PM";
    public final String SEARCH_TV = "SearchTV";
    public final String SUBTITLE_DEST = "SubtitleDest";
    public final String CALENDAR_SHOW = "CalendarShow";
    public final String REMINDER = "Reminder";
    public final String ITASA_NEWS = "ItasaNews";
    public final String TABLE_SEARCH_SUB = "SearchSub";
    public final String OPERATION_FOCUS = "Focus";
    public final String OPERATION_PROGRESS_SHOW = "ProgressShow";
    public final String OPERATION_PROGRESS_INCREMENT = "ProgressIncrement";
    // PRIVATE FINAL VARIABLES
    private final String RSS_TORRENT_EZTV = "http://ezrss.it/feed/";
    private final String RSS_TORRENT_BTCHAT = "http://rss.bt-chat.com/?cat=9";
    private final String LINK_SCHEDA_ITASA =
            "http://www.italiansubs.net/index.php?option=com_info&Itemid=12&idserie=";
    private final String ITASA_CALENDAR_ICS =
                        "http://www.italiansubs.net/icalendar/calendars/itasa.ics";
    private final File FILE_RULE = ResourceLocator.getFILE_RULE();
    private final File FILE_CALENDAR = ResourceLocator.getFILE_CALENDAR();
    private final File FILE_REMINDER = ResourceLocator.getFILE_REMINDER();
    private final File FILE_MYLIST = ResourceLocator.getFILE_MYLIST();
    private final File FILE_ITASA = ResourceLocator.getFILE_ITASA();
    private final File FILE_LINK_ITASA = ResourceLocator.getFILE_LINK_ITASA();
    // PRIVATE STATIC VARIABLES
    private static Kernel core = null;
    private static boolean debug_flag;
    private static String[] lastDates;
    // PRIVATE VARIABLES
    private String lastItasa=null, lastMyItasa=null, lastSubsf=null,
            lastEztv=null, lastBtchat=null, lastMySubsf=null, lastBlog=null;
    private int countItasa, countMyitasa, countSubsf, countMysubsf, countEztv, 
            countBtchat, countBlog, countPM, lastNewsID=0, countNews;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private Timer timer;
    private TreeMap<KeyRule, ValueRule> mapRules;
    private TreeMap<String, String> mapShowItasa;
    private ManageException error = ManageException.getIstance();
    private Calendar xmlCalendar;
    private Reminder xmlReminder;
    private ImportTaskList importTaskList;
    private ImportTaskCalendar importTaskCalendar;
    private RefreshTask refreshTask;
    private TreeSet tsIdCalendar;
    private ItasaOnline itasa;
    private ItasaUser user;
    private HttpItasa httpItasa;
    private XmlRPC xmlrpc;
    private ArrayList<Object[]> removeReminder;

    /**
     * Restituisce l'istanza corrente del kernel
     * 
     * @return istanza kernel
     */
    public static Kernel getIstance() {
        if (core == null)
            core = new Kernel();
        return core;
    }
    
    public static Kernel getIstance(boolean debug) {
        if (core == null){
            core = new Kernel();
            debug_flag = debug;
        }
        return core;
    }

    /**Scarica lo zip, estrae i sub e invoca l'analizzatore del path di
     * destinazione
     * 
     * @param als  arraylist di link
     * @param itasa
     */
    public void downloadSub(ArrayList<String> als, boolean itasa, boolean id) {
        DownloadThread dt = null;
        if (itasa && loginItasaHttp()){
            if (id){
                String url = "http://www.italiansubs.net/index.php?option=com_remository&"
                        + "Itemid=6&func=fileinfo&id=";
                for (int i=0; i<als.size(); i++)
                    als.set(i, url+als.get(i));
            }
            dt = new DownloadThread(mapRules, xmlReminder, als, httpItasa, false);
        } else if (!itasa){
            HttpOther http = new HttpOther(Lang.stringToInt(prop.getHttpTimeout())*1000);
            dt = new DownloadThread(mapRules, xmlReminder, als, http, false);
        } else if (httpItasa==null)
            printAlert("Non posso procedere al download per problemi di login ad itasa, "
                    + "controllare user e password");
        if (dt!=null){
            Thread t = new Thread(dt, "Thread download");
            t.start();
        }
    }

    /**Scarica i torrent
     * 
     * @param als arraylist di link
     */
    public void downloadTorrent(ArrayList<String> als) {
        DownloadTorrentThread dt = new DownloadTorrentThread(als);
        Thread t = new Thread(dt, "Torrent");
        t.start();
    }

    /**testa la connessione a samba
     * 
     * @param ip ip della macchina samba
     * @param dir directory condivisa
     * @param dom dominio
     * @param user utente
     * @param pwd password
     * @return true se positivo, false altrimenti
     */
    public boolean testSamba(String ip, String dir, String dom, String user,
            String pwd) {
        //TODO: thread
        boolean test = false;
        Samba.resetInstance();
        Samba s = Samba.getIstance(ip, dir, dom, user, pwd);
        try {
            test = s.testConn();
        } catch (SmbException ex) {
            error.launch(ex, getClass(), dir);
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        return test;
    }

    /**chiude/restarta l'applicazione salvando la data nel settings
     * 
     * @param data data da salvare
     * @param restart riavvio dell'applicazione
     */
    public void closeApp(boolean restart) {
        if (!Lang.verifyTextNotNull(prop.getLastDateTimeRefresh()))
            prop.setLastDateTimeRefresh(Common.actualTime());
        if (!prop.isApplicationFirstTimeUsed())
            prop.writeOnlyLastDate();
        if (restart) {
            try {
                FeedWorkerClient.getApplication().restart();
            } catch (UnableRestartApplicationException ex) {
                error.launch(ex, this.getClass());
            }
        } else
            System.exit(0);
    }

    /** Scrive le proprietà dell'applicazione nel file properties */
    public void writeProp() {
        prop.writeItasaSettings();
        prop.writeAdvisorSettings();
        prop.writePaneVisibleSetting();
        prop.writeTorrentSettings();
        prop.writeIrcSettings();
        if (prop.isSubsfactoryOption())
            prop.writeSubsfactorySettings();
        if (prop.isApplicationFirstTimeUsed())
            prop.writeApplicationFirstTimeUsedFalse();
        prop.writeGeneralSettings();
    }

    /** Esegue gli rss */
    public void runRss(boolean autoloaddownload) {
        if (!prop.isApplicationFirstTimeUsed()) {
            String temp = Common.actualTime();
            runItasaRss(true, autoloaddownload);
            if (prop.isSubsfactoryOption())
                runSubsfactory(true);
            if (prop.isTorrentOption())
                runTorrent(true);
            if (prop.isItasaBlog())
                runItasaBlog(true);
            if (prop.isItasaPM())
                runItasaPM(true);
            if (prop.isCalendarDay())
                parseCalendarICS();
            prop.setLastDateTimeRefresh(temp);
            int delay = Lang.stringToInt(prop.getRefreshInterval()) * 60000;
            runTimer(delay);
        }
    }
    
    public boolean runItasaNews(boolean first) {
        boolean status = false;
        countNews = 0;
        NewsThread nt = new NewsThread(lastNewsID);
        Thread t = new Thread(nt, ITASA_NEWS);
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (nt.getCount()>0){
            lastNewsID = nt.getLastId();
            if (!first){
                status = true;
                countNews = nt.getCount();
            }
        }
        return status;
    }

    /**esegue gli rss sotto timer
     * 
     * @param delay
     *            tempo in secondi per il timer
     */
    private void runTimer(int delay) {
        timer = new Timer();
        try {
            timer.scheduleAtFixedRate(new TimerTask()  {
                @Override
                public void run() {
                    boolean icontray = false;
                    prop.setLastDateTimeRefresh(Common.actualTime());
                    if (runItasaRss(false,true))
                        icontray = true;
                    if (runSubsfactory(false))
                        icontray = true;
                    if (prop.isTorrentOption() && runTorrent(false))
                        icontray = true;
                    if (prop.isItasaBlog() && runItasaBlog(false))
                        icontray = true;
                    if (prop.isItasaPM() && runItasaPM(false))
                        icontray = true;
                    if ((icontray) && (prop.isEnableNotifyAudioRss())) {
                        try {
                            AudioPlay.playFeedWav();
                        } catch (UnsupportedAudioFileException ex) {
                            error.launch(ex, getClass());
                        } catch (LineUnavailableException ex) {
                            error.launch(ex, getClass());
                        } catch (IOException ex) {
                            error.launch(ex, getClass(), null);
                        }
                    }
                    String msg = countItasa + ":" + countMyitasa + ":" + countBlog + 
                            ":" + countEztv + ":" + countBtchat + ":" + countSubsf + 
                            ":" + countMysubsf + ":" + countPM;
                    ManageListener.fireFrameEvent(this, icontray, msg);
                }// end run
            }, delay, delay);
        } catch (IllegalStateException ex) {
            error.launch(ex, getClass());
        }
    }

    /**Esegue la parte rss itasa
     * 
     * @param first primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runItasaRss(boolean first, boolean autoloaddownload) {
        boolean status = false;
        countItasa = 0;
        countMyitasa = 0;
        
        if (Lang.verifyTextNotNull(prop.getItasaFeedURL())) {
            RssThread rt = new RssThread(lastItasa, prop.getItasaFeedURL(), ITASA);
            Thread t = new Thread(rt, ITASA);
            t.start();
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (rt.getCount()>0){
                lastItasa = rt.getLastDate();
                if (!first){
                    status = true;
                    countItasa = rt.getCount();
                }
            }
        }
        if (Lang.verifyTextNotNull(prop.getMyitasaFeedURL())) {
            if (first && prop.isAutoLoadDownloadMyItasa())
                lastMyItasa = prop.getLastDateTimeRefresh();
            RssThread rt;
            if (first && !autoloaddownload)
                rt = new RssThread(lastMyItasa, prop.getMyitasaFeedURL(), MYITASA, 
                        loginItasaHttp(), autoloaddownload, first, mapRules, 
                                                        httpItasa, xmlReminder);
            else
                rt = new RssThread(lastMyItasa, prop.getMyitasaFeedURL(), MYITASA, 
                        loginItasaHttp(), prop.isAutoDownloadMyItasa(), first, mapRules, 
                                                        httpItasa, xmlReminder);
            Thread t = new Thread(rt, MYITASA);
            t.start();
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (rt.getCount()>0){
                lastMyItasa = rt.getLastDate();
                if (!first){
                    status = true;
                    countMyitasa = rt.getCount();
                }
            }
        }
        return status;
    }
    
    private boolean runItasaBlog(boolean first) {
        boolean status = false;
        String url = "http://feeds.feedburner.com/itasa-blog";
        countBlog = 0;
        RssBlogThread rt = new RssBlogThread(lastBlog, url);
        Thread t = new Thread(rt, BLOG);
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (rt.getCount()>0){
            lastBlog = rt.getLastDate();
            if (!first){
                status = true;
                countBlog = rt.getCount();
            }
        }
        return status;
    }
    
    private boolean runItasaPM(boolean first){
        boolean status = false;
        if (!first && loginItasaXmlRPC()){
            try {
                countPM = xmlrpc.getMessage();
                if (countPM > 0){
                    status = true;
                    ManageListener.fireTextPaneEvent(this,
                                    "Sono presenti dei messaggi privati",
                                    TextPaneEvent.ITASA_PM, true);
                }
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            }
        }
        return status;
    }

    /**Esegue la parte rss subsfactory
     * 
     * @param first primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runSubsfactory(boolean first) {
        boolean status = false;
        countSubsf = 0;
        countMysubsf = 0;
        if (Lang.verifyTextNotNull(prop.getSubsfactoryFeedURL())) {
            RssThread rt = new RssThread(lastSubsf, prop.getSubsfactoryFeedURL(), SUBSF);
            Thread t = new Thread(rt, SUBSF);
            t.start();
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (rt.getCount()>0){
                lastSubsf = rt.getLastDate();
                if (!first){
                    status = true;
                    countSubsf = rt.getCount();
                }
            }
        }
        if (Lang.verifyTextNotNull(prop.getMySubsfactoryFeedUrl())) {
            RssThread rt = new RssThread(lastMySubsf, 
                                        prop.getMySubsfactoryFeedUrl(), MYSUBSF);
            Thread t = new Thread(rt, MYSUBSF);
            t.start();
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (rt.getCount()>0){
                lastMySubsf = rt.getLastDate();
                if (!first){
                    status = true;
                    countMysubsf = rt.getCount();
                }
            }
        }
        return status;
    }

    /**Esegue la parte rss torrent
     * 
     * @param first primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runTorrent(boolean first) {
        boolean status = false;
        countBtchat = 0;
        countEztv = 0;
        RssThread rtE = new RssThread(lastEztv, RSS_TORRENT_EZTV, EZTV);
        RssThread rtB = new RssThread(lastBtchat, RSS_TORRENT_BTCHAT, BTCHAT);
        Thread tE = new Thread(rtE, EZTV);
        Thread tB = new Thread(rtB, BTCHAT);
        tE.start();
        tB.start();
        try {
            tE.join();
            tB.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (rtE.getCount()>0){
            lastEztv = rtE.getLastDate();
            if (!first){
                status = true;
                countEztv = rtE.getCount();
            }
        }
        if (rtB.getCount()>0){
            lastBtchat = rtB.getLastDate();
            if (!first){
                status = true;
                countBtchat = rtB.getCount();
            }
        }
        return status;
    }

    /**Interrompe il timer attuale e ne fa partire uno nuovo col nuovo intervallo*/
    public void stopAndRestartTimer() {
        if (timer!=null){
            timer.cancel();
            timer.purge();
        }
        runTimer(Lang.stringToInt(prop.getRefreshInterval()) * 60000);
    }

    private void parseCalendarICS(){
        IcsThread it = new IcsThread(ITASA_CALENDAR_ICS);
        Thread t = new Thread(it, "ItasaCalendarIcs");
        t.start();
    }

    /**Sostituisce la treemap delle regole con quella creata dal mediator
     * 
     * @param temp treepam regole
     */
    public void saveMap(TreeMap<KeyRule, ValueRule> temp) {
        try {
            new RuleDestination(FILE_RULE, false).writeMap(temp);
            mapRules = temp;
            printOk("Regola/e memorizzate");
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** Carica gli xml */
    @SuppressWarnings("unchecked")
    public void loadXml() {
        try {
            checkPath(FILE_RULE);
            RuleDestination xmlSubDest = new RuleDestination(FILE_RULE, true);
            ArrayList temp = xmlSubDest.initializeReader();
            mapRules = (TreeMap<KeyRule, ValueRule>) temp.get(0);
            if (mapRules != null)
                ManageListener.fireTableEvent(this,
                        (ArrayList<Object[]>) temp.get(1), SUBTITLE_DEST);
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        try {
            checkPath(FILE_CALENDAR);
            xmlCalendar = new Calendar(FILE_CALENDAR, true);
            ArrayList temp = xmlCalendar.readingDocument();
            tsIdCalendar = (TreeSet) temp.get(0);
            if (tsIdCalendar.size() > 0) 
                ManageListener.fireTableEvent(this,
                        (ArrayList<Object[]>) temp.get(1), CALENDAR_SHOW);
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        try {
            checkPath(FILE_REMINDER);
            xmlReminder = new Reminder(FILE_REMINDER, true);
            ArrayList<Object[]> temp = xmlReminder.readingDocument();
            if (temp.size() > 0) 
                ManageListener.fireTableEvent(this, temp, REMINDER);
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        try {
            checkPath(FILE_MYLIST);
            ListShow xml = new ListShow(FILE_MYLIST, true);
            TreeMap<String, Object[][]> map = xml.initializeReader();
            if (map!=null){
                ArrayList<String> array = new ArrayList<String>(map.keySet());
                ManageListener.fireTabbedPaneEvent(this, array, null);
                for (int i=0; i<array.size(); i++)
                    ManageListener.fireListEvent(this, array.get(i), map.get(array.get(i)));
            }
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        try {
            checkPath(FILE_LINK_ITASA);
            ItasaOffline xml = new ItasaOffline(FILE_LINK_ITASA, true);
            String[][] array = xml.initializeReaderLink();
            if (array!=null && array.length > 0)
               ManageListener.fireFrameEvent(this, array);
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** Effettua l'aggiornamento dei feed forzato */
    public void bruteRefreshRSS() {
        printOk("Timer in fase di reinizializzazione.");
        runItasaRss(false, true);
        if (prop.isSubsfactoryOption())
            runSubsfactory(false);
        if (prop.isTorrentOption())
            runTorrent(false);
        if (prop.isItasaBlog())
            runItasaBlog(false);
        stopAndRestartTimer();
        printOk("Timer restart ok.");
    }
    
    private void checkPath(File f) throws IOException{
        if (!f.exists()){
            File old = new File(f.getName());
            if (old.exists()){
                File dir = new File(f.getParent());
                if (!dir.exists())
                    dir.mkdir();
                Io.moveFile(old, f.getParent());
            }
        }
    }

    private String getSynoId(InputStream is) {
        String id = null;
        /*
         * JSONValue value = JSONParser.parse(jSon); JSONArray arr =
         * value.isArray(); for (int i = 0; i < arr.size(); i++) { JSONObject
         * obj = arr.get(I).isObject(); if (obj != null)
         * recordList.add(getProductAsRecord(obj, false)); }
         * record.setAttribute("id", JSONUtil.getLong("id", prodObj));
         * record.setAttribute("code", JSONUtil.getString("code", prodObj));
         * record.setAttribute("name", JSONUtil.getString("name", prodObj));
         * record.setAttribute("creationDate", JSONUtil.getDate("creationDate",
         * prodObj));
         */
        return id;
    }

    /**Effettua l'inserimento dei link al download redirectory del synology
     * 
     * @param link Arraylist di link
     */
    public void synoDownloadRedirectory(ArrayList<String> link) {
        HttpOther http = new HttpOther();
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        try {
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                for (int i = 0; i < link.size(); i++){
                    http = new HttpOther();
                    http.synoAddLink(url, synoID, link.get(i));
                    http.closeClient();
                }
                printSynology("link inviati al download redirectory Synology");
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /**
     * Effettua il backup dei settings vari
     * 
     * @param name
     *            nome file
     */
    public void backup(String name) {
        ArrayList<File> files = new ArrayList<File>();
        File s = new File("settings.properties");
        if (FILE_RULE.exists())
            files.add(FILE_RULE);
        if (s.exists())
            files.add(s);
        if (files.size() > 0) {
            if (!name.substring(name.length() - 4).toLowerCase().equalsIgnoreCase(".zip")) {
                name += ".zip";
            }
            File f = new File(name);
            try {
                Util.createZip(files, f);
                printOk("backup effettuato: " + f.getName());
            } catch (IOException ex) {
                error.launch(ex, getClass());
            }
        } else {
            printAlert("Non posso fare il backup poichè non ci sono files da backuppare.");
        }
    }

    /**
     * 
     * @param tv
     */
    public void detailedSearchShow(String tv) {
        TvRage t = new TvRage();
        try {
            ArrayList<Object[]> array = t.readingDetailedSearch_byShow(tv, false, 
                                                                            true);
            if (array != null) {
                ManageListener.fireTableEvent(this, array, SEARCH_TV);
                ManageListener.fireFrameEvent(this, SEARCH_TV);
            } else {
                printAlert("La ricerca di " + tv + " non ha prodotto risultati");
                ManageListener.fireFrameEvent(this, OPERATION_FOCUS);
            }
        } catch (JDOMException ex) {
            error.launch(ex, null);
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    public void searchIdTv(ArrayList<Object[]> from) {
        Object[] show = null;
        try {
            TvRage t = new TvRage();
            ArrayList<Object[]> al = new ArrayList<Object[]>();
            for (int i = 0; i < from.size(); i++) {
                show = from.get(i);
                if (!tsIdCalendar.contains(show[0])) {
                    Object[] array = setArray(t, show, true);
                    al.add(array);
                    xmlCalendar.addShowTV(array);
                }
            }
            xmlCalendar.write();
            ManageListener.fireTableEvent(this, al, CALENDAR_SHOW);
        } catch (JDOMException ex) {
            error.launch(ex, null);
        } catch (IOException ex) {
            error.launch(ex, null);
        } catch (IndexOutOfBoundsException ex) {
            printAlert("XML " + show[1].toString() + " non valido");
        }
    }

    private Object[] setArray(TvRage t, Object[] show, boolean status) throws 
                                    ConnectException, JDOMException, IOException {
        Object[] array = t.readingEpisodeList_byID(show[0].toString(),
                show[2].toString());
        array[0] = show[0];
        array[1] = show[1];
        if (status)
            array[2] = Common.getStatus((String) show[3]);
        else
            array[2] = show[3];
        array[3] = show[4];
        return array;
    }

    public void removeShowTv(int row, Object id) {
        try {
            xmlCalendar.removeShowTv(row);
            tsIdCalendar.remove(id);
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    public void removeAllShowTv() {
        try {
            xmlCalendar.removeAllShowTv();
            tsIdCalendar = new TreeSet();
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    public void importTvFromDestSub() {
        ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_SHOW, mapRules.size());
        importTaskCalendar = new ImportTaskCalendar(mapRules.keySet().iterator());
        importTaskCalendar.addPropertyChangeListener(this);
        importTaskCalendar.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getSource().getClass().getName();
        String className = this.getClass().getName();
        if (evtName.equalsIgnoreCase(className + "$ImportTaskCalendar")) {
            if (evt.getPropertyName().equals("progress")) {
                ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_INCREMENT, 
                        importTaskCalendar.getProgress());
                if (importTaskCalendar.isDone() && !importTaskCalendar.isCancelled()) {
                    try {
                        ManageListener.fireTableEvent(this, importTaskCalendar.get(), 
                                                                            CALENDAR_SHOW);
                    } catch (Exception e) {
                        error.launch(e, getClass());
                    }
                }
            }
        } else if (evtName.equalsIgnoreCase(className + "$RefreshTask")) {
            if (evt.getPropertyName().equals("progress")){
                ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_INCREMENT,
                                            refreshTask.getProgress());
                if (refreshTask.isDone())
                    fireCalendar();
            } else if (evt.getPropertyName().equals("state")){
                if (refreshTask.isDone() && refreshTask.getProgress()==0) {
                    ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_INCREMENT, 1);
                    fireCalendar();
                } else if (refreshTask.isCancelled())
                    fireCalendar();
            }
        } else if (evtName.equalsIgnoreCase(className + "$ImportTaskList")) {
            if (evt.getPropertyName().equals("progress")) {
                ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_INCREMENT, 
                        importTaskList.getProgress());
                if (importTaskList.isDone() && !importTaskList.isCancelled()) {
                    try {
                        ManageListener.fireListEvent(this, importTaskList.getDest(), 
                                importTaskList.get());
                    } catch (Exception e) {
                        error.launch(e, getClass());
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void fireCalendar(){
        try {
            xmlCalendar = new Calendar(FILE_CALENDAR, true);
            ArrayList temp = xmlCalendar.readingDocument();
            tsIdCalendar = (TreeSet) temp.get(0);
            if (tsIdCalendar.size() > 0)
                ManageListener.fireTableEvent(this,
                            (ArrayList<Object[]>) temp.get(1), CALENDAR_SHOW);
        } catch (Exception e) {
            error.launch(e, this.getClass());
        }
    }
    
    public void refreshSingleCalendar(String id) {
        Long temp = null;
        try{
            temp = XPathCalendar.queryRowId(id);
        } catch (ParserConfigurationException ex) {
            error.launch(ex, getClass());
        } catch (SAXException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (XPathExpressionException ex) {
            error.launch(ex, getClass());
        }
        if (temp!=null){
            ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_SHOW, 1);
            refreshTask = new RefreshTask(temp.intValue(), id);
            refreshTask.addPropertyChangeListener(this);
            refreshTask.execute();
        }
    }

    public void refreshCalendar() {
        TreeMap<Long, String> array = null;
        try {
            array = XPathCalendar.queryDayID(Common.dateString(Common.actualDate()));
        } catch (ParserConfigurationException ex) {
            error.launch(ex, getClass());
        } catch (SAXException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (XPathExpressionException ex) {
            error.launch(ex, getClass());
        }
        if (array!=null && array.size() > 0) {
            ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_SHOW, array.size());
            refreshTask = new RefreshTask(array);
            refreshTask.addPropertyChangeListener(this);
            refreshTask.execute();
        }
    }

    public void searchDay(int temp) {
        String day = null;
        if (temp == 0)
            day = Common.dateString(Common.actualDate());
        else if (temp == -1)
            day = Common.dateString(Common.yesterdayDate());
        else if (temp == 1)
            day = Common.dateString(Common.tomorrowDate());
        try {
            String result = XPathCalendar.queryDayEquals(day);
            if (result != null) {
                String msg;
                if (result.equalsIgnoreCase(""))
                    msg = "Non ci sono serial tv previsti per " + getDay(temp) + ".";
                else
                    msg = "Serial tv previsti per " + getDay(temp) + ": " + result;
                ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.DAY_SERIAL, true);
            }
        } catch (ParserConfigurationException ex) {
            error.launch(ex, getClass());
        } catch (SAXException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (XPathExpressionException ex) {
            error.launch(ex, getClass());
        }
    }

    private String getDay(int temp) {
        if (temp == 0)
            return "oggi";
        else if (temp == 1)
            return "domani";
        else if (temp == -1)
            return "ieri";
        return null;
    }

    public void openFolder(String dir) {
        if (prop.isLocalFolder()) {
            try {
                SystemFileManager.openExplorer(new File(dir));
            } catch (IOException ex) {
                error.launch(ex, this.getClass());
            }
        } else {
            String newdir = "\\\\" + prop.getCifsShareLocation() + "\\" + 
                    prop.getCifsSharePath()+"\\"+dir;
            try {
                SystemFileManager.browse(newdir);
            } catch (MalformedURLException ex) {
                error.launch(ex, this.getClass());
            } catch (IOException ex) {
                error.launch(ex, this.getClass());
            } catch (URISyntaxException ex) {
                error.launch(ex, this.getClass());
            }
        }
    }
    
    public void stopImportRefreshCalendar() {
        if (importTaskCalendar!=null && 
                    importTaskCalendar.getState()==SwingWorker.StateValue.STARTED)
            importTaskCalendar.cancel(true);
        else if (refreshTask!=null && refreshTask.getState()==SwingWorker.StateValue.STARTED)
            refreshTask.cancel(true);
    }

    /**
     * Stampa il messaggio di alert invocando il metodo fire opportuno
     * 
     * @param msg
     *            testo da stampare
     */
    private void printAlert(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, true);
    }

    private void printSynology(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.SYNOLOGY, true);
    }

    private void printOk(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.OK, true);
    }
    
    private boolean loginItasaHttp(){
        boolean login = false;
        if (httpItasa==null){
            try {
                httpItasa = new HttpItasa(Integer.parseInt(prop.getHttpTimeout())*1000);
                if (httpItasa.testConnectItasa(prop.getItasaUsername(), prop.getItasaPassword())){
                    httpItasa.connectItasa(prop.getItasaUsername(), prop.getItasaPassword());
                    login = true;
                } else
                    httpItasa = null;
            } catch (ClientProtocolException ex) {
                ex.printStackTrace();
                httpItasa = null;
            } catch (IOException ex) {
                ex.printStackTrace();
                httpItasa = null;
            }
        } else 
            login = true;
        return login;
    }

    private boolean loginItasaAPI(String username, String pwd) {
        boolean login = false;
        if (user==null){
            try {
                user = new ItasaOnline().login(username, pwd);
                login = true;
            } catch (JDOMException ex) {
                error.launch(ex, this.getClass());
            } catch (IOException ex) {
                error.launch(ex, this.getClass());
            } catch (ItasaException ex) {
                printAlert("Login itasa API: " + ex.getMessage());
            } catch (Exception ex) {
                error.launch(ex, this.getClass());
            }
        } else 
            login = true;
        return login;
    }
    
    private boolean loginItasaXmlRPC(){
        boolean login = false;
        if (xmlrpc==null){
            try {
                xmlrpc = new XmlRPC();
                if (xmlrpc.testConn(prop.getItasaUsername(), prop.getItasaPassword()))
                    login = true;
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                xmlrpc = null;
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
                xmlrpc = null;
            } 
        } else 
            login = true;
        return login;
    }
    
    public void checkLoginItasaAPI(String username, String pwd) {
        String msg = "CheckLogin Itasa API: "; 
        try {
            new ItasaOnline().login(username, pwd);
            printOk(msg + "ok");
        } catch (JDOMException ex) {
            error.launch(ex, this.getClass());
        } catch (IOException ex) {
            error.launch(ex, this.getClass());
        } catch (ItasaException ex) {
            printAlert(msg + ex.getMessage());
        } catch (Exception ex) {
            error.launch(ex, this.getClass());
        }
    }
    
    public void checkLoginItasaPM(String username, String pwd) {
        String msg = "CheckLogin Itasa Forum messaggi privati: "; 
        try {
            new XmlRPC().testConn(username, pwd);
            printOk(msg + "ok");
        } catch (MalformedURLException ex) {
            error.launch(ex, this.getClass());
        } catch (XmlRpcException ex) {
            error.launch(ex, this.getClass());
        } 
    }

    public void removeReminders(ArrayList<Integer> numbers, ArrayList<Object[]> removed) {
        try {
            for (int i=0; i<numbers.size(); i++)
                xmlReminder.removeItem(numbers.get(i).intValue());
            xmlReminder.write();
            removeReminder = removed;
        } catch (IOException ex) {
            error.launch(ex, getClass());
        }
    }
    
    public void undoLastRemoveReminder(){
        try {
            for (int i=0; i<removeReminder.size(); i++)
                xmlReminder.addItem(removeReminder.get(i));
            xmlReminder.write();
            ManageListener.fireTableEvent(this, removeReminder, REMINDER);
            removeReminder = null;
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    public void searchSubItasa(Object show, Object version, boolean complete, 
                                                String season, String episode) {
        String id = mapShowItasa.get(show);
        String _version = null;
        String query = null;
        if (!version.equals(new String("*")))
            _version = version.toString();
        if (complete){
            if (season!=null)
                query = season + " Completa";
            else 
                query = "Completa";
        } else {
            if (season!=null){
                query = season + "x";
                if (episode!=null)
                    query += episode;
            } else if (episode!=null)
                query = "x" + episode;
        }
        try {
            ArrayList<Subtitle> array = itasa.subtitleSearch(id, _version, query, -1);
            if (array.size()>0)
                ManageListener.fireTableEvent(this, TABLE_SEARCH_SUB, array);
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (ItasaException ex) {
            printAlert(ex.getMessage());
        } catch (Exception ex) {
            error.launch(ex, getClass());
        }
    }

    public void calendarImportNameTvFromMyItasa() {
        if (loginItasaAPI(prop.getItasaUsername(), prop.getItasaPassword())){
            if (user.isMyitasa()){
                ArrayList<String> myShows = null;
                try {
                    myShows = itasa.myItasaShowsName(user.getAuthcode());
                } catch (JDOMException ex) {
                    error.launch(ex, getClass());
                } catch (IOException ex) {
                    error.launch(ex, getClass());
                } catch (ItasaException ex) {
                    printAlert(ex.getMessage());
                } catch (Exception ex) {
                    error.launch(ex, getClass());
                }
                if (myShows!=null && myShows.size()>0){
                    ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_SHOW,
                        myShows.size());
                    importTaskCalendar = new ImportTaskCalendar(myShows);
                    importTaskCalendar.addPropertyChangeListener(this);
                    importTaskCalendar.execute();
                }
            } else 
                printAlert("Non hai abilitato l'uso di myItasa");
        }
    }

    /**Inizializza dal web l'elenco delle serie itasa, 
     * o le preleva dall'xml nel caso in cui ci sono problemi vari con itasa
     */
    public void loadItasaSeries() {
        if (mapShowItasa==null){
            itasa = new ItasaOnline();
            try {
                mapShowItasa = itasa.showList();
                new ItasaOffline(FILE_ITASA, false).writeMap(mapShowItasa);
            } catch (JDOMException ex) {
                try {
                    mapShowItasa = new ItasaOffline(FILE_ITASA, true).initializeReader();
                } catch (JDOMException ex1) {
                    error.launch(ex1, getClass());
                } catch (IOException ex1) {
                    error.launch(ex1, getClass());
                }
            } catch (IOException ex) {
                error.launch(ex, getClass());
            } catch (ItasaException ex) {
                printAlert(ex.getMessage());
            } catch (Exception ex) {
                error.launch(ex, getClass());
            }
        }
        if (mapShowItasa!=null)
            ManageListener.fireComboBoxEvent(this, mapShowItasa.keySet().toArray());
    }

    public void setPropNotify(int i, boolean value) {
        if (i==0)
            prop.setEnableNotifyAudioRss(value);
        else if (i==1)
            prop.setEnableNotifyAudioSub(value);
        else if (i==2)
            prop.setEnableNotifyMail(value);
        else if (i==3)
            prop.setEnableNotifySms(value);
        prop.writeNotifySettings();
    }
 
    public void setWriteLaf(String laf, Frame frame){
        try {
            FeedWorkerClient.getApplication().getLookAndFeelInstance().
                                                setLookAndFeelRuntime(laf, frame);
            prop.setApplicationLookAndFeel(laf);
        } catch (NotAvailableLookAndFeelException ex) {
            error.launch(ex, this.getClass());
        } catch (GeneralSecurityException ex) {
            error.launch(ex, this.getClass());
        } catch (IOException ex) {
            error.launch(ex, this.getClass());
        }
    }

    public void requestInfoShow(String name) {
        ShowThread st = new ShowThread(name, mapShowItasa.get(name));
        Thread t = new Thread(st, "Thread Show");
        t.start();
    }

    public void requestSingleAddList(String tab, Object serial) {
        ItasaOnline i = new ItasaOnline();
        String file = null;
        try {
            String temp = i.getUrlThumbnail(mapShowItasa.get(serial));
            String thumbnail = Https.getInstance().getLocationRedirect(temp);
            file = downloadImage(thumbnail);
        } catch (FileNotFoundException ex) {
            String thumbnail = "http://www.italiansubs.net/varie/ico/unknown.png";
            try {
                file = downloadImage(thumbnail);
            } catch (IOException e) {
                error.launch(e, getClass());
            }
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (Exception ex) {
            error.launch(ex, getClass());
        }
        ManageListener.fireListEvent(this, tab, 
                                    new Object[][]{{serial, new ImageIcon(file)}});
    }
    
    public void listImportNameTvFromMyItasa(String dest) {
        if (loginItasaAPI(prop.getItasaUsername(), prop.getItasaPassword())){
            if (user.isMyitasa()){
                ArrayList<String> myShows = null;
                try {
                    myShows = itasa.myItasaShowsName(user.getAuthcode());
                } catch (JDOMException ex) {
                    error.launch(ex, getClass());
                } catch (IOException ex) {
                    error.launch(ex, getClass());
                } catch (ItasaException ex) {
                    printAlert(ex.getMessage());
                } catch (Exception ex) {
                    error.launch(ex, getClass());
                }
                if (myShows!=null && myShows.size()>0){
                    ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_SHOW,
                        myShows.size());
                    importTaskList = new ImportTaskList(myShows, dest);
                    importTaskList.addPropertyChangeListener(this);
                    importTaskList.execute();
                }
            } else 
                printAlert("Non hai abilitato l'uso di myItasa");
        }
    }
    
    private String downloadImage(String link) throws MalformedURLException, 
                                                                    IOException{
        String[] split = link.split("/");
        String temp = split[split.length-1].replaceAll(":", "");
        temp = temp.replaceAll("%20", " ");
        String file = ResourceLocator.getThumbnailShows() + temp;
        File dir = new File(ResourceLocator.getThumbnailShows());
        if (!dir.exists())
            dir.mkdir();
        File f = new File(file);
        if (!f.exists())
            Io.downloadSingle(new URL(link.replaceAll(" ", "%20")).openStream(), f);
        return file;
    }
    
    public void openWebsite(String url) {
        try {
            SystemFileManager.browse(url);
        } catch (URISyntaxException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        }
    }

    public void openItasaID(String name) {
        String url = LINK_SCHEDA_ITASA + mapShowItasa.get(name);
        try {
            SystemFileManager.browse(url);
        } catch (URISyntaxException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        }
    }

    public void saveList(TreeMap<String, Object[]> map) {
        try {
            new ListShow(FILE_MYLIST, false).writeList(map);
            printOk("Lista/e salvata/e");
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        }
    }

    public void checkLoginItasa(String user, String pwd) {
        HttpItasa h = new HttpItasa(6000);
        String msg = "Check login Itasa: ";
        try {
            if (h.testConnectItasa(user, pwd))
                printOk(msg + "ok");
            else
                printAlert(msg + "Utente e/o password errata");
        } catch (ClientProtocolException ex) {
            error.launch(ex, null);
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    public void openWebsiteItasaPM() {
        String url = "http://www.italiansubs.net/forum/index.php?action=pm";
        try {
            SystemFileManager.browse(url);
        } catch (URISyntaxException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        }
    }
    
    public void connectIrc() {
        Emoticon.getInstance();
        String nick = prop.getIrcNick();
        if (!Lang.verifyTextNotNull(nick))
            nick = "GuestFeedworker";
        try {
            Irc.getInstance(nick, prop.getIrcPwd());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void joinChan(String name){
        Irc i = Irc.getInstance();
        if (i!=null && i.isConnected())
            i.joinChannel(name);
    }
    
    public void disconnectIrc(){
        Irc i = Irc.getInstance();
        if (i != null)
            i.disconnect();
    }
    
    public boolean isConnectedIrc(){
        Irc i = Irc.getInstance();
        if (i==null)
            return false;
        return i.isConnected();
    }

    public void sendIrcMessage(String name, String text) {
        Irc i = Irc.getInstance();
        if (i!=null && i.isConnected())
            i.sendMessage(name, text);
    }

    public void renameIrcNick(String nick) {
        Irc i = Irc.getInstance();
        if (i!=null && i.isConnected())
            i.changeNick(nick);
    }

    class ImportTaskCalendar extends SwingWorker<ArrayList<Object[]>, Void> {
        private boolean myitasa;
        private ArrayList<String> myShows; 
        private Iterator<KeyRule> iterKey;
        
        public ImportTaskCalendar(ArrayList<String> shows){
            myShows = shows;
            myitasa = true;
        }
        
        public ImportTaskCalendar(Iterator<KeyRule> key){
            iterKey = key;
            myitasa = false;
        }
        
        @Override
        public ArrayList<Object[]> doInBackground() {
            int progress = 0;
            ArrayList<Object[]> alObjs = new ArrayList<Object[]>();
            TreeSet<Object> ts = new TreeSet<Object>();
            setProgress(progress);
            try {
                TvRage t = new TvRage();
                if (myitasa){
                    int count = 0;
                    while (count<myShows.size() && !this.isCancelled()) {
                        importShow(myShows.get(count), t, ts, alObjs);
                        count++;
                        setProgress(++progress);
                    }
                    if (!this.isCancelled()) {
                        tsIdCalendar = ts;
                        xmlCalendar.write();
                    }
                }else{
                    while (iterKey.hasNext() && !this.isCancelled()) {
                        importShow(iterKey.next().getName(), t, ts, alObjs);
                        setProgress(++progress);
                    }
                    if (!this.isCancelled()) {
                        tsIdCalendar = ts;
                        xmlCalendar.write();
                    }
                }
            } catch (JDOMException ex) {
                error.launch(ex, null);
            } catch (IOException ex) {
                error.launch(ex, null);
            }
            return alObjs;
        }
        
        private void importShow(String name, TvRage t, TreeSet<Object> ts, ArrayList<Object[]> al) 
                                                throws JDOMException, IOException{
            ArrayList<Object[]> temp = t.readingDetailedSearch_byShow(
                                                                name, true, false);
            if (temp != null) {
                Object[] show = temp.get(0);
                if (!ts.contains(show[0])) {
                    Object[] array = setArray(t, show, false);
                    al.add(array);
                    ts.add(show[0]);
                    xmlCalendar.addShowTV(array);
                }
            }
        }
    }

    class RefreshTask extends SwingWorker<Void, Void> {
        private TreeMap<Long, String> tmRefresh;
        private String id;
        private int row;
        private boolean all;
        
        public RefreshTask(TreeMap<Long, String> _tm) {
            tmRefresh = _tm;
            all = true;
        }
        
        public RefreshTask(int k, String v){
            row = k;
            id = v;
            all = false;
        }
        
        @Override
        public Void doInBackground() {
            int progress = 0;
            TvRage t = new TvRage();
            setProgress(progress);
            Calendar xmlClone = xmlCalendar.clone();
            try {
                if (all){
                    Iterator<Long> iter = tmRefresh.descendingKeySet().iterator();
                    while (iter.hasNext() && !this.isCancelled()) {
                        Long index = iter.next();
                        id = tmRefresh.get(index);
                        Object[] array;
                        try {
                            array = setArray(t, t.showInfo_byID(id), true);
                        } catch (TvrageException te){
                            array = setArrayException();
                        }
                        if (debug_flag)
                            System.out.println(id + " " + array[1]);
                        xmlClone.removeShowTv(index.intValue() - 1);
                        xmlClone.addShowTV(array);
                        setProgress(++progress);
                    } //end while
                    if (!this.isCancelled())
                        xmlCalendar.reverseDataCloning(xmlClone);
                } else { // REFRESH SINGLE
                    Object[] array;
                    try {
                        array = setArray(t, t.showInfo_byID(id), true);
                    } catch (TvrageException te){
                        array = setArrayException();
                    }
                    xmlClone.removeShowTv(row-1);
                    xmlClone.addShowTV(array);
                    setProgress(++progress);
                    if (!this.isCancelled())
                        xmlCalendar.reverseDataCloning(xmlClone);
                }
            } catch (ConnectException ex) {
                try {
                    xmlCalendar.reverseDataCloning(xmlClone);
                    printAlert("Errore di connessione con tvrage");
                } catch (IOException ex1) {
                    error.launch(ex1, null);    
                }
            } catch (JDOMException ex) {
                error.launch(ex, null);
            } catch (IOException ex) {
                error.launch(ex, null);
            }
            return null;
        }
        
        private Object[] setArrayException(){
            Object[] values = new Object[10];
            values[0] = id;
            values[6] = values[9] = null;
            values[3] = values[4] = values[5] = values[7] = values[8] = "";
            values[2] = -1;
            try {
                values[1] = XPathCalendar.queryIdShow(id);
            } catch (SAXException ex) {
                error.launch(ex, null);
            } catch (ParserConfigurationException ex) {
                error.launch(ex, null);
            } catch (IOException ex) {
                error.launch(ex, null);
            } catch (XPathExpressionException ex) {
                error.launch(ex, null);
            }
            printAlert("Tvrage errore: non esiste lo show " + values[1] + " avente id " + id);
            return values;
        }
    } //end class RefreshTask
    
    class ImportTaskList extends SwingWorker<Object[][], Void> {
        private ArrayList<String> myShows; 
        private String dest;
        public ImportTaskList(ArrayList<String> array, String _dest){
            myShows = array; 
            dest = _dest;
        }

        public String getDest() {
            return dest;
        }
        
        @Override
        public Object[][] doInBackground() {
            int progress = 0;
            ItasaOnline i = new ItasaOnline();
            String serial=null, temp, thumbnail, file=null;
            Object[][] array = new Object[myShows.size()][2];
            while (progress<myShows.size() && !this.isCancelled()) {
                try {
                    serial = myShows.get(progress);
                    temp = i.getUrlThumbnail(mapShowItasa.get(serial));
                    thumbnail = Https.getInstance().getLocationRedirect(temp);
                    file = downloadImage(thumbnail);
                } catch (FileNotFoundException ex) {
                    thumbnail = "http://www.italiansubs.net/varie/ico/unknown.png";
                    try {
                        file = downloadImage(thumbnail);
                    } catch (IOException e) {
                        error.launch(e, getClass());
                    }
                } catch (IOException ex) {
                    error.launch(ex, getClass());
                } catch (Exception ex) {
                    error.launch(ex, getClass());
                }
                array[progress][0] = serial;
                array[progress][1] = new ImageIcon(file);
                setProgress(++progress);
            }
            return array;
        }
    }//end class ImportTaskList
} //END class Kernel