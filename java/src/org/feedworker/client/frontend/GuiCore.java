package org.feedworker.client.frontend;

import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.feedworker.client.frontend.events.ComboboxEventListener;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.client.frontend.panel.tabInternalShow;
import org.feedworker.core.ManageListener;
import org.feedworker.object.KeyRule;
import org.feedworker.object.Quality;
import org.feedworker.object.ValueRule;

import org.jfacility.java.awt.AWT;
import org.jfacility.java.lang.Lang;
/**
 *
 * @author Administrator
 */
public class GuiCore {
    private static GuiCore core = null;
    private Mediator proxy = Mediator.getIstance();
    private TreeMap<Object, tabInternalShow> mapPaneShows = 
                                    new TreeMap<Object, tabInternalShow>();
    
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
    
    public void addNewSerial(){
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
    }
    
    public tabInternalShow addNewTabShow(Object name){
        tabInternalShow pane;
        if (!mapPaneShows.containsKey(name)){
            pane = new tabInternalShow(name.toString());
            mapPaneShows.put(name, pane);
            proxy.infoShow(name.toString());
        } else 
           pane = (tabInternalShow) mapPaneShows.get(name);
        return pane;
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
    public void copyLinkTorrent(JTable jt1, JTable jt2) {
        String text = "";
        for (int i = 0; i < jt1.getRowCount(); i++) {
            if (jt1.getValueAt(i, 3) == Boolean.TRUE) {
                text += jt1.getValueAt(i, 0).toString() + "\n";
                jt1.setValueAt(false, i, 3);
            }
        }
        for (int i = 0; i < jt2.getRowCount(); i++) {
            if (jt2.getValueAt(i, 3) == Boolean.TRUE) {
                text += jt2.getValueAt(i, 0).toString() + "\n";
                jt2.setValueAt(false, i, 3);
            }
        }
        if (!text.equalsIgnoreCase("")) {
            AWT.setClipboard(text);
            proxy.printOk("link copiati nella clipboard");
        }
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
    
    public void downloadSub(JTable jt1, JTable jt2, boolean itasa, boolean id) {
        ArrayList<String> alLinks = new ArrayList<String>();
        alLinks = addLinks(jt1);
        if (jt2!=null)
            alLinks.addAll(addLinks(jt2));
        if (alLinks.size() > 0)
            proxy.downloadSub(alLinks, itasa, id);
        else {
            String temp = "dalle tabelle";
            if (!itasa)
                temp = "dalla tabella";
            proxy.printAlert("Selezionare almeno un rigo " + temp);
        }
    }
    
    public void downloadTorrent(JTable jt1, JTable jt2) {
        if (Lang.verifyTextNotNull(proxy.getProperties().getTorrentDestinationFolder())) {
            ArrayList<String> alLinks = addLinks(jt1);
            alLinks.addAll(addLinks(jt2));
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
            boolean rename = false, delete = false;
            try {
                rename = Boolean.parseBoolean(jtable.getValueAt(r, ++c).toString());
            } catch (NullPointerException e) {
            }
            try {
                delete = Boolean.parseBoolean(jtable.getValueAt(r, ++c).toString());
            } catch (NullPointerException e) {
            }
            if (rename && delete) {
                proxy.printAlert("Riga: " + r + " non possono coesistere entrambi "
                                + "i flag true di rename e delete");
                _break = true;
                break;
            } else {
                if (Lang.verifyTextNotNull(name)) {
                    if (delete || Lang.verifyTextNotNull(path)) {
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
                            ValueRule value = new ValueRule(path, rename, delete);
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
    public void fireTorrentToNas(JTable jt1, JTable jt2) {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < jt1.getRowCount(); i++) {
            if (jt1.getValueAt(i, 3) == Boolean.TRUE) {
                al.add(jt1.getValueAt(i, 0).toString());
                jt1.setValueAt(false, i, 3);
            }
        }
        for (int i = 0; i < jt2.getRowCount(); i++) {
            if (jt2.getValueAt(i, 3) == Boolean.TRUE) {
                al.add(jt2.getValueAt(i, 0).toString());
                jt2.setValueAt(false, i, 3);
            }
        }
        if (al.size() > 0)
            proxy.synoDownloadRedirectory(al);
    }
    
    void checkMenuNotify(int i, boolean value) {
        boolean check = true;
        if (i==2){
            if (!Lang.verifyTextNotNull(proxy.getProperties().getMailTO())||
                    !Lang.verifyTextNotNull(proxy.getProperties().getMailSMTP())) {
                check = false;
                proxy.printAlert("Per usare le notifiche email devono essere "
                        + "impostati i campi MailTO & SMTP");
            }
        }
        if (i==3){
            if (!Lang.verifyTextNotNull(proxy.getProperties().getGoogleUser())||
                    !Lang.verifyTextNotNull(proxy.getProperties().getGooglePwd()) || 
                    !Lang.verifyTextNotNull(proxy.getProperties().getGoogleCalendar())) {
                check = false;
                proxy.printAlert("Per usare le notifiche sms devono essere "
                        + "impostati i campi Google User Password Calendar");
            }
        }
        if (check)
            proxy.setPropNotify(i,value);
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
    
    /**Aggiunge i link corrispondenti al true della colonna download nell'arraylist
     *
     * @param jt jtable su cui operare
     * @return Arraylist di stringhe
     */
    private ArrayList<String> addLinks(JTable jt) {
        ArrayList<String> alLinks = new ArrayList<String>();
        for (int i = 0; i < jt.getRowCount(); i++) {
            if (jt.getValueAt(i, 3) == Boolean.TRUE)
                alLinks.add(jt.getValueAt(i, 0).toString());
        }
        return alLinks;
    }
}