package org.feedworker.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipException;

import org.apache.http.HttpEntity;
import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.util.Common;
import org.feedworker.util.KeyRule;
import org.feedworker.util.ManageException;
import org.feedworker.util.Quality;
import org.feedworker.util.Samba;
import org.feedworker.util.ValueRule;
import org.jfacility.Io;
import org.jfacility.Util;
import org.jfacility.java.lang.Lang;

/**
 * 
 * @author luca
 */
public class DownloadThread implements Runnable {

    private final String CMD_DELETE = "delete";
    private final String SPLIT_SUB = ".sub";
    private final String SPLIT_POINT = "\\.";
    private final String[] QUALITY = new String[]{Quality.ALL.toString(),
        Quality.NORMAL.toString(), Quality.FORM_720p.toString(),
        Quality.FORM_1080p.toString(), Quality.BLURAY.toString(),
        Quality.DVDRIP.toString(), Quality.HR.toString(),
        Quality.DIFF.toString()};
    private ArrayList<String> als;
    private boolean itasa;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private TreeMap<KeyRule, ValueRule> mapRules;
    private List listenerTextPane = new ArrayList();
    private static DownloadThread dt = null;

    DownloadThread(TreeMap<KeyRule, ValueRule> map, ArrayList<String> _als, boolean _itasa) {
        als = _als;
        itasa = _itasa;
        mapRules = map;
    }

    /**Estrae lo zip e restituisce l'arraylist di file contenuti nello zip
     *
     * @param f file zip di riferimento da estrarre
     * @return
     */
    private ArrayList<File> extract(File f) {
        String temp = f.getName().substring(f.getName().length() - 3);
        ArrayList<File> alf = null;
        if (temp.toUpperCase().equalsIgnoreCase("ZIP")) {
            try {
                String path = f.getParent();
                if (!path.substring(path.length() - 1).equals(File.separator)) {
                    path += File.separator;
                }
                alf = Util.unzip(f, "__MACOSX/", path);
                f.delete();
            } catch (ZipException ex) {
                error.launch(ex, getClass());
            } catch (IOException ex) {
                error.launch(ex, getClass(), null);
            }
        } else {
            fireNewTextPaneEvent("Scaricato: " + f.getName(), MyTextPaneEvent.OK);
        }
        //return Zip.getAlFile();
        return alf;
    }

