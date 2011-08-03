package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author Administrator
 */
public class ListEvent extends EventObject{
    private Object[][] array;

    public ListEvent(Object source, Object[][] array) {
        super(source);
        this.array = array;
    }

    public Object[][] getArray() {
        return array;
    }
}