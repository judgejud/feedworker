package org.feedworker.client.frontend;

import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.feedworker.client.frontend.events.ComboboxEventListener;

import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.events.FrameEventListener;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.StatusBarEventListener;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;
import org.feedworker.client.frontend.events.TableEventListener;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.client.frontend.events.TextPaneEventListener;
import org.feedworker.client.frontend.panel.tabInternalShow;
import org.feedworker.core.ManageListener;
import org.jfacility.java.awt.AWT;

import org.jfacility.java.lang.Lang;
/**
 *
 * @author Administrator
 */
public class GuiCore {
    private static GuiCore core = null;
    
    private Mediator proxy = Mediator.getIstance();
    private TreeMap<Object, JScrollPane> mapPaneShows = 
                                    new TreeMap<Object, JScrollPane>();
    
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
    
    public JScrollPane addNewTabShow(Object name){
        tabInternalShow pane;
        if (!mapPaneShows.containsKey(name)){
            pane = new tabInternalShow(name.toString());
            mapPaneShows.put(name, pane);
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
            printOk("link copiati nella clipboard");
        }
    }
    
    void printOk(String msg) {
        ManageListener.fireTextPaneEvent(this, msg, TextPaneEvent.OK, true);
    }
}