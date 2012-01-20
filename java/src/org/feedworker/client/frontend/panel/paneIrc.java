package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.TabbedPaneEvent;
import org.feedworker.client.frontend.events.TabbedPaneEventListener;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.client.frontend.events.TextPaneEventListener;

import org.jfacility.java.lang.Lang;

import org.jdesktop.swingx.JXList;
import org.jfacility.javax.swing.ButtonTabComponent;

/**
 *
 * @author luca judge
 */
public class paneIrc extends paneAbstract implements TextPaneEventListener, TabbedPaneEventListener{
    private final String AZZURRA = "Azzurra";
    
    private static paneIrc pane;
    private JTabbedPane tab;   
    private paneChan chanItaliansubs, chanItasaCastle;
    private paneConsole jpConsole;

    private paneIrc(String name) {
        super(name);
        initializeButtons();
        initializePanel();
        core.setTextPaneListener(this);
        core.setTabbedPaneListener(this);
    }
    
    public static paneIrc getPanel(){
        if (pane==null)
            pane = new paneIrc("Irc");
        return pane;
    }

    @Override
    void initializePanel() {
        tab = new JTabbedPane();
        jpCenter.add(tab);
        jpConsole = new paneConsole(AZZURRA);
    }

    @Override
    void initializeButtons() {
        JButton jbConnect = new JButton("Connect Azzurra");
        jbConnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                connect();
            }
        });
        
        JButton jbDisconnect = new JButton("Disconnect Azzurra");
        jbDisconnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.disconnectIrc();
                for (int i=tab.getTabCount()-1; i>0; i--)
                    tab.remove(tab.getComponent(i));
            }
        });
        
        JButton jbJoinItaliansubs = new JButton("Join #italiansubs");
        jbJoinItaliansubs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                joinItaliansubs();
            }
        });
        
        JButton jbJoinItasaCastle = new JButton("Join #itasa-castle");
        jbJoinItasaCastle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                joinItasaCastle();
            }
        });
        
        JButton jbRenameNick = new JButton("Cambia nick");
        jbRenameNick.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (proxy.isConnectedIrc()){
                    String nick = JOptionPane.showInputDialog("Inserire nuovo nick");
                    if (Lang.verifyTextNotNull(nick))
                        proxy.changeIrcNick(nick);
                }
            }
        });
        
        jpAction.add(jbConnect);
        jpAction.add(jbDisconnect);
        jpAction.add(jbJoinItaliansubs);
        jpAction.add(jbJoinItasaCastle);
        jpAction.add(jbRenameNick);
    }
    
    private void connect(){
        if (!tab.isAncestorOf(jpConsole)){
            jpConsole = new paneConsole(AZZURRA);
            tab.addTab("Azzurra", jpConsole);
        }
        proxy.connectIrc();
    }
    
    private void joinItaliansubs(){
        if (proxy.isConnectedIrc()){
            if (!tab.isAncestorOf(chanItaliansubs)){
                String name = "#italiansubs";
                chanItaliansubs = new paneChan(name);
                tab.addTab(name, chanItaliansubs);
                proxy.joinIrcChan(name);
            }
        }
    }
    
    private void joinItasaCastle(){
        if (proxy.isConnectedIrc()){
            if (!tab.isAncestorOf(chanItasaCastle)){
                String name = "#itasa-castle";
                chanItasaCastle = new paneChan(name);
                tab.addTab(name, chanItasaCastle);
                proxy.joinIrcChan(name);
            }
        }
    }

    @Override
    public void objReceived(TextPaneEvent evt) {
        if (evt.getType().equals("quit")){
            String nick = evt.getMsg().split(" ")[0].toString();
            if (chanItaliansubs!=null && chanItaliansubs.checkNick(nick))
                chanItaliansubs.addMsgTextPane(evt.getMsg());
            if (chanItasaCastle!=null && chanItasaCastle.checkNick(nick))
                chanItasaCastle.addMsgTextPane(evt.getMsg());
        }
        if (evt.getType().equals("nick")){
            String nick = evt.getMsg().split(" ")[0].toString();
            if (chanItaliansubs!=null && chanItaliansubs.checkNick(nick))
                chanItaliansubs.addMsgTextPane(evt.getMsg());
            if (chanItasaCastle!=null && chanItasaCastle.checkNick(nick))
                chanItasaCastle.addMsgTextPane(evt.getMsg());
        }
    }

    @Override
    public void objReceived(TabbedPaneEvent evt) {
        if (evt.getDest()!=null){
            if (evt.getDest().equals("query") && tab.indexOfTab(evt.getName())==-1){
                paneQuery pane = new paneQuery(evt.getName());
                tab.addTab(evt.getName(), pane);
                tab.setTabComponentAt(tab.getTabCount() - 1, new ButtonTabComponent(tab));
            } else if (evt.getDest().equals("close")) {
                if (evt.getName().equals(chanItaliansubs.getName())){
                    tab.remove(chanItaliansubs);
                    chanItaliansubs = null;
                } else if (evt.getName().equals(chanItasaCastle.getName())){
                    tab.remove(chanItasaCastle);
                    chanItasaCastle = null;
                }
            } else if (evt.getDest().equals("nick")){
                String[] name = evt.getName().split(" ");
                int i = tab.indexOfTab(name[0]);
                if (i>-1)
                    tab.setTitleAt(i, name[1]);
            }
        }
    }
}

