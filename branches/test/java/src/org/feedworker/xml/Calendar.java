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
public class Calendar extends AbstractXML{
    // VARIABLES PRIVATE FINAL
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
    
    public Calendar(File f, boolean read) throws JDOMException, IOException{
        initialize(f, read);
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

    public ArrayList readingDocument() throws JDOMException, IOException{
        ArrayList global = new ArrayList();
        ArrayList<Object[]> al = new ArrayList<Object[]>();
        TreeSet ts = new TreeSet();
        if (sizeRootChildren() > 0){
            Iterator iter = iteratorRootChildren();
            while (iter.hasNext()) {
                Element calendar = (Element) iter.next();
                Object[] obj = new Object[10];
                int i=-1;
                obj[++i]=calendar.getChild(TAG_CALENDAR_ID_TVRAGE).getText();
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
    
    @Override
    public Calendar clone(){
        Calendar clone=null;
        try {
            clone = new Calendar(File.createTempFile("tempcal", ".xml"), false);
            clone.setDocumentRoot(this.getDocument(), this.getRoot());
            clone.write();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return clone;
    }
    
    public void reverseDataCloning(Calendar clone) throws IOException{
        this.setDocumentRoot(clone.getDocument(),clone.getRoot());
        this.write();
    }
}