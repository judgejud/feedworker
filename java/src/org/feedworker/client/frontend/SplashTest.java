package org.feedworker.client.frontend;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

import javax.swing.JWindow;

public class SplashTest {
	static void renderSplashFrame(Graphics2D g, Graphics2D g3, int frame) {
		final String[] comps = { "foo", "bar", "baz" };
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(130, 240, 280, 40);
		g.setPaintMode();
		g.setColor(Color.ORANGE);
		g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 130, 260);
		g.fillRect(0, 280, (frame * 10) % 300, 20);
		g3.setColor(Color.BLUE);
		g3.drawRect(800, 600, 100, 20);
		g3.fillRect(800, 600, 100, 20);

		// Rectangle2D r = new Rectangle2D.Double(0, 400, 300, 20);
		// g2.fill(r);
	}

	public SplashTest() {
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			System.out.println("SplashScreen.getSplashScreen() returned null");
			return;
		}
		Graphics2D g = splash.createGraphics();
		if (g == null) {
			System.out.println("g is null");
			return;
		}

		JWindow w = new JWindow();
		w.setBounds(splash.getBounds().x,
				splash.getBounds().y + splash.getBounds().height,
				splash.getBounds().width, 20);
		w.setVisible(true);
		Graphics2D g3 = (Graphics2D) w.getGraphics();

		for (int i = 0; i < 100; i++) {
			renderSplashFrame(g, g3, i);
			splash.update();
			try {
				Thread.sleep(90);
			} catch (InterruptedException e) {
			}
		}

		splash.close();
	}

	public static void main(String args[]) {
		SplashTest test = new SplashTest();
	}
}
