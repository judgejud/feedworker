package org.feedworker.client.frontend.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.feedworker.client.frontend.GuiCore;
import org.feedworker.client.frontend.Mediator;
import org.feedworker.client.frontend.events.ListEvent;
import org.feedworker.client.frontend.events.ListEventListener;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXList.DelegatingRenderer;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.ListCellContext;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.rollover.ListRolloverController;
import org.jdesktop.swingx.rollover.RolloverRenderer;

/**
 *
 * @author luca judge
 */
public class listTaskBlog extends JScrollPane implements ListEventListener{
    private JXList list = null;
    private DefaultListModel<SampleTaskPaneModel> model = new ContentListeningListModel();
    
    public listTaskBlog(String name){
        super();
        setName(name);
        list = new JXList() {
            @Override
            protected ListRolloverController createLinkController() {
                return new XXListRolloverController<JXList>();
            }
        };
        setViewportView(list);
        
        list.setCellRenderer(createRolloverRenderer());
        list.setFixedCellHeight(-1);
        list.setRolloverEnabled(true);
        
        ListDataListener listener = new ListDataListener() {
            @Override
            public void contentsChanged(ListDataEvent e) {
                list.setFixedCellHeight(10);
                list.setFixedCellHeight(-1);
                list.revalidate();
                list.repaint();
            }
            @Override
            public void intervalAdded(ListDataEvent e) {}
            @Override
            public void intervalRemoved(ListDataEvent e) {}
        };
        
        model.addListDataListener(listener);
        list.setModel(model);
    }
    
    private LiveTaskPaneListRenderer createRolloverRenderer() {
        LiveTaskPaneListRenderer renderer = new LiveTaskPaneListRenderer();
        StringValue sv = new StringValue() {
            @Override
            public String getString(Object value) {
                if (value instanceof SampleTaskPaneModel)
                    return ((SampleTaskPaneModel) value).getTitle();
                return "";
            }
        };
        renderer.getComponentProvider().setStringValue(sv);
        return renderer;
    }

    @Override
    public void objReceived(ListEvent evt) {
        if (evt.getName().equals(getName())){
            String start = "<html><head></head><body>" +
                            "<table width=\"98%\"><tr>" + 
                            "<td width=\"9%\"><b>Autore</b><</td><td>";
            String mid = "</td></tr><tr><td width=\"10%\"><b>"; 
            String end = "</td></tr></table></body></html>";
            ArrayList<Object[]> items = evt.getArrayList();
            for (int i = 0; i < items.size(); i++) {
                Object[] obj = items.get(i);
                SampleTaskPaneModel task = new SampleTaskPaneModel();
                task.setExpanded(false);
                task.setTitle(obj[1].toString() + " " + obj[2].toString());
                String html = start +  obj[3].toString();
                html += mid + "Descrizione</b></td><td align=\"justify\">" + obj[4].toString();
                html += end;
                task.setText(html);
                task.setUrl(obj[0].toString());
                model.insertElementAt(task, i);
                //model.addElement(task);
            }
        }
    }
}

/**
* a quick@dirty model ... just to have something to show.
*/
class SampleTaskPaneModel extends AbstractBean {
    private boolean expanded;
    private String title, text, url;
    
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        boolean old = isExpanded();
        this.expanded = expanded;
        firePropertyChange("expanded", old, isExpanded());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String old = getTitle();
        this.title = title;
        firePropertyChange("title", old, getTitle());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = getText();
        this.text = text;
        firePropertyChange("text", old, getText());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        String old = getUrl();
        this.url = url;
        firePropertyChange("url", old, getUrl());
    }
} //end SampleTaskPaneModel

class ContentListeningListModel extends DefaultListModel<SampleTaskPaneModel> {
    private PropertyChangeListener p;
    
    @Override
    public void insertElementAt(SampleTaskPaneModel item, int i){
        super.insertElementAt(item, i);
        if (item instanceof AbstractBean)
            ((AbstractBean) item).addPropertyChangeListener(getPropertyChangeListener());
    }

    protected void fireContentPropertyChanged(PropertyChangeEvent evt) {
        int index = indexOf(evt.getSource());
        fireContentsChanged(this, index, index);
    }

    protected PropertyChangeListener getPropertyChangeListener() {
        if (p == null) {
            p = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    fireContentPropertyChanged(evt);
                }
            };
        }
        return p;
    }
}//end ContentListeningListModel

/**
* implementation of rolloverController which adds the live
* renderer to the list. This version uses the SwingX
* enhanced renderer support (TaskPane specific).
*/
class XXListRolloverController<T extends JList> extends ListRolloverController<JList> {
    Component activeRendererComponent;
    private MouseListener rendererMouseListener;
    
    @Override
    protected RolloverRenderer getRolloverRenderer(Point location, boolean prepare) {
        DelegatingRenderer renderer = (DelegatingRenderer) component.getCellRenderer();
        if (prepare) {
            Object element = component.getModel().getElementAt(location.y);
            renderer.getListCellRendererComponent(component, element, location.y, false, true);
        }
        return (RolloverRenderer) renderer.getDelegateRenderer();
    }

