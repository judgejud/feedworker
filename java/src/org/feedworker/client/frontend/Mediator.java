package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
//IMPORT JAVAX
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;
//IMPORT JRSS2SUB
import org.feedworker.client.ApplicationSettings;
import org.feedworker.client.Kernel;
import org.feedworker.client.Rss;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.client.frontend.events.MyJFrameEventListener;
import org.feedworker.client.frontend.events.MyTextPaneEvent;
import org.feedworker.client.frontend.events.MyTextPaneEventListener;
import org.feedworker.client.frontend.events.TableRssEventListener;
import org.feedworker.client.frontend.events.TableXmlEventListener;
import org.feedworker.util.FilterSub;
import org.feedworker.util.ManageException;
import org.feedworker.util.Quality;
//IMPORT MYUTILS
import org.lp.myUtils.Awt;
import org.lp.myUtils.lang.Lang;
//IMPORT SUN
import com.sun.syndication.io.FeedException;
/**Classe mediatrice tra gui e kernel, detta anche kernel della gui.
 *
 * @author luca
 */
public class Mediator {
    private static Mediator proxy = null;
    private Kernel core = Kernel.getIstance();
    private ApplicationSettings prop = ApplicationSettings.getIstance();
    private List listenerTextPane  = new ArrayList();
    private List listenerJFrame = new ArrayList();
    private ManageException error = ManageException.getIstance();
    /**Restituisce l'istanza attiva del Mediator
     * se non esiste la crea
     * @return Mediator
     */
    public static Mediator getIstance(){
        if (proxy==null)
            proxy = new Mediator();
        return proxy;        
    }
    /**Restituisce il testo itasa
     *
     * @return itasa
     */
    String getItasa(){
        return core.ITASA;
    }
    /**Restituisce il testo myitasa
     *
     * @return myitasa
     */
    String getMyItasa(){
        return core.MYITASA;
    }
    /**Restituisce il testo subsfactory
     *
     * @return subsfactory
     */
    String getSubsf(){
        return core.SUBSF;
    }

    String getMySubsf(){
        return core.MYSUBSF;
    }
    /**Restituisce il testo eztv
     *
     * @return eztv
     */
    String getEztv(){
        return core.EZTV;
    }
    /**Restituisce il testo btchat
     *
     * @return btchat
     */
    String getBtchat(){
        return core.BTCHAT;
    }
    /**Pulisce la tabella specificata dai check
     *
     * @param jt tabella
     */
    void cleanSelect(JTable jt){
        for (int i=0; i<jt.getRowCount(); i++)
            jt.setValueAt(false, i, 3);
    }
    /**Copia nella clipboard i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    void copyLinkTorrent(JTable jt1, JTable jt2){
        String text = "";
        for (int i=0; i<jt1.getRowCount(); i++)
            if (jt1.getValueAt(i, 3) == Boolean.TRUE){
                text += jt1.getValueAt(i, 0).toString() + "\n";
                jt1.setValueAt(false, i, 3);
            }
        for (int i=0; i<jt2.getRowCount(); i++)
            if (jt2.getValueAt(i, 3) == Boolean.TRUE){
                text += jt2.getValueAt(i, 0).toString() + "\n";
                jt2.setValueAt(false, i, 3);
            }
        if (!text.equalsIgnoreCase("")){
            Awt.setClipboard(text);
            fireNewTextPaneEvent("link copiati nella clipboard",MyTextPaneEvent.OK);
        }
    }    
    /**verifica le tabelle se sono flaggate per i download e invoca il kernel coi link per
     *il loro download
     * @param jt1 tabella1
     * @param jt2 tabella2
     * @param itasa tabelle itasa
     */
    void downloadSub(JTable jt1, JTable jt2, boolean itasa){
        ArrayList<String> alLinks = new ArrayList<String>();
        alLinks = addLinks(jt1);
        if (jt2!=null)
            alLinks.addAll(addLinks(jt2));
        if (alLinks.size() > 0)
            core.downloadSub(alLinks, itasa);
        else {
            String temp = "dalle tabelle";
            if (!itasa)
                temp = "dalla tabella";
            printAlert("Selezionare almeno un rigo " + temp);
        }
    }

    void downloadTorrent(JTable jt1, JTable jt2){
        if (Lang.verifyTextNotNull(prop.getTorrentDestinationFolder())) {
            ArrayList<String> alLinks = addLinks(jt1);
            alLinks.addAll(addLinks(jt2));
            if (alLinks.size() > 0)
                core.downloadTorrent(alLinks);
            else
                printAlert("Selezionare almeno un rigo dalle tabelle");
        } else
            printAlert("Non posso salvare perchè non hai specificato " +
                    "una cartella dove scaricare i file.torrent");
    }
    
