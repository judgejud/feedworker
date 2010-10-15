package org.feedworker.client.frontend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

public class SplashTest {
	private SplashScreen splash;
	private Graphics2D g;
	private int steps;
	private int stepCounter = 0;

	public SplashTest(int steps) {
		this.steps = steps;

		this.splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			System.out.println("SplashScreen.getSplashScreen() returned null");
			return;
		}
		g = splash.createGraphics();
		if (g == null) {
			System.out.println("g is null");
			return;
		}
		/*
		 * for (int i = 0; i < 100; i++) { renderSplashFrame(g, i);
		 * splash.update(); try { Thread.sleep(90); } catch
		 * (InterruptedException e) { } }
		 */
	}

	public void updateStartupState(String message) {
		stepCounter++;

		final String[] comps = { "foo", "bar", "baz" };
		g.setBackground(new Color(255, 255, 255, 0));
		g.clearRect(0, 280, 300, 20);
		g.setColor(new Color(157, 53, 7, 255));
		// g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 10, 295);
		g.drawString(message, 10, 295);
		g.setColor(new Color(243, 101, 35, 100));
		// g.fillRect(0, 280, (frame * 40) % 300, 20);
		g.fillRect(0, 280, (300 / steps) * stepCounter, 20);
		splash.update();

		if (stepCounter == steps) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			closeSplashScreen();
		}
	}

	private void closeSplashScreen() {
		splash.close();
	}
}