    /**analizza il sub e lo sposta nella destinazione pertinente
     *
     * @param al arraylist di file sub
     */
    private void analyzeDest(ArrayList<File> al) {
        /*TODO: Flash.Forward.s01e11e12.720p.R.sub.itasa.srt nella cartella
        condivisa samba\flash forward
         * problema: nessuna regola specificata per il 720, rivedere il search version.
         */
        if (al.size() > 0) {
            String newName = null;
            if (!prop.isLocalFolder()) {
                String dest = null;
                try {
                    Samba s = Samba.getIstance(prop.getCifsShareLocation(),
                            prop.getCifsSharePath(),
                            prop.getCifsShareDomain(),
                            prop.getCifsShareUsername(),
                            prop.getCifsSharePassword());
                    for (int i = 0; i < al.size(); i++) {
                        File filesub = al.get(i);
                        String namesub = al.get(i).getName();
                        KeyRule key = parsingNamefile(namesub, SPLIT_SUB);
                        dest = mapPath(key);
                        if (dest != null && dest.toLowerCase().equals(CMD_DELETE)) {
                            filesub.delete();
                        } else {
                            try{
                                newName = rename(key, namesub);
                            } catch (NullPointerException e){}
                            s.moveFromLocal(filesub, dest);
                            if (newName != null) {
                                String oldName = dest + File.separator + filesub.getName();
                                s.moveFile(oldName, dest, newName);
                            }
                            if (dest == null) {
                                dest = "";
                            }
                            fireNewTextPaneEvent("Estratto " + al.get(i).getName()
                                    + " nella cartella condivisa samba\\" + dest,
                                    MyTextPaneEvent.SUB);
                        }
                    }
                } catch (IOException ex) {
                    error.launch(ex, getClass(), dest);
                }
            } else {
                for (int i = 0; i < al.size(); i++) {
                    File filesub = al.get(i);
                    String namesub = filesub.getName();
                    KeyRule key = parsingNamefile(namesub, SPLIT_SUB);
                    String dest = mapPath(key);
                    if (dest != null && dest.toLowerCase().equals(CMD_DELETE)) {
                        filesub.delete();
                    } else {
                        if (dest == null) {
                            dest = prop.getSubtitleDestinationFolder();
                        }
                        try{
                            newName = rename(key, namesub);
                        } catch (NullPointerException e){}
                        try {
                            String msg = "Estratto " + filesub.getName();
                            if (newName == null) {
                                Io.moveFile(filesub, dest);
                            } else {
                                Io.moveFile(filesub, dest, newName);
                                msg += " e rinominato in " + newName;
                            }
                            msg += " nel seguente percorso: " + dest;
                            fireNewTextPaneEvent(msg, MyTextPaneEvent.SUB);
                        } catch (IOException ex) {
                            error.launch(ex, getClass());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        Http http = new Http(connection_Timeout);
        ArrayList<File> alf = new ArrayList<File>();
        try {
            if (itasa) {
                http.connectItasa(prop.getItasaUsername(),
                        prop.getItasaPassword());
            }

            for (int i = 0; i < als.size(); i++) {
                HttpEntity entity = http.requestGetEntity(als.get(i), itasa);
                if (entity != null) {
                    if (entity.getContentLength() != -1) {
                        String n = http.getNameFile();
                        int l = n.length();
                        File f = File.createTempFile(n.substring(0, l - 4), n.substring(l - 4));
                        Common.downloadSingle(entity.getContent(), f);
                        alf.addAll(extract(f));
                    } else {
                        printAlert("Sessione scaduta");
                    }
                }
            } //end for
        } catch (StringIndexOutOfBoundsException ex) {
            error.launch(ex, this.getClass(), itasa);
        } catch (IOException ex) {
            error.launch(ex, this.getClass(), null);
        }
        http.closeClient();
        analyzeDest(alf);
    }

    private String rename(KeyRule key, String namesub) throws NullPointerException{
        String newname = null;
        if (mapRules.get(key).isRename()) {
            String from = key.getName().replaceAll(" ", ".") + ".";
            newname = namesub.split(SPLIT_SUB)[0].toLowerCase().replaceFirst(
                    from, "");
            String ext = namesub.substring(namesub.length() - 4);
            if (newname.substring(0, 1).equalsIgnoreCase("s")) {
                newname = newname.substring(4);
            } else if (newname.substring(0, 1).equalsIgnoreCase("e")) {
                newname = newname.substring(1);
            }
            newname += ext;
        }
        return newname;
    }

    /**Restituisce il percorso della chiave ad esso associato nella treemap
     *
     * @param key chiave di ricerca
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
        String serieNum;
        pos = Common.searchPosSeries(temp);
        String episodeNum = null;
        if (pos > -1) {
            serieNum = Common.searchNumberSeries(temp[pos]);
        } else {
            serieNum = "1";
            for (int i = 0; i < temp.length; i++) {
            }
        }
        String name = temp[0];
        for (int i = 1; i < pos; i++) {
            name += " " + temp[i];
        }
        KeyRule key = new KeyRule(name, serieNum, version);
        if (key != null && mapRules != null) {
            if (mapRules.containsKey(key)) {
                return key;
            } else {
                key.setQuality(Quality.DIFF.toString());
                if (mapRules.containsKey(key)) {
                    return key;
                }
            }
        }
        return null;
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

    /**Permette alla classe di registrarsi per l'evento textpane
     *
     * @param listener evento textpane
     */
    public synchronized void addMyTextPaneEventListener(
            MyTextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }

    /**
     * Permette alla classe di de-registrarsi per l'evento textpane
     *
     * @param listener evento textpane
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

    /**Stampa il messaggio di alert invocando il metodo fire opportuno
     *
     * @param msg testo da stampare
     */
    private void printAlert(String msg) {
        fireNewTextPaneEvent(msg, MyTextPaneEvent.ALERT);
    }
}