    @Override
    protected void rollover(Point oldLocation, Point newLocation) {
    // this is not entirely clean: need to handle mouseExit from component
    // as opposed to mouseExit to activeRenderer
        if ((newLocation == null)) return;
        if (activeRendererComponent != null)
            removeActiveRendererComponent();
        if (hasRollover(newLocation)) {
            Rectangle cellBounds = component.getCellBounds(newLocation.y, newLocation.y);
            LiveTaskPaneListRenderer renderer = 
                    (LiveTaskPaneListRenderer) getRolloverRenderer(newLocation, false);
            Object element = component.getModel().getElementAt(newLocation.y);
            activeRendererComponent = renderer.getRolloverListCellRendererComponent(component, element,
            newLocation.y, false, true);
            activeRendererComponent.setBounds(cellBounds);
            activeRendererComponent.addMouseListener(getRendererMouseListener());
            component.add(activeRendererComponent);
            activeRendererComponent.validate();
        }
    }

    @Override
    protected void click(Point location) {
        super.click(location);
    }

    private MouseListener getRendererMouseListener() {
        if (rendererMouseListener == null) {
            rendererMouseListener = new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    removeActiveRendererComponent();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    // force the re-validate and sizing - dirty!
                    int index = component.locationToIndex(e.getPoint());
                    Rectangle cellBounds = component.getCellBounds(index, index);
                    activeRendererComponent.setBounds(cellBounds);
                    component.revalidate();
                    component.repaint();
                }
            };
        }
        return rendererMouseListener;
    }

    protected void removeActiveRendererComponent() {
        activeRendererComponent.removeMouseListener(getRendererMouseListener());
        component.remove(activeRendererComponent);
        activeRendererComponent = null;
        component.revalidate();
        component.repaint();
    }
} //end XXListRolloverController

/**
* Quick check if new swingx renderer support can handle "live" renderers. <p>
*
* The new interface LiveRolloverRenderer has to be implemented by all
* collaborators in the chain: componentController, rendererController,
* DefaultXXRenderer. Additionally, the concrete cellContext needs a new property
* "live" property. Might be migrated to super cellContext?
*/
class LiveTaskPaneListRenderer extends DefaultListRenderer implements LiveRolloverRenderer {
    public LiveTaskPaneListRenderer(LiveTaskPaneProvider renderingController) {
        super(renderingController);
        cellContext = new LiveListCellContext();
    }

    public LiveTaskPaneListRenderer() {
        this(new LiveTaskPaneProvider());
    }

    public Component getRolloverListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        ((LiveListCellContext) cellContext).installContext(list, value, index, 0, isSelected, cellHasFocus, true, true, true);
        componentController.getRendererComponent(cellContext);
        return getLiveRendererComponent();
    }

    @Override
    public JComponent getLiveRendererComponent() {
        return ((LiveRolloverRenderer) componentController).getLiveRendererComponent();
    }
}

/**
* Allows access to a component for "live" addition.
*
*/
interface LiveRolloverRenderer extends RolloverRenderer {
/**
* Returns a component suitable for live addition to the
* container hierarchy (during rollover). <p>
* Note: implementations should use a different instance than
* the "normal" renderer component because there might be
* unrelated normal rendering requests during the rollover.
* 
* @return a component suitable for live addition to the container
*   hierarchy.
*/
    public JComponent getLiveRendererComponent();
}

