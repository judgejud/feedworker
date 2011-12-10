package org.feedworker.client.frontend.component;

import java.awt.Font;
import javax.swing.JToolTip;

/**
 *
 * @author Administrator
 */
class z_labelTooltipZoom extends JToolTip{
    private final Font font = new Font("Arial", Font.PLAIN, 14);
    
    public z_labelTooltipZoom(String text) {
        super();
        this.setFont(font);
        this.setTipText(text);
    }
    
    
    
    
}