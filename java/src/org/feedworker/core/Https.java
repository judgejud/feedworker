package org.feedworker.core;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
    private SSLContext ctx;
    
    private Https () throws NoSuchAlgorithmException, KeyManagementException{
        ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], 
                new TrustManager[] {new DefaultTrustManager()}, 
                new SecureRandom());
        SSLContext.setDefault(ctx);
    }
    
    public static Https getInstance() throws NoSuchAlgorithmException, KeyManagementException{
        if (https==null)
            https = new Https();
        return https;
    }
    
    public InputStream connection(String url) throws Exception {
        InputStream is = null;

        URL u = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        if (conn.getResponseCode()==200)
            is = conn.getInputStream();
        return is;
    }

    private class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) 
                                                    throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) 
                                                    throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}