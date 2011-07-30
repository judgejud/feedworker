package org.feedworker.xml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Administrator
 */
public class ItasaOffline extends AbstractXML{
    private final String TAG_ITASA_ROOT = "SHOW";
    private final String TAG_ITASA_ID = "ID";
    private final String TAG_ITASA_NAME = "NAME";
    
    public ItasaOffline(File f, boolean read) throws JDOMException, IOException{
        initialize(f, read);
    }
    
    public void writeMap(TreeMap<String, String> map) throws IOException {
        if (map.size() > 0) {
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Element rule = new Element(TAG_ITASA_ROOT);
                rule.addContent(new Element(TAG_ITASA_NAME).setText(key));
                rule.addContent(new Element(TAG_ITASA_ID).setText(map.get(key)));
                root.addContent(rule);
            }
            write();
        }
    }
    
    /**Inizializza la lettura dell'xml e restituisce la map ordinata come
     * treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public TreeMap<String, String> initializeReader() throws JDOMException, IOException {
        TreeMap<String, String> map = null;
        if (sizeRootChildren() > 0){
            map = new TreeMap<String, String>();
            Iterator iter = iteratorRootChildren();
            while (iter.hasNext()) {
                Element rule = (Element) iter.next();
                String name = rule.getChild(TAG_ITASA_NAME).getText();
                String id = rule.getChild(TAG_ITASA_ID).getText();
                map.put(name, id);
            }
        }
        return map;
    }
}