    void runRss(){
        core.runRss();
    }

    void closeApp(String date){
        core.closeApp(date);
    }

    void setTableRssListener(TableRssEventListener listener){
        core.addTableRssEventListener(listener);
    }

    void setTableXmlListener(TableXmlEventListener listener){
        core.addTableXmlEventListener(listener);
    }

    void setTextPaneListener(MyTextPaneEventListener listener){
        core.addMyTextPaneEventListener(listener);
        core.setDownloadThreadListener(listener);
        ManageException.getIstance().addMyTextPaneEventListener(listener);
        addMyTextPaneEventListener(listener);

    }

    void setFrameListener(MyJFrameEventListener listener){
        core.addMyJFrameEventListener(listener);
        addMyJFrameEventListener(listener);
    }

    DefaultMutableTreeNode getTreeNode(){
        return core.getSettingsNode();
    }

    void writeProp(){
        core.writeProp();
    }

    void setLookFeel(){
        core.setLookFeel();
    }

    void restartRss(){
        core.stopAndRestartTimer();
    }
    /**testa la validità rss
     *
     * @param link rss
     * @return booleano che verifica la validità
     */
    boolean testRss(String link, String from) throws MalformedURLException {
        boolean passed = false;
        new URL(link);
        try {
            new Rss(link);
            passed = true;
        } catch (FeedException ex) {
            error.launch(ex, getClass(), from);
        } catch (IllegalArgumentException ex) {
            error.launch(ex, getClass());            
        } catch (IOException ex) {
            error.launch(ex, getClass(), null);
        }
        return passed;
    }

    boolean testSamba(String ip, String dir, String domain, String user, String pwd){
        return core.testSamba(ip, dir, dir, user, pwd);
    }

    void saveRole(tableXml jtable){
        boolean _break = false;
        TreeMap<FilterSub, String> temp = new TreeMap<FilterSub,String>();
        for (int i=0; i<jtable.getRowCount();i++){
            String name = ((String)jtable.getValueAt(i, 0));
            String season = (String)jtable.getValueAt(i, 1);
            String quality = (String)jtable.getValueAt(i, 2);
            String path = (String)jtable.getValueAt(i, 3);
            String status = (String)jtable.getValueAt(i, 4);
            String day = (String)jtable.getValueAt(i, 5);            

            if (Lang.verifyTextNotNull(name)){
                if (Lang.verifyTextNotNull(path)){
                    try {
                        if (Lang.verifyTextNotNull(season)){
                            int s = Lang.stringToInt(season);
                            season = Lang.intToString(s);
                        } else {
                            printAlert("Riga: "+ i + " immettere un numero alla stagione");
                            _break = true;
                            break;
                        }
                        FilterSub f = new FilterSub(name, season, quality, status, day);
                        if (!temp.containsKey(f))
                            temp.put(f, path);
                        else{
                            printAlert("Riga: "+ i +
                                    " trovato duplicato, si prega di correggerlo");
                        _break = true;
                        break;
                        }
                    } catch (NumberFormatException ex){
                        error.launch(ex, getClass(),Lang.intToString(i));
                        _break = true;
                        break;
                    }
                } else {
                    printAlert("Riga: "+ i + " immettere la destinazione per la regola/sub");
                    _break = true;
                    break;
                }
            } else {
                printAlert("Riga: "+ i + " immettere il nome della regola/sub/serie");
                _break = true;
                break;
            }
        }
        if (!_break)
            core.saveMap(temp);
    }    
    /**Aggiunge i link corrispondenti al true della colonna download nell'arraylist
     *
     * @param jt jtable su cui operare
     * @return Arraylist di stringhe
     */
    ArrayList<String> addLinks(JTable jt){
        ArrayList<String> alLinks = new ArrayList<String>();
        for (int i=0; i<jt.getRowCount(); i++){
            if (jt.getValueAt(i, 3) == Boolean.TRUE)
                alLinks.add(jt.getValueAt(i, 0).toString());
        }
        return alLinks;
    }

    String[] getElemEnum(){
        return core.getQuality();
    }

    void synoMoveVideo(){
        core.synoMoveVideo();
    }

    void synoStatus(){
        core.synoStatus();
    }

