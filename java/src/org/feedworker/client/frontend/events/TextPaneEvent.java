package org.feedworker.client.frontend.events;

import java.util.EventObject;
/**
 * 
 * @author luca
 */
public class TextPaneEvent extends EventObject {
    public static final String ERROR = "ERROR";
    public static final String TORRENT = "TORRENT";
    public static final String OK = "OK";
    public static final String ALERT = "ALERT";
    public static final String FEED_ITASA = "F_ITASA";
    public static final String FEED_MYITASA = "F_MYITASA";
    public static final String FEED_SUBSF = "F_SUBSF";
    public static final String FEED_MYSUBSF = "F_MYSUBSF";
    public static final String FEED_EZTV = "F_EZTV";
    public static final String FEED_BTCHAT = "F_BTCHAT";
    public static final String FEED_BLOG = "F_BLOG";
    public static final String SUB = "SUB";
    public static final String SYNOLOGY = "SYNOLOGY";
    public static final String DAY_SERIAL = "DAY_SERIAL";
    public static final String ITASA_PM = "ITASA_PM";
    public static final String ITASA_NEWS = "ITASA_NEWS";

    private String msg, type;

    public TextPaneEvent(Object source, String _msg, String _type) {
        super(source);
        msg = _msg;
        type = _type;
    }

    public String getMsg() {
        return msg;
    }

    public String getType() {
        return type;
    }
}