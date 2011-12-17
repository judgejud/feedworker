package org.feedworker.core.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.http.HttpItasa;
import org.feedworker.core.http.HttpOther;
import org.feedworker.core.Kernel;
import org.feedworker.core.ManageListener;
import org.feedworker.core.RssParser;
import org.feedworker.exception.ManageException;
import org.feedworker.object.KeyRule;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;
import org.feedworker.xml.Reminder;

import org.jfacility.Io;
import org.jfacility.java.lang.Lang;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
/**
 *
 * @author luca
 */
public class RssThread implements Runnable{
    
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private Kernel core = Kernel.getIstance();
    private boolean loginItasa, download, first;
    private String url, data, from, lastDate;
    private TreeMap<KeyRule, ValueRule> map;
    private Reminder xml;
    private HttpItasa http;
    private int count;

    public RssThread(String data, String url, String from, boolean login, 
            boolean download, boolean first, TreeMap<KeyRule, ValueRule> map, 
            HttpItasa http, Reminder xml) {
        loginItasa = login;
        this.data = data;
        this.url = url;
        this.from = from;
        this.download = download;
        this.first = first;
        this.map = map;
        this.xml = xml;
        this.http = http;
    }
    
    public RssThread(String data, String url, String from) {
        this.data = data;
        this.url = url;
        this.from = from;
        first = false;
        download = false;
    }

    @Override
    public void run() {
        try {            
            int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
            HttpOther http = new HttpOther(connection_Timeout);
            InputStream ist = http.getStream(url);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Io.downloadSingle(ist, ft);
                RssParser rss = new RssParser(ft);
                ArrayList<Object[]> matrice = rss.readRss();
                ft.delete();
                boolean continua = true;
                if (data != null) {
                    Date confronta = Common.stringDateTime(data);
                    ArrayList<String> links = new ArrayList<String>();
                    for (int i = matrice.size() - 1; i >= 0; i--) {
                        String date_matrix = String.valueOf(matrice.get(i)[1]);
                        if (confronta.before(Common.stringDateTime(date_matrix))) {
                            if (continua) {
                                if (from.equals(core.ITASA)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_ITASA, true);
                                } else if (from.equals(core.MYITASA)
                                        && !prop.isAutoDownloadMyItasa()) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYITASA, true);
                                } else if (from.equals(core.SUBSF)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_SUBSF,true);
                                } else if (from.equals(core.MYSUBSF)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYSUBSF, true);
                                } else if (from.equals(core.EZTV)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_EZTV, true);
                                } else if (from.equals(core.BTCHAT)) {
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_BTCHAT, true);
                                }
                                continua = false;
                            }
                            if (download){
                                if ((isNotStagione((String) matrice.get(i)[2])))
                                    links.add((String)matrice.get(i)[0]);
                                else 
                                    ManageListener.fireTextPaneEvent(this,
                                            "Nuovo/i feed " + from,
                                            TextPaneEvent.FEED_MYITASA, true);
                            }
                        } else if (first && from.equals(core.MYITASA)) {
                            // non deve fare nulla
                        } else // if confronta after
                            matrice.remove(i);
                    }//end for
                    if (links.size()>0)
                        downItasaAuto(links, first);
                }
                count = matrice.size();
                if (matrice!=null && count>0){
                    ManageListener.fireTableEvent(this, matrice, from);
                    lastDate = (String) matrice.get(0)[1];
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
    }
    
    /**Verifica se il nome non presenta la parola "stagione"
     * 
     * @param name nome da controllare
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
    
    private void downItasaAuto(ArrayList<String> links, boolean first) {
        prop.setLastDateTimeRefresh(Common.actualTime());
        prop.writeOnlyLastDate();
        if (loginItasa){
            DownloadThread dt = null;
            if (first)
                dt = new DownloadThread(map, xml, links, http, false);
            else
                dt = new DownloadThread(map, xml, links, http, true);
            Thread t = new Thread(dt, "AutoItasa");
            t.start();
        } else {
            String msg = "Non posso procedere al download per problemi di login ad itasa, "
                    + "controllare user e password";
            ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, true);
        }
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized String getLastDate() {
        return lastDate;
    }
}