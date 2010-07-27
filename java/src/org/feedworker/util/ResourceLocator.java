package org.feedworker.util;
//IMPORT JAVA
import java.io.File;
import java.net.URL;
/**Classe Locazione Risorse
 *
 * @author Luca
 */
public class ResourceLocator {    
    private static String curDir ;
    private final static String RESOURCES = "/org/jrss2sub/resource/";
    /**Restituisce il percorso di lavoro corrente comprensivo di primo separatore
     *
     * @return percorso lavoro
     */
    public static String getWorkspace() {
        return curDir;
    }
    /**imposta il percorso di lavoro di default
     *
     */
    public static void setWorkspace() {        
        curDir = new File(System.getProperty("user.dir")).toURI().getPath();
    }
    /**Restituisce il path delle risorse
     *
     * @return path resources
     */
    public static String getResourcePath(){
        return RESOURCES;
    }   
    /**Converte un percorso stringa in una URL
     *
     * @param path percorso
     * @return url
     */
    public static URL convertStringToURL(String path) {
        return ResourceLocator.class.getResource(path);
    }
}//end class