package org.feedworker.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.MessageEvent;
import jerklib.listeners.IRCEventListener;

/**
 *
 * @author luca judge
 */
public class Irc implements IRCEventListener{
    private final String server = "irc.azzurra.org";
    private static Irc irc;
    private static String nick, pwd;
    private ConnectionManager manager;
    private Session session;
    private boolean connected;
    private HashMap<String, Channel> chan;

    private Irc() {
        connected = false;
        /*
         * ConnectionManager takes a Profile to use for new connections.
         */
        manager = new ConnectionManager(new Profile(nick));
        /*
         * One instance of ConnectionManager can connect to many IRC networks.
         * ConnectionManager#requestConnection(String) will return a Session object.
         * The Session is the main way users will interact with this library and IRC
         * networks
         */
        session = manager.requestConnection(server);
        session.addIRCEventListener(this);
        chan = new HashMap<String, Channel>();
    }
    
    static Irc getInstance(){
        return irc;
    }
    
    static Irc getInstance(String _nick){
        if (irc==null){
            nick = _nick;
            irc = new Irc();
        }
        return irc;
    }
    
    void disconnect(){
        session.close("ciao");
        irc = null;
    }
    
    void joinChannel(String name){
        session.join(name);
    }

    void connectedTrue() {
        connected = true;
    }

    boolean isConnected() {
        return connected;
    }
    
    void sendMessage(String name, String msg){
        chan.get(name).say(msg);
    }
    
    @Override
    public void receiveEvent(IRCEvent e) {
        Type t = e.getType();
        if (t == Type.CONNECT_COMPLETE){
            ManageListener.fireTextPaneEvent(this, "Connessione al server irc Azzurra completata", 
                                                "Azzurra", true);
            irc.connectedTrue();
        } else if (e.getType() == Type.JOIN_COMPLETE) {
            JoinCompleteEvent jce = (JoinCompleteEvent) e;
            Channel c = jce.getChannel();
            if (!chan.containsKey(c.getName()))
                chan.put(c.getName(), c);
            ManageListener.fireTextPaneEvent(this, c.getTopic(), c.getName(), false);
            List l = c.getNicks();
            Collections.sort(l);
            ArrayList temp = new ArrayList();
            temp.add(l.toArray());
            ManageListener.fireListEvent(this, c.getName(), temp);
        } else if (t == Type.CHANNEL_MESSAGE) {
            MessageEvent cme = (MessageEvent) e;
            String msg = "<" + cme.getNick() + ">" + ":" + cme.getMessage();
            ManageListener.fireTextPaneEvent(this, msg, cme.getChannel().getName(), false);
        } else if (t == Type.MOTD || t == Type.TOPIC){//ignorare gli eventi
        } else 
            ManageListener.fireTextPaneEvent(this, e.getRawEventData(), "Azzurra", false);
    }
}