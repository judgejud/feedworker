package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 *
 * @author luca
 */
class jpCalendar extends AbstractJP{

    private static jpCalendar jpanel = null;
    private jtCalendar jtable;

    private jpCalendar(){
        super();
        initializePanel();
        initializeButtons();
    }

    public static jpCalendar getPanel(){
        if (jpanel==null)
            jpanel = new jpCalendar();
        return jpanel;
    }

    @Override
    void initializePanel() {
        jtable = new jtCalendar();
        JScrollPane jScrollTable1 = new JScrollPane(jtable);
        jScrollTable1.setAutoscrolls(true);
        add(jScrollTable1, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    void initializeButtons() {
        JButton jbAddRow = new JButton(" Aggiungi Serie ");
        jbAddRow.setToolTipText("Aggiungi riga/serie alla tabella");
        jbAddRow.setBorder(BORDER);
        jbAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbAddRowMouseClicked();
            }
        });

        JButton jbRemoveRow = new JButton(" Rimuove Serie ");
        jbRemoveRow.setToolTipText("Rimuovi riga/serie selezionata dalla tabella");
        jbRemoveRow.setBorder(BORDER);
        jbRemoveRow.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                jbRemoveRowMouseClicked();
            }
        });

        JButton jbSaveCalendar = new JButton(" Salva Serie ");
        jbSaveCalendar.setToolTipText("Salva Serie della tabella");
        jbSaveCalendar.setBorder(BORDER);
        jbSaveCalendar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                jbSaveCalendarMouseClicked();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = BUTTON_SPACE_INSETS;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        actionJP.add(jbAddRow, gbc);
        gbc.gridx = 1;
        actionJP.add(jbRemoveRow, gbc);
        gbc.gridx = 2;
        actionJP.add(jbSaveCalendar, gbc);

        add(actionJP, BorderLayout.NORTH);
    }

    private void jbAddRowMouseClicked() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void jbRemoveRowMouseClicked() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void jbSaveCalendarMouseClicked() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}