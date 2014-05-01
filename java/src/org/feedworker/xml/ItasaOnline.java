package org.feedworker.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.feedworker.core.http.Https;
import org.feedworker.exception.ItasaException;
import org.feedworker.object.ItasaUser;
import org.feedworker.object.News;
import org.feedworker.object.Show;
import org.feedworker.object.Subtitle;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
/**
 *
 * @author luca
 */
public class ItasaOnline extends AbstractQueryXML{
    private final String API_KEY = "apikey=436e566f3d09b217cf687fa5bad5effc";
    
    private final String OPERATOR_AND = "&";
    
    private final String PARAM_AUTHCODE = "authcode=";
    private final String PARAM_PAGE = "page=";
    private final String PARAM_PASSWORD = "password=";
    private final String PARAM_QUERY = "q=";
    private final String PARAM_SHOW_ID = "show_id=";
    private final String PARAM_SUBTITLE_ID = "subtitle_id=";
    private final String PARAM_USERNAME = "username=";
    private final String PARAM_VERSION = "version=";

    private final String STATUS_FAIL = "fail";
    private final String STATUS_SUCCESS = "success";
    
    private final String STRING_REPLACE = "StringReplace";
    
    private final String TAG_STATUS = "status";
    private final String TAG_ERROR = "error";
    private final String TAG_ERROR_MESSAGE = "message";
    private final String TAG_COUNT = "count";
    //TAG NEWS
    private final String TAG_NEWS_ID = "id";
    private final String TAG_NEWS_SHOWNAME = "show_name";
    private final String TAG_NEWS_IMAGE = "image";
    private final String TAG_NEWS_THUMB = "thumb";
    private final String TAG_NEWS_EPISODES = "episodes";
    private final String TAG_NEWS_TRANSLATION = "translation";
    private final String TAG_NEWS_SYNC = "sync";
    private final String TAG_NEWS_INFO = "info";
    private final String TAG_NEWS_IMAGEBY = "image_by";
    private final String TAG_NEWS_SUBMITTEDBY = "submitted_by";
    private final String TAG_NEWS_SUBTITLES = "subtitles";
    //TAG SHOW
    private final String TAG_SHOW_PLOT = "plot";
    private final String TAG_SHOW_GENRES = "genres";
    private final String TAG_SHOW_BANNER = "banner";
    private final String TAG_SHOW_STARTED = "started";
    private final String TAG_SHOW_ENDED = "ended";
    private final String TAG_SHOW_COUNTRY = "country";
    private final String TAG_SHOW_SEASONS = "seasons";
    private final String TAG_SHOW_NETWORK = "network";
    private final String TAG_SHOW_NAME = "name";
    private final String TAG_SHOW_ID = "id";
    private final String TAG_SHOW_ID_TVRAGE = "id_tvrage";
    private final String TAG_SHOW_STATUS = "status";
    private final String TAG_SHOW_ACTORS = "actors";
    private final String TAG_SHOW_ACTOR_NAME = "name";
    private final String TAG_SHOW_ACTOR_AS = "as";
    private final String TAG_SHOW_ACTOR_IMAGE = "image";
    //TAG SUBTITLE
    private final String TAG_SUBTITLE_ID = "id";
    private final String TAG_SUBTITLE_NAME = "name";
    private final String TAG_SUBTITLE_VERSION = "version";
    private final String TAG_SUBTITLE_FILENAME = "filename";
    private final String TAG_SUBTITLE_FILESIZE = "filesize";
//    private final String TAG_SUBTITLE_DESCRIPTION = "description";
//    private final String TAG_SUBTITLE_SUBMIT_DATE = "submit_date";
    //TAG USER
    private final String TAG_USER_AUTHCODE = "authcode";
    private final String TAG_USER_ID = "id";
    private final String TAG_USER_HASMYITASA = "has_myitasa";
    //URL
    private final String URL_BASE = "https://api.italiansubs.net/api/rest";
    private final String URL_LOGIN = URL_BASE + "/users/login/?"; 
    private final String URL_MYITASA_LAST_SUB = URL_BASE + "/myitasa/lastsubtitles?";
    private final String URL_MYITASA_SHOWS = URL_BASE + "/myitasa/shows?";
    private final String URL_NEWS = URL_BASE + "/news?";
    private final String URL_NEWS_SINGLE = URL_BASE + "/news/" + STRING_REPLACE + "?";
    private final String URL_SHOW_SINGLE = 
                                    URL_BASE + "/shows/" + STRING_REPLACE + "?";
    private final String URL_SHOW_THUMBNAIL = 
                                    URL_BASE + "/shows/" + STRING_REPLACE + "/folderThumb?";    
    private final String URL_SHOW_LIST = URL_BASE + "/shows/?";
    private final String URL_SUBTITLE_DOWNLOAD = URL_BASE + "/subtitles/download?"; //
    private final String URL_SUBTITLE_SEARCH = URL_BASE + "/subtitles/search?"; //
    private final String URL_SUBTITLE_SINGLE = 
                                URL_BASE + "/subtitles/" + STRING_REPLACE + "?"; 
//    private final String URL_SUBTITLE_SHOW = URL_BASE + "/subtitles?"; //    
    
