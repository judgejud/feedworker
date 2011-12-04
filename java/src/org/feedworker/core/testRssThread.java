package org.feedworker.core;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.object.KeyRule;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;
import org.feedworker.exception.ManageException;
import org.jfacility.Io;
import org.jfacility.java.lang.Lang;

/**TODO: terminare la trasformazione del feed rss sotto 3d
 *
 * @author luca
 */
public class testRssThread implements Runnable{
    
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private Kernel core = Kernel.getIstance();
    private TreeMap<KeyRule, ValueRule> map;
    private String url, data, from;
    private boolean download, first;

    testRssThread(TreeMap<KeyRule, ValueRule> map, String urlRss, String data, 
                String from, boolean download, boolean first) {
        this.map = map;
        this.url = urlRss;
        this.data = data;
        this.from = from;
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
    
    /**
     * effettua il download automatico di myitasa comprende le fasi anche di
     * estrazione zip e analizzazione percorso definitivo.
     * 
     * @param link
     *            link da analizzare
     */
    private void downItasaAuto(Object link) {
        ArrayList<String> als = new ArrayList<String>();
        als.add(link.toString());
        /*
        DownloadThread dt = new DownloadThread(map, als, true);
        Thread t = new Thread(dt, "AutoItasa");
        t.start();
         */
    }
    
    @Override
    public void run() {
        RssParser rss = null;
        ArrayList<Object[]> matrice = null;
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        try {
            InputStream ist = http.getStreamRss(url);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Io.downloadSingle(ist, ft);
                rss = new RssParser(ft);
                matrice = rss.readRss();
                ft.delete();
                boolean continua = true;
                if (data != null) {
                    Date confronta = Common.stringDateTime(data);
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
                            if ((isNotStagione((String) matrice.get(i)[2]))
                                    && download) {
                                downItasaAuto(matrice.get(i)[0]);
                            }
                        } else if (first && from.equals(core.MYITASA)) {
                            // non deve fare nulla
                        } else {// if confronta after
                            matrice.remove(i);
                        }
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

        //return matrice; fare il fire 
    }
}