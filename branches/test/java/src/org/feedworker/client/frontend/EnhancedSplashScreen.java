package org.feedworker.client.frontend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;

public class EnhancedSplashScreen extends ClassicSplashScreen {
	private SplashScreen splash;

	private EnhancedSplashScreen(int steps) {
		super(steps);
	}

	public static ClassicSplashScreen getInstance() {
		if (splashscreen == null)
			splashscreen = new EnhancedSplashScreen(12);
		return splashscreen;
	}

	public void start() {
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
		
		initialize();
		splash.update();
	}	

	public void updateStartupState(String message) {
		super.updateStartupState(message);
		splash.update();
	}
}