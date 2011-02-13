package org.feedworker.client.frontend.panel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.table.tableRss;
/**
 * 
 * @author luca
 */
public class paneSubsfactory extends paneAbstract {

    private static paneSubsfactory jpanel = null;
    private JButton jbDown, jbClean;
    private tableRss jtSubsf, jtMySubsf;

    private paneSubsfactory() {
        super();
        setName("Subsfactory");
        initializePanel();
        initializeButtons();
        proxy.setTableListener(jtSubsf);
        proxy.setTableListener(jtMySubsf);
    }

    public static paneSubsfactory getPanel() {
        if (jpanel == null) {
            jpanel = new paneSubsfactory();
        }
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtSubsf = new tableRss(proxy.getSubsf());
        jtMySubsf = new tableRss(proxy.getMySubsf());

        JScrollPane jScrollTable1 = new JScrollPane(jtSubsf);
        jScrollTable1.setMinimumSize(TABLE_SCROLL_SIZE);
        jScrollTable1.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable1.setAutoscrolls(true);
        
        JScrollPane jScrollTable2 = new JScrollPane(jtMySubsf);
        jScrollTable2.setMinimumSize(TABLE_SCROLL_SIZE);
        jScrollTable2.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable2.setAutoscrolls(true);
        
        jpCenter.add(jScrollTable1);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jScrollTable2);
        add(jpCenter, BorderLayout.CENTER);

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
        jpAction.add(jbDown, gbc);
        gbc.gridx = 1;
        jpAction.add(jbClean, gbc);
        add(jpAction, BorderLayout.NORTH);
    }

    private void jbDownMouseClicked() {
        if (jbDown.isEnabled()) {
            proxy.downloadSub(jtSubsf, jtMySubsf, false);
            proxy.cleanSelect(jtSubsf);
            proxy.cleanSelect(jtMySubsf);
        }
    }

    public void setEnableButton(boolean e) {
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
    }
}