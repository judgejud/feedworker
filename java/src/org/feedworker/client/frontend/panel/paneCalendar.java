package org.feedworker.client.frontend.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.feedworker.client.frontend.table.tableCalendar;
import org.jfacility.java.lang.Lang;
/**
 *
 * @author luca
 */
public class paneCalendar extends paneAbstract{

    private static paneCalendar jpanel = null;
    private tableCalendar jtable;

    private paneCalendar(){
        super("Calendar");
        initializePanel();
        initializeButtons();
    }

    public static paneCalendar getPanel(){
        if (jpanel==null)
            jpanel = new paneCalendar();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtable = new tableCalendar(proxy.getNameTableCalendar());
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        jpCenter.add(jScrollTable1);
        proxy.setTableListener(jtable);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(" + ");
        jbAddRow.setToolTipText("Aggiungi riga/serie alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" - ");
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });
        
        JButton jbRemoveAll = new JButton(" Remove All ");
        jbRemoveAll.setToolTipText("Rimuove tutte le serie dalla tabella");
        jbRemoveAll.setBorder(BORDER);
        jbRemoveAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveAllRowsMouseClick();
            }
        });

        JButton jbRefresh = new JButton(" Aggiorna ");
        jbRefresh.setToolTipText("Aggiorna le informazioni sulle serie che hanno data "
                + "prossima puntata bianca o minore odierna");
        jbRefresh.setBorder(BORDER);
        jbRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshCalendarMouseClick();
            }
        });
        
        JButton jbSingleRefresh = new JButton(" Aggiorna Singolo ");
        jbSingleRefresh.setToolTipText("Aggiorna le informazioni sulla serie selezionata");
        jbSingleRefresh.setBorder(BORDER);
        jbSingleRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRefreshSingleMouseClick();
            }
        });

        JButton jbImport = new JButton(" Importa ");
        jbImport.setToolTipText("Importa dai nomi serie di subtitle destination");
        jbImport.setBorder(BORDER);
        jbImport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbImportCalendarMouseClick();
            }
        });
        
        JButton jbImportMyItasa = new JButton(" Importa da myItasa");
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

    private void jbAddRowMouseClicked() {
        String tv = JOptionPane.showInputDialog(null,"Inserire nome serie tv");
        if (Lang.verifyTextNotNull(tv))
            proxy.searchTV(tv);
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
        proxy.removeAllShowCalendar();
        jtable.removeAllRows();
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
            row = jtable.convertRowIndexToModel(row);
            String value = (String) jtable.getValueAt(row, 0);
            jtable.removeAllRows();
            proxy.refreshSingleCalendar(value,row);
        }
    }
    
    private void jbImportCalendarMouseClick() {
        if (jtable.getRowCount()==0)
            proxy.importFromSubDest();
    }
    
    private void jbImportMyItasaCalendarMouseClick() {
        if (jtable.getRowCount()==0)
            proxy.importFromMyItasa();
    }
}