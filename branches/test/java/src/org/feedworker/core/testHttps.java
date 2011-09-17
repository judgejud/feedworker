package org.feedworker.core;
//IMPORT JAVA
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
//IMPORT JAVAX
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
/**
 *http://www.exampledepot.com/egs/javax.net.ssl/Client.html
 * @author luca
 */
public class testHttps {
    private static testHttps https = null;
    private int port = 443;
    private SocketFactory socketFactory;
    
    // Install the all-trusting trust manager
    private testHttps () throws IOException {
        socketFactory = SSLSocketFactory.getDefault();
    }
    
    public static testHttps getInstance() throws IOException{
        if (https==null)
            https = new testHttps();
        return https;
    }
    // Now you can access an https URL without having the certificate in the truststore
    public InputStream connection(String url) throws Exception {
        Socket socket = socketFactory.createSocket(url, port);
        return socket.getInputStream();
    }
}