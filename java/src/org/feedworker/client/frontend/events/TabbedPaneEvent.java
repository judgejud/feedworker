package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author Administrator
 */
public class TabbedPaneEvent extends EventObject{
    
    private ArrayList<String> array;
    private String dest, name;

    public TabbedPaneEvent(Object o, ArrayList<String> array, String dest) {
        super(o);
        this.array = array;
        this.dest = dest;
    }

    public TabbedPaneEvent(Object o, String name, String dest) {
        super(o);
        this.dest = dest;
        this.name = name;
    }
    
    public String getDest() {
        return dest;
    }

    public ArrayList<String> getArray() {
        return array;
    }

    public String getName() {
        return name;
    }   
}