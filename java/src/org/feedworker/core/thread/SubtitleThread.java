package org.feedworker.core.thread;

import java.io.IOException;
import java.util.ArrayList;

import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.ManageListener;
import org.feedworker.exception.ItasaException;
import org.feedworker.object.Subtitle;
import org.feedworker.xml.ItasaOnline;

import org.jdom.JDOMException;
/**
 *
 * @author luca judge
 */
public class SubtitleThread implements Runnable{
    private ArrayList<String> list;
    private String tableDest;
    private boolean msg;

    public SubtitleThread(ArrayList<String> list, String tableDest, boolean msg) {
        this.list = list;
        this.tableDest = tableDest;
        this.msg = msg;
    }
    
    @Override
    public void run() {
        ArrayList<Subtitle> subs = new ArrayList<Subtitle>();
        ItasaOnline itasa = new ItasaOnline();
        try {
            for (int i=0; i<list.size(); i++)
                subs.add(itasa.subtitleSingle(list.get(i)));
            if (msg)
                ManageListener.fireTextPaneEvent(this, "Ricerca sottotitoli completata", 
                                                TextPaneEvent.OK, true);
            ManageListener.fireTableEvent(this, tableDest, subs);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ItasaException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}