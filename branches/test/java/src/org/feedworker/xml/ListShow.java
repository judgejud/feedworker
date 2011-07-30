package org.feedworker.xml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Administrator
 */
public class ListShow extends AbstractXML{
    private final String TAG_SHOW_ROOT = "SHOW";
    private final String TAG_SHOW_NAME = "NAME";
    
    public ListShow(File f, boolean read) throws JDOMException, IOException{
        initialize(f, read);
    }
    
    public void writeList(Object[] array) throws IOException {
        for (int i=0; i<array.length; i++){
            Element rule = new Element(TAG_SHOW_ROOT);
            rule.addContent(new Element(TAG_SHOW_NAME).setText(array[i].toString()));
            root.addContent(rule);
        }
        write();
    }
    
    /**Inizializza la lettura dell'xml e restituisce la map ordinata come
     * treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public Object[] initializeReader() throws JDOMException, IOException {
        Object[] array = null;
        if (sizeRootChildren() > 0){
            array = new Object[sizeRootChildren()];
            Iterator iter = iteratorRootChildren();
            int i=-1;
            while (iter.hasNext()) {
                Element rule = (Element) iter.next();
                array[++i] = rule.getChild(TAG_SHOW_NAME).getText();
            }
        }
        return array;
    }
}