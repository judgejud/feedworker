package org.feedworker.core.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.ManageListener;
import org.feedworker.core.RssParser;
import org.feedworker.core.http.HttpOther;
import org.feedworker.exception.ManageException;

import org.jfacility.Io;
import org.jfacility.java.lang.Lang;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
/**
 *
 * @author luca judge
 */
public class RssBlogThread implements Runnable{
    private ManageException error = ManageException.getIstance();
    private String url, data, lastDate;
    private int count;
    
    public RssBlogThread(String data, String url) {
        this.data = data;
        this.url = url;
    }
    
    @Override
    public void run() {
        String blog = "Blog";
        try {
            HttpOther http = new HttpOther(Lang.stringToInt(ApplicationSettings.getIstance().
                                                    getHttpTimeout()) * 1000);
            InputStream ist = http.getStream(url);
            if (ist != null) {
                File ft = File.createTempFile("rss", ".xml");
                Io.downloadSingle(ist, ft);
                RssParser rss = new RssParser(ft);
                ArrayList<Object[]> matrice = rss.readRssBlog(data);
                ft.delete();
                count = matrice.size();
                if (count>0){
                    if (lastDate!=null)
                        ManageListener.fireTextPaneEvent(this, "Nuovo/i feed Blog",
                                                        TextPaneEvent.FEED_BLOG, true);
                    ManageListener.fireListEvent(this, blog, matrice);
                    lastDate = (String) matrice.get(0)[1];
                }
            }
        } catch (ParseException ex) {
            error.launch(ex, getClass());
        } catch (ParsingFeedException ex) {
            error.launch(ex, getClass(), blog);
        } catch (FeedException ex) {
            error.launch(ex, getClass(), blog);
        } catch (IllegalArgumentException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass(), blog);
        }
    }
    
    public synchronized int getCount() {
        return count;
    }

    public synchronized String getLastDate() {
        return lastDate;
    }
}