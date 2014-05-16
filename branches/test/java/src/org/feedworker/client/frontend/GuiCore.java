package org.feedworker.client.frontend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.frontend.component.taskpaneShowInfo;
import org.feedworker.client.frontend.component.listShow;
import org.feedworker.client.frontend.events.CanvasEventListener;
import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.core.Kernel;
import org.feedworker.core.ManageListener;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Operation;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;
import org.feedworker.util.Common;

import org.jfacility.java.awt.AWT;
import org.jfacility.java.lang.JVM;
import org.jfacility.java.lang.Lang;
import org.jfacility.java.lang.SystemProperty;
import org.jfacility.javax.swing.Swing;
/**
 *
 * @author Administrator
 */
public class GuiCore {
    private final String ENABLE_BUTTON = "enableButton";
    private final String IMAGE_FEED_NEW = "rss_feed.png";
    private final String IMAGE_FEED_MYITASA = "rss_myitasa.png";
    private final String IMAGE_FEED_NORMAL = "ApplicationIcon.png";
    //private final String IMAGE_FEED_NORMAL = "feed1.png";
    private final String IMAGE_ADD = "add.png";
    private final String IMAGE_CLEAN1 = "clean1.png";
    private final String IMAGE_CLEAN2 = "clean2.png";
    private final String IMAGE_CLONE = "clone.png";
    private final String IMAGE_CLOSE = "close.png";
    private final String IMAGE_CLIPBOARD = "clipboard.png";
    private final String IMAGE_DOWNLOAD = "download.png";
    private final String IMAGE_EMAIL = "email.png";
    private final String IMAGE_FOLDER = "folder.png";
    private final String IMAGE_IMPORT1 = "import1.png";
    private final String IMAGE_IMPORT2 = "import2.png";
    private final String IMAGE_NAS = "nas.png";
    private final String IMAGE_REFRESH1 = "refresh1.png";
    private final String IMAGE_REFRESH2 = "refresh2.png";
    private final String IMAGE_REMOVE = "remove.png";
    private final String IMAGE_REMOVE_ALL = "remove_all.png";
    private final String IMAGE_RESET = "reset.png";
    private final String IMAGE_SAVE = "save.png";
    private final String IMAGE_SEARCH = "search.png";
    private final String IMAGE_SEE = "see.png";
    private final String IMAGE_SELECT1 = "select1.png";
    private final String IMAGE_SELECT2 = "select2.png";
    private final String IMAGE_TAB_ADD = "tab_add.png";
    private final String IMAGE_TAB_DEL = "tab_del.png";
    private final String IMAGE_TAB_EDIT = "tab_edit.png";
    private final String IMAGE_UNDO = "undo.png";
    private final String IMAGE_WWW = "www.png";
    private final String IMAGE_CONNECT_IRC = "irc.png";
    private final String IMAGE_DISCONNECT = "disconnect.png";
    private final String IMAGE_JOIN_ITALIANSUBS = "join_itasa.png";
    private final String IMAGE_JOIN_ITASA_CASTLE = "join_castle.png";
    private final String IMAGE_NICK = "nick.png";
    
    private final FileNameExtensionFilter fnfeZIP =
                                new FileNameExtensionFilter("ZIP file", "zip");
    
    private static GuiCore core = null;
    private Mediator proxy = Mediator.getIstance();
    private TreeMap<Object, taskpaneShowInfo> mapPaneShows = 
                                    new TreeMap<Object, taskpaneShowInfo>();
    private TreeSet<String> setListShows = new TreeSet<String>();
    private ApplicationSettings prop = proxy.getSettings();
    private Kernel kernel = Kernel.getIstance();
    
    public static GuiCore getInstance(){
        if (core==null)
            core = new GuiCore();
        return core;
    }
    
    public void setTableListener(TableEventListener listener) {
        ManageListener.addTableEventListener(listener);
    }

    public void setTextPaneListener(TextPaneEventListener listener) {
        ManageListener.addTextPaneEventListener(listener);
    }
    
    public void setStatusBarListener(StatusBarEventListener listener) {
        ManageListener.addStatusBarEventListener(listener);
    }

