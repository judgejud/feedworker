package org.feedworker.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.util.Common;
import org.feedworker.util.ManageException;

import org.jfacility.java.lang.Lang;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
/**
 *
 * @author luca
 */
public class RssThread implements Runnable{

    private String urlRss, data, from;
    private boolean download, first;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private Kernel core = Kernel.getIstance();
    private List listenerTextPane = new ArrayList();

    public RssThread(String urlRss, String data, String from, boolean download, boolean first) {
        this.urlRss = urlRss;
        this.data = data;
        this.from = from;
        this.download = download;
        this.first = first;
    }

    @Override
    public void run() {
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
                                if (from.equals(core.ITASA)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_ITASA);
                                } else if (from.equals(core.MYITASA)
                                        && !prop.isAutoDownloadMyItasa()) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_MYITASA);
                                } else if (from.equals(core.SUBSF)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_SUBSF);
                                } else if (from.equals(core.EZTV)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_EZTV);
                                } else if (from.equals(core.BTCHAT)) {
                                    fireNewTextPaneEvent(
                                            "Nuovo/i feed " + from,
                                            MyTextPaneEvent.FEED_BTCHAT);
                                }
                                continua = false;
                            }
                            if ((isNotStagione((String) matrice.get(i)[2]))
                                    && download) {
                                downItasaAuto(matrice.get(i)[0]);
                            }
                        } else {
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
        //TODO: come restituire la matrice???
        //return matrice;
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

    /**
     * effettua il download automatico di myitasa comprende le fasi anche di
     * estrazione zip e analizzazione percorso definitivo.
     *
     * @param link
     *            link da analizzare
     */
    private void downItasaAuto(Object link) {
        //TODO sistemare
        ArrayList<String> als = new ArrayList<String>();
        als.add(link.toString());
        /*
         DownloadThread dt = new DownloadThread(mapRules, als, true);
        Thread t = new Thread(dt, "Thread download");
        dt.addMyTextPaneEventListener(mytpel);
        t.start();
         */
    }

    /**Permette alla classe di registrarsi per l'evento textpane
     *
     * @param listener
     *            evento textpane
     */
    public synchronized void addMyTextPaneEventListener(
            MyTextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }

    /**Permette alla classe di de-registrarsi per l'evento textpane
     *
     * @param listener
     *            evento textpane
     */
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
}