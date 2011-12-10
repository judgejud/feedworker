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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

import net.fortuna.ical4j.data.ParserException;
import org.apache.http.client.ClientProtocolException;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.exception.ItasaException;
import org.feedworker.exception.ManageException;
import org.feedworker.object.*;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.ResourceLocator;
import org.feedworker.util.Samba;
import org.feedworker.xml.Calendar;
import org.feedworker.xml.ItasaOnline;
import org.feedworker.xml.ItasaOffline;
import org.feedworker.xml.ListShow;
import org.feedworker.xml.Reminder;
import org.feedworker.xml.RuleDestination;
import org.feedworker.xml.TvRage;
import org.feedworker.xml.XPathCalendar;

import org.jfacility.Io;
import org.jfacility.Util;
import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.SystemFileManager;

import org.opensanskrit.exception.NotAvailableLookAndFeelException;
import org.opensanskrit.exception.UnableRestartApplicationException;

import jcifs.smb.SmbException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import java.io.FileInputStream;
import org.apache.xmlrpc.XmlRpcException;
import org.feedworker.xml.XmlRPC;
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
    public final String CALENDAR = "Calendar";
    public final String REMINDER = "Reminder";
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
    private final File FILE_RULE = new File("rules.xml");
    private final File FILE_CALENDAR = new File("calendar.xml");
    private final File FILE_REMINDER = new File("reminder.xml");
    private final File FILE_MYLIST = new File("mylist.xml");
    private final File FILE_ITASA = new File("itasa.xml");
    private final File FILE_LINK_ITASA = new File("link_itasa.xml");    
    // PRIVATE STATIC VARIABLES
    private static Kernel core = null;
    private static boolean debug_flag;
    // PRIVATE VARIABLES
    private String lastItasa=null, lastMyItasa=null, lastSubsf=null,
            lastEztv=null, lastBtchat=null, lastMySubsf=null, lastBlog=null;
    private int countItasa, countMyitasa, countSubsf, countMysubsf, countEztv, 
            countBtchat, countBlog;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private Timer timer;
    
    private TreeMap<KeyRule, ValueRule> mapRules;
    private TreeMap<String, String> mapShowItasa;
    private ManageException error = ManageException.getIstance();
    private Calendar xmlCalendar;
    private RuleDestination xmlSubDest;
    private Reminder xmlReminder;
    private ImportTaskList importTaskList;
    private ImportTaskCalendar importTaskCalendar;
    private RefreshTask refreshTask;
    private TreeSet tsIdCalendar;
    private ItasaOnline itasa;
    private ItasaUser user;
    private HttpItasa httpItasa;
    private XmlRPC xmlrpc;
    

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

    /**effettua il download automatico di myitasa comprende le fasi anche di
     * estrazione zip e analizzazione percorso definitivo.
     * 
     * @param link link da analizzare
     */
    private void downItasaAuto(ArrayList<String> links, boolean first) {
        prop.setLastDateTimeRefresh(Common.actualTime());
        prop.writeOnlyLastDate();
        if (loginItasaHttp()){
            DownloadThread dt = null;
            if (first)
                dt = new DownloadThread(mapRules, xmlReminder, links, httpItasa, false);
            else
                dt = new DownloadThread(mapRules, xmlReminder, links, httpItasa, true);
            Thread t = new Thread(dt, "AutoItasa");
            t.start();
        } else 
            printAlert("Non posso procedere al download per problemi di login ad itasa, "
                    + "controllare user e password");
    }

    /**Scarica i torrent
     * 
     * @param als arraylist di link
     */
    public void downloadTorrent(ArrayList<String> als) {
        //TODO:creare il thread
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        try {
            for (int i = 0; i < als.size(); i++) {
                InputStream is = http.getTorrent(als.get(i));
                if (is != null) {
                    File f = new File(prop.getTorrentDestinationFolder()
                            + File.separator + http.getNameFile());
                    Io.downloadSingle(is, f);
                    ManageListener.fireTextPaneEvent(this,
                            "Scaricato: " + http.getNameFile(),
                            TextPaneEvent.TORRENT, true);
                } else {
                    printAlert("Non posso gestire " + als.get(i).split(".")[1]);
                }
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        http.closeClient();
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
        if (prop.isSubsfactoryOption())
            prop.writeSubsfactorySettings();
        if (prop.isApplicationFirstTimeUsed())
            prop.writeApplicationFirstTimeUsedFalse();
        prop.writeGeneralSettings();
    }

    /**Restituisce l'arraylist contenente i feed rss
     * 
     * @param urlRss url rss da analizzare
     * @param data data da confrontare
     * @param from  provenienza
     * @param download download automatico
     * @param first
     * @return arraylist di feed(array di oggetti)
     */
    private ArrayList<Object[]> getFeedRss(String urlRss, String data,
            String from, boolean autodownload, boolean first) {
        RssParser rss = null;
        ArrayList<Object[]> matrice = null;
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        try {
            InputStream ist = http.getStreamRss(urlRss);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Io.downloadSingle(ist, ft);
                rss = new RssParser(ft);
                matrice = rss.readRss();
                ft.delete();
                boolean continua = true;
                if (data != null) {
                    Date confronta = Common.stringDateTime(data);
                    ArrayList<String> links = new ArrayList<String>();
                    for (int i = matrice.size() - 1; i >= 0; i--) {
                        String date_matrix = String.valueOf(matrice.get(i)[1]);
                        if (confronta.before(Common.stringDateTime(date_matrix))) {
                            if (continua) {
                                if (from.equals(ITASA)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_ITASA, true);
                                } else if (from.equals(MYITASA)
                                        && !prop.isAutoDownloadMyItasa()) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYITASA, true);
                                } else if (from.equals(SUBSF)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_SUBSF,true);
                                } else if (from.equals(MYSUBSF)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYSUBSF, true);
                                } else if (from.equals(EZTV)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_EZTV, true);
                                } else if (from.equals(BTCHAT)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_BTCHAT, true);
                                }
                                continua = false;
                            }
                            if (autodownload){
                                if ((isNotStagione(matrice.get(i)[2])))
                                    links.add((String)matrice.get(i)[0]);
                                else 
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYITASA, true);
                            }
                            
                        } else if (first && from.equals(MYITASA)) {
                            // non deve fare nulla
                        } else // if confronta after
                            matrice.remove(i);
                    } //end for
                    if (links.size()>0)
                        downItasaAuto(links, first);
                }
            }
        } catch (ParseException ex) {
            error.launch(ex, getClass());
        } catch (ParsingFeedException ex) {
            error.launch(ex, getClass(), from);
        } catch (FeedException ex) {
            error.launch(ex, getClass(), from);
        } catch (IllegalArgumentException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), from);
        }
        return matrice;
    }
    
    private ArrayList<Object[]> getFeedBlog(String urlRss, String data) {
        RssParser rss = null;
        ArrayList<Object[]> matrice = null;
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        try {
            InputStream ist = http.getStreamRss(urlRss);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Io.downloadSingle(ist, ft);
                rss = new RssParser(ft);
                matrice = rss.readRssBlog();
                ft.delete();
                boolean continua = true;
                if (data != null) {
                    Date confronta = Common.stringDateTime(data);
                    for (int i = matrice.size() - 1; i >= 0; i--) {
                        String date_matrix = String.valueOf(matrice.get(i)[1]);
                        if (confronta.before(Common.stringDateTime(date_matrix))) {
                            if (continua) {
                                ManageListener.fireTextPaneEvent(this,
                                    "Nuovo/i feed " + BLOG,
                                    TextPaneEvent.FEED_BLOG, true);
                                continua = false;
                            }
                        } else // if confronta after
                            matrice.remove(i);
                    } //end for
                }
            }
        } catch (ParseException ex) {
            error.launch(ex, getClass());
        } catch (ParsingFeedException ex) {
            error.launch(ex, getClass(), BLOG);
        } catch (FeedException ex) {
            error.launch(ex, getClass(), BLOG);
        } catch (IllegalArgumentException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), BLOG);
        }
        return matrice;
    }

    /** Esegue gli rss */
    public void runRss(boolean autoloaddownload) {
        if (!prop.isApplicationFirstTimeUsed()) {
            String temp = Common.actualTime();
            runItasa(true, autoloaddownload);
            if (prop.isSubsfactoryOption())
                runSubsfactory(true);
            if (prop.isTorrentOption())
                runTorrent(true);
            if (prop.isItasaBlog())
                runItasaBlog(true);
            if (prop.isItasaPM())
                runItasaPM(true);
            prop.setLastDateTimeRefresh(temp);
            int delay = Lang.stringToInt(prop.getRefreshInterval()) * 60000;
            runTimer(delay);
        }
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
                    System.out.println(Common.actualTime());
                    boolean icontray = false;
                    prop.setLastDateTimeRefresh(Common.actualTime());
                    if (runItasa(false,true))
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
                            ":" + countMysubsf;
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
    private boolean runItasa(boolean first, boolean autoloaddownload) {
        boolean status = false;
        countItasa = 0;
        countMyitasa = 0;
        ArrayList<Object[]> feedIta, feedMyita;
        if (Lang.verifyTextNotNull(prop.getItasaFeedURL())) {
            feedIta = getFeedRss(prop.getItasaFeedURL(), lastItasa, ITASA,
                    false, first);
            if ((feedIta != null) && (feedIta.size() > 0)) {
                if (!first){
                    status = true;
                    countItasa = feedIta.size();
                }
                lastItasa = (String) feedIta.get(0)[1];
                ManageListener.fireTableEvent(this, feedIta, ITASA);
            }
        }
        if (Lang.verifyTextNotNull(prop.getMyitasaFeedURL())) {
            if (first && prop.isAutoLoadDownloadMyItasa())
                lastMyItasa = prop.getLastDateTimeRefresh();
            if (first && !autoloaddownload)
                feedMyita = getFeedRss(prop.getMyitasaFeedURL(), lastMyItasa,
                    MYITASA, autoloaddownload, first);
            else
                feedMyita = getFeedRss(prop.getMyitasaFeedURL(), lastMyItasa,
                    MYITASA, prop.isAutoDownloadMyItasa(), first);
            if ((feedMyita != null) && (feedMyita.size() > 0)) {
                if (!first){
                    status = true;
                    countMyitasa = feedMyita.size();
                }
                lastMyItasa = (String) feedMyita.get(0)[1];
                ManageListener.fireTableEvent(this, feedMyita, MYITASA);
            }
        }
        return status;
    }
    
    private boolean runItasaBlog(boolean first) {
        boolean status = false;
        //TODO: sostituire url con prop.getblog 1volta creato
        String url = "http://feeds.feedburner.com/itasa-blog";
        ArrayList<Object[]> feedBlog;
        countBlog = 0;
        if (Lang.verifyTextNotNull(url)) {
            feedBlog = getFeedBlog(url, lastBlog);
            if ((feedBlog != null) && (feedBlog.size() > 0)) {
                if (!first){
                    status = true;
                    countBlog = feedBlog.size();
                }
                lastBlog = (String) feedBlog.get(0)[1];
                ManageListener.fireListEvent(this, BLOG, feedBlog);
            }
        }
        return status;
    }
    
    private boolean runItasaPM(boolean first){
        boolean status = false;
        if (!first && loginItasaXmlRPC()){
            try {
                int c = xmlrpc.getMessage();
                if (c>0)
                    ManageListener.fireFrameEvent(this, ITASA_PM, c);
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
        ArrayList<Object[]> subsf, mysubsf;
        if (Lang.verifyTextNotNull(prop.getSubsfactoryFeedURL())) {
            subsf = getFeedRss(prop.getSubsfactoryFeedURL(), lastSubsf,
                    SUBSF, false, first);
            if ((subsf != null) && (subsf.size() > 0)) {
                if (!first){
                    status = true;
                    countSubsf = subsf.size();
                }
                lastSubsf = (String) subsf.get(0)[1];
                ManageListener.fireTableEvent(this, subsf, SUBSF);
            }
        }
        if (Lang.verifyTextNotNull(prop.getMySubsfactoryFeedUrl())) {
            mysubsf = getFeedRss(prop.getMySubsfactoryFeedUrl(),
                    lastMySubsf, MYSUBSF, false, first);
            if ((mysubsf != null) && (mysubsf.size() > 0)) {
                if (!first) {
                    status = true;
                    countMysubsf = mysubsf.size();
                }
                lastMySubsf = (String) mysubsf.get(0)[1];
                ManageListener.fireTableEvent(this, mysubsf, MYSUBSF);
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
        ArrayList<Object[]> feedEz, feedBt;
        countBtchat = 0;
        countEztv = 0;
        feedEz = getFeedRss(RSS_TORRENT_EZTV, lastEztv, EZTV, false, first);
        feedBt = getFeedRss(RSS_TORRENT_BTCHAT, lastBtchat, BTCHAT, false,
                first);
        if ((feedEz != null) && (feedEz.size() > 0)) {
            if (!first){
                status = true;
                countEztv = feedEz.size();
            }
            lastEztv = (String) feedEz.get(0)[1];
            ManageListener.fireTableEvent(this, feedEz, EZTV);
        }
        if ((feedBt != null) && (feedBt.size() > 0)) {
            if (!first){
                status = true;
                countBtchat = feedBt.size();
            }
            lastBtchat = (String) feedBt.get(0)[1];
            ManageListener.fireTableEvent(this, feedBt, BTCHAT);
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
    //TODO finire calendario ics
    private void parseCalendarICS() throws FileNotFoundException, IOException, ParserException{
        int connection_Timeout = Lang.stringToInt(ApplicationSettings.getIstance().getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        CalendarICS cal = new CalendarICS(http.getStreamRss(ITASA_CALENDAR_ICS));
        cal.getData();
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
    public void loadXml() {
        try {
            xmlSubDest = new RuleDestination(FILE_RULE, true);
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
            xmlCalendar = new Calendar(FILE_CALENDAR, true);
            ArrayList temp = xmlCalendar.readingDocument();
            tsIdCalendar = (TreeSet) temp.get(0);
            if (tsIdCalendar.size() > 0) 
                ManageListener.fireTableEvent(this,
                        (ArrayList<Object[]>) temp.get(1), CALENDAR);
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        try {
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

    /** Verifica se il nome non presenta la parola "stagione"
     * 
     * @param name nome da controllare
     * @return risultato controllo
     */
    private boolean isNotStagione(Object obj) {
        boolean check = true;
        String[] array = ((String) obj).split(" ");
        for (int i = 0; i < array.length; i++) {
            String confronta = array[i].toLowerCase();
            if (confronta.equals("stagione") || confronta.equals("season")
                    || confronta.equals("completa")) {
                check = false;
                break;
            }
        }
        return check;
    }

    /** Effettua l'aggiornamento dei feed forzato */
    public void bruteRefreshRSS() {
        printOk("Timer in fase di reinizializzazione.");
        runItasa(false, true);
        if (prop.isSubsfactoryOption())
            runSubsfactory(false);
        if (prop.isTorrentOption())
            runTorrent(false);
        if (prop.isItasaBlog())
            runItasaBlog(false);
        stopAndRestartTimer();
        printOk("Timer restart ok.");
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

    /**
     * Effettua l'inserimento dei link al download redirectory del synology
     * 
     * @param link
     *            Arraylist di link
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
            ManageListener.fireTableEvent(this, al, CALENDAR);
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
                                                                            CALENDAR);
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
    
    private void fireCalendar(){
        try {
            xmlCalendar = new Calendar(FILE_CALENDAR, true);
            ArrayList temp = xmlCalendar.readingDocument();
            tsIdCalendar = (TreeSet) temp.get(0);
            if (tsIdCalendar.size() > 0)
                ManageListener.fireTableEvent(this,
                            (ArrayList<Object[]>) temp.get(1), CALENDAR);
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

    public void removeReminders(ArrayList<Integer> numbers) {
        try {
            for (int i=0; i<numbers.size(); i++)
                xmlReminder.removeItem(numbers.get(i).intValue());
            xmlReminder.write();
        } catch (IOException ex) {
            error.launch(ex, getClass());
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
            TreeSet ts = new TreeSet();
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
        
        private void importShow(String name, TvRage t, TreeSet ts, ArrayList<Object[]> al) 
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
                        Object[] array = setArray(t, t.showInfo_byID(id), true);
                        if (debug_flag)
                            System.out.println(id + " " + array[1]);
                        xmlClone.removeShowTv(index.intValue() - 1);
                        xmlClone.addShowTV(array);
                        setProgress(++progress);
                    } //end while
                    if (!this.isCancelled())
                        xmlCalendar.reverseDataCloning(xmlClone);
                } else { // REFRESH SINGLE
                    Object[] array = setArray(t, t.showInfo_byID(id), true);
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

    // TODO: non usata, se sarà implementata, cambiare la parte di stampa
    /**
     * effuetta la stampa dei file con l'estensione e la directory in cui
     * cercare
     * 
     * @param dir
     *            directory su cui effettuare la ricerca
     * @param ext
     *            estensione dei file da cercare
     */
/*
    private void listDir(String dir, String ext) {
        // Get list of names
        String[] list = new File(dir).list(new ExtensionFilter(ext));
        // Sort it (Data Structuring chapter))
        Arrays.sort(list);
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }
    }
 * 
 */