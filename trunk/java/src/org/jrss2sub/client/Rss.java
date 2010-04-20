package org.jrss2sub.client;
//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//IMPORT SUN
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
//IMPORT JRSS2SUB
import org.jrss2sub.util.Convert;
/**
 *
 * @author luca judge
 */
public class Rss {
    private SyndFeed feed;
    /**Costruttore
     *
     * @param f File da trasformare in xmlreader
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public Rss(File f) throws IllegalArgumentException, FeedException, IOException{
        SyndFeedInput input = new SyndFeedInput();
        feed = input.build(new XmlReader(f));
    }
    /**Costruttore per testare la connessione e validità del link-feed rss
     *
     * @param url
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public Rss(String url) throws IllegalArgumentException, FeedException, IOException{
        SyndFeedInput input = new SyndFeedInput();        
        feed = input.build(new XmlReader(new URL(url)));
    }
    /**effettua la lettura del feed e restituisce le informazioni
     *
     * @return arraylist del feed "parsato"
     */
    public ArrayList<Object[]> read(){
        List list = feed.getEntries();
        ArrayList<Object[]> alObj = new ArrayList<Object[]>();        
        for (int i=0; i<list.size(); i++) {
            Object[] array = new Object[5];
            SyndEntry entry = (SyndEntry) list.get(i);
            array[0] = entry.getLink();
            array[1] = Convert.dateToString(entry.getPublishedDate());
            array[2] = entry.getTitle();
            array[3] = false;
            alObj.add(array);
        }
        return alObj;
    }
}//end class