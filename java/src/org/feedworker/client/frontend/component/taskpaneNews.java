package org.feedworker.client.frontend.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.events.EditorPaneEvent;
import org.feedworker.client.frontend.events.EditorPaneEventListener;
import org.feedworker.client.frontend.table.tableResultSearchSub;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
/**
 *
 * @author luca judge
 */
public class taskpaneNews extends JScrollPane implements EditorPaneEventListener{
    private JEditorPane jepNews;
    private JXTaskPane taskNews, taskSubtitle;
    private JXTaskPaneContainer container;
    private tableResultSearchSub jtSub;
    private GuiCore core = GuiCore.getInstance();
    
    public taskpaneNews() {
        super();
        setName("taskpaneNews");
        setPreferredSize(new Dimension(1000, 450));
        container = new JXTaskPaneContainer();
        setViewportView(container);
        
        jepNews = new JEditorPane();
        jepNews.setContentType("text/html");
        jepNews.setBackground(Color.LIGHT_GRAY);
        jepNews.setForeground(Color.BLACK);
        jepNews.setOpaque(false);
        jepNews.setEditable(false);
        
        JButton jbDown = new JButton(core.getIconDownload());
        jbDown.setToolTipText("Scarica i sub selezionati");
        jbDown.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                core.downloadSub(jtSub, null, true, true, jtSub.getColumnCount()-1);
                core.cleanSelect(jtSub, jtSub.getColumnCount()-1);
            }
        });

        JButton jbClean = new JButton(core.getIconClean1());
        jbClean.setToolTipText("Pulisce le righe selezionate");
        jbClean.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                core.cleanSelect(jtSub, jtSub.getColumnCount()-1);
            }
        });
        
        JXPanel jpButton = new JXPanel();
        jpButton.add(jbDown);
        jpButton.add(jbClean);
        
        jtSub = new tableResultSearchSub(getName());
        JScrollPane jsp = new JScrollPane(jtSub);
        jsp.setPreferredSize(new Dimension(900, 150));
        
        taskNews = new JXTaskPane();
        taskNews.setTitle("Info News");
        taskNews.add(jepNews);

        taskSubtitle = new JXTaskPane();
        taskSubtitle.setTitle("Sottotitoli");
        taskSubtitle.add(jpButton);
        taskSubtitle.add(jsp);

        taskCollapsedTrue();
        container.add(taskNews);
        container.add(taskSubtitle);
        GuiCore.getInstance().setEditorPaneListener(this);
        GuiCore.getInstance().setTableListener(jtSub);
    }
    
    public void taskCollapsedTrue(){
        taskNews.setCollapsed(true);
        taskSubtitle.setCollapsed(true);
    }
    
    public void tableRemoveAllRows(){
        jtSub.removeAllRows();
    }

    @Override
    public void objReceived(EditorPaneEvent evt) {
        if (evt.getDest().equals(this.getName()))
            jepNews.setText(evt.getHtml());
    }
}