package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author luca
 */
public class ComboboxEvent extends EventObject{
    private Object[] array;

    public ComboboxEvent(Object source, Object[] array) {
        super(source);
        this.array = array;
    }

    public Object[] getArray() {
        return array;
    }
}