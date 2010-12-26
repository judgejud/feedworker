package org.feedworker.util;

//IMPORT JAVA
import java.net.URL;

import org.jfacility.java.lang.SystemProperty;

/**
 * Classe Locazione Risorse
 * 
 * @author Luca
 */
public class ResourceLocator {

    private static String curDir;
    private final static String RESOURCES = "/org/feedworker/resource/";

    /**
     * Restituisce il percorso di lavoro corrente comprensivo di primo
     * separatore
     *
     * @return percorso lavoro
     */
    public static String getWorkspace() {
        return curDir;
    }

    /**
     * imposta il percorso di lavoro di default
     *
     */
    public static void setWorkspace() {
        curDir = SystemProperty.getFileUserDir().toURI().getPath();
        //System.out.println(curDir);
    }

    /**
     * Restituisce il path delle risorse
     *
     * @return path resources
     */
    public static String getResourcePath() {
        return RESOURCES;
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
}// end class
