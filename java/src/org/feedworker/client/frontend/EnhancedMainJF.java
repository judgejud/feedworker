package org.feedworker.client.frontend;

import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
import org.feedworker.util.Convert;

public class EnhancedMainJF extends MainJF {

	private static final String INCOMING_FEED_ICON = "IncomingFeedIcon2.png";

	public EnhancedMainJF() {
		super();
		//initSysTray();
	}

	private void initSysTray() {
		Display display = new Display();
		Shell shell = new Shell(display);
		// Image image = new Image(display, 16, 16);
		Image image = new Image(display, (new File(INCOMING_FEED_ICON))
				.getAbsolutePath());
		final Tray tray = display.getSystemTray();
		if (tray == null) {
			System.out.println("The system tray is not available");
		} else {
			final TrayItem item = new TrayItem(tray, SWT.NONE);
			item.setToolTipText(FeedWorkerClient.APPLICATION_NAME);
			item.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("show");
				}
			});
			item.addListener(SWT.Hide, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("hide");
				}
			});
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("selection");
				}
			});
			item.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("default selection");
				}
			});

			final Menu menu = new Menu(shell, SWT.POP_UP);

			MenuItem mi = new MenuItem(menu, SWT.PUSH);
			mi.setText("Close");
			mi.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("Closing "
							+ FeedWorkerClient.APPLICATION_NAME + "...");
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
			if (!display.readAndDispatch())
				display.sleep();
		}
		image.dispose();
		display.dispose();
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
	
	/*
	@Override
	public void objReceived(MyJFrameEvent evt) {
		if ((evt.isIcontray()) && (!this.isVisible())) {
			trayIcon.setToolTip("jRss2Sub Nuovi Feed");
			// trayIcon.setImage(iconSub);
			ti.setIcon(new ImageIcon(Convert
					.getResourceImage(INCOMING_FEED_ICON)));
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
	*/
}
