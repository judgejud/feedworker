package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;

import org.feedworker.client.frontend.component.listNews;
import org.feedworker.client.frontend.component.taskpaneNews;

import org.jdesktop.swingx.JXPanel;;
/**
 *
 * @author luca judge
 */
public class paneItasaNews extends paneAbstract{
    
    private static paneItasaNews jpanel = null;
    private listNews list;
    private taskpaneNews task;

    private paneItasaNews() {
        super("ItasaNews");
        initializePanel();
        initializeButtons();
    }
    
    /**Restituisce l'istanza del pannello itasa
     *
     * @return pannello itasa
     */
    public static paneItasaNews getPanel() {
        if (jpanel == null)
            jpanel = new paneItasaNews();
        return jpanel;
    }

    @Override
    void initializePanel() {
        JXPanel pane = new JXPanel(new BorderLayout());
        list = new listNews(getName());
        list.setPreferredSize(new Dimension(1000, 230));
        list.setListMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {                
                if (ev.getClickCount() == 2){ // Double-click 
                    proxy.singleNews(list.getListSelectedValue());
                    task.tableRemoveAllRows();
                }
            }
        });
        pane.add(new JScrollPane(list), BorderLayout.NORTH);
        task = new taskpaneNews();
        pane.add(task, BorderLayout.CENTER);
        jpCenter.add(pane);
    }

    @Override
    void initializeButtons() {
        remove(jpAction);
    }
}