    private final String LINK_ITASA = 
        "http://www.italiansubs.net/index.php?option=com_info&Itemid=12&idserie=";
    
    private String status, error;
    
    public Show showSingle(String id, boolean flag_actors) throws JDOMException, 
                                                    IOException, ItasaException, Exception{
        connectHttps(composeUrl(URL_SHOW_SINGLE.replaceFirst(STRING_REPLACE, id), null));
        checkStatus();
        Show show = null;
        if (isStatusSuccess()){
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            Element item = (Element) iter.next();
            String name = item.getChild(TAG_SHOW_NAME).getText();
            String plot = item.getChild(TAG_SHOW_PLOT).getText();
            String banner = item.getChild(TAG_SHOW_BANNER).getText();
            String season  = item.getChild(TAG_SHOW_SEASONS).getText();
            String started  = item.getChild(TAG_SHOW_STARTED).getText();
            String ended  = item.getChild(TAG_SHOW_ENDED).getText();
            String country = item.getChild(TAG_SHOW_COUNTRY).getText();
            String network = item.getChild(TAG_SHOW_NETWORK).getText();
            String _status = item.getChild(TAG_SHOW_STATUS).getText();
            String tvrage = item.getChild(TAG_SHOW_ID_TVRAGE).getText();
            ArrayList<String> genres = new ArrayList<String>();
            Iterator it = item.getChild(TAG_SHOW_GENRES).getChildren().iterator();
            while (it.hasNext()){
                genres.add(((Element) it.next()).getText());
            }
            ArrayList<String[]> actors = null;
            if (flag_actors){
                actors = new ArrayList<String[]>();
                it = ((Element)item.getChildren(TAG_SHOW_ACTORS).get(0))
                                .getChildren().iterator();
                while (it.hasNext()){
                    Element temp = (Element) it.next();
                    actors.add(new String[]{temp.getChildText(TAG_SHOW_ACTOR_NAME),
                                            temp.getChildText(TAG_SHOW_ACTOR_AS),
                                            temp.getChildText(TAG_SHOW_ACTOR_IMAGE)});
                }
            }
            show = new Show(name, plot, banner, season, country, network, started,
                                ended, _status, tvrage, genres, actors, LINK_ITASA+id);
        } else
            throw new ItasaException("show single: "+ error);
        return show;
    }

