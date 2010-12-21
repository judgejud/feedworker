package org.feedworker.client;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author luca
 */
class XPathReader {
    
    private static final File FILE_NAME = new File("calendar.xml");
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    public static String queryDayEquals(String day) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException{
                
        String query = "//SHOW[NEXT_DATE='" + day + "']/SHOW/text()";
        NodeList nodes = initializeXPathNode(query);
        
        int len = nodes.getLength();
        String result = "";
        if (len>0)
        result += nodes.item(0).getNodeValue();
        for (int i=1; i<len; i++)
            result += ", " + nodes.item(i).getNodeValue();
        return result;
    }
    
    public static TreeMap<Long, String> queryDayID(String day) throws 
            ParserConfigurationException, SAXException, IOException, XPathExpressionException{
        
        String query = "//SHOW[translate('" + day + "', '-', '') > "
                + "translate(NEXT_DATE, '-', '')]/ID_TVRAGE/text()";
        NodeList nodes = initializeXPathNode(query);        
        TreeMap<Long, String> array  = new TreeMap<Long, String>();
        for (int i=0; i<nodes.getLength(); i++){
            String temp = nodes.item(i).getNodeValue();
            String query1 = "count(//SHOW[ID_TVRAGE='" + temp +"']/preceding::ID_TVRAGE)+1";
            long number = (initializeXPathNumber(query1));
            array.put(Long.valueOf(number), temp);
        }
        
        query = "//SHOW[NEXT_DATE[not(text())]]/ID_TVRAGE/text()";
        nodes = initializeXPathNode(query);
        for (int i=0; i<nodes.getLength(); i++){
            String temp = nodes.item(i).getNodeValue();
            String query1 = "count(//SHOW[ID_TVRAGE='" + temp +"']/preceding::ID_TVRAGE)+1";
            long number = (initializeXPathNumber(query1));
            array.put(Long.valueOf(number), temp);
        }
        return array;
    }
    
    private static NodeList initializeXPathNode(String query) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException{
        
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Compile the XPath expression
        XPathExpression expr = xpath.compile(query);
        // Run the query and get a nodeset && Cast the result to a DOM NodeList
        return (NodeList) expr.evaluate(builder.parse(FILE_NAME), XPathConstants.NODESET);
    }
    
    private static long initializeXPathNumber(String query) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException{
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        XPathExpression expr = xpath.compile(query);
        // Run the query and get a nodeset && Cast the result to a DOM NodeList
        return ((Double) expr.evaluate(builder.parse(FILE_NAME), XPathConstants.NUMBER)).longValue();
    }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
        queryDayID("2010-12-25");
    }
}