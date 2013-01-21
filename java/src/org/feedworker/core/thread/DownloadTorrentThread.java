package org.feedworker.core.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.ManageListener;
import org.feedworker.core.http.HttpOther;
import org.feedworker.exception.ManageException;

import org.jfacility.Io;
import org.jfacility.java.lang.Lang;

/**
 *
 * @author luca judge
 */
public class DownloadTorrentThread implements Runnable{

    private ArrayList<String> links;
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    public DownloadTorrentThread(ArrayList<String> als) {
        links = als;
    }

    @Override
    public void run() {
        int connection_Timeout = Lang.stringToInt(prop.getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        try {
            for (int i = 0; i < links.size(); i++) {
                String link = links.get(i);
                if (!link.startsWith("magnet")){
                    InputStream is = http.getTorrent(link);
                    if (is != null) {
                        File f = new File(prop.getTorrentDestinationFolder()
                                + File.separator + http.getNameFile());
                        Io.downloadSingle(is, f);
                        ManageListener.fireTextPaneEvent(this,
                                "Scaricato: " + http.getNameFile(),
                                TextPaneEvent.TORRENT, true);
                    } else
                        ManageListener.fireTextPaneEvent(this, 
                                "Non posso gestire " + link.split(".")[1], 
                                TextPaneEvent.ALERT, true);
                } else 
                    ManageListener.fireTextPaneEvent(this, 
                        "Non posso scaricare link magnet, puoi però effetuare la copia dei link", 
                        TextPaneEvent.ALERT, true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            ManageException.getIstance().launch(ex, getClass(), null);
        }
        http.closeClient();
    }
}