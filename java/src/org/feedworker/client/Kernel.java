package org.feedworker.client;

//IMPORT JAVA
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import jcifs.smb.SmbException;

//import org.eclipse.swt.widgets.Display;
import org.feedworker.client.frontend.events.*;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.ExtensionFilter;
import org.feedworker.util.KeyRule;
import org.feedworker.util.ManageException;
import org.feedworker.util.Quality;
import org.feedworker.util.Samba;
import org.feedworker.util.ValueRule;

import org.jfacility.java.lang.Lang;
import org.jfacility.Util;

import org.jdom.JDOMException;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;

import org.xml.sax.SAXException;

/**Motore di Feedworker
 * 
 * @author luca
 */
public class Kernel {
    // PUBLIC FINAL STATIC VARIABLES
    public final static String[] daysOfWeek = new String[]{"", "Domenica", "Lunedì",
        "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"};
    // PUBLIC FINAL VARIABLES
    public final String ITASA = "Itasa";
    public final String SUBSF = "Subsf";
    public final String EZTV = "Eztv";
    public final String BTCHAT = "Btchat";
    public final String MYITASA = "MyItasa";
    public final String MYSUBSF = "MySubsf";
    public final String SEARCH_TV = "SearchTV";
    public final String SUBTITLE_DEST = "SubtitleDest";

    // PRIVATE FINAL VARIABLES
    private final String RSS_TORRENT_EZTV = "http://ezrss.it/feed/";
    private final String RSS_TORRENT_BTCHAT = "http://rss.bt-chat.com/?cat=9";
    private final String SPLIT_HDTV = ".hdtv";
    private final String SPLIT_POINT = "\\.";
    private final String[] QUALITY = new String[]{Quality.ALL.toString(),
        Quality.NORMAL.toString(), Quality.FORM_720p.toString(),
        Quality.FORM_1080p.toString(), Quality.FORM_1080i.toString(), Quality.BLURAY.toString(),
        Quality.DVDRIP.toString(), Quality.HR.toString(),
        Quality.DIFF.toString()};
    // PRIVATE STATIC VARIABLES
    private static Kernel core = null;
    // PRIVATE VARIABLES
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private List listenerTable = new ArrayList(), listenerTextPane = new ArrayList(),
            listenerJFrameID = new ArrayList(), listenerJFrameO = new ArrayList();
    private Timer timer;
    private String lastItasa = null, lastMyItasa = null, lastSubsf = null,
            lastEztv = null, lastBtchat = null, lastMySubsf = null;
    private TreeMap<KeyRule, ValueRule> mapRules;
    private ManageException error = ManageException.getIstance();
    private TextPaneEventListener mytpel;

    /**
     * Restituisce l'istanza corrente del kernel
     *
     * @return istanza kernel
     */
    public static Kernel getIstance() {
        if (core == null) {
            core = new Kernel();
        }
        return core;
    }
    /**Scarica lo zip, estrae i sub e invoca l'analizzatore del path di  destinazione
     *
     * @param als arraylist di link
     * @param itasa
     */
    public void downloadSub(ArrayList<String> als, boolean itasa) {
        DownloadThread dt = new DownloadThread(mapRules, als, itasa);
        Thread t = new Thread(dt, "Thread download");
        dt.addMyTextPaneEventListener(mytpel);
        t.start();
    }

    /**effettua il download automatico di myitasa comprende le fasi anche di
     * estrazione zip e analizzazione percorso definitivo.
     *
     * @param link
     *            link da analizzare
     */
    private void downItasaAuto(Object link) {
        ArrayList<String> als = new ArrayList<String>();
        als.add(link.toString());
        DownloadThread dt = new DownloadThread(mapRules, als, true);
        Thread t = new Thread(dt, "Thread download");
        dt.addMyTextPaneEventListener(mytpel);
        t.start();
    }

