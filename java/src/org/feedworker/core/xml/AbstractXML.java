package org.feedworker.core.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author luca
 */
class AbstractXML {
    protected Document document;
    
    protected void buildUrl(String url) throws JDOMException, IOException {
        document = new SAXBuilder().build(new URL(url));
    }
    
    protected void buildFile(File f) throws JDOMException, IOException{
        document = new SAXBuilder().build(f);
    }
    
    protected int sizeRootChildren(){
        return document.getRootElement().getChildren().size();
    }
    
    protected Iterator iteratorRootChildren(){
        return document.getRootElement().getChildren().iterator();
    }
}