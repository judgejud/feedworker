package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.table.jtResultSearchTv;
import org.jfacility.java.lang.Lang;

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
    private JButton jbSearch = new JButton(" Nuova Ricerca");

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
        jbSearch.addActionListener(this);
        pane.add(jbAdd);
        pane.add(jbCancel);
        pane.add(jbSearch);
        getContentPane().add(pane, BorderLayout.SOUTH);
        proxy.setTableListener(table);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == jbAdd){
            int[] row = table.getSelectedRows();
            int max = row.length;
            if (max>0){
                ArrayList<Object[]> array = new ArrayList<Object[]>(max);
                for (int i=0; i<max; i++){
                    Object[] obj = new Object[5];
                    for (int j=0; j<5; j++)
                        obj[j]=table.getValueAt(row[i], j);
                    array.add(obj);
                }
                proxy.searchIdTv(array);
            }
            dispose();
        } else if(ae.getSource() == jbSearch){
            dispose();
            String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
            if (Lang.verifyTextNotNull(tv))
                proxy.searchTV(tv);
        } else if(ae.getSource() == jbCancel)
            dispose();
    }
}