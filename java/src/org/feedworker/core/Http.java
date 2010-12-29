
package org.feedworker.core;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

/**
 * Gestisce le interazioni http lato client (metodi post e get)
 * 
 * @author luca judge
 */
class Http {

    private final String ADDRESS_ITASA = "http://www.italiansubs.net/index.php";
    private final String ADDRESS_SUBSF = "http://www.subsfactory.it/";
    private final String TORRENT_ZOINK = "http://torrent.zoink.it/";
    private final String TORRENT_BTCHAT = "http://www.bt-chat.com/";
    private final String TORRENT_MININOVA = "http://www.mininova.org/";
    private final String TAG_ITASA = "<h2><br /><center>";
    private final String TAG_SUBSF = "<td align=\"center\" class=\"functions\" width=\"100%\"><a href";
    private DefaultHttpClient client;
    private HttpResponse response;
    private HttpEntity entity;
    private HttpGet get;
    private CookieStore cookies;
    private String namefile, returnVal, val;

    /** Costruttore, inizializza il client http
     *
     */
    Http() {
        client = new DefaultHttpClient(new BasicHttpParams());
    }

    /**Costruttore, inizializza il client http con timeout
     *
     * @param timeout tempo di scadenza connessione
     */
    Http(int timeout) {
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, timeout);
        HttpConnectionParams.setSoTimeout(my_httpParams, timeout);
        client = new DefaultHttpClient(my_httpParams);
    }

    /**
     * effettua la connessione ad itasa con user e pwd
     *
     * @param user
     * @param pwd
     */
    void connectItasa(String user, String pwd) throws UnsupportedEncodingException,
            ClientProtocolException, IOException {
        //getValuesItasaForm();
        itasaLogonPOST(user, pwd);
    }

    /**
     * Esegue la POST ad itasa con login
     *
     * @param username
     * @param password
     */
    private void itasaLogonPOST(String username, String password) throws
            UnsupportedEncodingException, ClientProtocolException, IOException {
        HttpPost post = new HttpPost(ADDRESS_ITASA);
        // parametri presi dall'html form action del sito
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("username", username));
        lnvp.add(new BasicNameValuePair("passwd", password));
        lnvp.add(new BasicNameValuePair("remember", "yes"));
        lnvp.add(new BasicNameValuePair("Submit", "Login"));
        lnvp.add(new BasicNameValuePair("option", "com_user"));
        lnvp.add(new BasicNameValuePair("task", "login"));
        lnvp.add(new BasicNameValuePair("silent", "true"));
        //lnvp.add(new BasicNameValuePair("return", returnVal));
        //lnvp.add(new BasicNameValuePair(val, "1"));
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        //per vedere la stringa che manda in POST
        //BufferedReader br = new BufferedReader(new InputStreamReader(post.getEntity().getContent()));
        //System.out.println(br.readLine());
        response = client.execute(post);
        entity = response.getEntity();
        if (entity != null)
            entity.consumeContent();
        getCookiesItasa();
    } // end doPost

    HttpEntity requestGetEntity(String link, boolean itasa)
            throws IndexOutOfBoundsException, IOException {
        long lenght = -1;
        int temp = 0;
        while (lenght == -1) {
            ++temp;
            if (entity != null)
                entity.consumeContent();
            client.setCookieStore(cookies);
            get = new HttpGet(link);
            response = client.execute(get);
            entity = response.getEntity();
            String url = null;
            if (itasa)
                url = readInputStreamURL(entity.getContent(), TAG_ITASA, 2);
            else
                url = readInputStreamURL(entity.getContent(), TAG_SUBSF, 8);
            if (entity != null)
                entity.consumeContent();
            if (url != null) {
                get = new HttpGet(url);
                client.setCookieStore(cookies);
                response = client.execute(get);
                entity = response.getEntity();
                lenght = entity.getContentLength();
                if (lenght != -1) {
                    if (itasa)
                        getAttachement(response.getAllHeaders(), ADDRESS_ITASA);
                    else
                        getAttachement(response.getAllHeaders(), ADDRESS_SUBSF);
                }
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

    InputStream getTorrent(String oldUrl) throws IOException {
        InputStream is = null;
        if (oldUrl.substring(0, TORRENT_ZOINK.length()).equalsIgnoreCase(
                TORRENT_ZOINK)) {
            namefile = oldUrl.substring(TORRENT_ZOINK.length());
            get = new HttpGet(convertUrlEncoding(oldUrl));
            response = client.execute(get);
            is = response.getEntity().getContent();
        } else if (oldUrl.substring(0, TORRENT_MININOVA.length()).equalsIgnoreCase(TORRENT_MININOVA)) {
            String url = oldUrl.replace("tor", "get");
            get = new HttpGet(url);
            response = client.execute(get);
            getAttachement(response.getAllHeaders(), TORRENT_MININOVA);
            is = response.getEntity().getContent();
        } else if (oldUrl.substring(0, TORRENT_BTCHAT.length()).equalsIgnoreCase(TORRENT_BTCHAT)) {
            get = new HttpGet(oldUrl);
            response = client.execute(get);
            getAttachement(response.getAllHeaders(), TORRENT_BTCHAT);
            is = response.getEntity().getContent();
        }
        return is;
    }

    InputStream getStreamRss(String oldUrl) throws IOException {
        get = new HttpGet(oldUrl);
        response = client.execute(get);
        return response.getEntity().getContent();
    }

    private void getCookiesItasa() throws IOException {
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        get = new HttpGet(ADDRESS_ITASA);
        response = client.execute(get);
        entity = response.getEntity();
        if (entity != null) {
            entity.consumeContent();
        }
        cookies = client.getCookieStore();
    }

    private String readInputStreamURL(InputStream is, String tag, int start)
            throws StringIndexOutOfBoundsException, IOException {
        String search = null;
        boolean subsf = false;
        if (tag.equalsIgnoreCase(TAG_SUBSF)) {
            subsf = true;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            int min = start;
            int max = start + tag.length();
            int len = line.length();
            if (len < max) {
                max = len;
            }
            if (len < min) {
                min = len;
            }
            if (line.substring(min, max).equalsIgnoreCase(tag)) {
                search = line;
                if (subsf) {
                    search = search.substring(max);
                }
                search = search.substring(search.indexOf('"') + 1);
                search = search.substring(0, search.indexOf('"'));
                break;
            }
        }
        br.close();
        if (subsf && search != null) {
            search = ADDRESS_SUBSF + search;
        }
        return search;
    }

    private void getAttachement(Header[] head, String from) throws IndexOutOfBoundsException {
        int i;
        for (i = 0; i < head.length; i++) {
            if (head[i].getName().equalsIgnoreCase("Content-Disposition")) {
                break;
            }
        }
        String attachement = head[i].getValue();
        if (from.equalsIgnoreCase(ADDRESS_ITASA)) {
            namefile = attachement.substring(21);
        } else if (from.equalsIgnoreCase(ADDRESS_SUBSF)) {
            namefile = attachement.substring(22, attachement.length() - 1);
        } else if (from.equalsIgnoreCase(TORRENT_MININOVA)) {
            namefile = attachement.substring(18, attachement.length() - 1);
        } else if (from.equalsIgnoreCase(TORRENT_BTCHAT)) {
            namefile = attachement.substring(22, attachement.length() - 1);
        }
    }

    /**
     * restituisce il nome e l'estensione del file che si sta scaricando
     *
     * @return nome & estensione
     */
    String getNameFile() {
        return namefile;
    }

    private String convertUrlEncoding(String old) {
        String url = old.replace("[", "%5B");
        url = url.replace("]", "%5D");
        url = url.replace(" ", "%20");
        return url;
    }

    private void getValuesItasaForm() throws IOException {
        get = new HttpGet(ADDRESS_ITASA);
        String line = null;
        String tag = "<input type=\"hidden\" name=\"return\"";
        int start = 1;
        response = client.execute(get);
        entity = response.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                entity.getContent()));
        while ((line = br.readLine()) != null) {
            int min = start;
            int max = start + tag.length();
            int len = line.length();
            if (len < max)
                max = len;
            if (len < min)
                min = len;
            if (line.substring(min, max).equalsIgnoreCase(tag)) {
                System.out.println(line);
                line = line.substring(max + 1);
                line = line.substring(line.indexOf('"') + 1);
                returnVal = line.substring(0, line.indexOf('"'));
                line = br.readLine().substring(31);
                val = line.substring(0, line.indexOf('"'));
                break;
            }
        }
        br.close();
    }

    String synoConnectGetID(String url, String user, String pwd)
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
        if (entity != null) {
            entity.consumeContent();
        }
        return id;
    }

    /**
     * Aggiunge il link al download redirectory synology
     *
     * @param url
     *            url download redirectory
     * @param id
     *            id di sessione
     * @param link
     *            link da aggiungere
     * @throws IOException
     */
    void synoAddLink(String url, String id, String link) throws IOException {
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("id", id));
        lnvp.add(new BasicNameValuePair("action", "addurl"));
        lnvp.add(new BasicNameValuePair("url", link));
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        client.execute(post);
    }

    InputStream synoStatus(String url, String id) throws IllegalStateException,
            IOException {
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("id", id));
        lnvp.add(new BasicNameValuePair("action", "getall"));
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        HttpResponse res = client.execute(post);
        return res.getEntity().getContent();
    }

    void synoClearTask(String url, String id, String username)
            throws IOException {
        List<NameValuePair> lnvp = new ArrayList<NameValuePair>();
        lnvp.add(new BasicNameValuePair("id", id));
        lnvp.add(new BasicNameValuePair("action", "clear"));
        lnvp.add(new BasicNameValuePair("username", username));
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(lnvp, HTTP.UTF_8));
        client.execute(post);
    }

    /** Chiude il client http e dealloca le risorse usate */
    void closeClient() {
        client.getConnectionManager().shutdown();
    }
}// end Http
