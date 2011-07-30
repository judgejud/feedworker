package org.feedworker.xml;

import java.io.IOException;
import java.net.ConnectException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.feedworker.util.Common;
import org.feedworker.util.Translate;
import org.jfacility.java.lang.Lang;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author luca
 */
public class TvRage extends AbstractQueryXML{
    private final String URL_TVRAGE = "http://services.tvrage.com/feeds"; 
    private final String URL_TVRAGE_DETAILED_SEARCH = URL_TVRAGE +
                                                    "/full_search.php?show=";
    private final String URL_TVRAGE_EPISODE_LIST = URL_TVRAGE + 
                                                    "/episode_list.php?sid=";
    private final String URL_TVRAGE_SHOW_INFO = URL_TVRAGE + "/showinfo.php?sid=";
    private final String TAG_SHOW_ID = "showid";
    private final String TAG_NAME = "name";
    private final String TAG_SEASON = "seasons";
    private final String TAG_STATUS = "status";
    private final String TAG_AIRDAY = "airday";
    private final String TAG_AIRDATE = "airdate";
    private final String TAG_SEASON_NUM = "seasonnum";
    private final String TAG_TITLE = "title";
    private final String TAG_SHOWNAME = "showname";
    private final String TAG_EP_NUM = "epnum";

    public ArrayList<Object[]> readingDetailedSearch_byShow(String show, boolean stop, 
                                                                    boolean statusString)
                throws JDOMException, IOException{
        buildUrl(URL_TVRAGE_DETAILED_SEARCH + show);
        ArrayList<Object[]> matrix = null;
        if (sizeRootChildren()>0){
            matrix = new ArrayList<Object[]>();
            Iterator iterator = iteratorRootChildren();
            while (iterator.hasNext()) {
                Element item = (Element) iterator.next();
                String id = item.getChild(TAG_SHOW_ID).getText();
                String name = item.getChild(TAG_NAME).getText();
                String season = item.getChild(TAG_SEASON).getText();
                String status = item.getChild(TAG_STATUS).getText();
                String airday;
                try{
                    airday = Translate.Day(item.getChild(TAG_AIRDAY).getText());
                } catch(NullPointerException e){
                    airday = "";
                }
                Object[] temp;
                if (statusString)
                    temp = new Object[]{id, name, season, status, airday};
                else
                    temp = new Object[]{id, name, season, Common.getStatus(status), airday};
                matrix.add(temp);
                if (stop)
                    break;
            }
        }
        return matrix;
    }

    public Object[] readingEpisodeList_byID(String id, String season) throws 
            ConnectException, JDOMException, IOException, IndexOutOfBoundsException{
        buildUrl(URL_TVRAGE_EPISODE_LIST + id);
        List seasons = ((Element) document.getRootElement().getChildren().get(2))
                .getChildren();
        int last = Lang.stringToInt(season);        
        List tempList = null;
        while (tempList==null){
            try{
                tempList = ((Element) seasons.get(--last)).getChildren();
            } catch(IndexOutOfBoundsException e){}
        }
        season = Lang.intToString(last+1);
        Iterator iter = tempList.iterator();
        Date yesterday = Common.yesterdayDate();
        Object[] values = null;
        if (iter.hasNext())
            values = new Object[10];
        while (iter.hasNext()){
            Element item = (Element) iter.next();
            String airDate = item.getChild(TAG_AIRDATE).getText();
            Date d = null, tempDate = null;
            try {
                tempDate = Common.stringAmericanToDate(airDate);
                d = tempDate;
            } catch (ParseException ex) {}
            if (airDate.equalsIgnoreCase("0000-00-00") || 
                    airDate.substring(5).equalsIgnoreCase("00-00")){
                tempDate = yesterday;
                d = null;
            }
            String episodeSeason = null; 
            try{
                episodeSeason = item.getChild(TAG_SEASON_NUM).getText();
                String title = item.getChild(TAG_TITLE).getText();
                if (tempDate.before(yesterday)){
                    values[4] = season + "x" + episodeSeason;
                    values[5] = title;
                    values[6] = d;
                } else {
                    values[7] = season + "x" + episodeSeason;
                    values[8] = title;
                    values[9] = d;
                    break;
                }
            } catch (NullPointerException npe){}
        }
        return values;
    }
    
    public Object[] readingAllEpisodeList_byID(String id) throws 
            ConnectException, JDOMException, IOException, IndexOutOfBoundsException{
        buildUrl(URL_TVRAGE_EPISODE_LIST + id);
        Iterator seasons = ((Element) document.getRootElement().getChildren().get(2)).
                                                            getChildren().iterator();
        Object[] values = new Object[2];
        ArrayList<String> alNames = new ArrayList<String>();
        ArrayList<ArrayList<String[]>> alSeasons = new ArrayList<ArrayList<String[]>>();
        while (seasons.hasNext()){
            Element season = (Element) seasons.next();
            String number = season.getAttributeValue("no");
            Iterator episodes = season.getChildren().iterator();
            alNames.add("Season "+number);
            ArrayList<String[]> alEpisodes = new ArrayList<String[]>();
            while (episodes.hasNext()){
                String[] row = new String[4];
                Element episode = (Element) episodes.next();
                //TODO: 24 nullpointerexception?
                row[0] = episode.getChild(TAG_EP_NUM).getText();
                row[1] = episode.getChild(TAG_SEASON_NUM).getText();
                row[2] = episode.getChild(TAG_AIRDATE).getText();
                row[3] = episode.getChild(TAG_TITLE).getText();
                alEpisodes.add(row);
            }
            alSeasons.add(alEpisodes);
        }
        values[0]=alNames;
        values[1]=alSeasons;
        return values;
    }
    
    public Object[] showInfo_byID(String id) throws JDOMException, IOException{
        buildUrl(URL_TVRAGE_SHOW_INFO + id);
        Iterator iterator = document.getContent().iterator();
        Object[] show = new Object[5];
        show[0] = id;
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            show[1] = item.getChild(TAG_SHOWNAME).getText();
            show[2] = item.getChild(TAG_SEASON).getText();
            show[3] = item.getChild(TAG_STATUS).getText();
            try{
                show[4] = Translate.Day(item.getChild(TAG_AIRDAY).getText());
            } catch(NullPointerException e){
                show[4] ="";
            }
        }
        return show;
    }
    
    public static void main (String args[]){
        TvRage t = new TvRage();
        try {
            //t.readingAllEpisodeList_byID("2870");
            //t.readingAllEpisodeList_byID("2932");
            t.readingAllEpisodeList_byID("2874");
        } catch (ConnectException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }
}