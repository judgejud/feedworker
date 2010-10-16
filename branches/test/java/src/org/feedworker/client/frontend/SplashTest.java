package org.feedworker.client.frontend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;

public class SplashTest {

    private SplashScreen splash;
    private Graphics2D g;
    private int steps;
    private int stepCounter = 0;
    private static SplashTest splashtest = null;

    private SplashTest(int steps) {
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

        g.setColor(Color.BLACK);
        g.draw(new Rectangle2D.Double(0, 0, 299, 299));

        splash.update();
        System.out.println(splash.getBounds());

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {}
    }

    public static SplashTest getInstance(){
        if (splashtest==null)
            splashtest = new SplashTest(12);
        return splashtest;
    }

    public void updateStartupState(String message) {
        stepCounter++;

        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0, 301, 299, 20);
        g.setColor(new Color(157, 53, 7, 255));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        g.drawString(message, 10, 315);
        g.setColor(new Color(243, 101, 35, 100));
        g.fillRect(1, 301, (299 / steps) * stepCounter, 18);

        splash.update();
    }

    public void close(){
        splash.close();
    }
}