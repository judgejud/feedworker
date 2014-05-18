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
    private Image iconNormal, iconNew, iconMyItasa;
    private TrayIcon trayIcon;
    private Mediator proxy = Mediator.getIstance();
    private int itasa, myitasa, eztv, btchat, subsf, blog, tv24, itasaPM, itasaNews,
            karmorra, mykarmorra;
    private String msg;

    private EnhancedSystemTray(Window owner) {
        this.owner = owner;
        iconNormal = GuiCore.getInstance().getIconFeedNormal();
        iconNew = GuiCore.getInstance().getIconFeedNew();
        iconMyItasa = GuiCore.getInstance().getIconFeedMyItasa();
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
            tv24 = 0;
            itasaPM = 0;
            itasaNews = 0;
            karmorra = 0;
            mykarmorra = 0;
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
        int i=0;
        try {
            itasa += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            myitasa += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            blog += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            eztv += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            btchat += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            subsf += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            tv24 += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            itasaPM = Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            itasaNews += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            karmorra += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        try {
            mykarmorra += Integer.parseInt(split[i++]);
        } catch (NumberFormatException npe){}
        msg = "";
        if (itasa>0)
            msg += "Nuovi feed itasa: " + itasa + "\n";
        if (myitasa>0)
            msg += "Nuovi feed myitasa: " + myitasa + "\n";
        if (itasaNews>0)
            msg += "Nuove itasa news: " + itasaNews + "\n";
        if (blog>0)
            msg += "Nuovi feed blog: " + blog + "\n";
        if (itasaPM>0)
            msg += "Nuovi Messaggi privati: " + itasaPM + "\n";
        if (subsf>0)
            msg += "Nuovi feed subsfactory: " + subsf + "\n";
        if (tv24>0)
            msg += "Nuovi feed tv24: " + tv24 + "\n";
        if (eztv>0)
            msg += "Nuovi feed eztv: " + eztv + "\n";
        if (btchat>0)
            msg += "Nuovi feed btchat: " + btchat + "\n";
        if (karmorra>0)
            msg += "Nuovi feed karmorra: " + karmorra + "\n";
        if (mykarmorra>0)
            msg += "Nuovi feed mykarmorra: " + mykarmorra + "\n";
        trayIcon.displayMessage("FeedWorker", msg, MessageType.INFO);
        trayIcon.setToolTip(msg);
        if (myitasa>0)
            trayIcon.setImage(iconMyItasa);
        else
            trayIcon.setImage(iconNew);
    }
}