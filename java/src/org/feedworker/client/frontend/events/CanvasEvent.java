package org.feedworker.client.frontend.events;

import java.util.EventObject;
/**
 *
 * @author luca judge
 */
public class CanvasEvent extends EventObject{
    private Object[] array;

    public CanvasEvent(Object source, Object[] array) {
        super(source);
        this.array = array;
    }

    public Object[] getArray() {
        return array;
    }
}