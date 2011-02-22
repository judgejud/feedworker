package org.feedworker.xml;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.feedworker.util.Common;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jfacility.java.lang.Lang;

/**
 *
 * @author luca
 */
public class TvRage extends AbstractXML{
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
                    airday = convertDayIngToIta(item.getChild(TAG_AIRDAY).getText());
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
            JDOMException, IOException, IndexOutOfBoundsException{
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
            String seasonNum = item.getChild(TAG_SEASON_NUM).getText();
            String title = item.getChild(TAG_TITLE).getText();
            if (tempDate.before(yesterday)){
                values[4] = season + "x" + seasonNum;
                values[5] = title;
                values[6] = d;
            } else {
                values[7] = season + "x" + seasonNum;
                values[8] = title;
                values[9] = d;
                break;
            }
        }
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
                show[4] = convertDayIngToIta(item.getChild(TAG_AIRDAY).getText());
            } catch(NullPointerException e){
                show[4] ="";
            }
        }
        return show;
    }
    
    /**Trduce il giorno dall'inglese all'italiano
     * 
     * @param ing giorno inglese
     * @return giorno italiano
     */
    private String convertDayIngToIta(String ing){
        if (ing.toLowerCase().equalsIgnoreCase("sunday"))
            return "Domenica";
        else if(ing.toLowerCase().equalsIgnoreCase("monday"))
            return "Lunedì";
        else if(ing.toLowerCase().equalsIgnoreCase("tuesday"))
            return "Martedì";
        else if(ing.toLowerCase().equalsIgnoreCase("wednesday"))
            return "Mercoledì";
        else if(ing.toLowerCase().equalsIgnoreCase("thursday"))
            return "Giovedì";
        else if(ing.toLowerCase().equalsIgnoreCase("friday"))
            return "Venerdì";
        else if(ing.toLowerCase().equalsIgnoreCase("saturday"))
            return "Sabato";
        return "";
    }
}