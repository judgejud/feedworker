package org.feedworker.client.frontend;
//IMPORT JAVA
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.jfacility.Swing;
/**
 *
 * 
 * @author luca
 */
class paneRole extends AbstractJP{
    private static paneRole jpanel = null;
    private tableXml jtable;    

    public static paneRole getPanel(){
        if (jpanel == null)            
            jpanel = new paneRole();
        return jpanel;
    }

    private paneRole(){
        super();
        initializePanel();
        initializeButtons();
    }

    @Override
    void initializePanel() {
        jtable = new tableXml();
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        //jScrollTable1.setPreferredSize(new Dimension(650, 460));
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.CENTER);
        setVisible(true);
        proxy.setTableXmlListener(jtable);
    }

    @Override
    void initializeButtons() {
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
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        actionJP.add(jbAddRow, gbc);
        gbc.gridx = 1;
        actionJP.add(jbRemoveRow, gbc);
        gbc.gridx = 2;
        actionJP.add(jbSaveRole, gbc);
        gbc.gridx = 3;
        actionJP.add(jbAddDir, gbc);
        gbc.gridx = 4;                
        actionJP.add(jlTemp, gbc);

        add(actionJP, BorderLayout.NORTH);
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
