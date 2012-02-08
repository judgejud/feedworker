package org.feedworker.core.thread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.ManageListener;
import org.feedworker.exception.ManageException;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Operation;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;
import org.feedworker.util.ExtensionFilter;
import org.feedworker.util.Samba;

import org.jfacility.Io;
import org.jfacility.java.lang.Lang;

import jcifs.smb.SmbException;
/**
 *
 * @author luca judge
 */
class OperationFileThread implements Runnable{
    private final String SPLIT_SUB = ".sub";
    private final String SPLIT_POINT = "\\.";
    private final String[] QUALITY = Quality.toArray();
    
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private TreeMap<KeyRule, ValueRule> mapRules;
    private ArrayList<File> arraySub;
    private File filesub;
    private String namesub;
    private KeyRule key;
    

    public OperationFileThread(TreeMap<KeyRule, ValueRule> mapRules, ArrayList<File> arraySub) {
        this.mapRules = mapRules;
        this.arraySub = arraySub;
    }
    
    @Override
    public void run() {
        /*TODO: Flash.Forward.s01e11e12.720p.R.sub.itasa.srt nella cartella
        condivisa samba\flash forward
         * problema: nessuna regola specificata per il 720, rivedere il search version.
         */
        String newName = null;
        if (!prop.isLocalFolder()) {
            String dest = null;
            try {
                Samba s = Samba.getIstance(prop.getCifsShareLocation(),
                        prop.getCifsSharePath(),
                        prop.getCifsShareDomain(),
                        prop.getCifsShareUsername(),
                        prop.getCifsSharePassword());
                for (int i = 0; i < arraySub.size(); i++) {
                    setKey(i);
                    if (!deleteFile(key, filesub, namesub)){
                        dest = returnPath(key);
                        if (prop.isEnabledAdvancedDownload()){
                            try{
                                newName = rename(key, namesub);
                            } catch (NullPointerException e){}
                        } else 
                            dest = null;
                        try{
                            s.moveFromLocal(filesub, dest);
                            if (newName != null) {
                                String oldName = dest + File.separator + filesub.getName();
                                s.moveFile(oldName, dest, newName);
                            }
                        } catch (IOException e){
                            printAlert("percorso samba \"" +  dest + "\" non trovato");
                            s.moveFromLocal(filesub, null);
                            newName = null; 
                        }
                        if (newName==null)
                            printSub("Estratto " + arraySub.get(i).getName() + 
                                    " nella cartella condivisa samba\\") ;
                        else
                            printSub("Estratto " + arraySub.get(i).getName() + " e rinominato in "
                                + newName + " nella cartella condivisa samba\\" + dest);
                    }
                }
            } catch (SmbException ex) {
                error.launch(ex, getClass(), dest);
            } catch (IOException ex) {
                error.launch(ex, getClass(), dest);
            }
        } else {
            for (int i = 0; i < arraySub.size(); i++) {
                setKey(i);
                if (!deleteFile(key, filesub, namesub)){
                    String dest = returnPath(key);
                    if (dest == null || !prop.isEnabledAdvancedDownload())
                        dest = prop.getSubtitleDestinationFolder();
                    try{
                        newName = rename(key, namesub);
                    } catch (NullPointerException e){}
                    try {
                        String msg = "Estratto " + filesub.getName();
                        if (newName == null) {
                            Io.moveFile(filesub, dest);
                        } else {
                            try{
                                Io.moveFile(filesub, dest, newName);
                                msg += " e rinominato in " + newName;
                            } catch (IOException e){
                                Io.moveFile(filesub, prop.getSubtitleDestinationFolder());
                                printAlert(dest + " non esistente, verificarlo.");
                                dest = prop.getSubtitleDestinationFolder();
                            }
                        }
                        msg += " nel seguente percorso: " + dest;
                        printSub(msg);
                    } catch (IOException ex) {
                        error.launch(ex, getClass());
                    }
                }
            }
        }
    }
    
    private void setKey(int i){
        filesub = arraySub.get(i);
        namesub = filesub.getName();
        key = parsingNamefile(namesub, SPLIT_SUB);
    }
    
    private boolean deleteFile(KeyRule key, File filesub, String namesub){
        if (prop.isEnabledAdvancedDownload() && key!=null && 
                mapRules.get(key).getOperation().equalsIgnoreCase(Operation.DELETE.toString())){
            filesub.delete();
            printAlert(namesub + " cancellato per la regola DELETE");
            return true;
        }
        return false;
    }
    
