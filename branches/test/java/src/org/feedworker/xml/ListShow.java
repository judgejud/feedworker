package org.feedworker.xml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.jdom.Element;
import org.jdom.JDOMException;
/**
 *
 * @author Luca
 */
public class ListShow extends AbstractXML{
    private final String TAG_SHOW_CATEGORY = "CATEGORY";
    private final String TAG_SHOW_ROOT = "SHOW";
    private final String TAG_SHOW_NAME = "NAME";
    private final String TAG_SHOW_THUMBNAIL = "THUMBNAIL";
    
    public ListShow(File f, boolean read) throws JDOMException, IOException{
        initialize(f, read);
    }
    
    public void writeList(TreeMap<String, Object[]> map) throws IOException {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()){
            String value = it.next();
            Element cat = new Element(TAG_SHOW_CATEGORY);
            cat.setAttribute(TAG_SHOW_CATEGORY, value);
            Object[] array = (Object[])map.get(value);
            for (int i=0; i<array.length; i++){
                Element rule = new Element(TAG_SHOW_ROOT);
                Object[] row = (Object[])array[i];
                rule.addContent(new Element(TAG_SHOW_NAME).setText(row[0].toString()));
                rule.addContent(new Element(TAG_SHOW_THUMBNAIL).setText(row[1].toString()));
                cat.addContent(rule);
            }
            root.addContent(cat);
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
    public TreeMap<String, Object[][]> initializeReader() throws JDOMException, IOException {
        TreeMap<String, Object[][]> map = convert();
        if (map==null){
            if (sizeRootChildren() > 0){
                map = new TreeMap<String, Object[][]>();
                Iterator iterCat = iteratorRootChildren();
                while (iterCat.hasNext()) {
                    Element cat = (Element) iterCat.next();
                    Object[][] array = new Object[cat.getChildren().size()][2];
                    Iterator iterShow = cat.getChildren().iterator();
                    int i=-1;
                    while (iterShow.hasNext()) {
                        Element rule = (Element) iterShow.next();
                        array[++i][0] = rule.getChild(TAG_SHOW_NAME).getText();
                        array[i][1] = new ImageIcon(rule.getChild(TAG_SHOW_THUMBNAIL).getText());
                    }
                    map.put(cat.getAttributeValue(TAG_SHOW_CATEGORY), array);
                }
            }
        }
        return map;
    }
    
    private TreeMap<String, Object[][]> convert(){
        TreeMap<String, Object[][]> map = null;
        if (sizeRootChildren() > 0){
            String elem = document.getRootElement().getChildren().get(0).toString();
            if (elem.equalsIgnoreCase("[Element: <SHOW/>]")){
                Object[][] array = new Object[sizeRootChildren()][2];
                Iterator iter = iteratorRootChildren();
                int i=-1;
                while (iter.hasNext()) {
                    Element rule = (Element) iter.next();
                    array[++i][0] = rule.getChild(TAG_SHOW_NAME).getText();
                    array[i][1] = new ImageIcon(rule.getChild(TAG_SHOW_THUMBNAIL).getText());
                }
                map = new TreeMap<String, Object[][]>();
                map.put("my list", array);
            }
        }
        return map;
    }
}