    /**verifica impostazioni torrent
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveTorrent(String text) {
        if (!Lang.verifyTextNotNull(text))
            printAlert("La Destinazione dei Torrent non può essere vuota");
        prop.setTorrentDestinationFolder(text);
        return true;
    }
    /**verifica impostazioni subsf
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveSubsf(String text) {
        boolean check = true;
        if (Lang.verifyTextNotNull(text)) {
            try {
                new URL(text);
                check = testRss(text, "subsfactory");
                prop.setSubsfactoryFeedURL(text);
            } catch (MalformedURLException e) {
                error.launch(e, getClass(), "subsfactory");
                check = false;
            }
        } else
            printAlert("Non immettendo link RSS Subsfactory non potrai usare il tab Subsfactory");
        return check;
    }
    //TODO sistemare il controllo itasa
    /**verifica impostazioni itasa
     *
     * @return booleano che le impostazioni sono ok
     */
    boolean checkSaveItasa(String itasa, String myitasa, String user,
            String pwd) {
        boolean check = true;
        try {
            if (Lang.verifyTextNotNull(itasa))
                check = testRss(itasa, "itasa");
            if (check){
                if (Lang.verifyTextNotNull(myitasa))
                    check = testRss(myitasa, "myitasa");
                if (check){
                    if (!Lang.verifyTextNotNull(user))
                        printAlert("L'Username Itasa non può essere vuoto.");
                    else if (!Lang.verifyTextNotNull(new String(pwd)))
                        printAlert("La Password Itasa non può essere vuota.");
                }
            }
            if (!Lang.verifyTextNotNull(itasa) && !Lang.verifyTextNotNull(myitasa))
                printAlert("Non immettendo rss itasa e myitasa non potrai usare " +
                        "i feed di italiansubs");
        } catch (MalformedURLException ex) {
            error.launch(ex, getClass(), "Itasa");
            check = false;
        }
        return check;
    }

