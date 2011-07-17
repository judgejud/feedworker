package org.feedworker.client.frontend.table;

import java.awt.Font;
import javax.swing.JToolTip;

/**
 *
 * @author Administrator
 */
class labelTooltipZoom extends JToolTip{
    private final Font font = new Font("Arial", Font.PLAIN, 14);
    
    public labelTooltipZoom(String text) {
        super();
        this.setFont(font);
        this.setTipText(text);
    }
    
    
    
    
}