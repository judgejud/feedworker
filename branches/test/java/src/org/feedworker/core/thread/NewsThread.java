package org.feedworker.core.thread;

import java.io.IOException;
import java.util.ArrayList;

import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.core.ManageListener;
import org.feedworker.xml.ItasaOnline;

import org.jdom.JDOMException;
/**
 *
 * @author luca judge
 */
public class NewsThread implements Runnable{
    private int id, count; 
    
    public NewsThread(int id) {
        this.id = id;
    }
    
    @Override
    public void run() {
        ItasaOnline i = new ItasaOnline();
        try {
            ArrayList<Object[]> array = i.newsList(id);
            count = array.size();
            if (count>0){
                id = Integer.parseInt(array.get(0)[0].toString());
                ManageListener.fireTextPaneEvent(this,"Nuovo/i news itasa",
                                            TextPaneEvent.ITASA_NEWS,true);
                ManageListener.fireListEvent(this, "ItasaNews", array);
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized int getCount() {
        return count;
    }

    public synchronized int getLastId() {
        return id;
    }
}