package org.feedworker.core.http;

//IMPORT JAVA
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**Gestisce le interazioni http lato client (metodi post e get)
 * 
 * @author luca judge
 */
public class HttpItasa extends HttpAbstract{

    private final String ADDRESS_ITASA = "http://www.italiansubs.net/index.php";
    private final String TAG_ITASA = "<h2><br /><center>";
    private CookieStore cookies;

    /**Costruttore, inizializza il client http con timeout
     *
     * @param timeout tempo di scadenza connessione
     */
    public HttpItasa(int timeout) {
        super(timeout);
    }

    /**
     * effettua la connessione ad itasa con user e pwd
     *
     * @param user
     * @param pwd
     */
    public void connectItasa(String user, String pwd) throws UnsupportedEncodingException,
                                            ClientProtocolException, IOException {
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        response = client.execute(setPostItasa(user, pwd));
        entity = response.getEntity();
        cookies = client.getCookieStore();
        consumeEntity();
        //getCookiesItasa();
    } // end doPost
    
    /**
     * effettua la connessione ad itasa con user e pwd
     *
     * @param user
     * @param pwd
     */
    public boolean testConnectItasa(String user, String pwd) throws ClientProtocolException, 
                                                                    IOException {
        HttpItasa temp = new HttpItasa(10000);
        HttpResponse res = temp.client.execute(setPostItasa(user, pwd));
        String value = res.getHeaders("Location")[0].getValue();
        temp.closeClient();
        if (value.equals("http://www.italiansubs.net/index.php?option=com_user"))
            return true;
        else
            return false;
        
    } // end doPost
    
    private HttpPost setPostItasa(String user, String pwd) throws UnsupportedEncodingException{
        HttpPost post = new HttpPost(ADDRESS_ITASA);
        // parametri presi dall'html form action del sito
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("username", user));
        lnvp.add(new BasicNameValuePair("passwd", pwd));
        lnvp.add(new BasicNameValuePair("remember", "yes"));
        lnvp.add(new BasicNameValuePair("Submit", "Login"));
        lnvp.add(new BasicNameValuePair("option", "com_user"));
        lnvp.add(new BasicNameValuePair("task", "login"));
        lnvp.add(new BasicNameValuePair("silent", "true"));
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        //per vedere la stringa che manda in POST
        //BufferedReader br = new BufferedReader(new InputStreamReader(post.getEntity().getContent()));
        //System.out.println(br.readLine());
        return post;
    }

    @Override
    public HttpEntity requestGetEntity(String link) throws IndexOutOfBoundsException, IOException {
        long lenght = -1;
        int temp = 0;
        while (lenght == -1) {
            ++temp;
            consumeEntity();
            client.setCookieStore(cookies);
            get = new HttpGet(link);
            response = client.execute(get);
            entity = response.getEntity();
            String url = readInputStreamURL(entity.getContent(), TAG_ITASA, 2);
            consumeEntity();
            if (url != null) {
                getDownload(url);
                lenght = entity.getContentLength();
                get = null;
                response = null;
                if (temp == 26)
                    break;
            } else {
                entity = null;
                lenght = -2;
            }
        }// end while
        return entity;
    }

    private void getCookiesItasa() throws IOException {
        get = new HttpGet(ADDRESS_ITASA);
        response = client.execute(get);
        entity = response.getEntity();
        consumeEntity();
        cookies = client.getCookieStore();
    }
    
    public HttpEntity getDownload(String url) throws IOException{
        get = new HttpGet(url);
        client.setCookieStore(cookies);
        response = client.execute(get);
        entity = response.getEntity();
        if (entity.getContentLength() != -1)
            getAttachement(response.getAllHeaders(), ADDRESS_ITASA);
        return entity;
    }

    private String readInputStreamURL(InputStream is, String tag, int start)
            throws StringIndexOutOfBoundsException, IOException {
        String search = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            int min = start;
            int max = start + tag.length();
            int len = line.length();
            if (len < max)
                max = len;
            if (len < min)
                min = len;
            if (line.substring(min, max).equalsIgnoreCase(tag)) {
                search = line;
                search = search.substring(search.indexOf('"') + 1);
                search = search.substring(0, search.indexOf('"'));
                break;
            }
        }
        br.close();
        return search;
    }

    @Override
    protected void getAttachement(Header[] head, String from) throws IndexOutOfBoundsException {
        int i;
        for (i = 0; i < head.length; i++) {
            if (head[i].getName().equalsIgnoreCase("Content-Disposition"))
                break;
        }
        String attachement = head[i].getValue();
        namefile = attachement.substring(21);
    }
}// end Http