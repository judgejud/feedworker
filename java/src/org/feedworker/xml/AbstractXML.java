package org.feedworker.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author luca
 */
class AbstractXML {
    private final String TAG_ROOT = "ROOT";
    
    protected Document document;
    protected Element root;
    protected File file;
    
    protected void initialize(File f, boolean read) throws JDOMException, IOException{
        file = f;
        if (file.exists() && read){
            document = new SAXBuilder().build(f);
            root = document.getRootElement();
        } else
            initializeWriter();
    }
    
    protected void initializeWriter(){
        root = new Element(TAG_ROOT);
        document = new Document(root);
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
    
    /**Scrive l'xml
     *
     * @throws IOException
     */
    public void write() throws IOException {
        // Creazione dell'oggetto XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        // Imposto il formato dell'outputter come "bel formato"
        outputter.setFormat(Format.getPrettyFormat());
        // Produco l'output sul file xml.foo
        outputter.output(document, new FileOutputStream(file));
    }
    
    protected Document getDocument(){
        return document;
    }
    
    protected void setDocument(Document doc){
        document = doc;
    }
}