/**
* a quick implementation of a rollover-enabled ListCellRenderer using a
* JXTaskPane. Also has experimental support for a real live component,
* added to the list on cell-enter and removed on cell-exit.
*/
class LiveTaskPaneProvider extends ComponentProvider<JXTaskPane> implements 
                                                        LiveRolloverRenderer {
    private AbstractHyperlinkAction<SampleTaskPaneModel> linkAction;
    private JXTaskPane liveTaskPane;
    private SampleTaskPaneModel liveTaskPaneModel;
    private PropertyChangeListener taskPanePropertyListener;

    public LiveTaskPaneProvider() {
        super();
        liveTaskPane = createRendererComponent();
        //liveTaskPane.setBorder(null);
        liveTaskPane.setBorder(BorderFactory.createCompoundBorder(null, 
                                                        liveTaskPane.getBorder()));
            //BorderFactory.createLineBorder(Color.RED), liveTaskPane.getBorder()));
    }
    // ----------- implement RolloverRenderer
    @Override
    public void doClick() {
        if (!isEnabled()) return;
        getTaskPaneModelLinkAction().actionPerformed(null);
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    // ---------- implement ext RolloverRenderer
    @Override
    public JXTaskPane getLiveRendererComponent() {
        return liveTaskPane;
    }
    // ------------- implement abstract methods of RendererComponentController
    @Override
    protected void configureState(CellContext context) {}
    @Override
    protected JXTaskPane createRendererComponent() {
        JXTaskPane taskPane = new JXTaskPane();
        taskPane.setAnimated(false);
        taskPane.setScrollOnExpand(false);
        taskPane.setCollapsed(true);
        return taskPane;
    }

    @Override
    protected void format(CellContext context) {
        if (((LiveListCellContext) context).isLive() && isEnabled())
            configureLiveRenderingComponent(context.getValue());
        else
            configureDeadRenderingComponent(context.getValue());
    }
    //--------------------- configure dead renderer
    private void configureDeadRenderingComponent(Object value) {
        JXTaskPane taskPane = rendererComponent;
        if (value instanceof SampleTaskPaneModel)
            configureTaskPane(taskPane, (SampleTaskPaneModel) value);
        else
            unconfigureTaskPane(taskPane, value);
    }
    //--------------------- configure live renderer
    private void configureLiveRenderingComponent(Object value) {
        JXTaskPane taskPane = liveTaskPane;
        if (value instanceof SampleTaskPaneModel)
            configureLiveTaskPane(taskPane, value);
        else
            unconfigureTaskPane(taskPane, value);
    }

    private void configureLiveTaskPane(JXTaskPane taskPane, Object value) {
        if (liveTaskPaneModel != null) {
            taskPane.removePropertyChangeListener(getTaskPanePropertyChangeListener());
            liveTaskPaneModel = null;
        }
        liveTaskPaneModel = (SampleTaskPaneModel) value;
        configureTaskPane(taskPane, liveTaskPaneModel);
        taskPane.addPropertyChangeListener(getTaskPanePropertyChangeListener());
    }

    private PropertyChangeListener getTaskPanePropertyChangeListener() {
        if (taskPanePropertyListener == null) {
            taskPanePropertyListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("collapsed".equals(evt.getPropertyName()))
                        updateModelExpansion((Boolean) evt.getNewValue());
                }
            };
        }
        return taskPanePropertyListener;
    }

    protected void updateModelExpansion(Boolean collapsed) {
    //        if (liveTaskPaneModel == null) return;
        liveTaskPaneModel.setExpanded(!collapsed);
    // validation problems of the live component
    // the "usual suspects" didn't help ... leave it for now...
    }
    //--------------------- utility methods for both live and dead component
    private void configureTaskPane(JXTaskPane taskPane, SampleTaskPaneModel model) {
        JEditorPane jep = new JEditorPane();
        jep.setContentType("text/html");
        jep.setText(model.getText());
        jep.setFont(new Font("Arial", Font.PLAIN, 13));
        jep.setPreferredSize(new Dimension(900,150));
        final String url = model.getUrl();
        JButton jbBrowse = new JButton(GuiCore.getInstance().getIconWWW());
        jbBrowse.setName(url);
        jbBrowse.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jbBrowse.setToolTipText("Visualizza l'articolo corrente del blog nel browser");
        jbBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mediator.getIstance().openWebsite(url);
            }
        });
        JButton jbCopyUrl = new JButton(GuiCore.getInstance().getIconCopy());
        jbCopyUrl.setName(url);
        jbCopyUrl.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jbCopyUrl.setToolTipText("Copia l'url dell'articolo nella clipboard");
        jbCopyUrl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
                //GuiCore.getInstance().openWebsite(url);
            }
        });
        JPanel jp = new JPanel();
        jp.add(jbBrowse);
        jp.add(jbCopyUrl);
        taskPane.removeAll();
        taskPane.add(jp);
        taskPane.add(new JScrollPane(jep));
        taskPane.setTitle(getString(model));
        taskPane.setCollapsed(!model.isExpanded());
    }

    private void unconfigureTaskPane(JXTaskPane taskPane, Object value) {
        taskPane.removeAll();
        taskPane.setTitle(getString(value));
        taskPane.setCollapsed(true);
    }

    private AbstractHyperlinkAction<SampleTaskPaneModel> getTaskPaneModelLinkAction() {
        if (linkAction == null) {
            linkAction = new AbstractHyperlinkAction<SampleTaskPaneModel>() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    boolean expanded = getTarget().isExpanded();
//                    getTarget().setExpanded(!expanded);
                }
            };
        }
        return linkAction;
    }
    
    @Override
    protected void configureVisuals(CellContext context) {}
}

/**
08.* Custom cell context: has live property.
09.*/
class LiveListCellContext extends ListCellContext {
    boolean live;
    
    @Override
    public void installContext(JList component, Object value, int row, int column, boolean selected, boolean focused, boolean expanded, boolean leaf) {
        super.installContext(component, value, row, column, selected, focused, expanded, leaf);
        live = false;
    }

    public void installContext(JList component, Object value, int row, int column, boolean selected, boolean focused, boolean expanded, boolean leaf, boolean live) {
        super.installContext(component, value, row, column, selected, focused,expanded, leaf);
        this.live = live;
    }

    public boolean isLive() {
        return live;
    }
}