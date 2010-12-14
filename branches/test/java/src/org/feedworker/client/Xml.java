package org.feedworker.client;

//IMPORT JAVA
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private File file;

    public Xml(File f, boolean read) throws JDOMException, IOException{
        file = f;
        if (file.exists() && read)
            document = new SAXBuilder().build(file);
        else
            initializeWriter();
    }
    /**
     * Scrive le regole su file xml
     *
     * @param map
     * @throws IOException
     */
    void writeMap(TreeMap<KeyRule, ValueRule> map) throws IOException {
        if (map.size() > 0) {
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                KeyRule key = (KeyRule) it.next();
                ValueRule value = map.get(key);
                addRule(key.getName(), key.getSeason(), key.getQuality(),
                        value.getPath(), value.isRename(), value.isDelete());
            }
            write();
        }
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

    void addShowTV(Object[] array){
        root.addContent(createShow(array));
    }
    
    void removeShowTv(Object[] array){
        root.removeContent(createShow(array));
    }
    
    private Element createShow(Object[] array){
        Element calendar = new Element(TAG_CALENDAR_ROOT);
        int i=-1;
        Element id_tvrage = new Element(TAG_CALENDAR_ID_TVRAGE);
        id_tvrage.setText(array[++i].toString());
        calendar.addContent(id_tvrage);

        Element name = new Element(TAG_CALENDAR_NAME);
        name.setText(array[++i].toString());
        calendar.addContent(name);

        Element status = new Element(TAG_CALENDAR_STATUS);
        status.setText(array[++i].toString());
        calendar.addContent(status);

        Element day = new Element(TAG_CALENDAR_DAY);
        day.setText(array[++i].toString());
        calendar.addContent(day);

        Element lastEpisode = new Element(TAG_CALENDAR_LAST_EPISODE);
        lastEpisode.setText(checkNPE(array[++i]));
        calendar.addContent(lastEpisode);

        Element lastTitle = new Element(TAG_CALENDAR_LAST_TITLE);
        lastTitle.setText(checkNPE(array[++i]));
        calendar.addContent(lastTitle);

        Element lastDate = new Element(TAG_CALENDAR_LAST_DATE);
        lastDate.setText(checkNPE(array[++i]));
        calendar.addContent(lastDate);

        Element nextEpisode = new Element(TAG_CALENDAR_NEXT_EPISODE);
        nextEpisode.setText(checkNPE(array[++i]));
        calendar.addContent(nextEpisode);

        Element nextTitle = new Element(TAG_CALENDAR_NEXT_TITLE);
        nextTitle.setText(checkNPE(array[++i]));
        calendar.addContent(nextTitle);

        Element nextDate = new Element(TAG_CALENDAR_NEXT_DATE);
        nextDate.setText(checkNPE(array[++i]));
        calendar.addContent(nextDate);
        
        return calendar;
    }

    void readingDocumentCalendar() throws JDOMException, IOException{
        ArrayList<Object[]> al = new ArrayList<Object[]>();
        if (sizeDocument() > 0){
            Iterator iter = iteratorDocument();
            while (iter.hasNext()) {
                Element calendar = (Element) iter.next();

            }
        }
    }

    private String checkNPE(Object obj){
        try{
            return obj.toString();
        } catch (NullPointerException e){
            return "";
        }
    }

    /**Scrive l'xml
     *
     * @throws IOException
     */
    void write() throws IOException {
        // Creazione dell'oggetto XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        // Imposto il formato dell'outputter come "bel formato"
        outputter.setFormat(Format.getPrettyFormat());
        // Produco l'output sul file xml.foo
        outputter.output(document, new FileOutputStream(file));
    }

    private int sizeDocument(){
        return document.getRootElement().getChildren().size();
    }
    
    private Iterator iteratorDocument(){
        return document.getRootElement().getChildren().iterator();
    }

    private void initializeWriter(){
        root = new Element("ROOT");
        document = new Document(root);
    }

    /**Inizializza la lettura dell'xml e restituisce la map ordinata come
     * treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public ArrayList initializeReaderRule() throws JDOMException, IOException {
        ArrayList global = new ArrayList();
        TreeMap<KeyRule, ValueRule> map = null;
        ArrayList<Object[]> matrix = null;
        if (sizeDocument() > 0){
            map = new TreeMap<KeyRule, ValueRule>();
            matrix = new ArrayList<Object[]>();
            Iterator iter = iteratorDocument();
            while (iter.hasNext()) {
                Element rule = (Element) iter.next();
                String name = rule.getChild(TAG_RULE_NAME).getText();
                String season = rule.getChild(TAG_RULE_SEASON).getText();
                String quality = rule.getChild(TAG_RULE_QUALITY).getText();
                String path = rule.getChild(TAG_RULE_PATH).getText();
                boolean rename = false, delete = false;
                try{
                    rename = Boolean.parseBoolean(rule.getChild(TAG_RULE_RENAME).getText());
                } catch (NullPointerException npe) {}
                try{
                    delete = Boolean.parseBoolean(rule.getChild(TAG_RULE_DELETE).getText());
                } catch (NullPointerException npe) {}
                map.put(new KeyRule(name, season, quality), new ValueRule(path, rename, delete));
                matrix.add(new Object[]{name, season, quality, path, rename,delete});
            }
        }
        global.add(map);
        global.add(matrix);
        return global;
    }
}