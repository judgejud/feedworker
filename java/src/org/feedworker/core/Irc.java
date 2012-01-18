package org.feedworker.core;

import java.io.IOException;
import java.util.Arrays;

import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;
import org.schwering.irc.lib.IRCUtil;

/**
 *
 * @author luca judge
 */
public class Irc extends IRCEventAdapter implements IRCEventListener{
    private final String server = "irc.azzurra.org";
    private final int[] port = new int[] { 6664, 6665, 6666, 6667, 6668, 6669 };
    private static Irc irc;
    private static String nick, pwd;
    private IRCConnection conn;
    private int MOTD1 = 372, MOTD2 = 375, MOTD3 = 376, TOPIC = 332, USERLIST = 353,
            NAMES = 366, I333 = 333;
    private String last_join_chan;

    private Irc() throws IOException {
        ManageListener.fireTextPaneEvent(this, "Connessione al server irc Azzurra in corso...", 
                                                "Azzurra", false);
        conn = new IRCConnection(server, port, pwd, nick, null, null); 
        conn.addIRCEventListener(this); 
        conn.setEncoding("ISO-8859-1");
        //conn.setEncoding("UTF-8");
        conn.setDaemon(true);
        conn.setColors(false); 
        conn.setPong(true); 
        conn.connect(); 
    }
    
    static Irc getInstance(){
        return irc;
    }
    
    static Irc getInstance(String _nick, String _pwd) throws IOException{
        if (irc==null){
            nick = _nick;
            pwd = _pwd;
            irc = new Irc();
        }
        return irc;
    }
    
    void disconnect(){
        if (irc!=null && conn != null)
            conn.doQuit();
    }
    
    void joinChannel(String name){
        conn.doJoin(name);
    }

    boolean isConnected() {
        return conn.isConnected();
    }
    
    void sendMessage(String name, String msg){
        conn.doPrivmsg(name, msg);
    }
    
    void changeNick(String nick) {
        conn.doNick(nick);
    }
    /*
    public void onConnect() {
        System.out.println("Connected successfully.");
        ManageListener.fireTextPaneEvent(this, "Connessione al server irc Azzurra completata", 
                                                "Azzurra", true);
    }
    */
    @Override
    public void onDisconnected() {
        ManageListener.fireTextPaneEvent(this, "Disconnessione dal server completata.", 
                                                "Azzurra", false);
        irc = null;
    }

    @Override
    public void onError(String msg) {
        System.out.println("ERROR: "+ msg);
    }

    @Override
    public void onError(int num, String msg) {
        System.out.println("Error #"+ num +": "+ msg);
    }

    @Override
    public void onInvite(String chan, IRCUser user, String nickPass) {
        System.out.println("INVITE: "+ user.getNick() +" invites "+ nickPass +" to "+ chan);
    }

    @Override
    public void onJoin(String chan, IRCUser user) {
        if (nick.equalsIgnoreCase(user.getNick()))
            last_join_chan = chan;
        else{
            ManageListener.fireTextPaneEvent(this, user.getNick() + " è entrato/a", chan, false);
            ManageListener.fireListIrcEvent(this, chan, "user_join", user.getNick());
        }
    }

    @Override
    public void onKick(String chan, IRCUser user, String nickPass, String msg) {
        if (!nickPass.equalsIgnoreCase(nick)){
            ManageListener.fireListIrcEvent(this, chan, "user_kick", user.getNick());
            ManageListener.fireTextPaneEvent(this, user.getNick() + " ha espulso/a " + nickPass, chan, false);
            
        } else{
            System.out.println("KICK: "+ user.getNick() +" kicks "+ nickPass + " from " + chan 
                +" ("+ msg +")");
        }
        // remove the nickname from the nickname-table
    }

    @Override
    public void onMode(String chan, IRCUser user, IRCModeParser modeParser) {
        String[] temp = modeParser.getLine().split(" ");
        ManageListener.fireListIrcEvent(this, chan, temp[0], temp[1]);
        System.out.println("MODE: "+ user.getNick() 
            +" changes modes in "+ chan +": "+ modeParser.getLine());
    }

    @Override
    public void onNick(IRCUser user, String nickNew) {
        if (nick.equalsIgnoreCase(user.getNick()))
            nick = nickNew;
        ManageListener.fireTextPaneEvent(this, 
                            user.getNick() + " ha cambiato il nick in " + nickNew, 
                            "nick", false);
        ManageListener.fireListIrcEvent(this, "all", "nick", user.getNick() + " " + nickNew);
    }

    @Override
    public void onPart(String chan, IRCUser user, String msg) {
        ManageListener.fireTextPaneEvent(this, user.getNick() + " è uscito/a", chan, false);
        ManageListener.fireListIrcEvent(this, chan, "user_part", user.getNick());
    }

    @Override
    public void onPrivmsg(String target, IRCUser user, String msg) {        
        String message = "<" + user.getNick() + ">" + ": " + msg;
        if (IRCUtil.isChan(target))
            ManageListener.fireTextPaneEvent(this, message, target, false);
        else
            ManageListener.fireTextPaneEvent(this, message, user.getNick(), false);
    }

    @Override
    public void onQuit(IRCUser user, String msg) {
        ManageListener.fireTextPaneEvent(this, user.getNick() + " è uscito/a", "quit", false);
        ManageListener.fireListIrcEvent(this, "all", "quit", user.getNick());
    }
    
    @Override
    public void onReply(int num, String value, String msg) {
        if (num == TOPIC)
            ManageListener.fireTextPaneEvent(this, msg+"\n", last_join_chan, false);
        else if (num == USERLIST){
            Object[] array = msg.split(" ");
            Arrays.sort(array);
            ManageListener.fireListIrcEvent(this, last_join_chan, "join", array);
        } else if (num != MOTD1 && num != MOTD2 && num != MOTD3 && num != I333 
                && num != NAMES){
            ManageListener.fireTextPaneEvent(this, msg, "Azzurra", false);
            System.out.println("Reply #"+ num +": "+ msg);
        }
    }

    @Override
    public void onTopic(String chan, IRCUser user, String topic) {
        ManageListener.fireTextPaneEvent(this, topic+"\n", chan, false);
    }
}