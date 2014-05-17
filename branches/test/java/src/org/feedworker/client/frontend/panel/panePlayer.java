package org.feedworker.client.frontend.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.media.Player;
import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfacility.javax.swing.Swing;
/**
 *
 * @author Administrator
 */
public class panePlayer extends paneAbstract{
    private static panePlayer jpanel;
//    private Player player;
    private FileNameExtensionFilter fnfe = new FileNameExtensionFilter("Video file", "avi", "mp4", "mkv");

    private panePlayer() {
        super("Player");
        initializePanel();
        initializeButtons();
    }
    
    public static panePlayer getPanel(){
        if (jpanel==null)
            jpanel = new panePlayer();
        return jpanel;
    }
        
    @Override
    void initializePanel() {
        //jpCenter.add(player.getVisualComponent());
    }

    @Override
    void initializeButtons() {
        JButton jbOpen = new JButton("Open Video");
        jbOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectVideo();
            }
        });
        jpAction.add(jbOpen);
        //jpAction.add(player.getControlPanelComponent());
    }
    
    private void selectVideo(){
        String video  = Swing.getFile(this, "Seleziona il video", fnfe, null);
        if (video!=null){
            
        }
    }
    
}