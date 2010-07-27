package org.feedworker.client.frontend.events;

import java.util.EventObject;

/**
 *
 * @author luca
 */
public class MyTextPaneEvent extends EventObject{
    private String msg;
    private String type;

    public static final String ERROR = "ERROR";
    public static final String TORRENT = "TORRENT";
    public static final String OK = "OK";
    public static final String ALERT = "ALERT";
    public static final String FEED_ITASA = "F_ITASA";
    public static final String FEED_MYITASA = "F_MYITASA";
    public static final String FEED_SUBSF = "F_SUBSF";
    public static final String FEED_TORRENT1 = "F_TORRENT1";
    public static final String FEED_TORRENT2 = "F_TORRENT2";
    public static final String SUB = "SUB";
    public static final String SYNOLOGY = "SYNOLOGY";

    public MyTextPaneEvent(Object source, String _msg, String _type) {
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