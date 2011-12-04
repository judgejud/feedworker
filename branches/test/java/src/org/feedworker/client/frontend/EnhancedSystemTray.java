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
    private int itasa, myitasa, eztv, btchat, subsfactory;

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
            eztv = 0;
            btchat = 0;
            subsfactory = 0;
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
        JPopupMenu m = new JPopupMenu();
        final JMenuItem exitItem = new JMenuItem(" Esci ");
        exitItem.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                WindowEvent we = new WindowEvent(owner,
                        WindowEvent.WINDOW_CLOSING);
                we.setSource(exitItem);
                owner.dispatchEvent(we);
            }
        });
        m.add(exitItem);
        return m;
    }

    public void notifyIncomingFeed(String msg) {
        String[] split = msg.split("|");
        itasa += Integer.parseInt(split[0]);
        myitasa += Integer.parseInt(split[1]);
        msg = "Nuovi feed itasa: " + itasa + "\nNuovi feed myitasa: " + myitasa;
        trayIcon.displayMessage("FeedWorker", msg, MessageType.INFO);
        trayIcon.setToolTip(msg);
        trayIcon.setImage(iconNew);
    }
}