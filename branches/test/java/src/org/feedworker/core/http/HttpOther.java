package org.feedworker.core.http;

//IMPORT JAVA
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**Gestisce le interazioni http lato client (metodi post e get)
 * 
 * @author luca judge
 */
public class HttpOther extends HttpAbstract{
    private final String ADDRESS_SUBSF = "http://www.subsfactory.it/";
    private final String TORRENT_ZOINK = "http://torrent.zoink.it/";
    private final String TORRENT_BTCHAT = "http://www.bt-chat.com/";
    private final String TORRENT_MININOVA = "http://www.mininova.org/";
    private final String TORRENT_KARMORRA = "http://showrss.karmorra.info/";
    private final String TAG_SUBSF = "<td align=\"center\" class=\"functions\" width=\"100%\"><a href";
    //private CookieStore cookies;

    /**Costruttore, inizializza il client http con timeout
     *
     * @param timeout tempo di scadenza connessione
     */
    public HttpOther(int timeout) {
        super(timeout);
    }
    
    /**Costruttore, inizializza il client http con timeout
     *
     * @param timeout tempo di scadenza connessione
     */
    public HttpOther() {
        super();
    }
    
    public InputStream getStream(String oldUrl) throws IOException{
        get = new HttpGet(oldUrl);
        response = client.execute(get);
        return response.getEntity().getContent();
    }

    @Override
    public HttpEntity requestGetEntity(String link) throws IndexOutOfBoundsException, 
                                                                    IOException {
        long lenght = -1;
        int temp = 0;
        while (lenght == -1) {
            ++temp;
            consumeEntity();
            get = new HttpGet(link);
            response = client.execute(get);
            entity = response.getEntity();
            String url = readInputStreamURL(entity.getContent(), TAG_SUBSF, 8);
            url = url.replaceAll("amp;", "");
            consumeEntity();
            if (url != null) {
                get = new HttpGet(url);
                response = client.execute(get);
                entity = response.getEntity();
                lenght = entity.getContentLength();
                if (lenght != -1)
                    getAttachement(response.getAllHeaders(), ADDRESS_SUBSF);
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

    public InputStream getTorrent(String oldUrl) throws IOException, NullPointerException {
        InputStream is = null;
        if (oldUrl.startsWith(TORRENT_ZOINK)) {
            namefile = oldUrl.substring(TORRENT_ZOINK.length());
            get = new HttpGet(convertUrlEncoding(oldUrl));
            response = client.execute(get);
            is = response.getEntity().getContent();
        } else if (oldUrl.startsWith(TORRENT_MININOVA)) {
            String url = oldUrl.replace("tor", "get");
            get = new HttpGet(url);
            response = client.execute(get);
            getAttachement(response.getAllHeaders(), TORRENT_MININOVA);
            is = response.getEntity().getContent();
        } else if (oldUrl.startsWith(TORRENT_BTCHAT)) {
            System.out.println(oldUrl);
            get = new HttpGet(oldUrl);
            System.out.println("get");
            response = client.execute(get);
            System.out.println("response");
            getAttachement(response.getAllHeaders(), TORRENT_BTCHAT);
            System.out.println("getAttachement");
            is = response.getEntity().getContent();
            System.out.println("getEntity");
        } else if (oldUrl.startsWith(TORRENT_KARMORRA)) {
            get = new HttpGet(oldUrl);
            try{
                response = client.execute(get);
            } catch(ClientProtocolException e){
                is = getTorrent(e.getCause().getMessage().split(": ")[1]);
            }
        }
        return is;
    }

    private String readInputStreamURL(InputStream is, String tag, int start)
            throws StringIndexOutOfBoundsException, IOException {
        String search = null;
        boolean subsf = false;
        if (tag.equalsIgnoreCase(TAG_SUBSF))
            subsf = true;
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
                if (subsf)
                    search = search.substring(max);
                search = search.substring(search.indexOf('"') + 1);
                search = search.substring(0, search.indexOf('"'));
                break;
            }
        }
        br.close();
        if (subsf && search != null)
            search = ADDRESS_SUBSF + search;
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
        if (from.equalsIgnoreCase(ADDRESS_SUBSF))
            namefile = attachement.substring(22, attachement.length() - 1);
        else if (from.equalsIgnoreCase(TORRENT_MININOVA))
            namefile = attachement.substring(18, attachement.length() - 1);
        else if (from.equalsIgnoreCase(TORRENT_BTCHAT))
            namefile = attachement.substring(22, attachement.length() - 1);
        
    }

    private String convertUrlEncoding(String old) {
        String url = old.replace("[", "%5B");
        url = url.replace("]", "%5D");
        url = url.replace(" ", "%20");
        return url;
    }
    
    public String synoConnectGetID(String url, String user, String pwd)
            throws IOException {
        String id = null;
        String searchID = "   \"id\" : \"";
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("action", "login"));
        lnvp.add(new BasicNameValuePair("username", user));
        lnvp.add(new BasicNameValuePair("passwd", pwd));
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        response = client.execute(post);
        entity = response.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                entity.getContent()));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.length() > 11
                    && line.substring(0, 11).equalsIgnoreCase(searchID)) {
                id = (line.substring(11).split("\"")[0]);
                break;
            }
        }
        consumeEntity();
        return id;
    }

    /**Aggiunge il link al download redirectory synology
     *
     * @param url url download redirectory
     * @param id id di sessione
     * @param link link da aggiungere
     * @throws IOException
     */
    public void synoAddLink(String url, String id, String link) throws IOException {
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("id", id));
        lnvp.add(new BasicNameValuePair("action", "addurl"));
        lnvp.add(new BasicNameValuePair("url", link));
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        client.execute(post);
    }
}// end Http