package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author luca
 */
class jdResultSearchTv extends JDialog implements ActionListener{
    private static jdResultSearchTv dialog;
    private Mediator proxy = Mediator.getIstance();
    private jtResultSearchTv table;
    private JButton jbAdd = new JButton(" Aggiunge ");
    private JButton jbCancel = new JButton(" Annulla ");

    public static jdResultSearchTv getDialog(){
        if (dialog==null)
            dialog = new jdResultSearchTv();
        return dialog;
    }

    private jdResultSearchTv(){
        setTitle("Risultati ricerca serie tv");
        setModal(true);
        setPreferredSize(new Dimension(550,350));
        setLocation(100, 100);
        getContentPane().setLayout(new BorderLayout());
        table = new jtResultSearchTv(proxy.getSearchTV());
        JScrollPane jScrollTable1 = new JScrollPane(table);
        jScrollTable1.setAutoscrolls(true);
        getContentPane().add(jScrollTable1, BorderLayout.CENTER);
        JPanel pane = new JPanel();
        jbAdd.addActionListener(this);
        jbCancel.addActionListener(this);
        pane.add(jbAdd);
        pane.add(jbCancel);
        getContentPane().add(pane, BorderLayout.SOUTH);
        proxy.setTableListener(table);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == jbAdd){
            int row = table.getSelectedRow();
            if (row>-1)
                proxy.searchIdTv(table.getValueAt(row, 0));
        }
        dispose();
    }
}