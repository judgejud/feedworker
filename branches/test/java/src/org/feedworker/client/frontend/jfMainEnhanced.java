package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.SystemTray;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;

import org.feedworker.client.frontend.events.JFrameEventIconDate;

/**Jframe per versioni java 6 e superiori
 * 
 * @author luca judge
 */
public class jfMainEnhanced extends jfMain {
    // VARIABLES PRIVATE
    private EnhancedSystemTray systemTray;

    /** Costruttore */
    public jfMainEnhanced() {
        super();
    }

    /** inizializza la system tray */
    private void initSysTray() {
        if (SystemTray.isSupported())
            systemTray = new EnhancedSystemTray(this, proxy);
    }

    @Override
    public void windowClosing(WindowEvent we) {
        if (we.getSource() instanceof JMenuItem) {
            super.windowClosing(we);
        } else {
            setVisible(false);
            try {
            	initSysTray();
            } catch (Exception e) {
                logJTP.appendError(e.getMessage());
                setVisible(true);
            }
        }
    }

    @Override
    public void objReceived(JFrameEventIconDate evt) {
        if ((evt.isIcontray()) && (!this.isVisible()))
            systemTray.notifyIncomingFeed();
        if (evt.getDate() != null)
            settingsJP.setDataAggiornamento(evt.getDate());
    }
} // end class