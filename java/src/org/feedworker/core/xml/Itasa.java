package org.feedworker.core.xml;

import java.io.IOException;
import java.util.ArrayList;
import org.jdom.JDOMException;

/**
 *
 * @author luca
 */
public class Itasa extends AbstractXML{
    
    private final String API_KEY = "apikey=436e566f3d09b217cf687fa5bad5effc";
    private final String URL_SHOW = "http://api.italiansubs.net/api/rest/show/shows/?";
    private final String URL_SHOW_SEARCH = "http://api.italiansubs.net/api/rest/show/search/?";
    private final String AND = "&";
    private final String PARAM_FIELDS = "fields[]=";
    private final String PARAM_FIELDS_ALL = "fields[]=all";
    
    void singleShowAll(int id) throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        params.add("show_id=" + id);
        params.add(PARAM_FIELDS_ALL);
        buildUrl(composeUrl(URL_SHOW, params));
        
    }    

    void searchShow() throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(API_KEY);
        
        buildUrl(composeUrl(URL_SHOW_SEARCH, params));
    }
    
    private String composeUrl(final String url, ArrayList params){
        String newUrl = url + API_KEY;
        for (int i=1; i<params.size(); i++)
            newUrl+= AND + params.get(i).toString();
        return newUrl;
    }
    
    public static void main (String[] args){
        Itasa i = new Itasa();
        try {
            i.singleShowAll(1234);
        } catch (JDOMException ex) {
            
        } catch (IOException ex) {
            
        }
    }
}