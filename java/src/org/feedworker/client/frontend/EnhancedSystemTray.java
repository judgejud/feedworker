package org.feedworker.client.frontend;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
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
    private Image iconRss, iconSub;
    private TrayIcon trayIcon;
    private Mediator proxy = Mediator.getIstance();

    private EnhancedSystemTray(Window owner) {
        this.owner = owner;
        iconRss = proxy.getApplicationIcon();
        iconSub = proxy.getIncomingFeedIcon();
        trayIcon = new TrayIcon(owner, iconRss);
        trayIcon.setJPopuMenu(createJPopupMenu());
        trayIcon.setToolTip(" FeedWorker ");
    }

    public void showSystemTray() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
            System.out.println("tray added");
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

    public void notifyIncomingFeed() {
        trayIcon.setToolTip(" FeedWorker - ci sono nuovi feed :) ");
        trayIcon.setImage(iconSub);
    }
}