package org.feedworker.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.feedworker.object.Show;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author luca
 */
public class Itasa extends AbstractXML{
    private final String OPERATOR_AND = "&";
    private final String OPERATOR_EQUAL = "%3D";
    private final String API_KEY = "apikey=436e566f3d09b217cf687fa5bad5effc";    
    private final String PARAM_FIELDS = "fields[]=";
    private final String PARAM_SEARCH_FIELD = "search_field[]=";
    private final String PARAM_SEARCH_OP = "search_op=";
    private final String PARAM_FIELDS_ALL = "fields[]=all";
    private final String PARAM_LIMIT = "limit=";
    private final String PARAM_OFFSET = "offset=";
    private final String PARAM_SHOW_ID = "show_id=";
    private final String PARAM_VALUE = "value=";
    private final String STATUS_SUCCESS = "success";
    private final String STATUS_FAIL = "fail";
    private final String TAG_STATUS = "status";
    private final String TAG_ERROR = "error";
    private final String TAG_COUNT = "count";
    private final String TAG_SHOW_PLOT = "plot";
    private final String TAG_SHOW_PLOT_LANG = "plot_lang";
    private final String TAG_SHOW_BANNER = "banner";
    private final String TAG_SHOW_LASTUPDATE = "lastupdate";
    private final String TAG_SHOW_STARTED = "started";
    private final String TAG_SHOW_ENDED = "ended";
    private final String TAG_SHOW_COUNTRY = "country";
    private final String TAG_SHOW_SEASON = "season";
    private final String TAG_SHOW_NETWORK = "network";
    private final String TAG_SHOW_NAME = "name";
    private final String TAG_SHOW_ID = "id";
    private final String TAG_SHOW_ID_TVDB = "id_tvdb";
    private final String TAG_SHOW_ID_TVRAGE = "id_tvrage";
    private final String TAG_SHOW_FOLDER_THUMB = "folder_thumb";
    private final String URL_BASE = "http://api.italiansubs.net/api/rest";
    private final String URL_SHOW_SINGLE = URL_BASE + "/show/show/?";
    private final String URL_SHOW_LIST = URL_BASE + "/show/shows/?";
    private final String URL_SHOW_SEARCH = URL_BASE + "/show/search/?";
    
    private String status, error;
    