    public TreeMap<String, String> showList() throws JDOMException, IOException, 
                                                        ItasaException, Exception {
        TreeMap<String, String> container = null;
        connectHttps(composeUrl(URL_SHOW_LIST, null));            
        checkStatus();
        if (isStatusSuccess()){
            container = new TreeMap<String, String>();
            Iterator iter =  getDescendantsZero(2);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String name = item.getChild(TAG_SHOW_NAME).getText();
                String id = item.getChild(TAG_SHOW_ID).getText();
                container.put(name, id);
            }
        } else 
            throw new ItasaException("ShowList: "+ error);
        return container;
    }
    
    public String getUrlThumbnail(String id){
        return composeUrl(URL_SHOW_THUMBNAIL.replaceFirst(STRING_REPLACE, id), null);
    }
    
    public Subtitle subtitleSingle(String id) throws JDOMException, IOException, 
                                                        ItasaException, Exception{
        connectHttps(composeUrl(URL_SUBTITLE_SINGLE.replaceFirst(STRING_REPLACE, id), null));
        checkStatus();
        Subtitle sub = null;
        if (isStatusSuccess()){
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            Element item = (Element) iter.next();
            String name = item.getChild(TAG_SUBTITLE_NAME).getText();
            String version = item.getChild(TAG_SUBTITLE_VERSION).getText();
            String filename  = item.getChild(TAG_SUBTITLE_FILENAME).getText();
            String filesize  = item.getChild(TAG_SUBTITLE_FILESIZE).getText();  
            sub = new Subtitle(id, name, version, filename, filesize);
        } else
            throw new ItasaException("subtitleSingle: "+ error);
        return sub;
    }
