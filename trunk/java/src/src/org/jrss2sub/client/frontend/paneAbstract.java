/**TODO: rivedere il concetto di abstract e soprattutto capir xkè ne viene permesso l'istanziamento
 * diretto in myjframe ad es.
 */
package org.jrss2sub.client.frontend;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
//IMPORT JAVAX
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
/**Pannello Astratto che serve per far si che le classi pannello che ereditano
 *da questo astratto reimplementino i metodi
 * @author luca
 */
abstract class paneAbstract extends JPanel{
    private final Dimension TABBEDSIZE = new Dimension(1024, 540);
    private final Dimension BUTTONPANESIZE = new Dimension(1000,30);   
    protected final SoftBevelBorder BORDER = new SoftBevelBorder(BevelBorder.RAISED);
    protected final Insets BUTTONSPACEINSETS = new Insets(0, 2, 0, 2);
    protected final Dimension TABLESCROLLSIZE = new Dimension(500, 460);
    protected JPanel jpButton;
    protected Mediator proxy = Mediator.getIstance();
    /**Costruttore protetto, per essere invocato dai figli tramite ereditarietà*/
    protected paneAbstract(){
        super(new BorderLayout());
        setPreferredSize(TABBEDSIZE);
        jpButton = new JPanel(new GridBagLayout());
        jpButton.setPreferredSize(BUTTONPANESIZE);
    }
    /**inizializza il pannello*/
    abstract void initPane();
    /**inizializza i bottoni del pannello nord*/
    abstract void initButtons();
}