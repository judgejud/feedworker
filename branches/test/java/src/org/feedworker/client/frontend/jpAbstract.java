/**TODO: rivedere il concetto di abstract e soprattutto capir xkè ne viene permesso l'istanziamento
 * diretto in myjframe ad es.
 */
package org.feedworker.client.frontend;

//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

/**
 * Pannello Astratto che serve per far si che le classi pannello che ereditano
 * da questo astratto reimplementino i metodi
 * 
 * @author luca
 */
abstract class jpAbstract extends JPanel {
	private final Dimension TAB_SIZE = new Dimension(1024, 540);
	private final Dimension ACTION_PANEL_SIZE = new Dimension(1000, 30);
	protected final SoftBevelBorder BORDER = new SoftBevelBorder(
			BevelBorder.RAISED);
	protected final Insets BUTTON_SPACE_INSETS = new Insets(0, 2, 0, 2);
	protected final Dimension TABLE_SCROLL_SIZE = new Dimension(500, 460);
	protected JPanel actionJP;
	protected Mediator proxy = Mediator.getIstance();

	/** Costruttore protetto, per essere invocato dai figli tramite ereditarietà */
	protected jpAbstract() {
		super(new BorderLayout());
		setPreferredSize(TAB_SIZE);
		actionJP = new JPanel(new GridBagLayout());
		actionJP.setPreferredSize(ACTION_PANEL_SIZE);
	}

	/** inizializza il pannello */
	abstract void initializePanel();

	/** inizializza i bottoni del pannello nord */
	abstract void initializeButtons();
}