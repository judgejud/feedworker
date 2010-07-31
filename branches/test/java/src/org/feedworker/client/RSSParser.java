package org.feedworker.client;
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
import org.feedworker.util.Convert;

/**
 * 
 * @author luca judge
 */
public class RSSParser {

    private SyndFeed feed;

    /**Costruttore
     *
     * @param f File da trasformare in xmlreader
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public RSSParser(File f) throws IllegalArgumentException, FeedException,
            IOException {
        SyndFeedInput input = new SyndFeedInput();
        feed = input.build(new XmlReader(f));
    }
    /**Costruttore per testare la connessione e validità del link-feed rss
     *
     * @param feedURL
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public RSSParser(String feedURL) throws IllegalArgumentException,
            FeedException, IOException {
        SyndFeedInput input = new SyndFeedInput();
        feed = input.build(new XmlReader(new URL(feedURL)));
    }
    /**effettua la lettura del feed e restituisce le informazioni
     *
     * @return arraylist del feed "parsato"
     */
    public ArrayList<Object[]> read() {
        List rawEntryList = feed.getEntries();
        ArrayList<Object[]> structuredEntryList = new ArrayList<Object[]>();
        for (int i = 0; i < rawEntryList.size(); i++) {
            Object[] structuredEntry = new Object[5];
            SyndEntry rawEntry = (SyndEntry) rawEntryList.get(i);
            structuredEntry[0] = rawEntry.getLink();
            structuredEntry[1] = Convert.dateToString(rawEntry.getPublishedDate());
            structuredEntry[2] = rawEntry.getTitle();
            structuredEntry[3] = false;
            structuredEntryList.add(structuredEntry);
        }
        return structuredEntryList;
    }
}// end class