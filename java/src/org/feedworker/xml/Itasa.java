package org.feedworker.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author luca
 */
public class Itasa extends AbstractXML{
    private final String AND = "&";
    private final String API_KEY = "apikey=436e566f3d09b217cf687fa5bad5effc";    
    private final String PARAM_FIELDS = "fields[]=";
    private final String PARAM_FIELDS_ALL = "fields[]=all";
    private final String STATUS_SUCCESS = "success";
    private final String STATUS_FAIL = "fail";
    private final String TAG_STATUS = "status";
    private final String TAG_ERROR = "error";
    private final String TAG_SHOW_PLOT = "plot";
    private final String TAG_SHOW_BANNER = "banner";
    private final String TAG_SHOW_NAME = "name";
    private final String TAG_SHOW_ID_TVDB = "id_tvdb";
    private final String TAG_SHOW_ID_TVRAGE = "id_tvrage";
    
    
    
    private final String URL_SHOW_SINGLE = "http://api.italiansubs.net/api/rest/show/show/?";
    private final String URL_SHOW_LIST = "http://api.italiansubs.net/api/rest/show/shows/?";
    private final String URL_SHOW_SEARCH = "http://api.italiansubs.net/api/rest/show/search/?";
    
    
    private String status, error;
    
    public void showSingleAll(int id) throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        params.add("show_id=" + id);
        params.add(PARAM_FIELDS_ALL);
        buildUrl(composeUrl(URL_SHOW_SINGLE, params));
        checkStatus();
        if (isStatusSuccess()){
            //TODO: decidere la/e struttura/e contenitore prima di andare avanti
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String name = item.getChild(TAG_SHOW_NAME).getText();
                String tvdb = item.getChild(TAG_SHOW_ID_TVDB).getText();
                String tvrage = item.getChild(TAG_SHOW_ID_TVRAGE).getText();
                
                String plot = item.getChild(TAG_SHOW_PLOT).getText();
                String banner = item.getChild(TAG_SHOW_BANNER).getText();
                
            }
        }
    }

    void showSearch() throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        
        buildUrl(composeUrl(URL_SHOW_SEARCH, params));
        checkStatus();
        if (isStatusSuccess()){
            Iterator iter = super.iteratorRootChildren();
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                System.out.println(item.getName());
                
            }
        }
    }
    
    public void showList(int limit, int offset) throws JDOMException, IOException {
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        if (limit>0)
            params.add("limit="+limit);
        if (offset>0)
            params.add("offset="+offset);
        
        buildUrl(composeUrl(URL_SHOW_LIST, params));
        checkStatus();
        if (isStatusSuccess()){
            Element temp = (Element) document.getRootElement().getChildren().get(0);
            Iterator iter =  ((Element)temp.getChildren().get(0)).getChildren().iterator();
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String name = item.getChild(TAG_SHOW_NAME).getText();
                System.out.println(name);
                
            }
        } else 
            System.out.println("show list: "+ error);
    }
    
    private String composeUrl(final String url, ArrayList params){
        String newUrl = url + API_KEY;
        for (int i=1; i<params.size(); i++)
            newUrl+= AND + params.get(i).toString();
        System.out.println(newUrl);
        return newUrl;
    }
    
    private void checkStatus(){
        Iterator iterator = document.getContent().iterator();
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            status = item.getChild(TAG_STATUS).getText();
            if (isStatusFail())
                error = item.getChild(TAG_ERROR).getText();
        }
    }
    
    private boolean isStatusSuccess(){
        return status.equals(STATUS_SUCCESS);
    }
    
    private boolean isStatusFail(){
        return status.equals(STATUS_FAIL);
    }
    
    public static void main (String[] args){
        Itasa i = new Itasa();
        try {
            //i.showSingleAll(1363);
            i.showList(50, 0);
        } catch (JDOMException ex) {
            
        } catch (IOException ex) {
            
        }
    }
}