package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

/**
 *
 * @author luca
 */
class jdProgressBarImport extends JDialog implements Runnable{
    private JProgressBar bar;
    private int count = 0;

    public jdProgressBarImport(int max){
        setTitle("Importazione serie tv");
        setPreferredSize(new Dimension(300,60));
        setLocation(300, 300);
        bar = new JProgressBar(0, max);
        bar.setValue(count);
        bar.setStringPainted(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bar);
        pack();
        setVisible(true);
    }

    void incrementValue(){
        bar.setValue(++count);
    }

    @Override
    public void run() {
        incrementValue();
    }
}