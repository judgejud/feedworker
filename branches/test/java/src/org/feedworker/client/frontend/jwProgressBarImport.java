package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JProgressBar;
import javax.swing.JWindow;
import org.jfacility.Awt;

/**
 *
 * @author luca
 */
class jwProgressBarImport extends JWindow {

    private JProgressBar bar;
    private Frame owner;

    public jwProgressBarImport(Frame owner) {
        super(owner);
        this.owner = owner;
        owner.setEnabled(false);
        setPreferredSize(new Dimension(300, 20));
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setStringPainted(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bar);
        pack();
        Awt.centerComponent(owner, this);
        setVisible(true);
    }

    void setProgress(int progress) {
        bar.setValue(progress);
        if (progress == bar.getMaximum()) {
            owner.setEnabled(true);
            dispose();
        }
    }
}