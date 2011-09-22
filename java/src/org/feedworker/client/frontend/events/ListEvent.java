package org.feedworker.client.frontend.events;

import java.util.EventObject;
/**
 *
 * @author Luca
 */
public class ListEvent extends EventObject{
    private Object[][] array;
    private String name;

    public ListEvent(Object source, String name, Object[][] array) {
        super(source);
        this.name = name;
        this.array = array;
    }

    public String getName() {
        return name;
    }

    public Object[][] getArray() {
        return array;
    }
}