    private String rename(KeyRule key, String namesub) throws NullPointerException{
        String newname = null;
        String oper = mapRules.get(key).getOperation();
        if (oper.equalsIgnoreCase(Operation.TRUNCATE.toString())){
            String from = key.getName().replaceAll(" ", ".") + ".";
            newname = namesub.split(SPLIT_SUB)[0].toLowerCase().replaceFirst(from, "");
            String ext = namesub.substring(namesub.length() - 4);
            if (newname.substring(0, 1).equalsIgnoreCase("s"))
                newname = newname.substring(4);
            else if (newname.substring(0, 1).equalsIgnoreCase("e"))
                newname = newname.substring(1);
            newname += ext;
        } else if (oper.equalsIgnoreCase(Operation.EQUAL_VIDEO.toString())){
            //TODO
            String path = returnPath(key);
            String qual = key.getQuality();
            System.out.println(namesub.toLowerCase());
            if (prop.isLocalFolder()){
                if (qual.equals(Quality.NORMAL.toString()) || 
                        qual.equals(Quality.DVDRIP.toString())){
                    List<String> list = listDir(path, "avi");
                    for (int i=0; i<list.size(); i++){
                       System.out.println(list.get(i).toLowerCase());
                        //if (true)
                        //    break;
                    }
                } else if (qual.equals(Quality._720p.toString()) || 
                        qual.equals(Quality.WEB_DL.toString())){
                    List<String> list = listDir(path, "mkv");
                    for (int i=0; i<list.size(); i++){
                       System.out.println(list.get(i).toLowerCase());
                        //if (true)
                        //    break;
                    }
                }
            }
        }
        return newname;
    }

    /**Restituisce il percorso della chiave ad esso associato nella treemap
     *
     * @param key chiave di ricerca
     * @return path di destinazione
     */
    private String returnPath(KeyRule key) {
        if (key != null && mapRules != null)
            return mapRules.get(key).getPath();
        return null;
    }

    /**Effettua l'analisi del nome del file restituendo l'oggetto filtro da
     * confrontare
     *
     * @param name nome del file da analizzare
     * @param split stringa col quale effettuare lo split del nome del file
     * @return oggetto filtro
     */
    private KeyRule parsingNamefile(String namefile, String split) {
        String[] temp = (namefile.split(split))[0].split(SPLIT_POINT);
        int pos = temp.length - 1;
        String version = searchVersion(temp[pos]);
        pos = searchPosSeries(temp);
        String serieNum = "1";
        if (pos > -1)
            serieNum = searchNumberSeries(temp[pos]);
        String name = temp[0];
        for (int i = 1; i < pos; i++)
            name += " " + temp[i];
        KeyRule key = new KeyRule(name, serieNum, version);
        if (key != null && mapRules != null) {
            if (mapRules.containsKey(key))
                return key;
            else {
                key.setQuality(Quality.DIFF.toString());
                if (mapRules.containsKey(key))
                    return key;
            }
        }
        return null;
    }
    
    /**cerca la posizione della stringa corrispondente al numero di serie ed
     * episodio nell'array; es: s01e01
     * 
     * @param _array
     * @return restituisce la posizione se l'ha trovato, altrimenti -1
     */
    private int searchPosSeries(String[] array) {
        int pos = -1;
        for (int i = 0; i < array.length; i++) {
            if (searchNumberSeries(array[i]) != null) {
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
        String first = text.substring(0, 1).toLowerCase();
        if (first.equalsIgnoreCase("s") && (text.length()==6)) {
            int num = -1;
            try {
                num = Lang.stringToInt(text.substring(1, 3));
            } catch (NumberFormatException nfe) {} 
            if (num > -1)
                number = Lang.intToString(num);
        } else if (first.equalsIgnoreCase("e")) {
            int num = -1;
            try {
                num = Lang.stringToInt(text.substring(1, 3));
            } catch (NumberFormatException nfe) {}
            if (num > -1)
                number = "1";
        }
        return number;
    }
    
    /**cerca la versione/qualità del sub/video
     *
     * @param text testo da confrontare
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
        if (version == null)
            version = Quality.NORMAL.toString();
        return version;
    }
    
    /**effuetta la stampa dei file con l'estensione e la directory in cui cercare
     * 
     * @param dir directory su cui effettuare la ricerca
     * @param ext estensione dei file da cercare
     */
    private List<String> listDir(String dir, String ext) {
        return Arrays.asList(new File(dir).list(new ExtensionFilter(ext)));
    }
    
    private void printSub(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.SUB, true);
    }
    
    private void printAlert(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, true);
    }
}