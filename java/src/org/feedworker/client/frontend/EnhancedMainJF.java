package org.feedworker.client.frontend;

import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.util.ResourceLocator;

public class EnhancedMainJF extends MainJF {

    private final String INCOMING_FEED_ICON_FILE_NAME = "IncomingFeedIcon2.png";
    private final String APPLICATION_ICON_FILE_NAME = "ApplicationIcon2.png";
    private Image currentIcon;
    private Display display;
    private TrayItem trayIcon;
    private Tray tray;
    private Menu trayMenu;
    private MenuItem trayMenuItem;

    public EnhancedMainJF() {
        super();
    }

    @Override
    public void initializeSysTray() throws URISyntaxException {
        display = Display.getDefault();
        Shell shell = new Shell(display);

        currentIcon = new Image(display,
                getAbsoluteResourcePath(APPLICATION_ICON_FILE_NAME));

        Canvas canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
        canvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                e.gc.drawImage(currentIcon, 0, 0);
            }
        });

        tray = display.getSystemTray();
        if (tray == null)
            logJTP.appendAlert("La system tray non è disponibile.");
        else {
            trayIcon = new TrayItem(tray, SWT.NONE);
            trayIcon.setToolTipText(FeedWorkerClient.APPLICATION_NAME);
            trayIcon.setImage(currentIcon);

            trayIcon.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    currentIcon.dispose();
                    display.dispose();
                    setVisible(true);
                }
            });

            trayIcon.addListener(SWT.MenuDetect, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    trayMenu.setVisible(true);
                }
            });

            trayMenu = new Menu(shell, SWT.POP_UP);
            trayMenuItem = new MenuItem(trayMenu, SWT.PUSH);
            trayMenuItem.setText("Close");
            trayMenuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    applicationClose();
                }
            });
            trayMenu.setDefaultItem(trayMenuItem);
        }
        //shell.setBounds(50, 50, 300, 200);
        //shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent we) {
        setVisible(false);
        try {
            initializeSysTray();
        } catch (Exception e) {
            logJTP.appendError(e.getMessage());
            setVisible(true);
        }
    }

    private String getAbsoluteResourcePath(String name)
            throws URISyntaxException {
        return new File(ResourceLocator.convertStringToURL(
                ResourceLocator.getResourcePath() + name).toURI()).getAbsolutePath();
    }

    @Override
    public void objReceived(MyJFrameEvent evt) {
        if ((evt.isIcontray()) && (!this.isVisible())) {
            try {
                currentIcon = new Image(display,
                        getAbsoluteResourcePath(INCOMING_FEED_ICON_FILE_NAME));
                trayIcon.setToolTipText("Arrivato/i feed :-) ");
                trayIcon.setImage(currentIcon);
            } catch (URISyntaxException ex) {
                logJTP.appendAlert(ex.getMessage());
            }
        }
        if (evt.getDate() != null)
            settingsJP.setDataAggiornamento(evt.getDate());
        if (evt.getOperaz() != null) {
            if (evt.getOperaz().equalsIgnoreCase("ADD_PANE_RULEZ"))
                mainJTP.addTab("Destinazione avanzata", paneRole.getPanel());
            else if (evt.getOperaz().equalsIgnoreCase("REMOVE_PANE_RULEZ"))
                mainJTP.remove(paneRole.getPanel());
            else if (evt.getOperaz().equalsIgnoreCase("ENABLED_BUTTON"))
                changeEnabledButton(true);
        }
    }
}