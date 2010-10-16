package org.feedworker.client.frontend;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import org.feedworker.util.Common;

/**
 * SplashScreen
 * 
 * @author luca
 */
public class ClassicSplashScreen {
	protected static ClassicSplashScreen splashscreen = null;
	protected Graphics2D g;

	protected int steps;
	protected int stepCounter = 0;
	protected int width;
	protected int height;
	private JLabel startupProgressJL;

	public ClassicSplashScreen(int steps) {
		this.steps = steps;
		this.width = 300;
		this.height = 320;
	}

	public static ClassicSplashScreen getInstance() {
		if (splashscreen == null)
			splashscreen = new ClassicSplashScreen(12);
		return splashscreen;
	}

	public void start() {
		JWindow splashJW = new JWindow();
		//JPanel splashJP = (JPanel) splashJW.getContentPane();

		// content.setBackground(Color.white);

		// Set the window's bounds, centering the window
		splashJW.setPreferredSize(new Dimension(width, height));

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenDimension.width - width) / 4;
		int y = (screenDimension.height - height) / 2;

		splashJW.setBounds(x, y, width, height);

		// Build the splash screen
		ImageIcon splashImage = new ImageIcon(
				Common.getResourceImage("SplashImage2.png"));
		//g = (Graphics2D) splashImage.getImage().getGraphics();
		
		splashJW.setVisible(true);
		
		g=(Graphics2D) splashJW.getGraphics();
		
		g.clearRect(0, 0, 299, 299);

		g.drawImage(splashImage.getImage(),0,0,null);
		
		//JLabel splashJL = new JLabel(splashImage);

		// startupProgressJL = new JLabel();
		// startupProgressJL.setPreferredSize(new Dimension(width,20));
		// startupProgressJL.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		// JLabel copyrt = new JLabel("Copyright 2009", JLabel.CENTER);
		// copyrt.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		//splashJP.add(splashJL, BorderLayout.CENTER);
		// splashJP.add(startupProgressJL, BorderLayout.SOUTH);
		// content.add(copyrt, BorderLayout.NORTH);
		// splashJP.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		// Display it
		initialize();
	}

	protected void initialize() {
		g.setColor(Color.BLACK);
		g.draw(new Rectangle2D.Double(0, 0, 299, 299));
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
	}
}