package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.feedworker.client.frontend.jtpLog;
import org.feedworker.client.frontend.Mediator;
/**
 *
 * @author luca
 */
public class jpLog extends JPanel{
    private jtpLog logWest, logEast;
    private Mediator proxy = Mediator.getIstance();
    public jpLog(){
        super(new BorderLayout());
        logWest = new jtpLog(true);
        logEast = new jtpLog(false);
        add(new JScrollPane(logWest),BorderLayout.WEST);
        add(new JScrollPane(logEast),BorderLayout.EAST);
        proxy.setTextPaneListener(logWest);
        proxy.setTextPaneListener(logEast);
        setVisible(true);
    }
}