package org.feedworker.client.frontend.panel;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.Mediator;

import org.jdesktop.swingx.JXPanel;
/**
 * Pannello Astratto che serve per far si che le classi pannello che ereditano
 * da questo astratto reimplementino i metodi
 * 
 * @author luca
 */
abstract class paneAbstract extends JXPanel {

    private final Dimension TAB_SIZE = new Dimension(1024, 540);
    private final Dimension ACTION_PANEL_SIZE = new Dimension(1000, 30);
    protected final SoftBevelBorder BORDER = new SoftBevelBorder(BevelBorder.RAISED);
    protected final Insets BUTTON_SPACE_INSETS = new Insets(0, 2, 0, 2);
    protected final Dimension TABLE_SCROLL_SIZE = new Dimension(500, 460);
    protected final Component RIGID_AREA = Box.createRigidArea(new Dimension(5,0));
    protected GridBagConstraints gbcAction;

    protected JXPanel jpAction, jpCenter;
    protected Mediator proxy = Mediator.getIstance();
    protected GuiCore core = GuiCore.getInstance();

    /** Costruttore protetto, per essere invocato dai figli tramite ereditarietà */
    protected paneAbstract(String name) {
        super(new BorderLayout());
        setName(name);
        setPreferredSize(TAB_SIZE);
        jpCenter = new JXPanel();
        jpCenter.setLayout(new BoxLayout(jpCenter, BoxLayout.X_AXIS));
        jpAction = new JXPanel(new GridBagLayout());
        jpAction.setPreferredSize(ACTION_PANEL_SIZE);
        add(jpAction, BorderLayout.NORTH);
        add(jpCenter, BorderLayout.CENTER);
        gbcAction = new GridBagConstraints();
        gbcAction.gridx = 0;
        gbcAction.gridy = 0;
        gbcAction.insets = BUTTON_SPACE_INSETS;
        gbcAction.anchor = GridBagConstraints.NORTHWEST;
    }

    /** inizializza il pannello */
    abstract void initializePanel();

    /** inizializza i bottoni del pannello nord */
    abstract void initializeButtons();
}