class paneConsole extends paneAbstract implements TextPaneEventListener{
    private JTextPane text;
    private StyledDocument sd;

    public paneConsole(String name) {
        super(name);
        remove(jpAction);
        initializePanel();
        core.setTextPaneListener(this);
    }
    
    @Override
    public void objReceived(TextPaneEvent evt) {
        if (evt.getType().equals(this.getName()))
            addMsgTextPane(evt.getMsg());
    }
    
    protected void addMsgTextPane(String msg){
        try {
            sd.insertString(sd.getLength(), msg + "\n", null);
            text.setCaretPosition(sd.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    void initializePanel() {
        text = new JTextPane();
        text.setEditable(false);
        text.setContentType("text/html");
        sd = (StyledDocument) text.getDocument();
        jpCenter.add(new JScrollPane(text), BorderLayout.CENTER);
    }

    @Override
    void initializeButtons() {}
}

class paneQuery extends paneConsole{
    private JTextField textfield;
    
    public paneQuery(String name) {
        super(name);
        textfield = new JTextField();
        textfield.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e){
                if( e.getKeyCode() == KeyEvent.VK_ENTER )
                    sendMessage();
            }
        });
        textfield.setFocusable(true);
        add(textfield, BorderLayout.SOUTH);
    }
    
    private void sendMessage(){
        String text = textfield.getText();
        if (Lang.verifyTextNotNull(text)){
            proxy.sendIrcMessage(getName(), text);
            addMsgTextPane(text);
            textfield.setText(null);
        }
    }
    
}

class paneChan extends paneQuery implements ListEventListener{
    private JXList list;
    private DefaultListModel model;

    public paneChan(String name) {
        super(name);
        model = new DefaultListModel();
        list = new JXList(model);

        list.addMouseListener(new MouseAdapter() {
        @Override
            public void mouseReleased(MouseEvent ev) {
                if (ev.isPopupTrigger()){
                    list.setSelectedIndex(list.locationToIndex(ev.getPoint()));
                    createPopupMenu().show(ev.getComponent(), ev.getX(), ev.getY());
                }
            }
        });
        add(new JScrollPane(list), BorderLayout.EAST);
        core.setListListener(this);
    }
    
    private JPopupMenu createPopupMenu(){
        JPopupMenu menu = new JPopupMenu("Popup");
        JMenuItem jmiChat = new JMenuItem("Dialoga in privato");
        jmiChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action();
            }
        });
        menu.add(jmiChat);
        return menu;
    }
    
    private void action(){
        String name = list.getSelectedValue().toString();
        char c = name.charAt(0);
        if (c == '+' || c == '@' || c == '%')
            name = name.substring(1);
        core.checkQueryIrc(name);
    }

    @Override
    public void objReceived(ListEvent evt) {
        if (evt.getName().equals(this.getName())){
            String oper = evt.getOper();
            if (oper.equalsIgnoreCase("join")){
                for (int i=0; i<evt.getArray().length; i++)
                    model.addElement(evt.getArray()[i]);
            } else if (oper.equalsIgnoreCase("user_join"))
                model.addElement(evt.getNick());
            else if (oper.equalsIgnoreCase("user_part") || oper.equalsIgnoreCase("user_kick"))
                removeNick(evt.getNick());
            else if (oper.equalsIgnoreCase("+v")){
                removeNick(evt.getNick());
                model.addElement("+" + evt.getNick());
            } else if (oper.equalsIgnoreCase("+h")){
                removeNick(evt.getNick());
                model.addElement("%" + evt.getNick());
            } else if (oper.equalsIgnoreCase("+o")){
                removeNick(evt.getNick());
                model.addElement("@" + evt.getNick());
            } else if (oper.substring(0,1).equalsIgnoreCase("-")){
                removeNick(evt.getNick());
                model.addElement(evt.getNick());
            }
        } else if (evt.getName().equals("all")){
            String oper = evt.getOper();
            if (oper.equalsIgnoreCase("quit"))
                removeNick(evt.getNick());
            else if (oper.equalsIgnoreCase("nick")){
                String[] nick = evt.getNick().split(" ");
                if (checkNick(nick[0])){
                    removeNick(nick[0]);
                    model.addElement(nick[1]);
                }
            }
        }
    }
    
    private void removeNick(String nick){
        if (model.removeElement(nick)){}
        else if (model.removeElement("@"+nick)){}
        else if (model.removeElement("%"+nick)){}
        else if (model.removeElement("+"+nick)){}
    }
    
    public boolean checkNick(String nick){
        if (model.indexOf(nick)>-1)
            return true;
        else if (model.indexOf("@" + nick)>-1)
            return true;
        else if (model.indexOf("%" + nick)>-1)
            return true;
        else if (model.indexOf("+" + nick)>-1)
            return true;
        return false;
    }
}