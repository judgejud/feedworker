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

import org.feedworker.client.FeedWorkerClient;
import org.feedworker.client.frontend.events.MyJFrameEvent;
import org.feedworker.util.Convert;

/**
 * Jframe per versioni java 6 e superiori
 * 
 * @author luca judge
 */
public class NewerMainJF extends MainJF {
	// VARIABLES PRIVATE FINAL
	private static final String INCOMING_FEED_ICON = "IncomingFeedIcon2.png";;
	// VARIABLES PRIVATE
	private SystemTray tray;
	private TrayIcon trayIcon;
	private Image iconRss, iconSub;

	/** Costruttore */
	public NewerMainJF() {
		super();
		initSysTray();
	}

	/** inizializza la system tray */
	private void initSysTray() {
		if (SystemTray.isSupported()) {
			iconRss = FeedWorkerClient.APPLICATION_ICON;
			iconSub = Convert.getResourceImage(INCOMING_FEED_ICON);
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem(" Close "
					+ FeedWorkerClient.APPLICATION_NAME);
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
			trayIcon = new TrayIcon(iconRss, FeedWorkerClient.APPLICATION_NAME,
					popup);
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					try {
						tray.remove(trayIcon);
						if (trayIcon.getImage().equals(iconSub)) {
							trayIcon.setToolTip(FeedWorkerClient.APPLICATION_NAME);
							trayIcon.setImage(iconRss);
						}
					} catch (Exception e) {
						logJTP.appendError(e.getMessage());
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
			logJTP.appendError(e.getMessage());
			setVisible(true);
		}
	}

	@Override
	public void objReceived(MyJFrameEvent evt) {
		if ((evt.isIcontray()) && (!this.isVisible())) {
			trayIcon.setToolTip("jRss2Sub Nuovi Feed");
			trayIcon.setImage(iconSub);
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
} // end class