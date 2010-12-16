package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;

import org.feedworker.client.frontend.events.JFrameEventIconDate;

/**
 * Jframe per versioni java 6 e superiori
 * 
 * @author luca judge
 */
public class jfMainNewer extends jfMain {
    // VARIABLES PRIVATE
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Image iconRss, iconSub;

    /** Costruttore */
    public jfMainNewer() {
        super();
        initSysTray();
    }

    /** inizializza la system tray */
    private void initSysTray() {
        if (SystemTray.isSupported()) {
            iconRss = proxy.getApplicationIcon();
            iconSub = proxy.getIncomingFeedIcon();
            PopupMenu popup = new PopupMenu();

            MenuItem defaultItem = new MenuItem(" Chiudi " + proxy.getApplicationName());
            defaultItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    applicationClose();
                }
            });
            popup.add(defaultItem);
            // get the SystemTray instance
            tray = SystemTray.getSystemTray();
            // load an image
            trayIcon = new TrayIcon(iconRss, proxy.getApplicationName(), popup);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    try {
                        tray.remove(trayIcon);
                        if (trayIcon.getImage().equals(iconSub)) {
                            trayIcon.setToolTip(proxy.getApplicationName());
                            trayIcon.setImage(iconRss);
                        }
                    } catch (Exception e) {
                        proxy.printError(e);
                    }
                    setVisible(true);
                }
            });
        }
    }

    @Override
    public void windowClosing(WindowEvent we) {
        if (we.getSource() instanceof JMenuItem) {
            super.windowClosing(we);
        } else {
            setVisible(false);
            try {
                tray.add(trayIcon);
            } catch (Exception e) {
                logJTP.appendError(e.getMessage());
                setVisible(true);
            }
        }
    }

    @Override
    public void objReceived(JFrameEventIconDate evt) {
        if ((evt.isIcontray()) && (!this.isVisible())) {
            trayIcon.setToolTip("FeedWorker - ci sono nuovi feed :)");
            trayIcon.setImage(iconSub);
        }
        if (evt.getDate() != null)
            settingsJP.setDataAggiornamento(evt.getDate());
    }
} // end class