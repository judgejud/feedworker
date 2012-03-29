package org.feedworker.util;

//IMPORT JAVA
import java.io.File;
import java.net.URL;

import org.jfacility.java.lang.SystemProperty;

/**
 * Classe Locazione Risorse
 * 
 * @author Luca
 */
public class ResourceLocator {

    private static final File FILE_RULE = new File("xml" + File.separator + "rules.xml");
    private static final File FILE_CALENDAR = new File("xml" + File.separator + "calendar.xml");
    private static final File FILE_REMINDER = new File("xml" + File.separator + "reminder.xml");
    private static final File FILE_MYLIST = new File("xml" + File.separator + "mylist.xml");
    private static final File FILE_ITASA = new File("xml" + File.separator + "itasa.xml");
    private static final File FILE_LINK_ITASA = new File("xml" + File.separator + "link_itasa.xml");
    private static final String RESOURCES = "/org/feedworker/resource/";
    private static final String EMOTICONS = RESOURCES + "emoticon/";
    private static final String THUMBNAIL_SHOW = "thumbnail";
    private static String curDir;

    /**
     * Restituisce il percorso di lavoro corrente comprensivo di primo
     * separatore
     *
     * @return percorso lavoro
     */
    public static String getWorkspace() {
        return curDir;
    }

    public static String getThumbnailShows() {
        return curDir + THUMBNAIL_SHOW + File.separator;
    }

    /**
     * imposta il percorso di lavoro di default
     *
     */
    public static void setWorkspace() {
        curDir = SystemProperty.getFileUserDir().toURI().getPath();
    }

    /**
     * Restituisce il path delle risorse
     *
     * @return path resources
     */
    public static String getResourcePath() {
        return RESOURCES;
    }

    public static String getEmoticonPath() {
        return EMOTICONS;
    }

    /**
     * Converte un percorso stringa in una URL
     *
     * @param path
     *            percorso
     * @return url
     */
    public static URL convertStringToURL(String path) {
        return ResourceLocator.class.getResource(path);
    }

    public static File getFILE_CALENDAR() {
        return FILE_CALENDAR;
    }

    public static File getFILE_ITASA() {
        return FILE_ITASA;
    }

    public static File getFILE_LINK_ITASA() {
        return FILE_LINK_ITASA;
    }

    public static File getFILE_MYLIST() {
        return FILE_MYLIST;
    }

    public static File getFILE_REMINDER() {
        return FILE_REMINDER;
    }

    public static File getFILE_RULE() {
        return FILE_RULE;
    }
}// end class