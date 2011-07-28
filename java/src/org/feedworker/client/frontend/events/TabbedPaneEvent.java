/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author Administrator
 */
public class TabbedPaneEvent extends EventObject{
    
    private ArrayList<String> name;

    public TabbedPaneEvent(Object o, ArrayList<String> name) {
        super(o);
        this.name = name;
    }
}