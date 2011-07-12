package org.feedworker.client.frontend.events;

import java.util.EventObject;
/**
 *
 * @author luca
 */
public class StatusBarEvent extends EventObject{
    private String text;
    
    public StatusBarEvent(Object source, String msg){
        super(source);
        text = msg;
    }

    public String getText() {
        return text;
    }
}