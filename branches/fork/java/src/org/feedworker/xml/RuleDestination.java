package org.feedworker.xml;

//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.feedworker.object.KeyRule;
import org.feedworker.object.ValueRule;

import org.jdom.Element;
import org.jdom.JDOMException;

/**Scrive e legge su/da file xml le regole di destinazione
 * 
 * @author luca
 */
public class RuleDestination extends AbstractXML{
    // VARIABLES PRIVATE FINAL
    private final String TAG_RULE_ROOT = "RULE";
    private final String TAG_RULE_NAME = "NAME";
    private final String TAG_RULE_SEASON = "SEASON";
    private final String TAG_RULE_QUALITY = "QUALITY";
    private final String TAG_RULE_PATH = "PATH";
    private final String TAG_RULE_RENAME = "RENAME";
    private final String TAG_RULE_DELETE = "DELETE";
    
    public RuleDestination(File f, boolean read) throws JDOMException, IOException{
        initialize(f, read);
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

    /**Inizializza la lettura dell'xml e restituisce la map ordinata come
     * treemap
     *
     * @return treemap
     * @throws JDOMException
     * @throws IOException
     */
    public ArrayList initializeReader() throws JDOMException, IOException {
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