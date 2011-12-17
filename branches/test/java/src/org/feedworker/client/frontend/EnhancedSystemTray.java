package org.feedworker.client.frontend;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.opensanskrit.widget.TrayIcon;

public class EnhancedSystemTray {

    private static EnhancedSystemTray systemTray = null;
    private Window owner;
    private Image iconNormal, iconNew;
    private TrayIcon trayIcon;
    private Mediator proxy = Mediator.getIstance();
    private int itasa, myitasa, eztv, btchat, subsf, blog, mysubsf, itasaPM;
    private String msg;

    private EnhancedSystemTray(Window owner) {
        this.owner = owner;
        iconNormal = GuiCore.getInstance().getIconFeedNormal();
        iconNew = GuiCore.getInstance().getIconFeedNew();
        trayIcon = new TrayIcon(owner, iconNormal);
        trayIcon.setJPopuMenu(createJPopupMenu());
        trayIcon.setToolTip(" FeedWorker ");
    }

    public void showSystemTray() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon.setImage(iconNormal);
            trayIcon.setToolTip(" FeedWorker ");
            tray.add(trayIcon);
            itasa = 0;
            myitasa = 0;
            blog = 0;
            eztv = 0;
            btchat = 0;
            subsf = 0;
            mysubsf = 0;
            itasaPM = 0;
        } catch (AWTException e) {
            proxy.printError(e);
        }
    }

    public static EnhancedSystemTray getInstance(Window owner) {
        if ((systemTray == null) && (SystemTray.isSupported()))
            systemTray = new EnhancedSystemTray(owner);
        return systemTray;
    }

    private JPopupMenu createJPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        final JMenuItem jmiExit = new JMenuItem(" Esci ");
        jmiExit.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowEvent we = new WindowEvent(owner,
                        WindowEvent.WINDOW_CLOSING);
                we.setSource(jmiExit);
                owner.dispatchEvent(we);
            }
        });
        final JMenuItem jmiResetMP = new JMenuItem(" Azzera conteggio mp");
        jmiResetMP.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                msg = msg.replaceFirst("Nuovi Messaggi privati: " + itasaPM + "\n", "");
                itasaPM = 0;
                if (!msg.equals("")){
                    trayIcon.displayMessage("FeedWorker", msg, MessageType.INFO);
                    trayIcon.setToolTip(msg);
                } else {
                    trayIcon.setImage(iconNormal);
                    trayIcon.setToolTip(" FeedWorker ");
                }
            }
        });
        menu.add(jmiResetMP);
        menu.add(jmiExit);
        return menu;
    }

    public void notifyIncomingFeed(String _msg) {
        String[] split = _msg.split(":");
        try {
            itasa += Integer.parseInt(split[0]);
        } catch (NumberFormatException npe){}
        try {
            myitasa += Integer.parseInt(split[1]);
        } catch (NumberFormatException npe){}
        try {
            blog += Integer.parseInt(split[2]);
        } catch (NumberFormatException npe){}
        try {
            eztv += Integer.parseInt(split[3]);
        } catch (NumberFormatException npe){}
        try {
            btchat += Integer.parseInt(split[4]);
        } catch (NumberFormatException npe){}
        try {
            subsf += Integer.parseInt(split[5]);
        } catch (NumberFormatException npe){}
        try {
            mysubsf += Integer.parseInt(split[6]);
        } catch (NumberFormatException npe){}
        try {
            itasaPM = Integer.parseInt(split[7]);
        } catch (NumberFormatException npe){}
        msg = "";
        if (itasa>0)
            msg += "Nuovi feed itasa: " + itasa + "\n";
        if (myitasa>0)
            msg += "Nuovi feed myitasa: " + myitasa + "\n";
        if (blog>0)
            msg += "Nuovi feed blog: " + blog + "\n";
        if (itasaPM>0)
            msg += "Nuovi Messaggi privati: " + itasaPM + "\n";
        if (subsf>0)
            msg += "Nuovi feed subsfactory: " + subsf + "\n";
        if (mysubsf>0)
            msg += "Nuovi feed mySubsfactory: " + mysubsf + "\n";
        if (eztv>0)
            msg += "Nuovi feed eztv: " + eztv + "\n";
        if (btchat>0)
            msg += "Nuovi feed btchat: " + btchat + "\n";
        trayIcon.displayMessage("FeedWorker", msg, MessageType.INFO);
        trayIcon.setToolTip(msg);
        trayIcon.setImage(iconNew);
    }
}