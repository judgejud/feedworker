package org.feedworker.core.http;
//IMPORT JAVA
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
//IMPORT JAVAX
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 *
 * @author luca
 */
public class Https {
    private static Https https = null;
    private SSLContext ssl;
    // Install the all-trusting trust manager
    private Https () throws NoSuchAlgorithmException, KeyManagementException{
        ssl = SSLContext.getInstance("TLS");
        ssl.init(new KeyManager[0], 
                new TrustManager[] {new DefaultTrustManager()}, 
                new SecureRandom());
        SSLContext.setDefault(ssl);
    }
    
    public static Https getInstance() throws NoSuchAlgorithmException, KeyManagementException{
        if (https==null)
            https = new Https();
        return https;
    }
    // Now you can access an https URL without having the certificate in the truststore
    public InputStream connection(String url) throws Exception {
        URL u = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
        conn.setRequestProperty("User-Agent", "FeedWorker 448");
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        if (conn.getResponseCode()==200)
            return conn.getInputStream();
        return null;
    }
    
    public String getLocationRedirect(String url) throws Exception {
        URL u = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        return conn.getHeaderField("Location");
    }
    
    // Create a trust manager that does not validate certificate chains
    private class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authtype) 
                                                    throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authtype) 
                                                    throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}