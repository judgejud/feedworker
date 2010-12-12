package org.feedworker.client;

//IMPORT JAVA
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import org.feedworker.util.KeyRule;
import org.feedworker.util.ValueRule;

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

    private final File FILE_RULE = new File("rules.xml");
    private final File FILE_CALENDAR = new File("calendar.xml");
    private final String TAG_RULE_ROOT = "RULE";
    private final String TAG_RULE_NAME = "NAME";
    private final String TAG_RULE_SEASON = "SEASON";
    private final String TAG_RULE_QUALITY = "QUALITY";
    private final String TAG_RULE_PATH = "PATH";
    private final String TAG_RULE_RENAME = "RENAME";
    private final String TAG_RULE_DELETE = "DELETE";
    private final String TAG_CALENDAR_ROOT = "SHOW";
    private final String TAG_CALENDAR_ID_TVRAGE = "ID_TVRAGE";
    private final String TAG_CALENDAR_NAME = "SHOW";
    private final String TAG_CALENDAR_DAY = "DAY";
    private final String TAG_CALENDAR_STATUS = "STATUS";
    private final String TAG_CALENDAR_LAST_EPISODE = "LAST_EPISODE";
    private final String TAG_CALENDAR_LAST_TITLE = "LAST_TITLE";
    private final String TAG_CALENDAR_LAST_DATE = "LAST_DATE";
    private final String TAG_CALENDAR_NEXT_EPISODE = "NEXT_EPISODE";
    private final String TAG_CALENDAR_NEXT_TITLE = "NEXT_TITLE";
    private final String TAG_CALENDAR_NEXT_DATE = "NEXT_DATE";

    // VARIABLES PRIVATE
    private Element root;
    private Document document;

    /**
     * Scrive le regole su file xml
     *
     * @param map
     * @throws IOException
     */
    public void writeMap(TreeMap<KeyRule, ValueRule> map) throws IOException {
        if (map.size() > 0) {
            Iterator it = map.keySet().iterator();
            initializeWriter();
            while (it.hasNext()) {
                KeyRule key = (KeyRule) it.next();
                ValueRule value = map.get(key);
                addRule(key.getName(), key.getSeason(), key.getQuality(),
                        value.getPath(), value.isRename(), value.isDelete());
            }
            write(FILE_RULE);
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
            String _path, boolean _rename, boolean _delete) {
        Element rule = new Element(TAG_RULE_ROOT);
        Element name = new Element(TAG_RULE_NAME);
        name.setText(_name);

        Element season = new Element(TAG_RULE_SEASON);
        season.setText(_season);

        Element quality = new Element(TAG_RULE_QUALITY);
        quality.setText(_version);

        Element path = new Element(TAG_RULE_PATH);
        path.setText(_path);

        Element rename = new Element(TAG_RULE_RENAME);
        rename.setText(Boolean.toString(_rename));

        Element delete = new Element(TAG_RULE_DELETE);
        delete.setText(Boolean.toString(_delete));

        rule.addContent(name);
        rule.addContent(season);
        rule.addContent(quality);
        rule.addContent(path);
        rule.addContent(rename);
        rule.addContent(delete);
        root.addContent(rule);
    }

    /**Scrive l'xml
     *
     * @throws IOException
     */
    private void write(File f) throws IOException {
        // Creazione dell'oggetto XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        // Imposto il formato dell'outputter come "bel formato"
        outputter.setFormat(Format.getPrettyFormat());
        // Produco l'output sul file xml.foo
        outputter.output(document, new FileOutputStream(f));
    }

    private int initializeDocument(File f) throws JDOMException, IOException{
        // Creo un SAXBuilder e con esso costruisco un document
        document = new SAXBuilder().build(f);
        return document.getRootElement().getChildren().size();
    }

    /**Inizializza la lettura dell'xml e restituisce la map ordinata come
     * treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public TreeMap<KeyRule, ValueRule> initializeReaderRule() throws JDOMException,
            IOException {
        TreeMap<KeyRule, ValueRule> map = null;
        File old = new File("roles.xml");
        if (FILE_RULE.exists()) {
            int size = initializeDocument(FILE_RULE);
            if (size > 0)
                map = readingDocumentRule();
        } else if (old.exists()){
            int size = initializeDocument(old);
            if (size > 0) {
                map = readingDocumentRule();
                writeMap(map);
                old.delete();
            }
        }
        return map;
    }

    private TreeMap<KeyRule, ValueRule> readingDocumentRule(){
        TreeMap<KeyRule, ValueRule> map = new TreeMap<KeyRule, ValueRule>();
        Iterator iterator = document.getRootElement().getChildren().iterator();
        while (iterator.hasNext()) {
            Element rule = (Element) iterator.next();
            String name = rule.getChild(TAG_RULE_NAME).getText();
            String season = rule.getChild(TAG_RULE_SEASON).getText();
            String quality = rule.getChild(TAG_RULE_QUALITY).getText();
            String path = rule.getChild(TAG_RULE_PATH).getText();
            boolean rename = false, delete = false;
            try{
                rename = Boolean.parseBoolean(rule.getChild(TAG_RULE_RENAME).getText());
                delete = Boolean.parseBoolean(rule.getChild(TAG_RULE_DELETE).getText());
            } catch (NullPointerException npe) {}
            map.put(new KeyRule(name, season, quality), new ValueRule(path, rename, delete));
        }
        return map;
    }
}