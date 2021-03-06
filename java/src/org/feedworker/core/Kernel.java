package org.feedworker.core;
//IMPORT JAVA
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.exception.ItasaException;
import org.feedworker.exception.ManageException;
import org.feedworker.object.ItasaUser;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Quality;
import org.feedworker.object.Subtitle;
import org.feedworker.object.ValueRule;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.ExtensionFilter;
import org.feedworker.util.Samba;
import org.feedworker.xml.Calendar;
import org.feedworker.xml.Itasa;
import org.feedworker.xml.Reminder;
import org.feedworker.xml.RuleDestination;
import org.feedworker.xml.TvRage;
import org.feedworker.xml.XPathCalendar;

import org.jfacility.Io;
import org.jfacility.Util;
import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.SystemFileManager;

import org.opensanskrit.exception.UnableRestartApplicationException;

import jcifs.smb.SmbException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import java.awt.Frame;
import org.opensanskrit.exception.NotAvailableLookAndFeelException;

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
    private final String SPLIT_HDTV = ".hdtv";
    private final String SPLIT_POINT = "\\.";
    private final String[] QUALITY = Quality.toArray();
    private final File FILE_RULE = new File("rules.xml");
    private final File FILE_CALENDAR = new File("calendar.xml");
    private final File FILE_REMINDER = new File("reminder.xml");
    // PRIVATE STATIC VARIABLES
    private static Kernel core = null;
    // PRIVATE VARIABLES
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private Timer timer;
    private String lastItasa = null, lastMyItasa = null, lastSubsf = null,
            lastEztv = null, lastBtchat = null, lastMySubsf = null;
    private TreeMap<KeyRule, ValueRule> mapRules;
    private TreeMap<String, String> mapShowItasa;
    private ManageException error = ManageException.getIstance();
    private Calendar xmlCalendar;
    private RuleDestination xmlSubDest;
    private Reminder xmlReminder;
    private ImportTask importTask;
    private RefreshTask refreshTask;
    private TreeSet tsIdCalendar;
    private Itasa itasa;
    private ItasaUser user;
    private static boolean debug_flag;

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
        if (id){
            String url = "http://www.italiansubs.net/index.php?option=com_remository&"
                    + "Itemid=6&func=fileinfo&id=";
            for (int i=0; i<als.size(); i++)
                als.set(i, url+als.get(i));
        }
        DownloadThread dt = new DownloadThread(mapRules, xmlReminder, als, itasa, false);
        Thread t = new Thread(dt, "Thread download");
        t.start();
    }

    /**effettua il download automatico di myitasa comprende le fasi anche di
     * estrazione zip e analizzazione percorso definitivo.
     * 
     * @param link link da analizzare
     */
    private void downItasaAuto(ArrayList<String> links, boolean first) {
        DownloadThread dt = null;
        if (first)
            dt = new DownloadThread(mapRules, xmlReminder, links, true, false);
        else
            dt = new DownloadThread(mapRules, xmlReminder, links, true, true);
        Thread t = new Thread(dt, "AutoItasa");
        t.start();
    }

    /**Scarica i torrent
     * 
     * @param als arraylist di link
     */
    public void downloadTorrent(ArrayList<String> als) {
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        Http http = new Http(connection_Timeout);
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

    /**
     * Restituisce il valore/percorso della chiave ad esso associato nella
     * treemap
     * 
     * @param name
     *            nome del file da analizzare
     * @param parsing
     *            valore sul quale effettuare lo split
     * @return path di destinazione
     */
    private String mapPath(KeyRule key) {
        if (key != null && mapRules != null)
            return mapRules.get(key).getPath();
        return null;
    }

    /**
     * Effettua l'analisi del nome del file restituendo l'oggetto filtro da
     * confrontare
     * 
     * @param name
     *            nome del file da analizzare
     * @param split
     *            stringa col quale effettuare lo split del nome del file
     * @return oggetto filtro
     */
    private KeyRule parsingNamefile(String namefile, String split) {
        String[] temp = (namefile.split(split))[0].split(SPLIT_POINT);
        int pos = temp.length - 1;
        String version = searchVersion(temp[pos]);
        String seriesNum;
        pos = Common.searchPosSeries(temp);
        if (pos > -1) {
            seriesNum = Common.searchNumberSeries(temp[pos]);
        } else {
            seriesNum = "1";
        }
        String name = temp[0];
        for (int i = 1; i < pos; i++) {
            name += " " + temp[i];
        }
        return new KeyRule(name, seriesNum, version);
    }

    /**cerca la versione/qualità del sub/video
     * 
     * @param text
     *            testo da confrontare
     * @return versione video/sub
     */
    private String searchVersion(String text) {
        String version = null;
        for (int i = 0; i < QUALITY.length; i++) {
            if (text.toLowerCase().equalsIgnoreCase(QUALITY[i])) {
                version = QUALITY[i];
                break;
            }
        }
        if (version == null) {
            version = Quality.NORMAL.toString();
        }
        return version;
    }

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
    private void listDir(String dir, String ext) {
        // Get list of names
        String[] list = new File(dir).list(new ExtensionFilter(ext));
        // Sort it (Data Structuring chapter))
        Arrays.sort(list);
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }
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
            s.testConn();
            test = s.testConn();
        } catch (SmbException ex) {
            error.launch(ex, getClass(), null);
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
            String from, boolean download, boolean first) {
        RssParser rss = null;
        ArrayList<Object[]> matrice = null;
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        Http http = new Http(connection_Timeout);
        try {
            InputStream ist = http.getStreamRss(urlRss);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Io.downloadSingle(ist, ft);
                rss = new RssParser(ft);
                matrice = rss.read();
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
                            if ((isNotStagione(matrice.get(i)[2])) && download)
                                links.add((String)matrice.get(i)[0]);
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

    /** Esegue gli rss */
    public void runRss(boolean autoloaddownload) {
        if (!prop.isApplicationFirstTimeUsed()) {
            String temp = Common.actualTime();
            runItasa(true);
            runSubsfactory(true);
            if (prop.isTorrentOption())
                runTorrent(true);
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
                    boolean icontray = false;
                    prop.setLastDateTimeRefresh(Common.actualTime());
                    if (runItasa(false))
                        icontray = true;
                    if (runSubsfactory(false))
                        icontray = true;
                    if (prop.isTorrentOption() && runTorrent(false))
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
                    ManageListener.fireFrameEvent(this, icontray);
                }// end run
            }, delay, delay);
        } catch (IllegalStateException ex) {
            error.launch(ex, getClass());
        }
    }

    /**
     * Esegue la parte rss itasa
     * 
     * @param first
     *            primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runItasa(boolean first) {
        boolean status = false;
        ArrayList<Object[]> feedIta, feedMyita;
        if (Lang.verifyTextNotNull(prop.getItasaFeedURL())) {
            feedIta = getFeedRss(prop.getItasaFeedURL(), lastItasa, ITASA,
                    false, first);
            if ((feedIta != null) && (feedIta.size() > 0)) {
                if (!first)
                    status = true;
                lastItasa = (String) feedIta.get(0)[1];
                ManageListener.fireTableEvent(this, feedIta, ITASA);
            }
        }
        if (Lang.verifyTextNotNull(prop.getMyitasaFeedURL())) {
            if (first && prop.isAutoLoadDownloadMyItasa())
                lastMyItasa = prop.getLastDateTimeRefresh();
            feedMyita = getFeedRss(prop.getMyitasaFeedURL(), lastMyItasa,
                    MYITASA, prop.isAutoDownloadMyItasa(), first);
            if ((feedMyita != null) && (feedMyita.size() > 0)) {
                if (!first)
                    status = true;
                lastMyItasa = (String) feedMyita.get(0)[1];
                ManageListener.fireTableEvent(this, feedMyita, MYITASA);
            }
        }
        return status;
    }

    /**
     * Esegue la parte rss subsfactory
     * 
     * @param first
     *            primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runSubsfactory(boolean first) {
        boolean status = false;
        if (prop.isSubsfactoryOption()) {
            ArrayList<Object[]> subsf, mysubsf;
            if (Lang.verifyTextNotNull(prop.getSubsfactoryFeedURL())) {
                subsf = getFeedRss(prop.getSubsfactoryFeedURL(), lastSubsf,
                        SUBSF, false, first);
                if ((subsf != null) && (subsf.size() > 0)) {
                    if (!first) {
                        status = true;
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
                    }
                    lastMySubsf = (String) mysubsf.get(0)[1];
                    ManageListener.fireTableEvent(this, mysubsf, MYSUBSF);
                }
            }
        }
        return status;
    }

    /**
     * Esegue la parte rss torrent
     * 
     * @param first
     *            primo lancio
     * @return true se ci sono nuovi feed, false altrimenti
     */
    private boolean runTorrent(boolean first) {
        boolean status = false;
        if (prop.isTorrentOption()) {
            ArrayList<Object[]> feedEz, feedBt;
            feedEz = getFeedRss(RSS_TORRENT_EZTV, lastEztv, EZTV, false, first);
            feedBt = getFeedRss(RSS_TORRENT_BTCHAT, lastBtchat, BTCHAT, false,
                    first);
            if ((feedEz != null) && (feedEz.size() > 0)) {
                if (!first)
                    status = true;
                lastEztv = (String) feedEz.get(0)[1];
                ManageListener.fireTableEvent(this, feedEz, EZTV);
            }
            if ((feedBt != null) && (feedBt.size() > 0)) {
                if (!first)
                    status = true;
                lastBtchat = (String) feedBt.get(0)[1];
                ManageListener.fireTableEvent(this, feedBt, BTCHAT);
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
        runItasa(false);
        runSubsfactory(false);
        runTorrent(false);
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
     * Stampa lo stato del download redirectory coi download in corso o nessun
     * download
     */
    public void synoStatus() {
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        String filename = "         \"filename\" : \"";
        String progress = "         \"progress\" : \"";
        String itemsNull = "   \"items\" : [],";
        String dss = "Download Station Synology: ";
        try {
            Http http = new Http();
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                http = new Http();
                InputStream is = http.synoStatus(url, synoID);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String line, _filename = null, _progress = null;
                while ((line = br.readLine()) != null) {
                    if (line.equals(itemsNull)) {
                        printSynology(dss + "Non ci sono download in corso");
                        break;
                    } else if (line.length() > filename.length()) {
                        String _substring = line.substring(0, filename.length());
                        if (_substring.equals(filename)) {
                            _filename = line.substring(filename.length(),
                                    line.length() - 2);
                        } else if (_substring.equals(progress)) {
                            _progress = line.substring(progress.length(),
                                    line.length() - 2);
                        }
                    }
                    if (Lang.verifyTextNotNull(_progress)) {
                        printSynology(dss + _filename + " " + _progress);
                        _progress = null;
                    }
                }
            }
        } catch (IllegalStateException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** effettua la move video sul synology */
    public void synoMoveVideo() {
        Samba s = Samba.getIstance(prop.getCifsShareLocation(),
                prop.getCifsSharePath(), prop.getCifsShareDomain(),
                prop.getCifsShareUsername(), prop.getCifsSharePassword());
        try {
            analyzeVideoSamba(s, s.listDir(null, "avi"));
            analyzeVideoSamba(s, s.listDir(null, "mkv"));
        } catch (MalformedURLException ex) {
            error.launch(ex, getClass(), null);
        } catch (SmbException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /**
     * Analizza i nomi dei file e se per ciascuno trova una corrispondenza tra
     * le regole, sposta il file nel path opportuno
     * 
     * @param s
     *            istanza samba
     * @param fileList
     *            array di nomi di file
     */
    private void analyzeVideoSamba(Samba s, String[] fileList) {
        for (int i = 0; i < fileList.length; i++) {
            String name = fileList[i];
            if (name.toLowerCase().contains(SPLIT_HDTV)) {
                KeyRule key = parsingNamefile(name.toLowerCase(), SPLIT_HDTV);
                String dest = mapPath(key);
                if (dest != null) {
                    try {
                        String[] _array = name.toLowerCase().split(SPLIT_HDTV)[0].split("\\.");
                        int pos = Common.searchPosSeries(_array);
                        int conta = 0;
                        for (int j = 0; j < pos; j++) {
                            conta += _array[j].length() + 1;
                        }
                        String newName = name.substring(conta + 4);
                        newName.replaceAll("\\.", " ");
                        s.moveFile(name, dest, newName);
                        printSynology("Spostato " + name + " in " + dest);
                    } catch (SmbException ex) {
                        error.launch(ex, getClass(), name);
                    } catch (IOException ex) {
                        error.launch(ex, getClass(), null);
                    }
                }
            }
        }
    }

    /**
     * Effettua l'inserimento dei link al download redirectory del synology
     * 
     * @param link
     *            Arraylist di link
     */
    public void synoDownloadRedirectory(ArrayList<String> link) {
        Http http = new Http();
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        try {
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                for (int i = 0; i < link.size(); i++) {
                    http = new Http();
                    http.synoAddLink(url, synoID, link.get(i));
                    http.closeClient();
                }
                printSynology("link inviati al download redirectory Synology");
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** Pulisce i task completati del synology */
    public void synoClearFinish() {
        Http http = new Http();
        String url = "http://" + prop.getCifsShareLocation()
                + ":5000/download/download_redirector.cgi";
        try {
            String synoID = http.synoConnectGetID(url,
                    prop.getCifsShareUsername(), prop.getCifsSharePassword());
            http.closeClient();
            if (Lang.verifyTextNotNull(synoID)) {
                http = new Http();
                http.synoClearTask(url, synoID, prop.getCifsShareUsername());
                http.closeClient();
                printSynology("Download Station Synology: cancellati task completati.");
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

    private Object[] setArray(TvRage t, Object[] show, boolean status)
                                            throws JDOMException, IOException {
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
        importTask = new ImportTask(mapRules.keySet().iterator());
        importTask.addPropertyChangeListener(this);
        importTask.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getSource().getClass().getName();
        String className = this.getClass().getName();
        if (evtName.equalsIgnoreCase(className + "$ImportTask")) {
            if (evt.getPropertyName().equals("progress")) {
                ManageListener.fireFrameEvent(this, OPERATION_PROGRESS_INCREMENT, 
                        importTask.getProgress());
                if (importTask.isDone() && !importTask.isCancelled()) {
                    try {
                        ManageListener.fireTableEvent(this, importTask.get(), CALENDAR);
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
        } else
            printAlert("apertura di cartella samba non implementata");
    }
    
    public void stopImportRefreshCalendar() {
        if (importTask!=null && importTask.getState()==SwingWorker.StateValue.STARTED)
            importTask.cancel(true);
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

    public void checkLoginItasa(String username, String pwd) {
        if (user==null){
            try {
                Itasa i = new Itasa();
                user = i.login(username, pwd);
                printOk("CheckLogin Itasa: ok");
            } catch (JDOMException ex) {
                error.launch(ex, this.getClass());
            } catch (IOException ex) {
                error.launch(ex, this.getClass());
            } catch (ItasaException ex) {
                printAlert("Login itasa: " + ex.getMessage());
            } catch (Exception ex) {
                error.launch(ex, this.getClass());
            }
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

    public void importTvFromMyItasa() {
        checkLoginItasa(prop.getItasaUsername(), prop.getItasaPassword());
        if (user!=null){
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
                    importTask = new ImportTask(myShows);
                    importTask.addPropertyChangeListener(this);
                    importTask.execute();
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
            itasa = new Itasa();
            try {
                mapShowItasa = itasa.showList();
                //TODO: se itasa = errore, caricare da file
            } catch (JDOMException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ItasaException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
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
            FeedWorkerClient.getApplication().getIstanceLAF().setLookAndFeelRuntime(laf, frame);
            prop.setApplicationLookAndFeel(laf);
        } catch (NotAvailableLookAndFeelException ex) {
            error.launch(ex, this.getClass());
        } catch (GeneralSecurityException ex) {
            error.launch(ex, this.getClass());
        } catch (IOException ex) {
            error.launch(ex, this.getClass());
        }
    }

    class ImportTask extends SwingWorker<ArrayList<Object[]>, Void> {
        private boolean myitasa;
        private ArrayList<String> myShows; 
        private Iterator<KeyRule> iterKey;
        
        public ImportTask(ArrayList<String> shows){
            myShows = shows;
            myitasa = true;
        }
        
        public ImportTask(Iterator<KeyRule> key){
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
            } catch (JDOMException ex) {
                error.launch(ex, null);
            } catch (IOException ex) {
                error.launch(ex, null);
            }
            return null;
        }
    }
}