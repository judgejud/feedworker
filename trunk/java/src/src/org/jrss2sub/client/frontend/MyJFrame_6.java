package org.jrss2sub.client.frontend;
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
//IMPORT JRSS2SUB
import org.jrss2sub.client.frontend.events.MyJFrameEvent;
import org.jrss2sub.util.Convert;
/**Jframe per versioni java 6 e superiori
 *
 * @author luca judge
 */
public class MyJFrame_6 extends MyJFrame {
    //VARIABLES PRIVATE FINAL
    private final String GIF_RSS = "rss_icon.gif";
    //VARIABLES PRIVATE
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Image iconRss, iconSub;
    /**Costruttore*/
    public MyJFrame_6() {
        super();
        initSysTray();
    }
    /**inizializza la system tray*/
    private void initSysTray() {
        if (SystemTray.isSupported()) {
            iconRss = Convert.getResourceImage(GIF_RSS);
            iconSub = Convert.getResourceImage(SUB_RSS);
            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Chiudi jRss2Sub");
            defaultItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    appClosing();
                }
            });
            popup.add(defaultItem);
            // get the SystemTray instance
            tray = SystemTray.getSystemTray();
            // load an image
            trayIcon = new TrayIcon(iconRss, "jRss2Sub", popup);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    try {
                        tray.remove(trayIcon);
                        if (trayIcon.getImage().equals(iconSub)) {
                            trayIcon.setToolTip("jRss2Sub");
                            trayIcon.setImage(iconRss);
                        }
                    } catch (Exception e) {
                        jtpLog.appendError(e.getMessage());
                    }
                    setVisible(true);
                }
            });
        }
    }
    @Override
    public void windowClosing(WindowEvent we) {
        setVisible(false);
        try {
            tray.add(trayIcon);
        } catch (Exception e) {
            jtpLog.appendError(e.getMessage());
            setVisible(true);
        }
    }
    @Override
    public void objReceived(MyJFrameEvent evt) {
        if ((evt.isIcontray()) && (!this.isVisible())) {
            trayIcon.setToolTip("jRss2Sub Nuovi Feed");
            trayIcon.setImage(iconSub);
        }
        if (evt.getDate()!=null)
            jpSettings.setDataAggiornamento(evt.getDate());
        if (evt.getOperaz()!=null){
            if (evt.getOperaz().equalsIgnoreCase("ADD_PANE_RULEZ"))
                jtabbedpane.addTab("Destinazione avanzata",paneRole.getPanel());
            else if (evt.getOperaz().equalsIgnoreCase("REMOVE_PANE_RULEZ"))
                jtabbedpane.remove(paneRole.getPanel());
            else if (evt.getOperaz().equalsIgnoreCase("ENABLED_BUTTON"))
                changeEnabledButton(true);
        }
    }    
} // end class