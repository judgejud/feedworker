package org.feedworker.client;

import java.io.IOException;
import java.net.URL;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author luca
 */
public class Itasa {
    
    private final String API_KEY = "apikey=436e566f3d09b217cf687fa5bad5effc";
    private final String URL_SHOW = "http://api.italiansubs.net/api/rest/show/shows/?";
    private final String TAG_SHOW_ID = "show_id";
    private final String AND = "show_id";
    
    private Document document;
    
    private void singleShow(int id, boolean all) throws JDOMException, IOException{
        String url = URL_SHOW + API_KEY + AND + TAG_SHOW_ID + "=" + id;
        if (all)
            url += AND + "fields[]=all";
        build(url);
        
    }

    private void build(String url) throws JDOMException, IOException {
        document = new SAXBuilder().build(new URL(url));
    }
}