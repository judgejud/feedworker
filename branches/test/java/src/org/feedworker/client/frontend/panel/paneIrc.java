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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.Mediator;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;
import org.feedworker.client.frontend.events.TextPaneEvent;
import org.feedworker.client.frontend.events.TextPaneEventListener;

import org.jdesktop.swingx.JXList;

/**
 *
 * @author luca judge
 */
public class paneIrc extends paneAbstract implements TextPaneEventListener{
    private final String AZZURRA = "Azzurra";
    
    private static paneIrc pane;
    private JTabbedPane tab;
    private JTextPane text;
    private JScrollPane jspText;
    private StyledDocument sd;
    private paneChan chanItaliansubs, chanItasaCastle;

    private paneIrc(String name) {
        super(name);
        initializeButtons();
        initializePanel();
        core.setTextPaneListener(this);
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
        
        text = new JTextPane();
        text.setEditable(false);
        text.setContentType("text/html");
        sd = (StyledDocument) text.getDocument();
        jspText = new JScrollPane(text);
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
                addMsgTextPane("Disconnessione effettuata");
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
        
        jpAction.add(jbConnect);
        jpAction.add(jbDisconnect);
        jpAction.add(jbJoinItaliansubs);
        jpAction.add(jbJoinItasaCastle);
        jpAction.add(jbRenameNick);
    }
    
    private void connect(){
        if (!tab.isAncestorOf(jspText))
            tab.addTab("Azzurra", jspText);
        proxy.connectIrc();
        addMsgTextPane("Connessione al server azzurra in corso... \n");
    }
    
    private void joinItaliansubs(){
        if (proxy.isConnectedIrc()){
            if (!tab.isAncestorOf(chanItaliansubs)){
                String name = "#italiansubs";
                chanItaliansubs = new paneChan(name, true);
                tab.addTab(name, chanItaliansubs);
                proxy.joinChan(name);
            }
        }
    }
    
    private void joinItasaCastle(){
        if (proxy.isConnectedIrc()){
            if (!tab.isAncestorOf(chanItasaCastle)){
                String name = "#itasa-castle";
                chanItasaCastle = new paneChan(name, true);
                tab.addTab(name, chanItasaCastle);
                proxy.joinChan(name);
            }
        }
    }

    @Override
    public void objReceived(TextPaneEvent evt) {
        if (evt.getType().equals(AZZURRA))
            addMsgTextPane(evt.getMsg());
    }
    
    private void addMsgTextPane(String msg){
        try {
            sd.insertString(sd.getLength(), msg + "\n", null);
            text.setCaretPosition(sd.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
}

class paneChan extends JPanel implements ListEventListener, TextPaneEventListener{
    private JTextPane textpane;
    private StyledDocument sd;
    private JTextField textfield;
    private JXList list;
    private DefaultListModel model;

    public paneChan(String name, boolean _list) {
        super(new BorderLayout());
        setName(name);
        init(_list);
        GuiCore.getInstance().setTextPaneListener(this);
        GuiCore.getInstance().setListListener(this);
    }
    
    private void init(boolean _list){
        textpane = new JTextPane();
        textpane.setEditable(false);
        sd = (StyledDocument) textpane.getDocument();
        add(new JScrollPane(textpane), BorderLayout.CENTER);
        
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
        
        if (_list){
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
        }
    }
    
    private JPopupMenu createPopupMenu(){
        JPopupMenu menu = new JPopupMenu("Popup");
        JMenuItem jmiChat = new JMenuItem("Dialoga in privato");
        jmiChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(list.getSelectedValue());
            }
        });
        menu.add(jmiChat);
        return menu;
    }
    
    private void sendMessage(){
        String text = textfield.getText();
        if (text!=null){
            Mediator.getIstance().sendIrcMessage(getName(), text);
            addMsgTextPane(text);
            textfield.setText(null);
        }
    }
    
    private void addMsgTextPane(String msg){
        try {
            sd.insertString(sd.getLength(), msg + "\n", null);
            textpane.setCaretPosition(sd.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
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
        } else if (evt.getName().equals("all")){
            String oper = evt.getOper();
            if (oper.equalsIgnoreCase("quit"))
                removeNick(evt.getNick());
        }
    }
    
    private void removeNick(String nick){
        if (model.removeElement(nick)){}
        else if (model.removeElement("@"+nick)){}
        else if (model.removeElement("%"+nick)){}
        else if (model.removeElement("+"+nick)){}
    }
    
    public boolean checkNick(String nick){
        boolean check = false;
        System.out.println(model.indexOf(nick));
        System.out.println(model.indexOf("@" + nick));
        System.out.println(model.indexOf("%" + nick));
        System.out.println(model.indexOf("+" + nick));
        return check;
    }

    @Override
    public void objReceived(TextPaneEvent evt) {
        if (evt.getType().equals(this.getName()))
            addMsgTextPane(evt.getMsg());
    }
}