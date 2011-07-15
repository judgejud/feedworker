package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JEditorPane;

/**
 *
 * @author luca
 */
public class paneShow extends paneAbstract{
    
    private static paneShow jpanel = null;
    private JEditorPane jep;
    
    private paneShow(){
        super("Show");
        initializePanel();
        initializeButtons();
    }
    
    public static paneShow getPanel(){
        if (jpanel==null)
            jpanel = new paneShow();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jep = new JEditorPane();
        jep.setContentType("text/html");
        jpCenter.add(jep);
    }

    @Override
    void initializeButtons() {
        JButton jb = new JButton("test");
        jpAction.add(jb);
        jb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jep.setText(proxy.test());
                System.out.println(jep.getText());
            }
        });
        
    }
}