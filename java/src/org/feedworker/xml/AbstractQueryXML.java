package org.feedworker.xml;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author luca
 */
class AbstractQueryXML {
    protected Document document;
    
    protected void buildUrl(String url) throws ConnectException, JDOMException, IOException {
        document = new SAXBuilder().build(new URL(url));
    }
    
    protected int sizeRootChildren(){
        return document.getRootElement().getChildren().size();
    }
    
    protected Iterator iteratorRootChildren(){
        return document.getRootElement().getChildren().iterator();
    }
    
    protected String checkNPE(Object obj){
        try{
            return obj.toString();
        } catch (NullPointerException e){
            return "";
        }
    }
    
    protected String checkNPE(Element obj){
        try{
            return obj.getText();
        } catch (NullPointerException e){
            return "";
        }
    }
    
    protected Iterator getDescendantsZero(int level){
        Element doc = document.getRootElement();
        for (int i=0; i<level; i++)
            doc = (Element) doc.getChildren().get(0);
        return doc.getChildren().iterator();
    }
}