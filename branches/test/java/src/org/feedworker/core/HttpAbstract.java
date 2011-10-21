package org.feedworker.core;

//IMPORT JAVA
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**Gestisce le interazioni http lato client (metodi post e get)
 * 
 * @author luca judge
 */
abstract class HttpAbstract {
    protected DefaultHttpClient client;
    protected HttpResponse response;
    protected HttpEntity entity;
    protected HttpGet get;
    protected String namefile;
    
    /** Costruttore, inizializza il client http
     *
     */
    HttpAbstract() {
        client = new DefaultHttpClient(new BasicHttpParams());
    }

    /**Costruttore, inizializza il client http con timeout
     *
     * @param timeout tempo di scadenza connessione
     */
    HttpAbstract(int timeout) {
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, timeout);
        HttpConnectionParams.setSoTimeout(my_httpParams, timeout);
        client = new DefaultHttpClient(my_httpParams);
    }

    /**
     * restituisce il nome e l'estensione del file che si sta scaricando
     *
     * @return nome & estensione
     */
    String getNameFile() {
        return namefile;
    }

    /** Chiude il client http e dealloca le risorse usate */
    void closeClient() {
        client.getConnectionManager().shutdown();
    }
    
    protected void consumeEntity() throws IOException{
        if (entity != null)
            EntityUtils.consume(entity);
    }
    
    abstract HttpEntity requestGetEntity(String link) throws 
                                            IndexOutOfBoundsException, IOException;
    
    protected abstract void getAttachement(Header[] head, String from) throws 
                                                        IndexOutOfBoundsException;
}// end Http