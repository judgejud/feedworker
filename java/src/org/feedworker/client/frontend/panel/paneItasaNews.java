package org.feedworker.client.frontend.panel;

import org.feedworker.client.frontend.component.listNews;

/**
 *
 * @author luca judge
 */
public class paneItasaNews extends paneAbstract{
    
    private static paneItasaNews jpanel = null;
    private listNews list;

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
        list = new listNews("Itasa News");
        jpCenter.add(list);
    }

    @Override
    void initializeButtons() {
    
    }
    
}