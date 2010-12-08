package org.feedworker.client.frontend;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author luca
 */
class jpResultSearchTv extends JPanel{
    private Mediator proxy = Mediator.getIstance();
    private jtResultSearchTv table;

    public jpResultSearchTv(){
        super();
        //setPreferredSize(new Dimension(400, 300));
        table = new jtResultSearchTv(proxy.getSearchTV());
        JScrollPane jScrollTable1 = new JScrollPane(table);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1);
        proxy.setTableListener(table);
        setVisible(true);
    }
}