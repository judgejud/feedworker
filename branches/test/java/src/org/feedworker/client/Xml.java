package org.feedworker.client;
//IMPORT JAVA
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
//IMPORT FEEDWORKER
import org.feedworker.util.FilterSub;
//IMPORT JDOM
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * Scrive e legge su/da file xml le regole di destinazione
 * 
 * @author luca
 */
class Xml {
    // VARIABLES PRIVATE FINAL

    private final File FILE_NAME = new File("rules.xml");
    private final String RULE_TAG = "RULE";
    private final String NAME_TAG = "NAME";
    private final String SEASON_TAG = "SEASON";
    private final String QUALITY_TAG = "QUALITY";
    private final String PATH_TAG = "PATH";
    private final String DAY_TAG = "DAY";
    private final String STATUS_TAG = "STATUS";
    private final String RENAME_TAG = "RENAME";

    // VARIABLES PRIVATE
    private Element root;
    private Document document;

    /**
     * Scrive le regole su file xml
     *
     * @param map
     * @throws IOException
     */
    public void writeMap(TreeMap<FilterSub, String> map) throws IOException {
        if (map.size() > 0) {
            Iterator it = map.keySet().iterator();
            initializeWriter();
            while (it.hasNext()) {
                FilterSub key = (FilterSub) it.next();
                String value = map.get(key);
                addRule(key.getName(), key.getSeason(), key.getQuality(),
                        value, key.getStatus(), key.getDay());
            }
            write();
        }
    }

    /** inizializza il documento */
    private void initializeWriter() {
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
    private void addRule(String _name, String _season, String _version,
            String _path, String _status, String _day) {
        Element role = new Element(RULE_TAG);
        Element name = new Element(NAME_TAG);
        name.setText(_name);

        Element season = new Element(SEASON_TAG);
        season.setText(_season);

        Element quality = new Element(QUALITY_TAG);
        quality.setText(_version);

        Element path = new Element(PATH_TAG);
        path.setText(_path);

        Element status = new Element(STATUS_TAG);
        status.setText(_status);

        Element day = new Element(DAY_TAG);
        day.setText(_day);

        role.addContent(name);
        role.addContent(season);
        role.addContent(quality);
        role.addContent(path);
        role.addContent(status);
        role.addContent(day);
        root.addContent(role);
    }

    /**
     * Scrive l'xml
     *
     * @throws IOException
     */
    private void write() throws IOException {
        // Creazione dell'oggetto XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        // Imposto il formato dell'outputter come "bel formato"
        outputter.setFormat(Format.getPrettyFormat());
        // Produco l'output sul file xml.foo
        outputter.output(document, new FileOutputStream(FILE_NAME));
    }

    /**Inizializza la lettura dell'xml e restituisce la map ordinata come treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public TreeMap<FilterSub, String> initializeReader() throws JDOMException, IOException {
        TreeMap<FilterSub, String> map = null;
        if (FILE_NAME.exists()) {
            // Creo un SAXBuilder e con esso costruisco un document
            document = new SAXBuilder().build(FILE_NAME);
            int size = document.getRootElement().getChildren().size();
            if (size > 0) {
                map = new TreeMap<FilterSub, String>();
                String status, day;
                Iterator iterator = document.getRootElement().getChildren().iterator();
                while (iterator.hasNext()) {
                    Element role = (Element) iterator.next();
                    String name = role.getChild(NAME_TAG).getText();
                    String season = role.getChild(SEASON_TAG).getText();
                    String quality = role.getChild(QUALITY_TAG).getText();
                    String path = role.getChild(PATH_TAG).getText();
                    String rename = role.getChild(RENAME_TAG).getText();
                    try {
                        status = role.getChild(STATUS_TAG).getText();
                    } catch (NullPointerException npe) {
                        status = "";
                    }
                    try {
                        day = role.getChild(DAY_TAG).getText();
                    } catch (NullPointerException npe) {
                        day = "";
                    }
                    map.put(new FilterSub(name, season, quality, status, day, rename),path);
                }
            }
        }
        return map;
    }
}