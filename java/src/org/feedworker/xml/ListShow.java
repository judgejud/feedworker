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
    
    public void writeList(Object[] array) throws IOException {
        for (int i=0; i<array.length; i++){
            Element rule = new Element(TAG_SHOW_ROOT);
            Object[] row = (Object[])array[i];
            rule.addContent(new Element(TAG_SHOW_NAME).setText(row[0].toString()));
            rule.addContent(new Element(TAG_SHOW_THUMBNAIL).setText(row[1].toString()));
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
    public TreeMap<String, Object[][]> initializeReader() throws JDOMException, IOException {
        TreeMap<String, Object[][]> map = convert();
        if (map==null){
            Object[][] array = null;
            if (sizeRootChildren() > 0){
                array = new Object[sizeRootChildren()][2];
                Iterator iter = iteratorRootChildren();
                int i=-1;
                while (iter.hasNext()) {
                    Element rule = (Element) iter.next();
                    array[++i][0] = rule.getChild(TAG_SHOW_NAME).getText();
                    array[i][1] = new ImageIcon(rule.getChild(TAG_SHOW_THUMBNAIL).getText());
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
    
    public static void main (String[] args){
        try {
            ListShow ls = new ListShow(new File("mylist.xml"), true);
            ls.convert();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}