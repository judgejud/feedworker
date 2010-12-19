package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

/**
 * 
 * @author luca
 */
class jdProgressBarImport extends JDialog {
    private JProgressBar bar;

    public jdProgressBarImport(Frame owner) {
        super(owner, "Importazione ...", ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setPreferredSize(new Dimension(300, 50));
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setStringPainted(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bar);
        pack();

        Thread t = new Thread(new Runnable()  {
            @Override
            public void run() {
                jdProgressBarImport.this.setVisible(true);
            }
        });

        t.start();
    }

    void setProgress(int progress) {
        bar.setValue(progress);
        if (progress == bar.getMaximum())
            dispose();
    }
}