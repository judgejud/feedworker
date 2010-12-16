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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author luca
 */
public class XPathReader {
    private static final File FILE_NAME = new File("rules.xml");

    public static String queryDay(String day) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException{

        String result = "";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(FILE_NAME);
        // Create a XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance();
        // Create a XPath object
        XPath xpath = xFactory.newXPath();
        // Compile the XPath expression
        String query = "//RULE[DAY='" + day + "']/NAME/text()";
        XPathExpression expr = xpath.compile(query);
        // Run the query and get a nodeset
        Object resultQuery = expr.evaluate(doc, XPathConstants.NODESET);
        // Cast the result to a DOM NodeList
        NodeList nodes = (NodeList) resultQuery;
        int len = nodes.getLength();
        if (len>0)
        result += nodes.item(0).getNodeValue();
        for (int i=1; i<len; i++)
            result += ", " + nodes.item(i).getNodeValue();
        return result;
    }
}