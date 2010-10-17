package org.feedworker.client.frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;

import javax.swing.JWindow;

import org.feedworker.util.Common;

/**
 * SplashScreen
 * 
 * @author luca
 */
public class ClassicSplashScreen {

    protected static ClassicSplashScreen splashscreen = null;
    protected Graphics2D graphics2D;
    protected int steps;
    protected int stepCounter = 0;
    protected int width;
    protected int height;
    private JWindow splashJW;
    private final Image splashImage = Common.getResourceImage("SplashImage.png");

    protected ClassicSplashScreen(int steps) {
        this.steps = steps;
        this.width = splashImage.getWidth(null);
        this.height = splashImage.getHeight(null);
    }

    public static ClassicSplashScreen getInstance(int steps) {
        if (splashscreen == null) {
            splashscreen = new ClassicSplashScreen(steps);
        }
        return splashscreen;
    }

    public void start() {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenDimension.width - width) / 4;
        int y = (screenDimension.height - height) / 2;

        splashJW = new JWindow() {
            /*
             * Non è chiaro il motivo, ma sembrerebbe che occorra
             * necessariamente disegnare la splashImage nel metodo paint.
             * Non è chiaro perchè occorra invocare super.paint(g).
             * Non è chiaro perchè disegnare qualcosa che attenga ad una
             * image debba essere fatto anch'esso nel metodo paint.
             * Approfondire le API Graphics2D!!!
             */

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                graphics2D.drawImage(splashImage, 0, 0, null);
                graphics2D.setColor(Color.BLACK);
                graphics2D.draw(new Rectangle2D.Double(0, 0, 299, 299));
            }
        };
        splashJW.setBounds(x, y, width, height);
        splashJW.setVisible(true);

        /*
         * E' fondamentale che la JWindow sia visibile per ottenere l'oggetto
         * graphics2D. In caso contrario verrà restituito null.
         */
        graphics2D = (Graphics2D) splashJW.getGraphics();
    }

    public void updateStartupState(String message) {
        stepCounter++;

        graphics2D.setBackground(new Color(255, 255, 255, 0));
        graphics2D.clearRect(0, 301, 299, 20);
        graphics2D.setColor(new Color(157, 53, 7, 255));
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        graphics2D.drawString(message, 10, 315);
        graphics2D.setColor(new Color(243, 101, 35, 100));
        graphics2D.fillRect(1, 301, (299 / steps) * stepCounter, 18);
    }

    public void close() {
        splashJW.dispose();
    }
}