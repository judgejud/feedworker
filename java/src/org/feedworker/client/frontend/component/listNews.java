package org.feedworker.client.frontend.component;

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
/*
    public Object[] getArrayModel(){
        return model.toArray();
    }
    
    int getSizeModel(){
        return model.getSize();
    }
    
    public void removeSelectedItem(){
        int index = list.getSelectedIndex();
        if (index>-1)
            model.remove(index);
    }
    
    public Object getListSelectedValue(){
        return ((Object[])list.getSelectedValue())[0];
    }
    
    public Object[] getListCloneSelectedValue(){
        return ((Object[])list.getSelectedValue());
    }
    
    public void addItem(Object elem){
        model.addElement(elem);
    }
    
    public void rename(String name){
        setName(name);
        list.setName(name);
    }
    
    public void setListPointSelection(Point p){
        list.setSelectedIndex(list.locationToIndex(p));
    }
    
    public void setListMouseListener(MouseAdapter ma){
        list.addMouseListener(ma);
    }
*/
    @Override
    public void objReceived(ListEvent evt) {
        if (evt.getName().equals(list.getName())){
            for (int i=0; i<evt.getMatrix().length; i++)
                model.addElement(evt.getMatrix()[i]);
        }
    }
    
    class GraphicList extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, 
                                                    index, isSelected, cellHasFocus);
            label.setPreferredSize(new Dimension(116, 70));
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
/*
class ValueComparator<T> implements Comparator<T> {
    @Override    
    public int compare(Object o1, Object o2) {
        String s1 = ((Object[])o1)[0].toString().toLowerCase();
        String s2 = ((Object[])o2)[0].toString().toLowerCase();
        return s1.compareTo(s2);
    }
}

class ValueComparator<Component> implements Comparator<Component> {
    @Override    
    public int compare(Component o1, Component o2) {
        String s1 = ((JLabel)o1).getName().toLowerCase();
        String s2 = ((JLabel)o2).getName().toLowerCase();
        return s1.compareTo(s2);
    }
}

class ValueComparator implements Comparator<GraphicList> {
    @Override
    public int compare(GraphicList t, GraphicList t1) {
        return t.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
    }
}
*/