package org.feedworker.xml;

//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.feedworker.object.KeyRule;
import org.feedworker.object.Operation;
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
    private final String TAG_RULE_OPERATION = "OPERATION";
    
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
                Element rule = new Element(TAG_RULE_ROOT);
                rule.addContent(new Element(TAG_RULE_NAME).setText(key.getName()));
                rule.addContent(new Element(TAG_RULE_SEASON).setText(key.getSeason()));
                rule.addContent(new Element(TAG_RULE_QUALITY).setText(key.getQuality()));
                rule.addContent(new Element(TAG_RULE_PATH).setText(value.getPath()));
                rule.addContent(new Element(TAG_RULE_OPERATION).setText(value.getOperation()));
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
                String operation;
                try{
                    operation = rule.getChild(TAG_RULE_OPERATION).getText();
                } catch (NullPointerException npe) {
                    boolean rename = false, delete = false;
                    try{
                        rename = Boolean.parseBoolean(rule.getChild(TAG_RULE_RENAME).getText());
                    } catch (NullPointerException np) {}
                    try{
                        delete = Boolean.parseBoolean(rule.getChild(TAG_RULE_DELETE).getText());
                    } catch (NullPointerException np) {}
                    if (rename)
                        operation = Operation.TRUNCATE.toString();
                    else if (delete)
                        operation = Operation.DELETE.toString();
                    else 
                        operation = null;
                }
                map.put(new KeyRule(name, season, quality), new ValueRule(path, operation));
                matrix.add(new Object[]{name, season, quality, path, operation});
            }
        }
        global.add(map);
        global.add(matrix);
        return global;
    }
}