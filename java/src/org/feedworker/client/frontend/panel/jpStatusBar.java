package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.feedworker.client.frontend.events.StatusBarEvent;
import org.feedworker.client.frontend.events.StatusBarEventListener;
/**
 *
 * @author luca
 */
public class jpStatusBar extends JPanel implements StatusBarEventListener{
    private JLabel jlText = new JLabel(new AngledLinesWindowsCornerIcon());

    public jpStatusBar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(10, 23));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(jlText, BorderLayout.SOUTH);
        rightPanel.setOpaque(false);

        add(rightPanel, BorderLayout.EAST);
        setBackground(SystemColor.control);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int y = 0;
        g.setColor(new Color(156, 154, 140));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(196, 194, 183));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(218, 215, 201));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 217));
        g.drawLine(0, y, getWidth(), y);

        y = getHeight() - 3;
        g.setColor(new Color(233, 232, 218));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 216));
        g.drawLine(0, y, getWidth(), y);
        y = getHeight() - 1;
        g.setColor(new Color(221, 221, 220));
        g.drawLine(0, y, getWidth(), y);

    }

    @Override
    public void objReceived(StatusBarEvent evt) {
        jlText.setText(evt.getText());
    }
}

class AngledLinesWindowsCornerIcon implements Icon {
    private static final Color WHITE_LINE_COLOR = new Color(255, 255, 255);
    private static final Color GRAY_LINE_COLOR = new Color(172, 168, 153);
    private static final int WIDTH = 13;
    private static final int HEIGHT = 13;
    @Override
    public int getIconHeight() {
        return WIDTH;
    }
    @Override
    public int getIconWidth() {
        return HEIGHT;
    }
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(WHITE_LINE_COLOR);
        g.drawLine(0, 12, 12, 0);
        g.drawLine(5, 12, 12, 5);
        g.drawLine(10, 12, 12, 10);

        g.setColor(GRAY_LINE_COLOR);
        g.drawLine(1, 12, 12, 1);
        g.drawLine(2, 12, 12, 2);
        g.drawLine(3, 12, 12, 3);

        g.drawLine(6, 12, 12, 6);
        g.drawLine(7, 12, 12, 7);
        g.drawLine(8, 12, 12, 8);

        g.drawLine(11, 12, 12, 11);
        g.drawLine(12, 12, 12, 12);
    }
}