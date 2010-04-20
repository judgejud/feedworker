//TODO: provare a usare XPATH
package org.jrss2sub.client;
//IMPORT JAVA
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
//IMPORT JRSS2SUB
import org.jrss2sub.util.FilterSub;
//IMPORT JDOM
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**Scrive e legge su/da file xml le regole di destinazione
 *
 * @author luca
 */
class Xml {
    //VARIABLES PRIVATE FINAL
    private final File NAMEFILE = new File("roles.xml");
    private final String TAG_ROLE = "ROLE";
    private final String TAG_NAME = "NAME";
    private final String TAG_SEASON = "SEASON";
    private final String TAG_QUALITY = "QUALITY";
    private final String TAG_PATH = "PATH";
    //VARIABLES PRIVATE
    private Element root;
    private Document document;    
    /**Scrive le regole su file xml
     *
     * @param map
     * @throws IOException
     */
    public void writeMap(TreeMap<FilterSub, String> map) throws IOException{
        if (map.size()>0){
            Iterator it = map.keySet().iterator();
            initializeWriter();
            while (it.hasNext()) {
                FilterSub key = (FilterSub) it.next();
                String value = map.get(key);
                addRule(key.getName(), key.getSeason(), key.getQuality(), value);
            }
            write();
        }
    }
    /**inizializza il documento*/
    private void initializeWriter(){
        root = new Element("ROOT");
        document = new Document(root);
    }
    /**Aggiunge regola
     *
     * @param _name nome serie
     * @param _season stagione
     * @param _version versione
     * @param _path percorso
     */
    private void addRule(String _name, String _season, String _version, String _path){
        Element role = new Element(TAG_ROLE);
        Element name = new Element(TAG_NAME);
        name.setText(_name);
        Element season = new Element(TAG_SEASON);
        season.setText(_season);
        Element quality = new Element(TAG_QUALITY);
        quality.setText(_version);
        Element path = new Element(TAG_PATH);
        path.setText(_path);
        role.addContent(name);
        role.addContent(season);
        role.addContent(quality);
        role.addContent(path);
        root.addContent(role);
    }
    /**Scrive l'xml
     *
     * @throws IOException
     */
    private void write() throws IOException{
        //Creazione dell'oggetto XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        //Imposto il formato dell'outputter come "bel formato"
        outputter.setFormat(Format.getPrettyFormat());
        //Produco l'output sul file xml.foo
        outputter.output(document, new FileOutputStream(NAMEFILE));
    }
    /**Inizializza la lettura dell'xml e restituisce la map ordinata come treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public TreeMap<FilterSub, String> initializeReader() throws JDOMException, IOException{
        TreeMap<FilterSub, String> map = null;
        if (NAMEFILE.exists()){
            //Creo un SAXBuilder e con esso costruisco un document
            document = new SAXBuilder().build(NAMEFILE);
            int size = document.getRootElement().getChildren().size();
            if (size>0){
                map = new TreeMap<FilterSub, String>();
                Iterator iterator = document.getRootElement().getChildren().iterator();
                while(iterator.hasNext()){
                    Element role = (Element)iterator.next();
                    String name = role.getChild(TAG_NAME).getText();
                    String season = role.getChild(TAG_SEASON).getText();
                    String quality = role.getChild(TAG_QUALITY).getText();
                    String path = role.getChild(TAG_PATH).getText();
                    map.put(new FilterSub(name, season, quality), path);
                }
            }
        }
        return map;
    }
}