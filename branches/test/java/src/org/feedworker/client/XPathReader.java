package org.feedworker.client;

import java.io.File;
import java.io.IOException;

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

    public static String queryDayEquals(String day) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException{
                
        String query = "//SHOW[NEXT_DATE='" + day + "']/SHOW/text()";
        NodeList nodes = initializeXPath(query);
        
        int len = nodes.getLength();
        String result = "";
        if (len>0)
        result += nodes.item(0).getNodeValue();
        for (int i=1; i<len; i++)
            result += ", " + nodes.item(i).getNodeValue();
        return result;
    }
    
    //TODO: trovare un modo affinchè xpath sia in grado di prendere le date in bianco/vuote
    //su query2
    public static String[] queryDayID(String day) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException{
        
        String query1 = "//SHOW[translate('" + day + "', '-', '') > "
                + "translate(NEXT_DATE, '-', '')]/ID_TVRAGE/text()";
        String query2 = "//SHOW/NEXT_DATE[not(node())]/ID_TVRAGE/text()";
        NodeList nodes1 = initializeXPath(query1);
        NodeList nodes2 = initializeXPath(query2);
        int len1 = nodes1.getLength();
        int len2 = nodes2.getLength();
        int sum = len1 + len2;
        System.out.println(len1 + " "+len2);
        String result[] = null;
        
        if (sum>0)
            result = new String[sum];
        for (int i=0; i<len1; i++){
            result[i] = nodes1.item(i).getNodeValue();
            System.out.println(result[i]);
        }
        for (int i=0; i<len2; i++){
            int j = i+len1;
            result[j] = nodes2.item(i).getNodeValue();
            System.out.println(result[j]);
        }
        return result;
    }
    
    private static NodeList initializeXPath(String query) throws ParserConfigurationException, 
            SAXException, IOException, XPathExpressionException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Create a XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance();
        // Create a XPath object
        XPath xpath = xFactory.newXPath();
        // Compile the XPath expression
        System.out.println(query);
        XPathExpression expr = xpath.compile(query);
        // Run the query and get a nodeset && Cast the result to a DOM NodeList
        return (NodeList) expr.evaluate(builder.parse(FILE_NAME), XPathConstants.NODESET);
    }
    
    public static void main (String[] args){
        try {
            queryDayID("2010-12-24");
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }
    }
}