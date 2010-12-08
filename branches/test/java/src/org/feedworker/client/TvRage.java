package org.feedworker.client;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author luca
 */
class TvRage {
    private final String DETAILED_SEARCH = "http://services.tvrage.com/feeds/full_search.php?show=";
    private final String EPISODE_LIST = "http://services.tvrage.com/feeds/episode_list.php?sid=";
    private final String TAG_SHOW_ID = "showid";
    private final String TAG_NAME = "name";
    private final String TAG_LINK = "link";
    private final String TAG_SEASON = "seasons";
    private final String TAG_STATUS = "status";
    private final String TAG_AIRDAY = "airday";

    private Document document;

    ArrayList<Object[]> readingDetailedSearch_byShow(String show) throws JDOMException, IOException{
        document = new SAXBuilder().build(new URL(DETAILED_SEARCH + show));
        int size = document.getRootElement().getChildren().size();
        ArrayList<Object[]> matrix = null;
        if (size>0){
            matrix = new ArrayList<Object[]>();
            Iterator iterator = document.getRootElement().getChildren().iterator();
            while (iterator.hasNext()) {
                Element rule = (Element) iterator.next();
                String id = rule.getChild(TAG_SHOW_ID).getText();
                String name = rule.getChild(TAG_NAME).getText();
                String link = rule.getChild(TAG_LINK).getText();
                String season = rule.getChild(TAG_SEASON).getText();
                String status = rule.getChild(TAG_STATUS).getText();
                String airday = rule.getChild(TAG_AIRDAY).getText();
                Object[] temp = {id, name, link, season, status, airday};
                matrix.add(temp);
            }
        }
        return matrix;
    }

    void readingEpisodeList_byID(String id) throws JDOMException, IOException{
        document = new SAXBuilder().build(new URL(EPISODE_LIST + id));
        int size = document.getRootElement().getChildren().size();
        Iterator iterator = document.getRootElement().getChildren().iterator();
        while (iterator.hasNext()) {

        }
    }
}