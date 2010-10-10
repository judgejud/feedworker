package org.feedworker.client.frontend.events;

import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author luca
 */
public class TableXmlEvent extends EventObject{

    private ArrayList<Object[]> obj;

    public TableXmlEvent(Object source, ArrayList<Object[]> obj) {
        super(source);
        this.obj = obj;
    }

    public ArrayList<Object[]> getObj() {
        return obj;
    }
}
