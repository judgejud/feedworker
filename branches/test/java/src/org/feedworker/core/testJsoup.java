package org.feedworker.core;

import java.io.IOException;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Administrator
 */
public class testJsoup {
    private final static String ADDRESS_ITASA = "http://www.italiansubs.net/index.php";
    private final static String ADDRESS_SUBSF = "http://www.subsfactory.it/";
    
    private static Document connectItasa(String user, String pwd, int timeout) throws IOException{
        TreeMap<String, String> keyvals = new TreeMap<String, String>();
        keyvals.put("username", user);
        keyvals.put("passwd", pwd);
        keyvals.put("remember", "yes");
        keyvals.put("Submit", "Login");
        //keyvals.put("option", "com_user");
        keyvals.put("task", "login");
        keyvals.put("silent", "true");
        return Jsoup.connect(ADDRESS_ITASA).data(keyvals).timeout(timeout).post();
    }
    
    private static Document connectSubsfactory(int timeout) throws IOException{
        return Jsoup.connect(ADDRESS_SUBSF).timeout(timeout).get();
    }
    
    public static void main (String[] args){
        try {
            Document doc = connectItasa("judge", "qwerty", 5000);
            Elements div = doc.getElementsByClass("moduletable");
            for (Element link : div)
                System.out.println(link.text());
            //Document doc = connectSubsfactory(5000);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}