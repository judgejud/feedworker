package org.feedworker.client.frontend;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author luca
 */
class jpResultSearchTv extends JPanel{
    private static jpResultSearchTv jpanel;

    private Mediator proxy = Mediator.getIstance();
    private jtResultSearchTv table;

    public static jpResultSearchTv getPanel(){
        if (jpanel==null)
            jpanel = new jpResultSearchTv();
        return jpanel;
    }

    private jpResultSearchTv(){
        super();
        setPreferredSize(new Dimension(650,300));
        table = new jtResultSearchTv(proxy.getSearchTV());
        JScrollPane jScrollTable1 = new JScrollPane(table);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1);
        proxy.setTableListener(table);
        setVisible(true);
    }

}