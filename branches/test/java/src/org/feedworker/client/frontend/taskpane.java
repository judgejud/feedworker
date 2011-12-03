package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author luca judge
 */
public class taskpane {
    public static void main (String[] args) {
        JFrame frame = new JFrame();
        frame.setUndecorated(false);
        frame.setLayout(new BorderLayout());
        DefaultListModel model = new DefaultListModel();
        JXList list = new JXList(model);
        list.setRolloverEnabled(true);
        list.setMaximumSize(new Dimension(300,80));
        list.setPreferredSize(new Dimension(300,80));
        list.setCellRenderer(new GraphicList());
        model.insertElementAt("test",0);
        JXTaskPaneContainer tpc = new JXTaskPaneContainer();
        JXTaskPane tp = new JXTaskPane();
        tp.setTitle("taskpane fuori jlist");
        tpc.add(tp);
        frame.add(list, BorderLayout.NORTH);
        frame.add(tpc, BorderLayout.CENTER);
        frame.setMinimumSize(new Dimension(300,200));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    static class GraphicList extends JXTaskPaneContainer implements ListCellRenderer {
        public GraphicList() {
            super();
        }
        @Override
        public Component getListCellRendererComponent(JList jlist, Object e, int i, boolean bln, boolean bln1) {
            JXTaskPane tp = new JXTaskPane();
            tp.add(new JLabel(e.toString()));
            tp.setTitle("taskpane dentro jlist");
            tp.setAnimated(true);
            tp.setEnabled(true);
            add(tp);
            return this;
        }
    }
}