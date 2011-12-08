package org.feedworker.xml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;

/**
 *
 * @author luca judge
 */
public class XmlRP {
    // Very simple cookie storage
    private final LinkedHashMap<String, String> cookies = new LinkedHashMap<String, String>();
    private final String url = "http://www.italiansubs.net/forum/mobiquo/mobiquo.php";
    private XmlRpcClient client;

    public XmlRP() throws MalformedURLException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        client = new XmlRpcClient();
        config.setServerURL(new URL(url));
        client.setConfig(config);
        client.setTransportFactory(getSetCookies());
    }
    
    public boolean testConn(String user, String pwd) throws XmlRpcException{
        Object[] params = new Object[]{user.getBytes(), pwd.getBytes()};
        HashMap result = (HashMap) client.execute("login", params);
        boolean res = Boolean.parseBoolean(result.get("result").toString());
        if (!res)
            throw new XmlRpcException(new String((byte[]) result.get("result_text")));
        else
            return res;
    }
    
    public int getMessage() throws XmlRpcException {
        Object[] params = null;
        HashMap result = (HashMap) client.execute("get_box_info", params);
        HashMap box = (HashMap) ((Object[])result.get("list"))[0];
        return Integer.parseInt(box.get("unread_count").toString());
    }
    
    private XmlRpcTransportFactory getSetCookies(){
        return new XmlRpcTransportFactory() {
            @Override
            public XmlRpcTransport getTransport() {
                return new XmlRpcSunHttpTransport(client) {
                    private URLConnection conn;
                    
                    @Override
                    protected URLConnection newURLConnection(URL pURL) throws IOException {
                        conn = super.newURLConnection(pURL);
                        return conn;
                    }
                    
                    @Override
                    protected void initHttpHeaders(XmlRpcRequest pRequest) throws XmlRpcClientException {
                        super.initHttpHeaders(pRequest);
                        setCookies(conn);
                    }
                    
                    @Override
                    protected void close() throws XmlRpcClientException {
                        getCookies(conn);
                    }
                    
                    private void setCookies(URLConnection pConn) {
                        String cookieString = "";
                        for (String cookieName : cookies.keySet())
                            cookieString += "; " + cookieName + "=" + cookies.get(cookieName);
                        if (cookieString.length() > 2)
                            setRequestHeader("Cookie", cookieString.substring(2));
                    }

                    private void getCookies(URLConnection pConn) {
                        String headerName = null;
                        for (int i = 1; (headerName = pConn.getHeaderFieldKey(i)) != null; i++) {
                            if (headerName.equals("Set-Cookie")) {
                                String cookie = pConn.getHeaderField(i);
                                cookie = cookie.substring(0, cookie.indexOf(";"));
                                String cookieName = cookie.substring(0, cookie.indexOf("="));
                                String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                                cookies.put(cookieName, cookieValue);
                            }
                        }
                    }
                };
            }
        };
    }
}