package org.jrss2sub.client.frontend;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//IMPORT JAVAX
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
//IMPORT MYUTILS
import org.lp.myUtils.Swing;
/**
 *
 * @author luca
 */
class paneRole extends paneAbstract{
    private static paneRole jpanel = null;
    private tableXml jtable;    

    public static paneRole getPanel(){
        if (jpanel == null)            
            jpanel = new paneRole();
        return jpanel;
    }

    private paneRole(){
        super();
        initPane();
        initButtons();
    }

    @Override
    void initPane() {
        jtable = new tableXml();
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setPreferredSize(new Dimension(650, 460));
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.WEST);
        setVisible(true);
        proxy.setTableXmlListener(jtable);
    }

    @Override
    void initButtons() {
        JButton jbAddRow = new JButton(" + ");
        jbAddRow.setToolTipText("Aggiungi riga alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });

        JButton jbSaveRole = new JButton(" Salva ");
        jbSaveRole.setToolTipText("Salva impostazioni");
        jbSaveRole.setBorder(BORDER);
        jbSaveRole.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbSaveRoleMouseClicked();
            }
        });

        JButton jbAddDir = new JButton(" Destinazione ");
        jbAddDir.setToolTipText("");
        jbAddDir.setBorder(BORDER);
        jbAddDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddDirMouseClicked();
            }
        });

        JLabel jlTemp = new JLabel();
        jlTemp.setPreferredSize(new Dimension(700, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTONSPACEINSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        jpButton.add(jbAddRow, gbc);
        gbc.gridx = 1;
        jpButton.add(jbRemoveRow, gbc);
        gbc.gridx = 2;
        jpButton.add(jbSaveRole, gbc);
        gbc.gridx = 3;
        jpButton.add(jbAddDir, gbc);
        gbc.gridx = 4;                
        jpButton.add(jlTemp, gbc);

        add(jpButton, BorderLayout.NORTH);
    }

    private void jbAddRowMouseClicked(){
        DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
        dtm.insertRow(0,new Object[]{null,null,"normale",null});
    }

    private void jbSaveRoleMouseClicked(){        
        proxy.saveRole(jtable);
    }

    private void jbRemoveRowMouseClicked(){
        int row = jtable.getSelectedRow();
        if (row>-1)
            ((DefaultTableModel) jtable.getModel()).removeRow(row);        
    }

    private void jbAddDirMouseClicked(){
        int row = jtable.getSelectedRow();
        if (row>-1){
            String dir = Swing.getDirectory(this, "");
            if (dir!=null){
                DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
                dtm.setValueAt(dir, row, 3);
            }
        }
    }    
}