    public void showSingleAll(int id, boolean flag_actors) throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        params.add(PARAM_SHOW_ID + id);
        //params.add(PARAM_FIELDS_ALL);
        buildUrl(composeUrl(URL_SHOW_SINGLE, params));
        checkStatus();
        if (isStatusSuccess()){
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String name = item.getChild(TAG_SHOW_NAME).getText();
                //String tvdb = item.getChild(TAG_SHOW_ID_TVDB).getText();
                //String tvrage = item.getChild(TAG_SHOW_ID_TVRAGE).getText();
                
                String plot = item.getChild(TAG_SHOW_PLOT).getText();
                String banner = item.getChild(TAG_SHOW_BANNER).getText();
                ArrayList actors = null;
                if (flag_actors){}
                
            }
        }
    }
    
    public ArrayList<String> searchIdSingleByTvrage(int tvrage) throws 
                                                    JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        params.add(PARAM_VALUE + tvrage);
        params.add(PARAM_SEARCH_FIELD + TAG_SHOW_ID_TVRAGE);
        params.add(PARAM_SEARCH_OP + OPERATOR_EQUAL);
        params.add(PARAM_FIELDS + TAG_SHOW_ID);
        params.add(PARAM_FIELDS + TAG_SHOW_ID_TVDB);
        
        buildUrl(composeUrl(URL_SHOW_SEARCH, params));
        checkStatus();
        ArrayList<String> list = null;
        if (isStatusSuccess()){
            if (getResponseCount()>0){
                Element item = (Element) getDescendantsZero(2).next();
                list = new ArrayList<String>();
                list.add(item.getChild(TAG_SHOW_ID).getText());
                list.add(item.getChild(TAG_SHOW_ID_TVDB).getText());
            }
        } else 
            System.out.println("show list: "+ error);
        return list;
    }

    void showSearch() throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        
        buildUrl(composeUrl(URL_SHOW_SEARCH, params));
        checkStatus();
        if (isStatusSuccess()){
            Iterator iter = iteratorRootChildren();
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                System.out.println(item.getName());
                
            }
        } else 
            System.out.println("show list: "+ error);
        
    }
    
    public ArrayList<Show> showListNoLimit() throws JDOMException, IOException {
        return showList(0, 0, true);
    }
    
    public ArrayList<Show> showListNameIdNoLimit() throws JDOMException, IOException {
        return showList(0, 0, false);
    }
    
    
    private ArrayList<Show> showList(int limit, int offset, boolean flag_all) throws 
                                                    JDOMException, IOException {
        ArrayList<Show> container = null;
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        if (limit>0)
            params.add(PARAM_LIMIT + limit);
        if (offset>0)
            params.add(PARAM_OFFSET + offset);
        if (flag_all)
            params.add(PARAM_FIELDS_ALL);
        else{
            params.add(PARAM_FIELDS + TAG_SHOW_ID);
            params.add(PARAM_FIELDS + TAG_SHOW_NAME);
            params.add(PARAM_FIELDS + TAG_SHOW_ID_TVDB);
            params.add(PARAM_FIELDS + TAG_SHOW_ID_TVRAGE);
        }
        buildUrl(composeUrl(URL_SHOW_LIST, params));
        checkStatus();
        if (isStatusSuccess()){
            container = new ArrayList<Show>();
            Iterator iter =  getDescendantsZero(2);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                Show s;
                String id = item.getChild(TAG_SHOW_ID).getText();
                System.out.println(id);
                String name = item.getChild(TAG_SHOW_NAME).getText();
                String tvdb = checkNPE(item.getChild(TAG_SHOW_ID_TVDB));
                String tvrage = checkNPE(item.getChild(TAG_SHOW_ID_TVRAGE));
                if (flag_all) {
                    String plot = checkNPE(item.getChild(TAG_SHOW_PLOT));
                    String banner = checkNPE(item.getChild(TAG_SHOW_BANNER));
                    String icon = checkNPE(item.getChild(TAG_SHOW_FOLDER_THUMB));
                    Iterator genres = item.getChild("genres").getChildren().iterator();
                    ArrayList<String> gen = new ArrayList<String>();
                    while (genres.hasNext()){
                        gen.add(((Element) genres.next()).getText());
                    }                
                    s = new Show(name, id, tvdb, tvrage, plot, banner, icon, gen);
                } else
                    s = new Show(name, id, tvdb, tvrage);
                container.add(s);
            }
        } else 
            System.out.println("show list: "+ error);
        return container;
    }
    
    /**Compone la url compresa di parametri
     * 
     * @param url url base
     * @param params parametri da aggiungere per la query
     * @return 
     */
    private String composeUrl(final String url, ArrayList params){
        String newUrl = url + API_KEY;
        for (int i=1; i<params.size(); i++)
            newUrl+= OPERATOR_AND + params.get(i).toString();
        System.out.println(newUrl);
        return newUrl;
    }
    
    /**controlla lo status presente nel document
     * se falso preleva/imposta il msg d'error
     */
    private void checkStatus(){
        Iterator iterator = document.getContent().iterator();
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            status = item.getChild(TAG_STATUS).getText();
            if (isStatusFail())
                error = item.getChild(TAG_ERROR).getText();
        }
    }
    
    private int getResponseCount(){
        Element item = (Element) getDescendantsZero(0).next();
        return Integer.parseInt(item.getChild(TAG_COUNT).getText());
    }
    
    /**Controlla se lo status è positivo
     * 
     * @return
     */
    private boolean isStatusSuccess(){
        return status.equals(STATUS_SUCCESS);
    }
    
    /**Controlla se lo status è fail
     * 
     * @return 
     */
    private boolean isStatusFail(){
        return status.equals(STATUS_FAIL);
    }
    
    public static void main (String[] args){
        Itasa i = new Itasa();
        try {
            //i.showSingleAll(1363, false);
            i.searchIdSingleByTvrage(24996);
            //i.showList(5, 0, false);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}