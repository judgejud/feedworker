package org.feedworker.client.frontend;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class EnhancedSystemTray {

	class EnhancedTrayIcon extends TrayIcon {
		private JPopupMenu menu;

		public EnhancedTrayIcon(Image image) {
			super(image);

			addMouseListener(new MouseAdapter() {
				
				public void mouseClicked(MouseEvent e) {
					SystemTray.getSystemTray().remove(EnhancedTrayIcon.this);
					owner.setVisible(true);
				}	

				public void mousePressed(MouseEvent e) {
					showJPopupMenu(e);
				}

				public void mouseReleased(MouseEvent e) {
					showJPopupMenu(e);
				}
			});
		}

		public JPopupMenu getJPopuMenu() {
			return menu;
		}

		public void setJPopuMenu(JPopupMenu menu) {
			if (this.menu != null) {
				this.menu.removePopupMenuListener(popupListener);
			}
			this.menu = menu;
			menu.addPopupMenuListener(popupListener);
		}

		private void showJPopupMenu(MouseEvent e) {
			if (e.isPopupTrigger() && menu != null) {
				Dimension size = menu.getPreferredSize();
				windowMenu.setBounds(e.getX(), e.getY(), size.width, size.height);
				windowMenu.setVisible(true);
				menu.show(windowMenu.getContentPane(), 0, 0);
				windowMenu.toFront();
			}
		}

		private PopupMenuListener popupListener = new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						windowMenu.setVisible(false);
					}
				});
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						windowMenu.setVisible(false);
					}
				});
			}
		};
	}

	private Image iconRss, iconSub;
	private static JWindow windowMenu;
	private EnhancedTrayIcon trayIcon;
	private Window owner;

	public EnhancedSystemTray(Window owner, Mediator proxy) {
		this.owner = owner;
		iconRss = proxy.getApplicationIcon();
		iconSub = proxy.getIncomingFeedIcon();

		windowMenu = new JWindow((Frame) owner);
		windowMenu.setAlwaysOnTop(true);

		initializeSystemTray();
	}

	private void initializeSystemTray() {

		EnhancedTrayIcon trayIcon = new EnhancedTrayIcon(iconRss);
		trayIcon.setJPopuMenu(createJPopupMenu());
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private JPopupMenu createJPopupMenu() {
		JPopupMenu m = new JPopupMenu();
		JMenu submenu = new JMenu("SubMenuTest");
		submenu.add(new JMenuItem("SubMenuItem1"));
		m.add(submenu);
		final JMenuItem exitItem = new JMenuItem("Esci");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WindowEvent we = new WindowEvent(windowMenu, WindowEvent.WINDOW_CLOSING);
				we.setSource(exitItem);
				//((jfMainNewer)owner).windowClosing(we);
			}
		});
		m.add(exitItem);
		return m;
	}

	public void notifyIncomingFeed() {
		trayIcon.setToolTip("FeedWorker - ci sono nuovi feed :)");
		trayIcon.setImage(iconSub);
	}
}