    void setFrameListener(FrameEventListener listener) {
        ManageListener.addFrameEventListener(listener);
    }
    
    public void setComboboxListener(ComboboxEventListener listener) {
        ManageListener.addComboBoxEventListener(listener);
    }
    
    public void setListListener(ListEventListener listener) {
        ManageListener.addListEventListener(listener);
    }
    
    public void setEditorPaneListener(EditorPaneEventListener listener) {
        ManageListener.addEditorPaneEventListener(listener);
    }
    
    public void setTabbedPaneListener(TabbedPaneEventListener listener) {
        ManageListener.addTabbedPaneEventListener(listener);
    }
    
    public void setCanvasListener(CanvasEventListener listener) {
        ManageListener.addCanvasEventListener(listener);
    }
    
    public void addNewSerial(){
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
    }
    
    public taskpaneShowInfo addNewTabShow(Object name){
        taskpaneShowInfo pane;
        if (!mapPaneShows.containsKey(name)){
            pane = new taskpaneShowInfo(name.toString());
            mapPaneShows.put(name, pane);
            proxy.infoShow(name.toString());
        } else 
           pane = (taskpaneShowInfo) mapPaneShows.get(name);
        return pane;
    }
    
    public taskpaneShowInfo refreshTabShow(Object name){
        taskpaneShowInfo pane = (taskpaneShowInfo) mapPaneShows.get(name);
        pane.reset();
        proxy.infoShow(name.toString());
        return pane;
    }
    
    public boolean checkTabListShow(String name){
        if (!setListShows.contains(name)){
            setListShows.add(name);
            return true;
        } else{
            printAlert("nome tab esistente");
            return false;
        }
    }
    
    /**Pulisce la tabella specificata dai check
     *
     * @param jt
     *            tabella
     */
    public void cleanSelect(JTable jt, int col) {
        for (int i = 0; i < jt.getRowCount(); i++) {
            jt.setValueAt(false, i, col);
        }
    }

