package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**SplashScreen
 *
 * @author luca
 */
public class SplashScreenJW  extends JWindow {
    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    private JLabel jlStatus;

    public SplashScreenJW() {
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.white);

        // Set the window's bounds, centering the window
        int width =  450;
        int height = 115;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);

        // Build the splash screen
        jlStatus = new JLabel();
        JLabel label = new JLabel(new ImageIcon("java-tip.gif"));
        JLabel copyrt = new JLabel
                ("Copyright 2009", JLabel.CENTER);
        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
        content.add(jlStatus, BorderLayout.SOUTH);
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.NORTH);
        content.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
        // Display it
        setVisible(true);
    }

    public void setStatusText(String text){
        jlStatus.setText(text);
    }
}