    /**
     * Scarica i torrent
     *
     * @param als
     *            arraylist di link
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
                    Common.downloadSingle(is, f);
                    fireNewTextPaneEvent("Scaricato: " + http.getNameFile(),
                            TextPaneEvent.TORRENT);
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
        if (key != null && mapRules != null) {
            return mapRules.get(key).getPath();
        }
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

    /**
     * cerca la versione/qualità del sub/video
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
    public boolean testSamba(String ip, String dir, String dom, String user, String pwd) {
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

    /**chiude l'applicazione salvando la data nel settings
     *
     * @param data
     */
    public void closeApp(String data) {
        if (!Lang.verifyTextNotNull(data))
            data = Common.actualTime();
        prop.setLastDateTimeRefresh(data);
        if (!prop.isApplicationFirstTimeUsed())
            prop.writeOnlyLastDate();
        System.exit(0);
    }

    /** Scrive le proprietà dell'applicazione nel file properties */
    public void writeProp() {
        prop.writeGeneralSettings();
        if (prop.isItasaOption())
            prop.writeItasaSettings();
        if (prop.isSubsfactoryOption())
            prop.writeSubsfactorySettings();
        if (prop.isTorrentOption())
            prop.writeTorrentSettings();
        if (prop.isApplicationFirstTimeUsed())
            prop.writeApplicationFirstTimeUsedFalse();
    }