    /** Copia nella clipboard i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    public void copyLinkTorrent(JTable[] tables) {
        String text = "";
        for (int i=0; i<tables.length; i++){
            JTable jt = tables[i];
            for (int j = 0; j < jt.getRowCount(); j++) {
                if (jt.getValueAt(j, 3) == Boolean.TRUE) {
                    text += jt.getValueAt(j, 0).toString() + "\n";
                    jt.setValueAt(false, j, 3);
                }
            }
        }
        copy(text);
    }
    
    public Color searchVersion(String text) {
        Color col = Color.cyan;
        String[] temp = text.split(" ");
        String version = temp[temp.length - 1].toLowerCase();
        if (version.equals(Quality._1080p.toString())) {
            col = Color.blue;
        } else if (version.equals(Quality._1080i.toString())) {
            col = Color.orange;
        } else if (version.equals(Quality._720p.toString())) {
            col = Color.red;
        } else if (version.equals(Quality.DVDRIP.toString())) {
            col = new Color(183, 65, 14);
        } else if (version.equals(Quality.HR.toString())) {
            col = Color.green;
        } else if (version.equals(Quality.BLURAY.toString())) {
            col = Color.magenta;
        } else if (version.equals(Quality.WEB_DL.toString())) {
            col = Color.white;
        } else if (version.equals(Quality.BRRIP.toString())) {
            col = Color.black;
        } else if (version.equals(Quality.BDRIP.toString())) {
            col = Color.darkGray;
        }
        return col;
    }
    
    public void downloadSub(JTable jt1, JTable jt2, boolean itasa, boolean id, int col) {
        ArrayList<String> alLinks = new ArrayList<String>();
        alLinks = addLinks(jt1,col);
        if (jt2!=null)
            alLinks.addAll(addLinks(jt2,col));
        if (alLinks.size() > 0)
            proxy.downloadSub(alLinks, itasa, id);
        else {
            String temp = "dalle tabelle";
            if (!itasa)
                temp = "dalla tabella";
            proxy.printAlert("Selezionare almeno un rigo " + temp);
        }
    }
    
    public void downloadTorrent(JTable[] jt) {
        if (Lang.verifyTextNotNull(prop.getTorrentDestinationFolder())) {
            ArrayList<String> alLinks = new ArrayList<String>();
            for (int i=0; i<jt.length; i++)
                alLinks.addAll(addLinks(jt[i],3));
            if (alLinks.size() > 0)
                proxy.downloadTorrent(alLinks);
            else
                proxy.printAlert("Selezionare almeno un rigo dalle tabelle");
        } else 
            proxy.printAlert("Non posso salvare perchè non hai specificato "
                    + "una cartella dove scaricare i file.torrent");
    }
    
    public void saveRules(TableModel jtable) {
        boolean _break = false;
        TreeMap<KeyRule, ValueRule> temp = new TreeMap<KeyRule, ValueRule>();
        for (int r = 0; r < jtable.getRowCount(); r++) {
            int c = -1;
            String name = ((String) jtable.getValueAt(r, ++c));
            String season = jtable.getValueAt(r, ++c).toString();
            String quality = (String) jtable.getValueAt(r, ++c);
            String path = (String) jtable.getValueAt(r, ++c);
            String oper = (String) jtable.getValueAt(r, ++c);
            if (oper==null)
                oper = "";
            if (Lang.verifyTextNotNull(name)) {
                if (oper.equalsIgnoreCase(Operation.DELETE.toString()) || Lang.verifyTextNotNull(path)) {
                    try {
                        if (Lang.verifyTextNotNull(season)) {
                            int s = Lang.stringToInt(season);
                            season = Lang.intToString(s);
                        } else {
                            proxy.printAlert("Riga: " + r
                                    + " immettere un numero alla stagione");
                            _break = true;
                            break;
                        }
                        KeyRule key = new KeyRule(name, season, quality);
                        ValueRule value = new ValueRule(path, oper);
                        if (!temp.containsKey(key)) {
                            temp.put(key, value);
                        } else {
                            proxy.printAlert("Riga: " + r + " trovato "
                                    + "duplicato, si prega di correggerlo");
                            _break = true;
                            break;
                        }
                    } catch (NumberFormatException ex) {
                        proxy.getError().launch(ex, getClass(), Lang.intToString(r));
                        _break = true;
                        break;
                    }
                } else {
                    proxy.printAlert("Riga: " + r
                            + " immettere la destinazione per la regola/sub");
                    _break = true;
                    break;
                }
            } else {
                proxy.printAlert("Riga: " + r
                        + " immettere il nome della regola/sub/serie");
                _break = true;
                break;
            }
        } //end for
        if (!_break)
            proxy.saveMap(temp);
    }
    
    /**Invia alla download station del nas i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    public void fireTorrentToNas(JTable[] tables) {
        ArrayList<String> al = new ArrayList<String>();
        for (int j=0; j<tables.length; j++){
            JTable jt = tables[j];
            for (int i = 0; i < jt.getRowCount(); i++) {
                if (jt.getValueAt(i, 3) == Boolean.TRUE) {
                    al.add(jt.getValueAt(i, 0).toString());
                    jt.setValueAt(false, i, 3);
                }
            }
        }
        if (al.size() > 0)
            proxy.synoDownloadRedirectory(al);
    }
    
    public void searchSubItasa(Object show, Object version, boolean complete, 
                                String season, String episode) {
        try{
            boolean check = false;
            if (Lang.verifyTextNotNull(season)){
                Lang.stringToInt(season);
                check = true;
            }
            if (Lang.verifyTextNotNull(episode)){
                Lang.stringToInt(episode);
                check = true;
            }
            if (!check && complete)
                check = true;
            if (check)
                proxy.searchSubItasa(show, version, complete, season, episode);
            else 
                proxy.printAlert("Selezionare almeno un elemento di ricerca tra "
                        + "stagione completa, numero stagione e/o numero episodio");
        } catch (NumberFormatException e){
            proxy.printAlert("Immettere un numero alla stagione e/o episodio "
                        + "invece di una stringa");
        }
    }
    
    public boolean saveSettings(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String timeout,
            boolean advancedDownload, boolean runIconized, String itasa,
            String myitasa, String itasaUser, String itasaPwd, boolean autoMyitasa,
            boolean autoLoadMyItasa, String subsf, String tv24, String torrentDest,
            String mailTO,  String smtp, boolean paneLog, boolean paneSearch, 
            boolean paneSetting, boolean paneSubDest, boolean paneReminder, 
            boolean reminder, String googleUser, String googlePwd, String googleCalendar, 
            boolean paneTorrent, boolean paneCalendar, boolean paneShow, boolean blog, 
            boolean paneBlog, boolean itasapm, boolean paneCalendarDay, boolean calendarDay, 
            boolean paneIrc, String ircNick, String ircPwd, boolean itasaRss, 
            boolean myItasaRss, boolean itasaNews, boolean noDuplicateAll, 
            boolean noDuplicateSingle, String ircServer, boolean eztv, boolean btchat,
            boolean karmorra, boolean mykarmorra, String urlMyKarmorra, boolean paneITasaNews, 
            boolean paneITasaRss, boolean paneSubsfactory) {
                
        String oldMin = prop.getRefreshInterval();
        boolean first = prop.isApplicationFirstTimeUsed();
        boolean save = false;
        if (checkSaveGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                sambaUser, sambaPwd)) {
            save = true;
            if (save)
                save = checkSaveItasa(itasaRss, itasa, myItasaRss, myitasa, itasaUser, 
                                        itasaPwd, itasapm);
            if (prop.isOtherSubsOption() && save)
                save = checkSaveOtherSubs(subsf, tv24);
            if ((eztv || btchat || karmorra || mykarmorra) && save && !Lang.verifyTextNotNull(torrentDest))
                proxy.printAlert("Avviso: Non immettendo la Destinazione dei Torrent non potrai "
                    + "scaricare .torrent");
        }
        if (save) {
            setPropGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                    sambaUser, sambaPwd, time, timeout, 
                    advancedDownload, runIconized, reminder);
            setPropItasa(itasa, myitasa, itasaUser, itasaPwd, autoMyitasa, autoLoadMyItasa, 
                        blog, itasapm, calendarDay, itasaRss, myItasaRss, itasaNews);
            setPropOtherSub(subsf, tv24);
            setPropTorrent(torrentDest, eztv, btchat, karmorra, mykarmorra, urlMyKarmorra);
            setPropAdvisor(mailTO, smtp, googleUser, googlePwd, googleCalendar);
            setPropVisiblePane(paneLog, paneSearch, paneSetting, paneSubDest, 
                            paneReminder, paneTorrent, paneCalendar, paneShow, 
                            paneBlog, paneCalendarDay, paneIrc, paneITasaNews, 
                            paneITasaRss, paneSubsfactory);
            setPropIrc(ircNick, ircPwd, ircServer);
            setPropShow(noDuplicateAll, noDuplicateSingle);
            kernel.writeProp();
            if (!prop.isApplicationFirstTimeUsed() && first) {
                ManageListener.fireFrameEvent(this, ENABLE_BUTTON);
                kernel.runRss(true);
            } else if (Lang.verifyTextNotNull(oldMin) && 
                        !oldMin.equalsIgnoreCase(prop.getRefreshInterval()))
                proxy.restartRss();
            proxy.printOk("Impostazioni salvate in " + prop.getSettingsFilename());
        }
        return save;
    }
    
    public int requestRemoveSeries(Component from, boolean table){
        String msg = "Vuoi eliminare le serie dalla tabella?";
        if (!table)
            msg = "Vuoi eliminare le serie e la categoria selezionata?";
        return JOptionPane.showConfirmDialog(from, msg, "Info", 
                                            JOptionPane.YES_NO_OPTION);
    }
    
    public void saveList(JTabbedPane jtp) {
        TreeMap<String, Object[]> map = new TreeMap<String, Object[]>();
        boolean save = true;
        int i = 0;
        int count = jtp.getTabCount();
        if (prop.isShowNoDuplicateAll() || prop.isShowNoDuplicateSingle()){
            TreeMap<String,String> tmGlobal = new TreeMap<String,String>();
            while(save && i<count){
                Object[] obj = ((listShow) jtp.getComponentAt(i)).getArrayModel();
                String tab = jtp.getTitleAt(i);
                TreeSet<String> tsSingle = new TreeSet<String>();
                int j=0;
                while(save && j<obj.length){
                    String name = ((Object[])obj[j])[0].toString();
                    if (save && prop.isShowNoDuplicateSingle()){
                        if (tsSingle.contains(name)){
                            save = false;
                            printAlert("Show - doppione trovato nella categoria "+ tab + ": "+ name);
                        } else
                            tsSingle.add(name);
                    }
                    if (save && prop.isShowNoDuplicateAll()){
                        if (tmGlobal.containsKey(name)){
                            save = false;
                            printAlert("Show - doppione trovato nelle categorie "+ tab + 
                                    " & " + tmGlobal.get(name) + ": "+ name );
                        } else
                            tmGlobal.put(name,tab);
                    }
                    map.put(jtp.getTitleAt(i), ((listShow) jtp.getComponentAt(i)).getArrayModel());
                    j++;
                }
                i++;
            }
        } else {
            for (; i<count; i++)
                map.put(jtp.getTitleAt(i), ((listShow) jtp.getComponentAt(i)).getArrayModel());
        }
        if (save)
            proxy.saveList(map);
    }
    
    void checkMenuNotify(int i, boolean value) {
        boolean check = true;
        if (i==2){
            if (!Lang.verifyTextNotNull(prop.getMailTO())||
                    !Lang.verifyTextNotNull(prop.getMailSMTP())) {
                check = false;
                proxy.printAlert("Per usare le notifiche email devono essere "
                        + "impostati i campi MailTO & SMTP");
            }
        }
        if (i==3){
            if (!Lang.verifyTextNotNull(prop.getGoogleUser())||
                    !Lang.verifyTextNotNull(prop.getGooglePwd()) || 
                    !Lang.verifyTextNotNull(prop.getGoogleCalendar())) {
                check = false;
                proxy.printAlert("Per usare le notifiche sms devono essere "
                        + "impostati i campi Google User Password Calendar");
            }
        }
        if (check)
            proxy.setPropNotify(i,value);
    }
    
    void invokeBackup(Component parent) {
        String name = Swing.getFile(parent, "Creare il file zip per il backup",
                fnfeZIP, new File(SystemProperty.getUserDir() + File.separator));
        if (name != null)
            proxy.backup(name);
    }
    
    private void setPropGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd, String time,
            String timeout, boolean advancedDownload,
            boolean runIconized, boolean reminder) {
        prop.setLocalFolder(dirLocal);
        prop.setSubtitleDestinationFolder(destSub);
        prop.setRefreshInterval(time);
        prop.setCifsShareDomain(sambaDomain);
        prop.setCifsShareLocation(sambaIP);
        prop.setCifsSharePath(sambaDir);
        prop.setCifsShareUsername(sambaUser);
        prop.setCifsSharePassword(sambaPwd);
        prop.setHttpTimeout(timeout);
        prop.setEnableAdvancedDownload(advancedDownload);
        prop.setEnableIconizedRun(runIconized);
        prop.setReminderOption(reminder);
    }

    private void setPropItasa(String itasa, String myitasa, String user,
            String pwd, boolean auto, boolean autoload, boolean blog, boolean pm, 
            boolean cal, boolean itasaRss, boolean myItasaRss, boolean news) {
        prop.setItasaFeedURL(itasa);
        prop.setMyitasaFeedURL(myitasa);
        prop.setItasaUsername(user);
        prop.setItasaPassword(pwd);
        prop.setAutoDownloadMyItasa(auto);
        prop.setAutoLoadDownloadMyItasa(autoload);
        prop.setItasaBlog(blog);
        prop.setItasaPM(pm);
        prop.setCalendarDay(cal);
        prop.setItasaNews(news);
        
    }
    
    private void setPropOtherSub(String subsf, String tv24){
        prop.setSubsfactoryFeedURL(subsf);
        prop.setTv24FeedUrl(tv24);
    }
    
    private void setPropAdvisor(String mailTO, String smtp, String googleUser,
            String googlePwd, String googleCalendar){
        prop.setMailTO(mailTO);
        prop.setMailSMTP(smtp);
        prop.setGoogleUser(googleUser);
        prop.setGooglePwd(googlePwd);
        prop.setGoogleCalendar(googleCalendar);
    }
    
    private void setPropVisiblePane(boolean log, boolean search, boolean setting, 
                                    boolean subdest, boolean reminder, boolean torrent,
                                    boolean calendar, boolean show, boolean blog, 
                                    boolean calendarDay, boolean irc, boolean itasaNews,
                                    boolean itasaRss, boolean othersubs){
        prop.setEnablePaneCalendar(calendar);
        prop.setEnablePaneLog(log);
        prop.setEnablePaneSearchSubItasa(search);
        prop.setEnablePaneSetting(setting);
        prop.setEnablePaneSubDestination(subdest);
        prop.setEnablePaneReminder(reminder);
        prop.setEnablePaneTorrent(torrent);
        prop.setEnablePaneShow(show);
        prop.setEnablePaneBlog(blog);
        prop.setEnablePaneCalendarDay(calendarDay);
        prop.setEnablePaneIrc(irc);
        prop.setEnablePaneItasaNews(itasaNews);
        prop.setEnablePaneItasaRSS(itasaRss);
        prop.setEnablePaneOtherSubs(othersubs);
    }
    
    private void setPropTorrent(String dest, boolean eztv, boolean btchat, boolean karmorra, 
                                boolean mykarmorra, String urlMyKarmorra){
        int count = 0;
        if (eztv)
            count++;
        if (btchat)
            count++;
        if (karmorra)
            count++;
        if (mykarmorra)
            count++;
        prop.setTorrentDestinationFolder(dest);
        prop.setTorrentEztvOption(eztv);
        prop.setTorrentBtchatOption(btchat);
        prop.setTorrentKarmorraOption(karmorra);
        prop.setTorrentMyKarmorraOption(mykarmorra);
        prop.setTorrentUrlMyKarmorra(urlMyKarmorra);
        prop.setTorrentCount(count);
    }
    
    private void setPropIrc(String nick, String pwd, String server){
        prop.setIrcNick(nick);
        prop.setIrcPwd(pwd);
        prop.setIrcServer(server);
    }
    
    private void setPropShow(boolean all, boolean single){
        prop.setShowNoDuplicateAll(all);
        prop.setShowNoDuplicateSingle(single);
    }
    
    /**Aggiunge i link corrispondenti al true della colonna download nell'arraylist
     *
     * @param jt jtable su cui operare
     * @return Arraylist di stringhe
     */
    private ArrayList<String> addLinks(JTable jt, int col) {
        ArrayList<String> alLinks = new ArrayList<String>();
        for (int i = 0; i < jt.getRowCount(); i++) {
            if (jt.getValueAt(i, col) == Boolean.TRUE)
                alLinks.add(jt.getValueAt(i, 0).toString());
        }
        return alLinks;
    }

