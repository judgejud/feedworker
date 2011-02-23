package org.feedworker.xml;

//IMPORT JAVA
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
            while (iter.hasNext()) {
                Element reminder = (Element) iter.next();
                Object[] obj = new Object[2];
                int i=-1;
                obj[++i] = reminder.getChild(TAG_REMINDER_DATE).getText();
                obj[++i] = reminder.getChild(TAG_REMINDER_SUBTITLE).getText();
                al.add(obj);
            }            
        }
        return al;
    }
}