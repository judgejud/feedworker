package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.feedworker.client.frontend.table.tableRss;
/**
 * Pannello Itasa
 * 
 * @author luca
 */
public class paneItasa extends paneAbstract {

    private static paneItasa jpanel = null;
    private JButton jbAllItasa, jbDown, jbClean, jbAllMyItasa;
    private tableRss jtItasa, jtMyItasa;

    /** Costruttore privato */
    private paneItasa() {
        super("Italiansubs");
        initializePanel();
        initializeButtons();
        core.setTableListener(jtItasa);
        core.setTableListener(jtMyItasa);
    }

    /**
     * Restituisce l'istanza del pannello itasa
     *
     * @return pannello itasa
     */
    public static paneItasa getPanel() {
        if (jpanel == null)
            jpanel = new paneItasa();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtItasa = new tableRss(proxy.getItasa());
        jtItasa.setTitleDescriptionColumn("Sottotitolo Itasa");
        JScrollPane jScrollTable1 = new JScrollPane(jtItasa);
        jScrollTable1.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable1.setAutoscrolls(true);

        jtMyItasa = new tableRss(proxy.getMyItasa());
        jtMyItasa.setTitleDescriptionColumn("Sottotitolo MyItasa");
        JScrollPane jScrollTable2 = new JScrollPane(jtMyItasa);
        jScrollTable2.setPreferredSize(TABLE_SCROLL_SIZE);
        jScrollTable2.setAutoscrolls(true);

        jpCenter.add(jScrollTable1);
        jpCenter.add(RIGID_AREA);
        jpCenter.add(jScrollTable2);
    }

    @Override
    void initializeButtons() {
        jbAllItasa = new JButton(" Tutti Itasa ");
        jbAllItasa.setToolTipText("Seleziona tutti i sub itasa");
        jbAllItasa.setBorder(BORDER);
        jbAllItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                selectAll(jtItasa);
            }
        });

        jbDown = new JButton(" Download ");
        //jbDown = new JButton(Common.getResourceImageIcon(proxy.getPNG_DOWNLOAD()));
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
                jbCleanMouseClicked();
            }
        });

        jbAllMyItasa = new JButton(" Tutti MyItasa ");
        jbAllMyItasa.setToolTipText("Seleziona tutti i sub myItasa");
        jbAllMyItasa.setBorder(BORDER);
        jbAllMyItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                selectAll(jtMyItasa);
            }
        });
        
        int x = 0;
        jpAction.add(jbAllItasa, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbDown, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbClean, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbAllMyItasa, gbcAction);
    }

    public void setButtonEnabled(boolean e) {
        jbDown.setEnabled(e);
        jbClean.setEnabled(e);
        jbAllItasa.setEnabled(e);
        jbAllMyItasa.setEnabled(e);
    }

    private void jbDownMouseClicked() {
        if (jbDown.isEnabled()) {
            core.downloadSub(jtItasa, jtMyItasa, true, false);
            jbCleanMouseClicked();
        }
    }

    private void selectAll(tableRss jt) {
        for (int i = 0; i < jt.getRowCount(); i++)
            jt.setValueAt(true, i, 3);
    }

    private void jbCleanMouseClicked() {
        core.cleanSelect(jtItasa,3);
        core.cleanSelect(jtMyItasa,3);
    }
}