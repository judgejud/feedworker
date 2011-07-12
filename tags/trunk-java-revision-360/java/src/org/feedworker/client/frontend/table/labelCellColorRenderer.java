package org.feedworker.client.frontend.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.feedworker.client.frontend.Mediator;
import org.jfacility.javax.swing.Swing;

/**
 * Classe che restituisce la jlabel della cella tabella con determinati
 * colori e font di testo ed eventuale tooltip se testo lungo
 *
 * @author luca
 */
class labelCellColorRenderer extends JLabel implements TableCellRenderer {
    private final Font font = new Font("Arial", Font.PLAIN, 11);
    private Mediator proxy = Mediator.getIstance();
    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        // imposta il testo della cella
        String text = value.toString();
        setText(text);
        // imposta il font della cella
        setFont(font);
        setBackground(proxy.searchVersion(text));
        Color back = getBackground();
        if (back.equals(Color.blue) || back.equals(Color.red) 
            || back.equals(Color.black) || back.equals(new Color(183, 65, 14)) 
            || back.equals(Color.darkGray)) {        
            setForeground(Color.white);
        } else
            setForeground(Color.black);
        setOpaque(true);
        try{
            setToolTipText(Swing.getTextToolTip(table, column, this, text));
        } catch (NullPointerException e){
            setToolTipText(null);
        }
        this.repaint();
        return this;
    }
}