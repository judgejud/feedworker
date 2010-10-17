package org.feedworker.client.frontend;

import java.awt.Color;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;

public class EnhancedSplashScreen extends ClassicSplashScreen {

    private SplashScreen splash;

    private EnhancedSplashScreen(int steps) {
        super(steps);
    }

    public static ClassicSplashScreen getInstance(int steps) {
        if (splashscreen == null) {
            splashscreen = new EnhancedSplashScreen(steps);
        }
        return splashscreen;
    }

    @Override
    public void start() {
        this.splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }

        graphics2D = splash.createGraphics();
        if (graphics2D == null) {
            System.out.println("g is null");
            return;
        }

        graphics2D.setColor(Color.BLACK);
        graphics2D.draw(new Rectangle2D.Double(0, 0, 299, 299));

        splash.update();
    }

    @Override
    public void updateStartupState(String message) {
        super.updateStartupState(message);
        splash.update();
    }

    @Override
    public void close() {}
}