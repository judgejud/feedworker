package org.feedworker.core.thread;

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
import org.feedworker.core.http.HttpAbstract;
import org.feedworker.core.http.HttpItasa;
import org.feedworker.core.Kernel;
import org.feedworker.core.ManageListener;
import org.feedworker.exception.ManageException;
import org.feedworker.object.KeyRule;
import org.feedworker.object.ValueRule;
import org.feedworker.util.AudioPlay;
import org.feedworker.util.Common;
import org.feedworker.util.GCalNotifierSms;
import org.feedworker.util.Mail;
import org.feedworker.xml.Reminder;

import org.jfacility.Io;
import org.jfacility.Util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
/**
 * 
 * @author luca
 */
public class DownloadThread implements Runnable {
    
    private ArrayList<String> als;
    private boolean autoItasa;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private ManageException error = ManageException.getIstance();
    private TreeMap<KeyRule, ValueRule> mapRules;
    private Reminder xmlReminder;
    private HttpAbstract http;
    
    public DownloadThread(TreeMap<KeyRule, ValueRule> map, Reminder xml, 
                            ArrayList<String> _als, HttpAbstract _http, boolean _autoitasa) {
        als = _als;        
        http = _http;
        mapRules = map;
        xmlReminder = xml;
        autoItasa = _autoitasa;
    }

    @Override
    public void run() {
        ArrayList<File> alFile = new ArrayList<File>();
        ArrayList<Object[]> alReminder = new ArrayList<Object[]>();
        ArrayList<String> alNotify = new ArrayList<String>();
        boolean sub = false;
        try {
            for (int i = 0; i < als.size(); i++) {
                //HttpEntity entity = http.requestGetEntity(als.get(i), itasa);
                HttpEntity entity = http.requestGetEntity(als.get(i));
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
            error.launch(ex, this.getClass(), http instanceof HttpItasa);
        } catch (IOException ex) {
            error.launch(ex, this.getClass(), null);
        }
        if (alFile.size()>0){
            OperationFileThread oft = new OperationFileThread(mapRules, alFile);
            Thread t = new Thread(oft, "Thread operation files");
            t.start();
        }
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
    
    private void printAlert(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.ALERT, true);
    }
}