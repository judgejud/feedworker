package org.feedworker.core;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.feedworker.exception.ItasaException;
import org.feedworker.exception.ManageException;
import org.feedworker.object.Show;
import org.feedworker.xml.ItasaOnline;
import org.feedworker.xml.TvRage;

import org.jdom.JDOMException;

/**
 *
 * @author Administrator
 */
public class ShowThread implements Runnable {
    private String name, id;
    private ManageException error = ManageException.getIstance();

    public ShowThread(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public void run() {
        ItasaOnline i = new ItasaOnline();
        try {
            Show s = i.showSingle(id, true);
            ManageListener.fireEditorPaneEvent(this, s.getHtmlShow(), name, "show");
            ManageListener.fireEditorPaneEvent(this, s.getHtmlActors(), name, "actors");
            TvRage t = new TvRage();
            Object[] tvrage = t.readingAllEpisodeList_byID(s.getTvrage());
            ArrayList<String> destName = (ArrayList<String>)tvrage[0];
            ManageListener.fireTabbedPaneEvent(this, destName, name);
            ArrayList<ArrayList<Object[]>> all = (ArrayList<ArrayList<Object[]>>) tvrage[1];
            for (int n=0; n<all.size(); n++)
                ManageListener.fireTableEvent(this, all.get(n), name + destName.get(n));
        } catch (ConnectException ex) {
            error.launch(ex, getClass());
        } catch (JDOMException ex) {
            error.launch(ex, getClass());
        } catch (IOException ex) {
            error.launch(ex, getClass());
        } catch (ItasaException ex) {
            error.launch(ex, getClass());
        } catch (Exception ex) {
            error.launch(ex, getClass());
        }
    }
}