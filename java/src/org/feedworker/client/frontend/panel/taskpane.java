package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
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
public class taskpane {
    static JXList list;
    public static void main (String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        list = new JXList() {
            @Override
            protected ListRolloverController createLinkController() {
                return new XXListRolloverController();
            }
        };
        list.setCellRenderer(createRolloverRenderer());
        list.setFixedCellHeight(-1);
        list.setRolloverEnabled(true);
        ListModel model = createTaskPaneModel(10);
        ListDataListener listener = new ListDataListener() {
            @Override
            public void contentsChanged(ListDataEvent e) {
                list.setFixedCellHeight(10);
                list.setFixedCellHeight(-1);
                list.revalidate();
                list.repaint();
            }

            @Override
            public void intervalAdded(ListDataEvent e) {
            // TODO Auto-generated method stub
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
            // TODO Auto-generated method stub
            }
        };
        model.addListDataListener(listener);
        list.setModel(model); 
        
        frame.add(new JScrollPane(list), BorderLayout.CENTER);
        frame.setMinimumSize(new Dimension(300,200));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private static LiveTaskPaneListRenderer createRolloverRenderer() {
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
    
    private static ListModel createTaskPaneModel(int count) {
        final DefaultListModel l = new ContentListeningListModel();
        for (int i = 0; i < count; i++) {
            SampleTaskPaneModel item = new SampleTaskPaneModel();
            
            item.setExpanded(false);
            item.setTitle("TaskPane - " + i);
            List<Action> actions = createItemList(i);
            item.setActions(actions);
            item.setComponent(new JLabel("TaskPane - " + i));
            l.addElement(item);
        }
        return l;
    }
    
    private static List<Action> createItemList(int i) {
        List<Action> items = new ArrayList<Action>();
        for (int j = 0; j < i; j++)
            items.add(createAction(i, j));
        return items;
    }

    private static Action createAction(int i, int j) {
        Action action = new AbstractAction("taskPane - " + i + " :: action at - " + j) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
            }
        };
        return action;
    }
/**
* implementation of rolloverController which adds the live
* renderer to the list. This version uses the SwingX
* enhanced renderer support (TaskPane specific).
*/
static class XXListRolloverController<T extends JList> extends ListRolloverController<JList> {
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
static class LiveTaskPaneListRenderer extends DefaultListRenderer implements LiveRolloverRenderer {
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
023.* a quick implementation of a rollover-enabled ListCellRenderer using a
024.* JXTaskPane. Also has experimental support for a real live component,
025.* added to the list on cell-enter and removed on cell-exit.
026.*/
static class LiveTaskPaneProvider extends ComponentProvider<JXTaskPane> implements 
                                                        LiveRolloverRenderer {
    private AbstractHyperlinkAction<SampleTaskPaneModel> linkAction;
    private JXTaskPane liveTaskPane;
    private SampleTaskPaneModel liveTaskPaneModel;
    private PropertyChangeListener taskPanePropertyListener;

    public LiveTaskPaneProvider() {
        super();
        liveTaskPane = createRendererComponent();
        
        liveTaskPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.RED), liveTaskPane.getBorder()));
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
    protected void configureState(CellContext context) {
    // TODO Auto-generated method stub
    }

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
        taskPane.removeAll();
        for (Action item : model.getActions())
        // obviously this is a no-no in the real world
            taskPane.add(item);
        taskPane.setTitle(getString(model));
        taskPane.setCollapsed(!model.isExpanded());
        getTaskPaneModelLinkAction().setTarget(model);
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
                    boolean expanded = getTarget().isExpanded();
                    getTarget().setExpanded(!expanded);
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
static class LiveListCellContext extends ListCellContext {
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

/**
10.* a quick@dirty model ... just to have something to show.
11.*/
static class SampleTaskPaneModel extends AbstractBean {
    private boolean expanded;
    private String title;
    private List<Action> actions;
    private JComponent component;

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        List<Action> old = getActions();
        this.actions = actions;
        firePropertyChange("actions", old, getActions());
    }

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
    
    public JComponent getComponent() {
        return component;
    } 
    
    public void setComponent(JComponent comp) {
        JComponent old = getComponent();
        this.component = comp;
        firePropertyChange("add", old, getComponent());
    }
} //end SampleTaskPaneModel

static class ContentListeningListModel extends DefaultListModel {
    private PropertyChangeListener p;
    
    @Override
    public void addElement(Object obj) {
        super.addElement(obj);
        if (obj instanceof AbstractBean)
            ((AbstractBean) obj).addPropertyChangeListener(getPropertyChangeListener());
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
}