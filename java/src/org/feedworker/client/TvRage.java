package org.feedworker.client;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.feedworker.util.Common;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jfacility.java.lang.Lang;

/**
 *
 * @author luca
 */
class TvRage {
    private final String DETAILED_SEARCH = "http://services.tvrage.com/feeds/full_search.php?show=";
    private final String EPISODE_LIST = "http://services.tvrage.com/feeds/episode_list.php?sid=";
    private final String SHOW_INFO = "http://services.tvrage.com/feeds/showinfo.php?sid=";
    private final String TAG_SHOW_ID = "showid";
    private final String TAG_NAME = "name";
    private final String TAG_SEASON = "seasons";
    private final String TAG_STATUS = "status";
    private final String TAG_AIRDAY = "airday";
    private final String TAG_AIRDATE = "airdate";
    private final String TAG_SEASON_NUM = "seasonnum";
    private final String TAG_TITLE = "title";

    private Document document;

    ArrayList<Object[]> readingDetailedSearch_byShow(String show) throws JDOMException, IOException{
        document = new SAXBuilder().build(new URL(DETAILED_SEARCH + show));
        List children = document.getRootElement().getChildren();
        int size = children.size();
        ArrayList<Object[]> matrix = null;
        if (size>0){
            matrix = new ArrayList<Object[]>();
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                Element item = (Element) iterator.next();
                String id = item.getChild(TAG_SHOW_ID).getText();
                String name = item.getChild(TAG_NAME).getText();
                String season = item.getChild(TAG_SEASON).getText();
                String status = item.getChild(TAG_STATUS).getText();
                String airday;
                try{
                    airday = item.getChild(TAG_AIRDAY).getText();
                } catch(NullPointerException e){
                    airday ="";
                }
                Object[] temp = {id, name, season, status, airday};
                matrix.add(temp);
            }
        }
        return matrix;
    }

    String[] readingEpisodeList_byID(String id, String season) throws JDOMException, IOException{
        document = new SAXBuilder().build(new URL(EPISODE_LIST + id));
        List seasons = ((Element) document.getRootElement().getChildren().get(2)).getChildren();
        int last = Lang.stringToInt(season)-1;
        Iterator iter = ((Element) seasons.get(last)).getChildren().iterator();
        Date now = Common.actualDate();
        String[] values = new String[10];
        while (iter.hasNext()){
            Element item = (Element) iter.next();
            String airDate = item.getChild(TAG_AIRDATE).getText();
            Date d = null;
            try {
                d = Common.stringAmericanToDate(airDate);
            } catch (ParseException ex) {
            }
            if (airDate.equalsIgnoreCase("0000-00-00")){
                d = now;
                airDate = "";
            }
            String seasonNum = item.getChild(TAG_SEASON_NUM).getText();
            String title = item.getChild(TAG_TITLE).getText();
            if (d.before(now)){
                values[4] = seasonNum;
                values[5] = title;
                values[6] = airDate;
            } else {
                values[7] = seasonNum;
                values[8] = title;
                values[9] = airDate;
                break;
            }
        }
        return values;
    }
}