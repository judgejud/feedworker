package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.table.tableCalendar;
/**
 *
 * @author luca
 */
public class paneCalendarShow extends paneAbstract{

    private static paneCalendarShow jpanel = null;
    private tableCalendar jtable;
    private JTabbedPane tabpane;

    private paneCalendarShow(){
        super("");
        setName(proxy.getNameTableCalendarShow());
        initializePanel();
        initializeButtons();
    }

    public static paneCalendarShow getPanel(){
        if (jpanel==null)
            jpanel = new paneCalendarShow();
        return jpanel;
    }

    @Override
    void initializePanel() {
        tabpane = new JTabbedPane();
        jtable = new tableCalendar(getName());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        tabpane.addTab(getName(), jScrollTable1);
        jpCenter.add(tabpane);
        core.setTableListener(jtable);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(core.getIconAdd());
        jbAddRow.setToolTipText("Aggiungi riga/serie alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                core.addNewSerial();
            }
        });

        JButton jbRemoveRow = new JButton(core.getIconRemove());
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });
        
        JButton jbRemoveAll = new JButton(core.getIconRemoveAll());
        jbRemoveAll.setToolTipText("Rimuove tutte le serie dalla tabella");
        jbRemoveAll.setBorder(BORDER);
        jbRemoveAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveAllRowsMouseClick();
            }
        });

        JButton jbRefresh = new JButton(core.getIconRefresh1());
        jbRefresh.setToolTipText("Aggiorna le informazioni sulle serie che hanno data "
                + "prossima puntata bianca o minore odierna");
        jbRefresh.setBorder(BORDER);
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshCalendarMouseClick();
            }
        });
        
        JButton jbSingleRefresh = new JButton(core.getIconRefresh2());
        jbSingleRefresh.setToolTipText("Aggiorna le informazioni sulla serie selezionata");
        jbSingleRefresh.setBorder(BORDER);
        jbSingleRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshSingleMouseClick();
            }
        });

        JButton jbImport = new JButton(core.getIconImport1());
        jbImport.setToolTipText("Importa dai nomi serie di subtitle destination");
        jbImport.setBorder(BORDER);
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbImportCalendarMouseClick();
            }
        });
        
        JButton jbImportMyItasa = new JButton(core.getIconImport2());
        jbImportMyItasa.setToolTipText("Importa dai nomi serie di myItasa");
        jbImportMyItasa.setBorder(BORDER);
        jbImportMyItasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbImportMyItasaCalendarMouseClick();
            }
        });

        int x=0;
        jpAction.add(jbAddRow, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbRemoveRow, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbRemoveAll, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbRefresh, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbSingleRefresh, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbImport, gbcAction);
        gbcAction.gridx = ++x;
        jpAction.add(jbImportMyItasa, gbcAction);
    }

    private void jbRemoveRowMouseClicked() {
        int row = jtable.getSelectedRow();
        if (row > -1){
            row = jtable.convertRowIndexToModel(row);
            proxy.removeSingleShowCalendar(row, jtable.getValueAt(row, 0));
            ((DefaultTableModel) jtable.getModel()).removeRow(row);
        }
    }
    
    private void jbRemoveAllRowsMouseClick(){
        if (core.requestRemoveSeries(this, true)==0){
            proxy.removeAllShowCalendar();
            jtable.removeAllRows();
        }
    }
    
    private void jbRefreshCalendarMouseClick() {
        if (jtable.getRowCount()>0){
            jtable.removeAllRows();
            proxy.refreshCalendar();
        }
    }

    private void jbRefreshSingleMouseClick(){
        int row = jtable.getSelectedRow();
        if (row > -1){
            Object value = jtable.getValueAt(row, 0);
            jtable.removeAllRows();
            proxy.refreshSingleCalendar(value);
        }
    }
    
    private void jbImportCalendarMouseClick() {
        if (jtable.getRowCount()==0)
            proxy.importFromSubDest();
    }
    
    private void jbImportMyItasaCalendarMouseClick() {
        if (jtable.getRowCount()==0)
            proxy.calendarImportTVFromMyItasa();
    }
}