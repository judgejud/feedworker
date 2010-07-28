package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
/**
 *
 * @author luca
 */
class SubsfactoryJP extends AbstractJP{
    private static SubsfactoryJP jpanel = null;
    private JButton jbDown, jbClean;
    private tableRss jtSubsf, jtMySubsf;

    private SubsfactoryJP(){
        super();
        initializePanel();
        initializeButtons();
        proxy.setTableRssListener(jtSubsf);
        proxy.setTableRssListener(jtMySubsf);
    }

    public static SubsfactoryJP getPanel(){
        if (jpanel==null)
            jpanel = new SubsfactoryJP();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtSubsf = new tableRss(proxy.getSubsf());
        jtMySubsf = new tableRss(proxy.getMySubsf());
        JScrollPane jScrollTable1 = new JScrollPane(jtSubsf);
        jScrollTable1.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.WEST);
        JScrollPane jScrollTable2 = new JScrollPane(jtMySubsf);
        jScrollTable2.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable2.setAutoscrolls(true);
        add(jScrollTable2, BorderLayout.EAST);
        setVisible(true);
    }

    @Override
    void initializeButtons() {
        jbDown = new JButton(" Download ");
        jbDown.setToolTipText("Scarica i sub selezionati");
        jbDown.setBorder(BORDER);
        jbDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbDownMouseClicked();
            }
        });

        jbClean = new JButton(" Pulisci ");
        jbClean.setToolTipText("Pulisce le righe selezionate");
        jbClean.setBorder(BORDER);        
        jbClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                proxy.cleanSelect(jtSubsf);
                proxy.cleanSelect(jtMySubsf);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = BUTTON_SPACE_INSETS;
        actionJP.add(jbDown, gbc);
        gbc.gridx = 1;
        actionJP.add(jbClean, gbc);
        gbc.gridwidth=3;
        gbc.gridx=2;
        JLabel jlTemp = new JLabel();
        jlTemp.setPreferredSize(new Dimension(800, 20));
        actionJP.add(jlTemp, gbc);
        add(actionJP, BorderLayout.NORTH);
    }

    private void jbDownMouseClicked(){
        if (jbDown.isEnabled()){
            proxy.downloadSub(jtSubsf, jtMySubsf, false);
            proxy.cleanSelect(jtSubsf);
        }
    }

    public void setEnableButton(boolean e){
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
    }
}