    /**verifica impostazioni subsf
     *
     * @return booleano che le impostazioni sono ok
     */
    private boolean checkSaveOtherSubs(String subsf, String tv24) {
        boolean check = true;
        try {
            if (Lang.verifyTextNotNull(subsf))
                check = proxy.testRss(subsf, "subsfactory");
        } catch (MalformedURLException e) {
            proxy.getError().launch(e, getClass(), "subsfactory");
            check = false;
        }
        try {
            if (check && Lang.verifyTextNotNull(tv24))
                check = proxy.testRss(tv24, "tv24");
        } catch (MalformedURLException e) {
            proxy.getError().launch(e, getClass(), "tv24");
            check = false;
        }
        return check;
    }

    /**verifica impostazioni itasa
     *
     * @return booleano che le impostazioni sono ok
     */
    //TODO: inserire controllo itasapm con user e pwd
    private boolean checkSaveItasa(boolean itasaRss, String itasa, boolean myitasaRss, 
                                        String myitasa, String user, String pwd, boolean pm) {
        boolean check = true;
        try {
            //if (!Lang.verifyTextNotNull(itasa) && !Lang.verifyTextNotNull(myitasa)) {
            //if ((itasaRss && !Lang.verifyTextNotNull(itasa)) || (myitasaRss && !Lang.verifyTextNotNull(myitasa))) {
            if ((itasaRss && itasa.isEmpty()) || (myitasaRss && myitasa.isEmpty())) {
                printAlert("Avviso: Non immettendo link RSS itasa e/o myitasa " + 
                        "non potrai usare i feed italiansubs");
            } else {
                if (itasaRss && Lang.verifyTextNotNull(itasa))
                    check = proxy.testRss(itasa, "itasa");
                if (check) {
                    if (Lang.verifyTextNotNull(myitasa))
                        check = proxy.testRss(myitasa, "myitasa");
                    if (check) {
                        if (!Lang.verifyTextNotNull(user))
                            printAlert("Avviso: senza Username Itasa non " + 
                                    "potrai scaricare i subs");
                        else if (!Lang.verifyTextNotNull(new String(pwd)))
                            printAlert("Avviso: senza Password Itasa non " +
                                    "potrai scaricare i subs");
                    }
                }
            }
        } catch (MalformedURLException ex) {
            proxy.getError().launch(ex, getClass(), "Itasa");
            check = false;
        }
        return check;
    }

