package org.feedworker.client.frontend.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;

import org.jdesktop.swingx.JXList;
/**
 *
 * @author Administrator
 */
public class listNews extends JScrollPane implements ListEventListener{
    private DefaultListModel<Object> model = new DefaultListModel<Object>();
    private JXList list = new JXList(model);

    public listNews(String name) {
        super();
        setName(name);
        
        list.setName(name);
        list.setCellRenderer(new GraphicList());
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JXList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        
        setViewportView(list);
        GuiCore.getInstance().setListListener(this);
    }
    
    public Object getListSelectedValue(){
        return ((Object[])list.getSelectedValue())[0];
    }
    
    public void setListMouseListener(MouseAdapter ma){
        list.addMouseListener(ma);
    }

    @Override
    public void objReceived(ListEvent evt) {
        if (evt.getName().equals(list.getName())){
            for (int i=0; i<evt.getArrayList().size(); i++)
                model.addElement(evt.getArrayList().get(i));
        }
    }
    
    class GraphicList extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, 
                                                    index, isSelected, cellHasFocus);
            label.setPreferredSize(new Dimension(121, 75));
            label.setOpaque(false);
            Object values[] = (Object[]) value;
            label.setText(null);
            label.setName(values[0].toString());
            label.setToolTipText(values[1].toString());
            label.setIcon((ImageIcon)values[2]);
            return label;
        }
    }
}