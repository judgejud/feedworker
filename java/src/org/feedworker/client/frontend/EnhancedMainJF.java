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

    private final String INCOMING_FEED_ICON = "IncomingFeedIcon2.png";
    private Display display;
    private TrayItem item;

    public EnhancedMainJF() {
        super();
    }

    private void initSysTray() throws URISyntaxException {
        display = new Display();
        Shell shell = new Shell(display);
        // Image image = new Image(display, 16, 16);
        final Image image = new Image(display, getAbsoluteResourcePath(INCOMING_FEED_ICON));

        Canvas canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.drawImage(image, 0, 0);
            }
        });

        final Tray tray = display.getSystemTray();
        if (tray == null)
            System.out.println("The system tray is not available");
        else {
            item = new TrayItem(tray, SWT.NONE);
            item.setToolTipText(FeedWorkerClient.APPLICATION_NAME);

            item.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    image.dispose();
                    display.dispose();
                    setVisible(true);
                }
            });

            final Menu menu = new Menu(shell, SWT.POP_UP);
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Close");
            mi.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {                    
                    applicationClose();
                }
            });
            menu.setDefaultItem(mi);

            item.addListener(SWT.MenuDetect, new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            });
            item.setImage(image);
        }
        // shell.setBounds(50, 50, 300, 200);
        // shell.open();
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
            initSysTray();
        } catch (Exception e) {
            logJTP.appendError(e.getMessage());
            setVisible(true);
        }
    }

    private String getAbsoluteResourcePath(String name) throws URISyntaxException{
        return new File(ResourceLocator.convertStringToURL(
                ResourceLocator.getResourcePath() + name).toURI()).getAbsolutePath();
    }
    
    @Override
    public void objReceived(MyJFrameEvent evt) {
        if ((evt.isIcontray()) && (!this.isVisible())) {
            Image image;
            try {
                image = new Image(display, getAbsoluteResourcePath("ApplicationIcon2.png"));
                item.setToolTipText("Ci sono nuovi feed");
                item.setImage(image);
            } catch (URISyntaxException ex) {

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
