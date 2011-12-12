package org.feedworker.core.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.core.IcsParser;
import org.feedworker.core.ManageListener;
import org.feedworker.core.http.HttpOther;

import org.jfacility.Io;
import org.jfacility.java.lang.Lang;

import net.fortuna.ical4j.data.ParserException;
/**
 *
 * @author luca judge
 */
public class IcsThread implements Runnable{
    private String url;

    public IcsThread(String url) {
        this.url = url;
    }
    
    @Override
    public void run() {
        int connection_Timeout = Lang.stringToInt(ApplicationSettings.getIstance().getHttpTimeout()) * 1000;
        HttpOther http = new HttpOther(connection_Timeout);
        try {
            InputStream is = http.getStream(url);
            File f = File.createTempFile("itasa", "ics");
            Io.downloadSingle(is, f);
            IcsParser cal = new IcsParser(new FileInputStream(f));
            Object[] o = cal.getData();
            ManageListener.fireCanvasEvent(this, o);
        } catch (ParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}