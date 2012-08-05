package org.feedworker.xml;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.feedworker.util.Common;
import org.feedworker.util.ResourceLocator;

import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
/**
 *
 * @author luca
 */
public class XPathCalendar {
    private static final File FILE_NAME = ResourceLocator.getFILE_CALENDAR();
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private static TreeMap<Long, String> array;

    public static String queryDayEquals(String day) throws SAXException, 
            ParserConfigurationException, IOException, XPathExpressionException{
        if (FILE_NAME.exists()){
            try{
                day = Common.stringToAmerican(day);
                String query = "//SHOW[NEXT_DATE='" + day + "' or LAST_DATE='" + day 
                            + "']/SHOW/text()";
                NodeList nodes = initializeXPathNode(query);

                int len = nodes.getLength();
                String result = "";
                if (len>0)
                result += nodes.item(0).getNodeValue();
                for (int i=1; i<len; i++)
                    result += ", " + nodes.item(i).getNodeValue();
                return result;
            } catch (ParseException e){
                return null;
            }
        } else
            return null;
    }
    
    public static String queryIdShow(String id) throws SAXException, 
            ParserConfigurationException, IOException, XPathExpressionException{
        if (FILE_NAME.exists()){
            String query = "//SHOW[ID_TVRAGE='" + id + "']/SHOW/text()";
            NodeList nodes = initializeXPathNode(query);
            return nodes.item(0).getNodeValue();
        } else
            return null;
    }
    
    /**
     * 
     * @param day
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException 
     */
    public static TreeMap<Long, String> queryDayID(String day) throws SAXException,
            ParserConfigurationException, IOException, XPathExpressionException{
        String dayAfter = Common.dateStringAmerican(Common.afterDayDate(6));
        try{
            day = Common.stringToAmerican(day);
        } catch (ParseException e){e.printStackTrace();}
        array  = new TreeMap<Long, String>();
        //data > nextdate
        String query = "//SHOW[translate('" + day + "', '-', '') >= "
                + "translate(NEXT_DATE, '-', '')]/ID_TVRAGE/text()";
        addToArray(initializeXPathNode(query));
        //query next date blank
        query = "//SHOW[NEXT_DATE[not(text())]]/ID_TVRAGE/text()";
        addToArray(initializeXPathNode(query));
        //(nextdate-data)>8
        query = "//SHOW[translate(NEXT_DATE, '-', '') > "
                + "translate('" + dayAfter + "', '-', '')]/ID_TVRAGE/text()";
        addToArray(initializeXPathNode(query));
        return array;
    }
    
    public static Long queryRowId(String id)throws SAXException, 
            ParserConfigurationException, IOException, XPathExpressionException{
        String query = "count(//SHOW[ID_TVRAGE='" + id + "']/preceding::ID_TVRAGE)+1";
        return initializeXPathNumber(query);
    }
    
    private static void addToArray(NodeList nodes) throws SAXException, 
            ParserConfigurationException, IOException, XPathExpressionException{
        String temp;
        for (int i=0; i<nodes.getLength(); i++){
            temp = nodes.item(i).getNodeValue();
            array.put(queryRowId(temp), temp);
        }
    }
    
    private static NodeList initializeXPathNode(String query) throws SAXException, 
            ParserConfigurationException, IOException, XPathExpressionException{
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Compile the XPath expression
        XPathExpression expr = xpath.compile(query);
        // Run the query and get a nodeset && Cast the result to a DOM NodeList
        return (NodeList) expr.evaluate(builder.parse(FILE_NAME), XPathConstants.NODESET);
    }
    
    private static Long initializeXPathNumber(String query) throws SAXException, 
            ParserConfigurationException, IOException, XPathExpressionException{
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        XPathExpression expr = xpath.compile(query);
        // Run the query and get a nodeset && Cast the result to a DOM NodeList
        Double d = (Double) expr.evaluate(builder.parse(FILE_NAME), XPathConstants.NUMBER);
        return d.longValue();
    }
}