    /**Restituisce l'arraylist contenente i feed rss
     *
     * @param urlRss url rss da analizzare
     * @param data data da confrontare
     * @param from provenienza
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
                Common.downloadSingle(ist, ft);
                rss = new RssParser(ft);
                matrice = rss.read();
                ft.delete();
                boolean continua = true;
                if (data != null) {
                    Date confronta = Common.stringToDate(data);
                    for (int i = matrice.size() - 1; i >= 0; i--) {
                        String date_matrix = String.valueOf(matrice.get(i)[1]);
                        if (confronta.before(Common.stringToDate(date_matrix))) {
                            if (continua) {
                                if (from.equals(ITASA))
                                    fireNewTextPaneEvent("Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_ITASA);
                                else if (from.equals(MYITASA)
                                        && !prop.isAutoDownloadMyItasa()) 
                                    fireNewTextPaneEvent("Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYITASA);
                                else if (from.equals(SUBSF))
                                    fireNewTextPaneEvent("Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_SUBSF);
                                else if (from.equals(MYSUBSF))
                                    fireNewTextPaneEvent("Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYSUBSF);
                                else if (from.equals(EZTV))
                                    fireNewTextPaneEvent("Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_EZTV);
                                else if (from.equals(BTCHAT))
                                    fireNewTextPaneEvent("Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_BTCHAT);
                                continua = false;
                            }
                            if ((isNotStagione((String) matrice.get(i)[2])) && download)
                                downItasaAuto(matrice.get(i)[0]);
                        } else if (first && from.equals(MYITASA)){ //non deve fare nulla
                        } else //if confronta after
                            matrice.remove(i);
                    }
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
    public void runRss() {
        if (!prop.isApplicationFirstTimeUsed()) {
            //searchDay();
            String temp = Common.actualTime();
            runItasa(true);
            runSubsfactory(true);
            runTorrent(true);
            prop.setLastDateTimeRefresh(temp);
            int delay = Lang.stringToInt(prop.getRefreshInterval()) * 60000;
            runTimer(delay);
        }
    }

    /**
     * esegue gli rss sotto timer
     *
     * @param delay
     *            tempo in secondi per il timer
     */
    private void runTimer(int delay) {
        timer = new Timer();
        try {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    boolean icontray = false;
                    prop.setLastDateTimeRefresh(Common.actualTime());
                    if (runItasa(false))
                        icontray = true;
                    if (runSubsfactory(false))
                        icontray = true;
                    if (runTorrent(false))
                        icontray = true;
                    if ((icontray) && (prop.isEnabledAudioAdvisor())) {
                        try {
                            AudioPlay.playWav();
                        } catch (UnsupportedAudioFileException ex) {
                            error.launch(ex, getClass());
                        } catch (LineUnavailableException ex) {
                            error.launch(ex, getClass());
                        } catch (IOException ex) {
                            error.launch(ex, getClass(), null);
                        }
                    }
                    fireNewJFrameEventIconData(icontray, prop.getLastDateTimeRefresh());
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
        if (prop.isItasaOption()) {
            ArrayList<Object[]> feedIta, feedMyita;
            if (Lang.verifyTextNotNull(prop.getItasaFeedURL())) {
                feedIta = getFeedRss(prop.getItasaFeedURL(), lastItasa, ITASA,
                        false, first);
                if ((feedIta != null) && (feedIta.size() > 0)) {
                    if (!first) {
                        status = true;
                    }
                    lastItasa = (String) feedIta.get(0)[1];
                    fireTableEvent(feedIta, ITASA);
                }
            }
            if (Lang.verifyTextNotNull(prop.getMyitasaFeedURL())) {
                if (first && prop.isAutoLoadDownloadMyItasa()){
                    lastMyItasa = prop.getLastDateTimeRefresh();
                }
                feedMyita = getFeedRss(prop.getMyitasaFeedURL(), lastMyItasa,
                        MYITASA, prop.isAutoDownloadMyItasa(), first);
                if ((feedMyita != null) && (feedMyita.size() > 0)) {
                    if (!first)
                        status = true;
                    lastMyItasa = (String) feedMyita.get(0)[1];
                    fireTableEvent(feedMyita, MYITASA);
                }
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
        if (prop.isSubsfactoryOption()) {
            ArrayList<Object[]> subsf, mysubsf;
            if (Lang.verifyTextNotNull(prop.getSubsfactoryFeedURL())) {
                subsf = getFeedRss(prop.getSubsfactoryFeedURL(), lastSubsf, SUBSF, false, first);
                if ((subsf != null) && (subsf.size() > 0)) {
                    if (!first)
                        status = true;
                    lastSubsf = (String) subsf.get(0)[1];
                    fireTableEvent(subsf, SUBSF);
                }
            }
            if (Lang.verifyTextNotNull(prop.getMySubsfactoryFeedUrl())) {
                mysubsf = getFeedRss(prop.getMySubsfactoryFeedUrl(), lastMySubsf,
                        MYSUBSF, false, first);
                if ((mysubsf != null) && (mysubsf.size() > 0)) {
                    if (!first)
                        status = true;
                    lastMySubsf = (String) mysubsf.get(0)[1];
                    fireTableEvent(mysubsf, MYSUBSF);
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
            feedBt = getFeedRss(RSS_TORRENT_BTCHAT, lastBtchat, BTCHAT, false, first);
            if ((feedEz != null) && (feedEz.size() > 0)) {
                if (!first) {
                    status = true;
                }
                lastEztv = (String) feedEz.get(0)[1];
                fireTableEvent(feedEz, EZTV);
            }
            if ((feedBt != null) && (feedBt.size() > 0)) {
                if (!first) {
                    status = true;
                }
                lastBtchat = (String) feedBt.get(0)[1];
                fireTableEvent(feedBt, BTCHAT);
            }
        }
        return status;
    }

    /**
     * Interrompe il timer attuale e ne fa partire uno nuovo col nuovo
     * intervallo
     */
    public void stopAndRestartTimer() {
        timer.cancel();
        timer.purge();
        int delay = Lang.stringToInt(prop.getRefreshInterval()) * 60000;
        runTimer(delay);
    }

    /**
     * Sostituisce la treemap delle regole con quella creata dal mediator
     *
     * @param temp
     *            treepam regole
     */
    public void saveMap(TreeMap<KeyRule, ValueRule> temp) {
        try {
            new Xml().writeMap(temp);
            mapRules = temp;
            fireNewTextPaneEvent("Regola/e memorizzate", TextPaneEvent.OK);
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /**
     * converte la treemap delle regole in arraylist di String[]
     *
     * @return arraylist regole
     */
    private ArrayList<Object[]> convertTreemapToArraylist() {
        ArrayList<Object[]> matrix = null;
        if (mapRules.size() > 0) {
            Iterator it = mapRules.keySet().iterator();
            matrix = new ArrayList<Object[]>();
            while (it.hasNext()) {
                KeyRule key = (KeyRule) it.next();
                ValueRule value = mapRules.get(key);
                matrix.add(new Object[]{key.getName(), key.getSeason(),
                            key.getQuality(), value.getPath(), value.getStatus(),
                            value.getDay(), Boolean.valueOf(value.isRename())});
            }
        }
        return matrix;
    }

    /** Carica l'xml delle regole */
    public void loadXml() {
        if (prop.isEnabledCustomDestinationFolder()) {
            Xml x = new Xml();
            try {
                mapRules = x.initializeReader();
                if (mapRules != null)
                    fireTableEvent(convertTreemapToArraylist(),SUBTITLE_DEST);
            } catch (JDOMException ex) {
                error.launch(ex, getClass());
            } catch (IOException ex) {
                error.launch(ex, getClass(), null);
            }
        }
    }

    /**
     * Verifica se il nome non presenta la parola "stagione"
     *
     * @param name
     *            nome da controllare
     * @return risultato controllo
     */
    private boolean isNotStagione(String name) {
        boolean check = true;
        String[] array = name.split(" ");
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

    public void bruteRefreshRSS() {
        fireNewTextPaneEvent("Timer in fase di reinizializzazione.",
                TextPaneEvent.OK);
        runItasa(false);
        runSubsfactory(false);
        runTorrent(false);
        stopAndRestartTimer();
        fireNewTextPaneEvent("Timer restart ok.", TextPaneEvent.OK);
    }

    /**
     * restituice l'array con le informazioni sulle versioni video
     *
     * @return array versioni video
     */
    public String[] getQuality() {
        return QUALITY;
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
                        fireNewTextPaneEvent(dss
                                + "Non ci sono download in corso",
                                TextPaneEvent.SYNOLOGY);
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
                        fireNewTextPaneEvent(dss + _filename + " " + _progress,
                                TextPaneEvent.SYNOLOGY);
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
                        fireNewTextPaneEvent(
                                "Spostato " + name + " in " + dest,
                                TextPaneEvent.SYNOLOGY);
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
                fireNewTextPaneEvent(
                        "link inviati al download redirectory Synology",
                        TextPaneEvent.SYNOLOGY);
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    /** Pulisce i task completati */
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
                fireNewTextPaneEvent(
                        "Download Station Synology: cancellati task completati.",
                        TextPaneEvent.SYNOLOGY);
            }
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
    }

    public void setDownloadThreadListener(TextPaneEventListener listener) {
        mytpel = listener;
    }

    public void backup(String name) {
        ArrayList<File> files = new ArrayList<File>();
        File r = new File("rules.xml");
        File s = new File("settings.properties");
        if (r.exists())
            files.add(r);
        if (s.exists())
            files.add(s);
        if (files.size()>0){
            if (!name.substring(name.length()-4).toLowerCase().equalsIgnoreCase(".zip"))
                name += ".zip";
            File f = new File(name);
            try {
                Util.createZip(files, f);
                fireNewTextPaneEvent("backup effettuato: " + f.getName(),TextPaneEvent.OK);
            } catch (IOException ex) {
                error.launch(ex, getClass());
            }
        } else
            printAlert("Non posso fare il backup poichè non ci sono files da backuppare.");
    }

    public void searchTV(String tv) {
        TvRage t = new TvRage();
        try {
            ArrayList<Object[]> array = t.readingDetailedSearch_byShow(tv);
            if (array!=null){
                fireTableEvent(array,SEARCH_TV);
                fireNewJFrameEventOperation(SEARCH_TV);
            }
        } catch (JDOMException ex) {
            error.launch(ex, null);
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    //TODO
    public void searchDay(int temp){
        if (prop.isEnabledCustomDestinationFolder()){
            int day = Common.getDay() + temp;
            if (day==0)
                day = 7;
            else if (day==8)
                day = 1;
            try {
                String result = XPathReader.queryDay(daysOfWeek[day]);
                String msg;
                if (result.equalsIgnoreCase("")){
                    msg = "Non ci sono serial tv previsti per ";
                    if (temp==0)
                        msg += "oggi";
                    else if(temp == 1)
                        msg += "domani";
                    else if(temp == -1)
                        msg += "ieri";
                } else {
                    msg = "Serial tv previsti per ";
                    if (temp==0)
                        msg += "oggi: ";
                    else if(temp == 1)
                        msg += "domani: ";
                    else if(temp == -1)
                        msg += "ieri: ";
                    msg +=  result;
                }
                fireNewTextPaneEvent(msg, TextPaneEvent.DAY_SERIAL);
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
    }

    /**
     * Stampa il messaggio di alert invocando il metodo fire opportuno
     *
     * @param msg
     *            testo da stampare
     */
    private void printAlert(String msg) {
        fireNewTextPaneEvent(msg, TextPaneEvent.ALERT);
    }

    /**
     * Permette alla classe di registrarsi per l'evento tablerss
     *
     * @param listener
     *            evento tablerss
     */
    public synchronized void addTableEventListener(TableEventListener listener) {
        listenerTable.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento tablerss
     *
     * @param listener
     *            evento tablerss
     */
    public synchronized void removeTableEventListener(TableEventListener listener) {
        listenerTable.remove(listener);
    }

    private synchronized void fireTableEvent(ArrayList<Object[]> alObj, String source) {
        TableEvent event = new TableEvent(this, alObj, source);
        Iterator listeners = listenerTable.iterator();
        while (listeners.hasNext()) {
            TableEventListener myel = (TableEventListener) listeners.next();
            if (myel != null)
                myel.objReceived(event);
            else
                System.out.println("myel table null" + source);
        }
    }

    /**Permette alla classe di registrarsi per l'evento textpane
     *
     * @param listener
     *            evento textpane
     */
    public synchronized void addMyTextPaneEventListener(
            TextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }

    /**Permette alla classe di de-registrarsi per l'evento textpane
     *
     * @param listener
     *            evento textpane
     */
    public synchronized void removeMyTextPaneEventListener(
            TextPaneEventListener listener) {
        listenerTextPane.remove(listener);
    }

    private synchronized void fireNewTextPaneEvent(String msg, String type) {
        TextPaneEvent event = new TextPaneEvent(this, msg, type);
        Iterator listeners = listenerTextPane.iterator();
        while (listeners.hasNext()) {
            TextPaneEventListener myel = (TextPaneEventListener) listeners.next();
            myel.objReceived(event);
        }
    }

    /**Permette alla classe di registrarsi per l'evento jframe
     *
     * @param listener evento jframe
     */
    public synchronized void addJFrameEventIconDateListener(JFrameEventIconDateListener listener) {
        listenerJFrameID.add(listener);
    }

    /**Permette alla classe di de-registrarsi per l'evento jframe
     *
     * @param listener evento jframe
     */
    public synchronized void removeJFrameEventIconDateListener(JFrameEventIconDateListener listener) {
        listenerJFrameID.remove(listener);
    }

    /**Permette alla classe di registrarsi per l'evento jframe
     *
     * @param listener evento jframe
     */
    public synchronized void addJFrameEventOperationListener(JFrameEventIconDateListener listener){
        listenerJFrameO.add(listener);
    }

    /**Permette alla classe di de-registrarsi per l'evento jframe
     *
     * @param listener evento jframe
     */
    public synchronized void removeJFrameEventOperationListener(JFrameEventIconDateListener listener){
        listenerJFrameO.remove(listener);
    }

    private synchronized void fireNewJFrameEventOperation(String oper) {
        JFrameEventOperation event = new JFrameEventOperation(this, oper);
        Iterator listeners = listenerJFrameO.iterator();
        while (listeners.hasNext()) {
            JFrameEventOperationListener myel = (JFrameEventOperationListener) listeners.next();
            myel.objReceived(event);
        }
    }

    private synchronized void fireNewJFrameEventIconData(boolean _icontray, final String _data) {
        JFrameEventIconDate event = new JFrameEventIconDate(this, _icontray, _data);
        Iterator listeners = listenerJFrameID.iterator();
        while (listeners.hasNext()) {
            JFrameEventIconDateListener myel = (JFrameEventIconDateListener) listeners.next();
            myel.objReceived(event);
        }
        /* Nota: E' stato necessario utilizzare il metodo syncExec perchè
         * l'interazione con il thread UI di SWT che gestisce la SysTray può
         * avvenire solo in questo modo. In caso contrario l'azione su oggetti
         * SWT che vivono in uno specifico thread (UI thread) da un altro thread
         * come in questo caso causa una SWTException: Invalid Thread Access.
         */

        /*
        final boolean icontray = _icontray;
        final String data = _data;
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MyJFrameEvent event = new MyJFrameEvent(this, icontray, data);
                Iterator listeners = listenerJFrame.iterator();
                while (listeners.hasNext()) {
                    MyJFrameEventListener myel = (MyJFrameEventListener) listeners.next();
                    myel.objReceived(event);
                }
            }
        });
        */
    }
}