package org.feedworker.core;

//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.feedworker.util.Common;
import org.jfacility.java.lang.Lang;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * 
 * @author luca judge
 */
public class RssParser {

    private SyndFeed feed;

    /**Costruttore
     *
     * @param f File da trasformare in xmlreader
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public RssParser(File f) throws IllegalArgumentException, FeedException, ParsingFeedException,
            IOException {
        SyndFeedInput input = new SyndFeedInput();
        feed = input.build(new XmlReader(f));
    }

    /**
     * Costruttore per testare la connessione e validità del link-feed rss
     *
     * @param feedURL
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public RssParser(String feedURL) throws IllegalArgumentException,
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
            SyndEntry rawEntry = (SyndEntry) rawEntryList.get(i);
            String temp = rawEntry.getTitle();
            if (Lang.verifyTextNotNull(temp) && !temp.equalsIgnoreCase(" ")){
                Object[] structuredEntry = new Object[5];
                structuredEntry[0] = rawEntry.getLink();
                structuredEntry[1] = Common.dateTimeString(rawEntry.getPublishedDate());
                structuredEntry[2] = temp;
                structuredEntry[3] = false;
                structuredEntryList.add(structuredEntry);
            }
        }
        return structuredEntryList;
    }
}// end class