/*    
    public ArrayList<String> subtitleListByIdShow(int idShow, String _version, int page) 
                        throws JDOMException, IOException, ItasaException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        params.add(PARAM_SHOW_ID + idShow);
        if (_version!=null)
            params.add(PARAM_VERSION + _version);
        if (page>0)
            params.add(PARAM_PAGE + page);
        connectHttps(composeUrl(URL_SUBTITLE_SHOW, params));
        checkStatus();
        ArrayList<String> list = null;
        if (isStatusSuccess()){
            list = new ArrayList<String>();
            Iterator iter = getDescendantsZero(2);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                list.add(item.getChild(TAG_SUBTITLE_ID).getText());
            }
        } else
            throw new ItasaException("subtitleListByIdShow: "+ error);
        return list;
    }
 */
    public ArrayList<String> subtitleSearch(String id, String _version, String query, int page) 
                                throws JDOMException, IOException, ItasaException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        params.add(PARAM_SHOW_ID + id);
        if (_version!=null)
            params.add(PARAM_VERSION + _version);
        if (query!=null)
            params.add(PARAM_QUERY + query);
        if (page>0)
            params.add(PARAM_PAGE + page);
        connectHttps(composeUrl(URL_SUBTITLE_SEARCH, params));
        checkStatus();
        ArrayList<String> list = null;
        if (isStatusSuccess()){
            if (getResponseCount()>0){
                list = new ArrayList<String>();
                Iterator iter = getDescendantsZero(2);
                while (iter.hasNext()){
                    Element item = (Element) iter.next();
                    list.add(item.getChild(TAG_SUBTITLE_ID).getText());
                }
            } else
                throw new ItasaException("subtitleSearch: "
                        + "non ci sono sottotitoli che rispondono ai criteri di ricerca");
        } else
            throw new ItasaException("subtitleSearch: "+ error);
        return list;
    }
    
    public ItasaUser login(String user, String pwd) throws JDOMException, IOException, 
                                                                    ItasaException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        if (user.contains("Ø"))
            user.replaceAll("Ø", "%C3%98");
        params.add(PARAM_USERNAME + user);
        if (pwd.contains("Ø"))
            pwd.replaceAll("Ø", "%C3%98");
        params.add(PARAM_PASSWORD + pwd);
        connectHttps(composeUrl(URL_LOGIN, params));
        checkStatus();
        ItasaUser itasa = null;
        if (isStatusSuccess()){
            Element item = (Element) getDescendantsZero(1).next();
            String id = item.getChild(TAG_USER_ID).getText();
            String authcode = item.getChild(TAG_USER_AUTHCODE).getText();
            String hasmyitasa = item.getChild(TAG_USER_HASMYITASA).getText();
            boolean myitasa = false;
            if (hasmyitasa.equals("1"))
                myitasa = true;
            itasa = new ItasaUser(authcode, id, myitasa);
        } else
            throw new ItasaException(error);
        return itasa;
    }
    
    //TODO: decidere come restituire gli show
    public void myItasaShows(String authcode) throws JDOMException, IOException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        params.add(PARAM_AUTHCODE + authcode);
        connectHttps(composeUrl(URL_MYITASA_SHOWS, params));
        checkStatus();
        if (isStatusSuccess()){
            Iterator iter =  getDescendantsZero(2);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String id = item.getChild(TAG_SHOW_ID).getText();
                String name = item.getChild(TAG_SHOW_NAME).getText();
            }
        } else 
            throw new ItasaException("MyItasa Show: "+ error);
    }
    
    public ArrayList<String> myItasaShowsName(String authcode) 
                                    throws JDOMException, IOException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        params.add(PARAM_AUTHCODE + authcode);
        connectHttps(composeUrl(URL_MYITASA_SHOWS, params));
        checkStatus();
        ArrayList<String> showsName = new ArrayList<String>();
        if (isStatusSuccess()){
            Iterator iter =  getDescendantsZero(2);
            while (iter.hasNext())
                showsName.add(((Element) iter.next()).getChild(TAG_SHOW_NAME).getText());
        } else 
            throw new ItasaException("MyItasa Show: "+ error);
        return showsName;
    }
    
    public ArrayList<Object[]> newsList(int idStart) throws JDOMException, ConnectException, 
                                                        IOException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        connectHttps(composeUrl(URL_NEWS, params));
        checkStatus();
        ArrayList<Object[]> container = new ArrayList<Object[]>();
        if (isStatusSuccess()){
            Iterator iter =  getDescendantsZero(2);
            boolean next = true;
            while (next && iter.hasNext()){
                Element item = (Element) iter.next();
                String id = item.getChild(TAG_NEWS_ID).getText();
                if (Integer.parseInt(id)>idStart){
                    String thumb = item.getChild(TAG_NEWS_THUMB).getText();
                    String text = getNameShowEpisodes(item);
                    container.add(new Object[]{id, text, new ImageIcon(new URL(thumb))});
                } else 
                    next = false;
            }
        } else 
            throw new ItasaException("NewsList: "+ error);
        return container;
    }
    
    private String getNameShowEpisodes(Element item){
        String text = item.getChild(TAG_NEWS_SHOWNAME).getText();
        Iterator temp = item.getChild(TAG_NEWS_EPISODES).getChildren().iterator();
        while (temp.hasNext()){
            text += " " + ((Element)temp.next()).getText();
        }
        return text;
    }
    
    public News newsSingle(String id) throws JDOMException, IOException, Exception{
        connectHttps(composeUrl(URL_NEWS_SINGLE.replaceFirst(STRING_REPLACE, id), null));
        checkStatus();
        News news = null;
        if (isStatusSuccess()){
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            Element item = (Element) iter.next();
            String title = getNameShowEpisodes(item);
            String image = item.getChild(TAG_NEWS_IMAGE).getText();
            //String date = item.getChild(TAG_NEWS_SUBMITDATE).getText();
            String translation = item.getChild(TAG_NEWS_TRANSLATION).getText();
            String sync = item.getChild(TAG_NEWS_SYNC).getText();
            //String resync = item.getChild(TAG_NEWS_RESYNC).getText();
            String info = item.getChild(TAG_NEWS_INFO).getText();
            String imageBy = item.getChild(TAG_NEWS_IMAGEBY).getText();
            String submitted = item.getChild(TAG_NEWS_SUBMITTEDBY).getText();
            Iterator temp = item.getChild(TAG_NEWS_SUBTITLES).getChildren().iterator();
            ArrayList<String> subtitles = new ArrayList<String>();
            while (temp.hasNext()){
                Element e = (Element)temp.next();
                subtitles.add(e.getChild("id").getText());
            }
            news = new News(title, image, translation, sync, info, imageBy, submitted, subtitles);
        } else 
            throw new ItasaException("NewsList: "+ error);
        return news;
    }
    
    /*TODO: non implementare finchè non viene risolto il problema di risposta 
     * che impiega troppo
     */
    private void myItasaLastSub(String authcode) throws JDOMException, IOException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        params.add(PARAM_AUTHCODE + authcode);
        connectHttps(composeUrl(URL_MYITASA_LAST_SUB, params));
    }
    
    /**Compone la url compresa di parametri
     * 
     * @param url url base
     * @param params parametri da aggiungere per la query
     * @return 
     */
    private String composeUrl(final String url, ArrayList<String> params){
        String newUrl = url + API_KEY;
        if (params!=null)
            for (int i=0; i<params.size(); i++)
                newUrl+= OPERATOR_AND + params.get(i).toString();
        //per vedere la stringa che genera come url per la chiamata all'api itasa
        //System.out.println(newUrl);
        return newUrl;
    }
    
    /**controlla lo status presente nel document
     * se falso preleva/imposta il msg d'error
     */
    private void checkStatus(){
        Iterator iterator = document.getContent().iterator();
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            status = item.getChild(TAG_STATUS).getText();
            if (isStatusFail())
                error = ((Element)item.getChildren(TAG_ERROR).get(0)).
                                        getChild(TAG_ERROR_MESSAGE).getText();
        }
    }
    
    private int getResponseCount(){
        Element item = (Element) getDescendantsZero(0).next();
        return Integer.parseInt(item.getChild(TAG_COUNT).getText());
    }
    
    private void connectHttps(String url) throws JDOMParseException, JDOMException, 
                                                ConnectException, IOException, Exception {
        try {
            InputStream is = Https.getInstance().connection(url);
            document = new SAXBuilder().build(is);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (KeyManagementException ex) {
            ex.printStackTrace();
        }
    }
    
    /**Controlla se lo status è positivo
     * 
     * @return
     */
    private boolean isStatusSuccess(){
        return status.equals(STATUS_SUCCESS);
    }
    
    /**Controlla se lo status è fail
     * 
     * @return 
     */
    private boolean isStatusFail(){
        return status.equals(STATUS_FAIL);
    }
    
    public String getUrlDownloadSubById(String id, String auth) throws ConnectException, Exception{
        ArrayList<String> params = new ArrayList<String>();
        params.add(PARAM_SUBTITLE_ID + id);
        params.add(PARAM_AUTHCODE + auth);
        return Https.getInstance().getLocationRedirect(composeUrl(URL_SUBTITLE_DOWNLOAD, params));
    }
    
    public static void main (String[] args){
        ItasaOnline i = new ItasaOnline();
        try {
            ItasaUser iu = i.login("judge", "qwerty");
            if (iu.isMyitasa()){
                //i.myItasaLastSub(iu.getAuthcode());
                //i.myItasaShows(iu.getAuthcode());
                String u = i.getUrlDownloadSubById("43087", iu.getAuthcode());
            }
            //i.showList();
            //i.showSingle("1363", true);
            //i.subtitleSingle("20000");
            //https://api.italiansubs.net/api/rest/subtitles/20000?apikey=436e566f3d09b217cf687fa5bad5effc
            //i.subtitleListByIdShow(1363,"720p",1);
            //https://api.italiansubs.net/api/rest/subtitles?apikey=436e566f3d09b217cf687fa5bad5effc&show_id=1363&version=720p&page=1
            //i.searchSubtitleCompletedByIdShow(134,null,1);
            //i.subtitleSearch("134", null, "1x01", 1);
            //https://api.italiansubs.net/api/rest/subtitles/search?apikey=436e566f3d09b217cf687fa5bad5effc&show_id=134&q=1x01&page=1
            //i.newsList(0);
            //https://api.italiansubs.net/api/rest/news?apikey=436e566f3d09b217cf687fa5bad5effc
            //i.newsSingle("13617");
            //https://api.italiansubs.net/api/rest/news/13617?apikey=436e566f3d09b217cf687fa5bad5effc
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ItasaException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}