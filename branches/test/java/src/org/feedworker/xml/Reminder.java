package org.feedworker.xml;

//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.feedworker.util.Common;

import org.jdom.Element;
import org.jdom.JDOMException;

/**Scrive e legge su/da file xml le regole di destinazione
 * 
 * @author luca
 */
public class Reminder extends AbstractXML{
    // VARIABLES PRIVATE FINAL
    private final String TAG_REMINDER_ROOT = "REMINDER";
    private final String TAG_REMINDER_DATE = "DATE";
    private final String TAG_REMINDER_SUBTITLE = "SUBTITLE";

    public Reminder(File f, boolean read) throws JDOMException, IOException{
        initialize(f, read);
    }
        
    public ArrayList<Object[]> readingDocument() throws JDOMException, IOException{
        ArrayList<Object[]> al = new ArrayList<Object[]>();
        if (sizeRootChildren() > 0){
            Iterator iter = iteratorRootChildren();
            TreeSet ts = new TreeSet();
            while (iter.hasNext()) {
                Element reminder = (Element) iter.next();
                String text = reminder.getChild(TAG_REMINDER_SUBTITLE).getText();
                if (!ts.contains(text)){
                    int i=-1;
                    Date d = null;
                    try {
                        d = Common.stringDate(reminder.getChild(TAG_REMINDER_DATE).getText());
                    } catch (ParseException ex) {}
                    Object[] obj = new Object[3];
                    obj[++i] = d;
                    obj[++i] = text;
                    obj[++i] = false;
                    al.add(obj);
                    ts.add(text);
                }
            }
        }
        return al;
    }
    
    public void addItem(Object[] array) throws IOException{
        Element reminder = new Element(TAG_REMINDER_ROOT);
        int i=-1;
        Element date = new Element(TAG_REMINDER_DATE);
        date.setText(Common.dateString((Date)array[++i]));
        reminder.addContent(date);
        
        Element sub = new Element(TAG_REMINDER_SUBTITLE);
        sub.setText(array[++i].toString());
        reminder.addContent(sub);
        root.addContent(reminder);
    }
    
    public void removeItem(int row) throws IOException{
        root.getChildren().remove(row);
    }
}