    boolean checkSaveGlobal(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd) {
        boolean check = false;
        if (dirLocal) {
            if (!Lang.verifyTextNotNull(destSub))
                printAlert("INPUT OBBLIGATORIO: La Destinazione Locale non può essere vuota.");
            else
                check = true;
        } else { //SAMBA selected
            if (!Lang.verifyTextNotNull(sambaDomain))
                printAlert("INPUT OBBLIGATORIO: Il Dominio Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaIP))
                printAlert("INPUT OBBLIGATORIO: L'ip Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaDir))
                printAlert("INPUT OBBLIGATORIO: La cartella condivisa Samba non può essere vuota.");
            else if (!Lang.verifyTextNotNull(sambaUser))
                printAlert("INPUT OBBLIGATORIO: L'utente Samba non può essere vuoto.");
            else if (!Lang.verifyTextNotNull(sambaPwd))
                printAlert("INPUT OBBLIGATORIO: La password Samba non può essere vuota.");
            else if (!proxy.testSamba(sambaIP, sambaDir, sambaDomain,
                                    sambaUser, sambaPwd))
                printAlert("Impossibile connettermi al server/dir condivisa Samba");
            else
                check = true;
        }
        return check;
    }

    void saveSettings(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String laf, boolean audio, String timeout, boolean advancedDest,
            String itasa, String myitasa, String user, String pwd, boolean auto,
            String subsf, String torrent) {
        String oldLF = prop.getApplicationLookAndFeel();
        String oldMin = prop.getRefreshInterval();
        boolean first = prop.isFirstTimeRun();
        boolean oldAD = prop.enabledCustomDestinationFolder();
        boolean save = false;
        if (checkSaveGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                sambaUser, sambaPwd)) {
            save = true;
            if (prop.hasItasaOption() && save)
                save = checkSaveItasa(itasa, myitasa, user, pwd);            
            if (prop.hasSubsfactoryOption() && save)
                save = checkSaveSubsf(subsf);
            if (prop.hasTorrentOption() && save)
                checkSaveTorrent(torrent);
        }
        if (save) {
            setPropGlobal(dirLocal, destSub, sambaDomain, sambaIP, sambaDir,
                sambaUser, sambaPwd, time, laf, audio, timeout, advancedDest);
            setPropItasa(itasa, myitasa, user, pwd, auto);
            proxy.writeProp();
            if (!oldLF.equals(prop.getApplicationLookAndFeel()))
                setLookFeel();
            if (oldAD != prop.enabledCustomDestinationFolder()) {
                if (prop.enabledCustomDestinationFolder())
                    fireNewJFrameEvent("ADD_PANE_RULEZ");
                else
                    fireNewJFrameEvent("REMOVE_PANE_RULEZ");
            }
            if (!prop.isFirstTimeRun() && first) {
                fireNewJFrameEvent("ENABLED_BUTTON");
                runRss();
            } else {
                if (Lang.verifyTextNotNull(oldMin) &&
                        !oldMin.equalsIgnoreCase(prop.getRefreshInterval()))
                    restartRss();
                /*
                if ((Lang.verifyTextNotNull(oldTor)) &
                 (!oldTor.equalsIgnoreCase(prop.getRssTorrent()))){
                jtTorrent.removeAllRows();
                core.runTorrent();
                }
                 */
            }
            fireNewTextPaneEvent("Impostazioni salvate in " + prop.getSettingsFilename(),
                    MyTextPaneEvent.OK);
        }
    }

    private void setPropGlobal(boolean dirLocal, String destSub, String sambaDomain,
            String sambaIP, String sambaDir, String sambaUser, String sambaPwd,
            String time, String laf, boolean audio, String timeout, boolean advancedDest) {
        prop.localFolder(dirLocal);
        prop.setSubtitleDestinationFolder(destSub);
        prop.setRefreshInterval(time);
        prop.setApplicationLookAndFeel(laf);
        prop.enableAudioAdvisor(audio);
        prop.setCifsShareDomain(sambaDomain);
        prop.setCifsShareLocation(sambaIP);
        prop.setCifsSharePath(sambaDir);
        prop.setCifsShareUsername(sambaUser);
        prop.setCifsSharePassword(sambaPwd);
        prop.setHttpTimeout(timeout);
        prop.enableCustomDestinationFolder(advancedDest);
        //prop.setFont(jcbFont.getSelectedItem().toString());
    }

    private void setPropItasa(String itasa, String myitasa, String user, String pwd,
            boolean auto) {
        prop.setItasaFeedURL(itasa);
        prop.setMyitasaFeedURL(myitasa);
        prop.setMyitasaUsername(user);
        prop.setMyitasaPassword(pwd);
        prop.setAutoDownload(auto);
    }

    /**Invia alla download station del nas i link torrent selezionati
     *
     * @param jt1 tabella1
     * @param jt2 tabella2
     */
    void fireTorrentToNas(JTable jt1, JTable jt2){
        ArrayList<String> al = new ArrayList<String>();
        for (int i=0; i<jt1.getRowCount(); i++)
            if (jt1.getValueAt(i, 3) == Boolean.TRUE){
                al.add(jt1.getValueAt(i, 0).toString());
                jt1.setValueAt(false, i, 3);
            }
        for (int i=0; i<jt2.getRowCount(); i++)
            if (jt2.getValueAt(i, 3) == Boolean.TRUE){
                al.add(jt2.getValueAt(i, 0).toString());
                jt2.setValueAt(false, i, 3);
            }
        if (al.size()>0)
            core.synoDownloadRedirectory(al);
    }

    void synoClearFinish() {
        core.synoClearFinish();
    }

    Color searchVersion(String text){
        Color col = Color.cyan;
        String[] temp = text.split(" ");
        String version = temp[temp.length-1].toLowerCase();
        if (version.equals(Quality.FORM_1080p.toString()))
            col = Color.blue;
        else if (version.equals(Quality.FORM_720p.toString()))
            col = Color.red;
        else if (version.equals(Quality.DVDRIP.toString()))
            col = new Color(183, 65, 14);
        else if (version.equals(Quality.HR.toString()))
            col = Color.green;
        else if (version.equals(Quality.BLURAY.toString()))
            col = Color.magenta;
        return col;
    }

    private void printAlert(String msg){
        fireNewTextPaneEvent(msg, MyTextPaneEvent.ALERT);
    }

    // This methods allows classes to register for MyEvents
    public synchronized void addMyTextPaneEventListener(MyTextPaneEventListener listener) {
        listenerTextPane.add(listener);
    }

    // This methods allows classes to unregister for MyEvents
    public synchronized void removeMyTextPaneEventListener(MyTextPaneEventListener listener) {
        listenerTextPane.remove(listener);
    }

    private synchronized void fireNewTextPaneEvent(String msg, String type) {
        MyTextPaneEvent event = new MyTextPaneEvent(this, msg, type);
        Iterator listeners = listenerTextPane.iterator();
        while(listeners.hasNext() ) {
            MyTextPaneEventListener myel = (MyTextPaneEventListener)listeners.next();
            myel.objReceived(event);
        }
    }

    public synchronized void addMyJFrameEventListener(MyJFrameEventListener listener) {
        listenerJFrame.add(listener);
    }

    public synchronized void removeMyJFrameEventListener(MyJFrameEventListener listener) {
        listenerJFrame.remove(listener);
    }

    private synchronized void fireNewJFrameEvent(String oper) {
        MyJFrameEvent event = new MyJFrameEvent(this, oper);
        Iterator listeners = listenerJFrame.iterator();
        while (listeners.hasNext()) {
            MyJFrameEventListener myel = (MyJFrameEventListener) listeners.next();
            myel.objReceived(event);
        }
    }
}