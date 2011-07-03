package org.feedworker.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.zip.ZipException;

import javax.mail.MessagingException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.exception.ManageException;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.Samba;
import org.feedworker.xml.Reminder;

import org.jfacility.Io;
import org.jfacility.Util;
import org.jfacility.java.lang.Lang;

import jcifs.smb.SmbException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.feedworker.util.GCalNotifierSms;
import org.feedworker.util.Mail;
/**
 * 
 * @author luca
 */
public class DownloadThread implements Runnable {
    private final String SPLIT_SUB = ".sub";
    private final String SPLIT_POINT = "\\.";
    private final String[] QUALITY = Quality.toArray();
    private ArrayList<String> als;
    private boolean itasa;
    private boolean autoItasa;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private TreeMap<KeyRule, ValueRule> mapRules;
    private Reminder xmlReminder;

    DownloadThread(TreeMap<KeyRule, ValueRule> map, Reminder xml, 
                            ArrayList<String> _als, boolean _itasa, boolean _autoitasa) {
        als = _als;
        itasa = _itasa;
        mapRules = map;
        xmlReminder = xml;
        autoItasa = _autoitasa;
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
        } else 
            ManageListener.fireTextPaneEvent(this, "Scaricato: " + f.getName(), 
                                                        TextPaneEvent.OK, true);
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
                        String namesub = filesub.getName();
                        KeyRule key = parsingNamefile(namesub, SPLIT_SUB);
                        if (!deleteFile(key, filesub, namesub)){
                            dest = returnPath(key);
                            if (prop.isEnabledAdvancedDownload()){
                                try{
                                    newName = rename(key, namesub);
                                } catch (NullPointerException e){}
                            } else 
                                dest = null;
                            s.moveFromLocal(filesub, dest);
                            if (newName != null) {
                                String oldName = dest + File.separator + filesub.getName();
                                s.moveFile(oldName, dest, newName);
                            }
                            if (dest == null)
                                dest = "";
                            String msg;
                            if (newName==null)
                                msg = "Estratto " + al.get(i).getName()
                                    + " nella cartella condivisa samba\\" + dest ;
                            else
                                msg = "Estratto " + al.get(i).getName() + " e rinominato in "
                                    + newName + " nella cartella condivisa samba\\" + dest ;
                            printSub(msg);
                        }
                    }
                } catch (SmbException ex) {
                    error.launch(ex, getClass(), dest);
                } catch (IOException ex) {
                    error.launch(ex, getClass(), dest);
                }
            } else {
                for (int i = 0; i < al.size(); i++) {
                    File filesub = al.get(i);
                    String namesub = filesub.getName();
                    KeyRule key = parsingNamefile(namesub, SPLIT_SUB);
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
                                Io.moveFile(filesub, dest, newName);
                                msg += " e rinominato in " + newName;
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
    }
    
    private boolean deleteFile(KeyRule key, File filesub, String namesub){
        if (prop.isEnabledAdvancedDownload() && key!=null && 
                                                mapRules.get(key).isDelete()){
            filesub.delete();
            printAlert(namesub + " cancellato per la regola DELETE");
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        Http http = new Http(connection_Timeout);
        ArrayList<File> alFile = new ArrayList<File>();
        ArrayList<Object[]> alReminder = new ArrayList<Object[]>();
        ArrayList<String> alNotify = new ArrayList<String>();
        boolean sub = false;
        try {
            if (itasa)
                http.connectItasa(prop.getItasaUsername(), prop.getItasaPassword());
            for (int i = 0; i < als.size(); i++) {
                HttpEntity entity = http.requestGetEntity(als.get(i), itasa);
                if (entity != null) {
                    if (entity.getContentLength() != -1) {
                        String n = http.getNameFile();
                        int l = n.length();
                        File f = File.createTempFile(n.substring(0, l - 4), n.substring(l - 4));
                        Io.downloadSingle(entity.getContent(), f);
                        alFile.addAll(extract(f));
                        String temp = f.getName().split(".sub.")[0].replaceAll("\\.", " ");
                        if (prop.isReminderOption())
                            alReminder.add(new Object[]{Common.actualDate(), temp, false});
                        if (autoItasa && (prop.isEnableNotifyMail() || prop.isEnableNotifySms()))
                            alNotify.add(temp);
                        if (autoItasa && !sub)
                            sub=true;
                    } else
                        printAlert("Sessione scaduta");
                }
            } //end for
        } catch (UnsupportedEncodingException ex) {
            error.launch(ex, this.getClass());
        } catch (ClientProtocolException ex) {
            error.launch(ex, this.getClass());
        } catch (IllegalArgumentException ex) {
            error.launch(ex, this.getClass());
        } catch (StringIndexOutOfBoundsException ex) {
            error.launch(ex, this.getClass(), itasa);
        } catch (IOException ex) {
            error.launch(ex, this.getClass(), null);
        }        
        http.closeClient();
        analyzeDest(alFile);
        if (sub && prop.isEnableNotifyAudioSub())
            try {
                AudioPlay.playSubWav();
        } catch (UnsupportedAudioFileException ex) {
            error.launch(ex, getClass());
        } catch (LineUnavailableException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        if (prop.isReminderOption()){
            addXML(alReminder);
            ManageListener.fireTableEvent(this, alReminder, Kernel.getIstance().REMINDER);
        }
        if (alNotify.size()>0)
            startNotifyMailSms(alNotify);
    }
    
    private void startNotifyMailSms(ArrayList<String> array){
        String text = "";
        for (int i=0; i<array.size(); i++)
            text += array.get(i) + "\n";
        if (prop.isEnableNotifyMail()){
            try {
                Mail.send(prop.getMailSMTP(), prop.getMailTO(), text, 
                        prop.getItasaUsername());
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
        }
        if (prop.isEnableNotifySms())
            GCalNotifierSms.send(prop.getGoogleUser(), prop.getGooglePwd(), 
                                prop.getGoogleCalendar(), text);
    }
    
    private void addXML(ArrayList<Object[]> al){
        try {
            for (int i=0; i<al.size(); i++)
                xmlReminder.addItem(al.get(i));
            xmlReminder.write();
        } catch (IOException ex) {
            error.launch(ex, null);
        }
    }

    private String rename(KeyRule key, String namesub) throws NullPointerException{
        String newname = null;
        if (mapRules.get(key).isRename()) {
            String from = key.getName().replaceAll(" ", ".") + ".";
            newname = namesub.split(SPLIT_SUB)[0].toLowerCase().replaceFirst(
                    from, "");
            String ext = namesub.substring(namesub.length() - 4);
            if (newname.substring(0, 1).equalsIgnoreCase("s"))
                newname = newname.substring(4);
            else if (newname.substring(0, 1).equalsIgnoreCase("e"))
                newname = newname.substring(1);
            newname += ext;
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
        if (version == null)
            version = Quality.NORMAL.toString();
        return version;
    }
    
    private void printAlert(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, true);
    }
    
    private void printSub(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.SUB, true);
    }
}