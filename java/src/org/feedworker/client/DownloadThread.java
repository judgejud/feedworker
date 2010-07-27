package org.feedworker.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipException;

import org.apache.http.HttpEntity;

import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.util.FilterSub;
import org.feedworker.util.ManageException;
import org.feedworker.util.Quality;
import org.feedworker.util.Samba;
import org.lp.myUtils.Io;
import org.lp.myUtils.Util;
import org.lp.myUtils.lang.Lang;

/**
 *
 * @author luca
 */
public class DownloadThread implements Runnable{
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
    private TreeMap<FilterSub, String> mapRole;
    private List listenerTextPane = new ArrayList();
    private static DownloadThread dt = null;
    
    DownloadThread(TreeMap<FilterSub, String> map, ArrayList<String> _als, boolean _itasa){
        als = _als;
        itasa = _itasa;
        mapRole = map;        
    }

    public DownloadThread() {}

    /**
     * 
     */
    public void run() {
        int connection_Timeout = Lang.stringToInt(ApplicationSettings.getIstance().getTimeout())*1000;
        Http http = new Http(connection_Timeout);
        ArrayList<File> alf = new ArrayList<File>();
        try{
            if (itasa)
                http.connectItasa(prop.getItasaUser(), prop.getItasaPwd());

            for (int i = 0; i < als.size(); i++) {
                HttpEntity entity = http.requestGetEntity(als.get(i), itasa);
                if (entity != null) {
                    if (entity.getContentLength() != -1) {
                        String n = http.getNameFile();
                        int l = n.length();
                        File f = File.createTempFile(n.substring(0, l - 4), n.substring(l - 4));
                        downloadSingle(entity.getContent(), f);
                        alf.addAll(extract(f));
                    } else
                        printAlert("Sessione scaduta");
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
    /**Effettua il download dell'inputStream sotto forma di file
     *
     * @param is http content-stream
     * @param f file di riferimento su cui mandare il flusso di inputstream
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void downloadSingle(InputStream is, File f) throws FileNotFoundException,
            IOException {
        OutputStream out = new FileOutputStream(f);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
        out.close();
        is.close();
    }
    /**Estrae lo zip e restituisce l'arraylist di file contenuti nello zip
     *
     * @param f file zip di riferimento da estrarre
     * @return
     */
    private ArrayList<File> extract(File f) {
        String temp = f.getName().substring(f.getName().length() - 3);
        ArrayList<File> alf=null;
        if (temp.toUpperCase().equalsIgnoreCase("ZIP")) {
            try {
                String path = f.getParent();
                if (!path.substring(path.length() - 1).equals(File.separator))
                    path += File.separator;
                alf = Util.unzip(f, "__MACOSX/", path);
                f.delete();
            } catch (ZipException ex) {
                error.launch(ex, getClass());
            } catch (IOException ex) {
                error.launch(ex, getClass(), null);
            }
        } else
            fireNewTextPaneEvent("Scaricato: " + f.getName(), MyTextPaneEvent.OK);
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
            if (!prop.isDirLocal()) {
                String dest = null;
                try {
                    Samba s = Samba.getIstance(prop.getSambaIP(), prop.getSambaDir(),
                            prop.getSambaDomain(), prop.getSambaUser(), prop.getSambaPwd());
                    for (int i = 0; i < al.size(); i++) {
                        File filesub = al.get(i);
                        String namesub = al.get(i).getName();
                        dest = mapPath(namesub, SPLIT_SUB);
                        if (dest!=null && dest.toLowerCase().equals(CMD_DELETE))
                            filesub.delete();
                        else {
                            s.moveFromLocal(filesub, dest);
                            if (dest==null)
                                dest = "";
                            fireNewTextPaneEvent("Estratto " + al.get(i).getName() +
                                    " nella cartella condivisa samba\\" + dest,
                                    MyTextPaneEvent.SUB);
                        }
                    }
                } catch (IOException ex) {
                    error.launch(ex, getClass(), dest);
                }
            } else {
                for (int i = 0; i < al.size(); i++) {
                    File filesub = al.get(i);
                    String namesub = al.get(i).getName();
                    String dest = mapPath(namesub, SPLIT_SUB);
                    if (dest!=null && dest.toLowerCase().equals(CMD_DELETE))
                        filesub.delete();
                    else {
                        if (dest==null)
                            dest = prop.getSubDest();
                        try {
                            Io.moveFile(filesub, dest);
                            fireNewTextPaneEvent("Estratto " + al.get(i).getName() +
                                    " nel seguente percorso: " + dest,
                                    MyTextPaneEvent.SUB);
                        } catch (IOException ex) {
                            error.launch(ex, getClass(), dest);
                        }
                    }
                }
            }
        }
    }
    /**Restituisce il valore/percorso della chiave ad esso associato nella treemap
     *
     * @param name nome del file da analizzare
     * @param parsing valore sul quale effettuare lo split
     * @return path di destinazione
     */
    private String mapPath(String name, String parsing){
        String path = null;
        FilterSub sub = parsingNamefile(name, parsing);
        if (sub!=null && mapRole!=null) {
            if (mapRole.containsKey(sub))
                path = mapRole.get(sub);
            else {
                sub.setQuality(Quality.DIFF.toString());
                if (mapRole.containsKey(sub))
                    path = mapRole.get(sub);
            }
        }
        return path;
    }
    /**Effettua l'analisi del nome del file restituendo l'oggetto filtro da confrontare
     *
     * @param name nome del file da analizzare
     * @param split stringa col quale effettuare lo split del nome del file
     * @return oggetto filtro
     */
    private FilterSub parsingNamefile(String name, String split) {
        FilterSub fil = null;
        String[] temp = (name.split(split))[0].split(SPLIT_POINT);
        int pos = temp.length - 1;
        String version = searchVersion(temp[pos]);
        String num;
        pos = searchPosSeries(temp);
        if (pos>-1)
            num = searchNumberSeries(temp[pos]);
        else
            num = "1";
        String _serie = temp[0];
        for (int i = 1; i < pos; i++)
            _serie += " " + temp[i];
        fil = new FilterSub(_serie, num, version, null, null);
        return fil;
    }
    /**cerca la versione/qualità del sub/video
     *
     * @param text testo da confrontare
     * @return versione video/sub
     */
    private String searchVersion(String text){
        String version = null;
        for (int i = 0; i < QUALITY.length; i++) {
            if (text.toLowerCase().equalsIgnoreCase(QUALITY[i])) {
                version = QUALITY[i];
                break;
            }
        }
        if (version == null)
            version = Quality.NORMAL.toString();
        return version;
    }
    /**cerca la posizione della stringa corrispondente al numero di serie ed episodio
     * nell'array; es: s01e01
     * @param _array
     * @return restituisce la posizione se l'ha trovato, altrimenti -1
     */
    private int searchPosSeries(String[] _array){
        int pos = -1;
        for (int i=0; i<_array.length; i++){
            if (searchNumberSeries(_array[i])!=null){
                pos = i;
                break;
            }
        }
        return pos;
    }
    /**cerca il numero della serie nel testo
     *
     * @param text
     * @return numero serie/stagione
     */
    private String searchNumberSeries(String text){
        String number = null;
        String analyze = text.substring(0, 1).toLowerCase();
        if (analyze.equalsIgnoreCase("s")){
            String temp = text.substring(1, 3);
            int num = -1;
            try{
                num = Lang.stringToInt(temp);
            } catch(NumberFormatException nfe){}
            if (num>-1)
                number = Lang.intToString(num);
        }
        return number;
    }

    /**Permette alla classe di registrarsi per l'evento textpane
     *
     * @param listener evento textpane
     */
    public synchronized void addMyTextPaneEventListener(MyTextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }
    /**Permette alla classe di de-registrarsi per l'evento textpane
     *
     * @param listener evento textpane
     */
    public synchronized void removeMyTextPaneEventListener(MyTextPaneEventListener listener) {
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
    private void printAlert(String msg){
        fireNewTextPaneEvent(msg, MyTextPaneEvent.ALERT);
    }
}