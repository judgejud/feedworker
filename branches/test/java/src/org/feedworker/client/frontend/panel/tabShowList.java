package org.feedworker.client.frontend.panel;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.Point;
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

import org.jfacility.javax.swing.Swing;

import org.jdesktop.swingx.JXList;
/**
 *
 * @author Administrator
 */
public class tabShowList extends JScrollPane implements ListEventListener{
    private DefaultListModel model = new DefaultListModel();
    private JXList list = new JXList(model);

    public tabShowList(String name) {
        super();
        setName(name);
        setViewportView(list);
        list.setName(name);
        list.setCellRenderer(new GraphicList());
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JXList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        GuiCore.getInstance().setListListener(this);
    }

    public Object[] getArrayModel(){
        return model.toArray();
    }
    
    int getSizeModel(){
        return model.getSize();
    }
    
    void removeSelectedItem(){
        int index = list.getSelectedIndex();
        if (index>-1)
            model.remove(index);
    }
    
    Object getListSelectedValue(){
        return ((Object[])list.getSelectedValue())[0];
    }
    
    Object[] getListCloneSelectedValue(){
        return ((Object[])list.getSelectedValue());
    }
    
    void addItem(Object elem){
        model.addElement(elem);
    }
    
    void rename(String name){
        setName(name);
        list.setName(name);
    }
    
    void setListPointSelection(Point p){
        list.setSelectedIndex(list.locationToIndex(p));
    }
    
    void setListMouseListener(MouseAdapter ma){
        list.addMouseListener(ma);
    }

    @Override
    public void objReceived(ListEvent evt) {
        if (evt.getName().equals(list.getName())){
            for (int i=0; i<evt.getArray().length; i++)
                model.addElement(evt.getArray()[i]);
        }
    }
    
    class GraphicList extends DefaultListCellRenderer{
        int dim = 51;
        @Override
        public Component getListCellRendererComponent(JList list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, 
                                                    index, isSelected, cellHasFocus);
            label.setPreferredSize(new Dimension(dim, dim));
            Object values[] = (Object[]) value;
            setName(values[0].toString());
            label.setName(values[0].toString());
            label.setOpaque(false);
            label.setText(null);
            label.setToolTipText(values[0].toString());          
            label.setIcon(Swing.scaleImageARGB((ImageIcon)values[1], dim, dim));
            return label;
        }
    }
}