    private boolean checkSaveGlobal(boolean dirLocal, String destSub,
            String sambaDomain, String sambaIP, String sambaDir,
            String sambaUser, String sambaPwd) {
        boolean check = false;
        if (dirLocal) {
            if (!Lang.verifyTextNotNull(destSub))
                printAlert("INPUT OBBLIGATORIO: La Destinazione Locale non può "
                                                                + "essere vuota.");
            else
                check = true;
        } else { // SAMBA selected
            if (!Lang.verifyTextNotNull(sambaDomain))
                printAlert("INPUT OBBLIGATORIO: Il Dominio Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaIP))
                printAlert("INPUT OBBLIGATORIO: L'ip Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaDir))
                printAlert("INPUT OBBLIGATORIO: La cartella condivisa Samba non può essere "
                        + "vuota.");
            else if (!Lang.verifyTextNotNull(sambaUser))
                printAlert("INPUT OBBLIGATORIO: L'utente Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaPwd))
                printAlert("INPUT OBBLIGATORIO: La password Samba non può essere vuota.");
            else if (!proxy.testSamba(sambaIP, sambaDir, sambaDomain, sambaUser, sambaPwd))
                printAlert("Impossibile connettermi al server/dir condivisa Samba");
            else
                check = true;
        }
        return check;
    }
    
    private void printAlert(String msg){
        proxy.printAlert(msg);
    }

    public void checkQueryIrc(String name) {
        ManageListener.fireTabbedPaneEvent(this, name, "query");
    }
    
    public void copy(String text){
        if (!text.equalsIgnoreCase("")) {
            AWT.setClipboard(text);
            proxy.printOk("testo/link copiato/i nella clipboard");
        }
    }
    
    public void copySeasonEpisode(TableModel tm){
        String text = "";
        for (int i=0; i<tm.getRowCount(); i++)
            text += tm.getValueAt(i, 1) + " \"" + tm.getValueAt(i, 3) + "\" " +
                    tm.getValueAt(i, 2) +"\n";
        copy(text);
    }
    
    public boolean isJava17(){
        JVM jvm = new JVM();
        return jvm.isOrLater(17);
    }
    
    public void setPasteClipboard(JTextField text){
        String temp = "";
        try {
            temp = AWT.getClipboard();
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        text.setText(temp);
    }
    
    public ImageIcon getIconAdd() {
        return Common.getResourceImageButton(IMAGE_ADD);
    }
    
    public ImageIcon getIconClean1() {
        return Common.getResourceImageButton(IMAGE_CLEAN1);
    }
    
    public ImageIcon getIconClean2() {
        return Common.getResourceImageButton(IMAGE_CLEAN2);
    }
    
    public ImageIcon getIconClone() {
        return Common.getResourceImageButton(IMAGE_CLONE);
    }
    
    public ImageIcon getIconClose() {
        return Common.getResourceImageButton(IMAGE_CLOSE);
    }
    
    public ImageIcon getIconCopy() {
        return Common.getResourceImageButton(IMAGE_CLIPBOARD);
    }
    
    public ImageIcon getIconDownload() {
        return Common.getResourceImageButton(IMAGE_DOWNLOAD);
    }
    
    public ImageIcon getIconEmail() {
        return Common.getResourceImageButton(IMAGE_EMAIL);
    }
    
    public ImageIcon getIconFolder() {
        return Common.getResourceImageButton(IMAGE_FOLDER);
    }
    
    public ImageIcon getIconImport1() {
        return Common.getResourceImageButton(IMAGE_IMPORT1);
    }
    
    public ImageIcon getIconImport2() {
        return Common.getResourceImageButton(IMAGE_IMPORT2);
    }
        
    public ImageIcon getIconNas() {
        return Common.getResourceImageButton(IMAGE_NAS);
    }
    
    public ImageIcon getIconRefresh1() {
        return Common.getResourceImageButton(IMAGE_REFRESH1);
    }
    
    public ImageIcon getIconRefresh2() {
        return Common.getResourceImageButton(IMAGE_REFRESH2);
    }
    
    public ImageIcon getIconRemove() {
        return Common.getResourceImageButton(IMAGE_REMOVE);
    }
    
    public ImageIcon getIconRemoveAll() {
        return Common.getResourceImageButton(IMAGE_REMOVE_ALL);
    }
    
    public ImageIcon getIconReset() {
        return Common.getResourceImageButton(IMAGE_RESET);
    }
    public ImageIcon getIconSave() {
        return Common.getResourceImageButton(IMAGE_SAVE);
    }
    
    public ImageIcon getIconSearch() {
        return Common.getResourceImageButton(IMAGE_SEARCH);
    }
    
    public ImageIcon getIconSee() {
        return Common.getResourceImageButton(IMAGE_SEE);
    }
    
    public ImageIcon getIconSelect1() {
        return Common.getResourceImageButton(IMAGE_SELECT1);
    }
    
    public ImageIcon getIconSelect2() {
        return Common.getResourceImageButton(IMAGE_SELECT2);
    }
    
    public ImageIcon getIconTabAdd() {
        return Common.getResourceImageButton(IMAGE_TAB_ADD);
    }
    
    public ImageIcon getIconTabDel() {
        return Common.getResourceImageButton(IMAGE_TAB_DEL);
    }
    
    public ImageIcon getIconTabEdit() {
        return Common.getResourceImageButton(IMAGE_TAB_EDIT);
    }
        
    public ImageIcon getIconUndo() {
        return Common.getResourceImageButton(IMAGE_UNDO);
    }
    
    public ImageIcon getIconWWW() {
        return Common.getResourceImageButton(IMAGE_WWW);
    }
    
    public ImageIcon getIconConnectIrc() {
        return Common.getResourceImageButton(IMAGE_CONNECT_IRC);
    }
    
    public ImageIcon getIconDisconnect() {
        return Common.getResourceImageButton(IMAGE_DISCONNECT);
    }
    
    public ImageIcon getIconJoinItaliansubs() {
        return Common.getResourceImageButton(IMAGE_JOIN_ITALIANSUBS);
    }
    
    public ImageIcon getIconJoinItasaCastle() {
        return Common.getResourceImageButton(IMAGE_JOIN_ITASA_CASTLE);
    }
    
    Image getIconFeedNew() {
        return Common.getResourceIcon(IMAGE_FEED_NEW);
    }
    
    Image getIconFeedMyItasa() {
        return Common.getResourceIcon(IMAGE_FEED_MYITASA);
    }
    
    Image getIconFeedNormal() {
        return Common.getResourceIcon(IMAGE_FEED_NORMAL);
    }
    
    public ImageIcon getIconNick() {
        return Common.getResourceImageButton(IMAGE_NICK);
    }
    
    String getOperationEnableButton() {
        return ENABLE_BUTTON;
    }
}