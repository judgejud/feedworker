package org.feedworker.xml;

//IMPORT JAVA
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.feedworker.object.KeyRule;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**Scrive e legge su/da file xml le regole di destinazione
 * 
 * @author luca
 */
public class Xml extends AbstractXML{
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
    private final String TAG_CALENDAR_ID_ITASA = "ID_ITASA";
    private final String TAG_CALENDAR_ID_TVDB = "ID_TVDB";
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
    private File file;

    public Xml(File f, boolean read) throws JDOMException, IOException{
        file = f;
        if (file.exists() && read){
            buildFile(file);
            root = document.getRootElement();
        } else
            initializeWriter();
    }
    /**
     * Scrive le regole su file xml
     *
     * @param map
     * @throws IOException
     */
    public void writeMap(TreeMap<KeyRule, ValueRule> map) throws IOException {
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

    public void addShowTV(Object[] array){
        root.addContent(createShow(array));
    }
    
    public void removeShowTv(int row) throws IOException{
        root.getChildren().remove(row);
        write();
    }
    
    public void removeAllShowTv() throws IOException{
        root.removeContent();
        write();
    }
    
    private Element createShow(Object[] array){
        Element calendar = new Element(TAG_CALENDAR_ROOT);
        int i=-1;
        Element id_tvrage = new Element(TAG_CALENDAR_ID_TVRAGE);
        id_tvrage.setText(array[++i].toString());
        calendar.addContent(id_tvrage);
        
        Element id_itasa = new Element(TAG_CALENDAR_ID_ITASA);
        id_itasa.setText(checkNPE(array[++i]));
        calendar.addContent(id_itasa);
        
        Element id_tvdb = new Element(TAG_CALENDAR_ID_TVDB);
        id_tvdb.setText(checkNPE(array[++i]));
        calendar.addContent(id_tvdb);
        
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
        String d1 = Common.dateStringAmerican((Date) array[++i]);
        lastDate.setText(checkNPE(d1));
        calendar.addContent(lastDate);

        Element nextEpisode = new Element(TAG_CALENDAR_NEXT_EPISODE);
        nextEpisode.setText(checkNPE(array[++i]));
        calendar.addContent(nextEpisode);

        Element nextTitle = new Element(TAG_CALENDAR_NEXT_TITLE);
        nextTitle.setText(checkNPE(array[++i]));
        calendar.addContent(nextTitle);

        Element nextDate = new Element(TAG_CALENDAR_NEXT_DATE);
        String d2 = Common.dateStringAmerican((Date) array[++i]);
        nextDate.setText(checkNPE(d2));
        calendar.addContent(nextDate);
        
        return calendar;
    }

    public ArrayList readingDocumentCalendar() throws JDOMException, IOException{
        ArrayList global = new ArrayList();
        ArrayList<Object[]> al = new ArrayList<Object[]>();
        TreeSet ts = new TreeSet();
        if (sizeRootChildren() > 0){
            Iterator iter = iteratorRootChildren();
            while (iter.hasNext()) {
                Element calendar = (Element) iter.next();
                Object[] obj = new Object[12];
                int i=-1;
                obj[++i]=calendar.getChild(TAG_CALENDAR_ID_TVRAGE).getText();
                obj[++i]=calendar.getChild(TAG_CALENDAR_ID_ITASA).getText();
                obj[++i]=calendar.getChild(TAG_CALENDAR_ID_TVDB).getText();
                obj[++i]=calendar.getChild(TAG_CALENDAR_NAME).getText();
                obj[++i]=Boolean.parseBoolean(calendar.getChild(TAG_CALENDAR_STATUS).getText());
                obj[++i]=calendar.getChild(TAG_CALENDAR_DAY).getText();
                obj[++i]=calendar.getChild(TAG_CALENDAR_LAST_EPISODE).getText();
                obj[++i]=calendar.getChild(TAG_CALENDAR_LAST_TITLE).getText();
                String s1 = calendar.getChild(TAG_CALENDAR_LAST_DATE).getText();
                Date d1 = null;
                try {
                    d1 = Common.stringAmericanToDate(s1);
                } catch (ParseException ex) {}
                obj[++i]= d1;
                obj[++i]=calendar.getChild(TAG_CALENDAR_NEXT_EPISODE).getText();
                obj[++i]=calendar.getChild(TAG_CALENDAR_NEXT_TITLE).getText();
                String s2 = calendar.getChild(TAG_CALENDAR_NEXT_DATE).getText();
                Date d2 = null;
                try {
                    d2 = Common.stringAmericanToDate(s2);
                } catch (ParseException ex) {}
                obj[++i]= d2;
                ts.add(obj[0]);
                al.add(obj);
            }            
        }
        global.add(ts);
        global.add(al);
        return global;
    }

    /**Scrive l'xml
     *
     * @throws IOException
     */
    public void write() throws IOException {
        // Creazione dell'oggetto XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        // Imposto il formato dell'outputter come "bel formato"
        outputter.setFormat(Format.getPrettyFormat());
        // Produco l'output sul file xml.foo
        outputter.output(document, new FileOutputStream(file));
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
        if (sizeRootChildren() > 0){
            map = new TreeMap<KeyRule, ValueRule>();
            matrix = new ArrayList<Object[]>();
            Iterator iter = iteratorRootChildren();
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