package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 *
 * @author luca
 */
class jdProgressBarImport extends JWindow {
    private JProgressBar bar;
    private int count = 0;
    private int max;
    private Frame owner;

    public jdProgressBarImport(Frame owner, int max){
    	super(owner);
    	this.owner = owner;
    	this.max = max;
    	owner.setEnabled(false);
        setPreferredSize(new Dimension(300,20));
        setLocation(300, 300);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        bar = new JProgressBar(0, max);
        bar.setValue(0);
        bar.setStringPainted(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bar);
        pack();
        setVisible(true);
    }

    void setProgress(int progress){
        bar.setValue(progress);
        
        if (progress == bar.getMaximum()) {
        	this.setVisible(false);
        	JOptionPane.showMessageDialog(this, "Importazione terminata con successo!");
        	owner.setEnabled(true);
        	dispose();
        }
    }

    /*
    @Override
    public void run() {
    	incrementValue();
    }
    */
}