package org.feedworker.client.frontend;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * Pannello Itasa
 * 
 * @author luca
 */
class ItasaJP extends AbstractJP {

	private static ItasaJP jpanel = null;
	private JButton jbAllItasa, jbDown, jbClean, jbAllMyItasa, jbCopySub;
	private tableRss jtItasa, jtMyItasa;

	/** Costruttore privato */
	private ItasaJP() {
		super();
		initializePanel();
		initializeButtons();
		proxy.setTableRssListener(jtItasa);
		proxy.setTableRssListener(jtMyItasa);
	}

	/**
	 * Restituisce l'istanza del pannello itasa
	 * 
	 * @return pannello itasa
	 */
	public static ItasaJP getPanel() {
		if (jpanel == null)
			jpanel = new ItasaJP();
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

		add(jScrollTable1, BorderLayout.WEST);
		add(jScrollTable2, BorderLayout.EAST);

		setVisible(true);
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
		jbDown.setToolTipText("Scarica tutti i sub selezionati");
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

		jbCopySub = new JButton(" Copia Sub ");
		jbCopySub.setEnabled(false);
		jbCopySub
				.setToolTipText("copia il sub per la regola destinazione avanzata");
		jbCopySub.setBorder(BORDER);
		jbCopySub.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				// jbCopySubMouseClicked();
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = BUTTON_SPACE_INSETS;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		actionJP.add(jbAllItasa, gbc);
		gbc.gridx = 1;
		actionJP.add(jbDown, gbc);
		gbc.gridx = 2;
		actionJP.add(jbClean, gbc);
		gbc.gridx = 3;
		actionJP.add(jbAllMyItasa, gbc);
		gbc.gridx = 4;
		actionJP.add(jbCopySub, gbc);

		add(actionJP, BorderLayout.NORTH);
	}

	public void setButtonEnabled(boolean e) {
		jbDown.setEnabled(e);
		jbClean.setEnabled(e);
		jbAllItasa.setEnabled(e);
		jbAllMyItasa.setEnabled(e);
	}

	private void jbDownMouseClicked() {
		if (jbDown.isEnabled()) {
			proxy.downloadSub(jtItasa, jtMyItasa, true);
			jbCleanMouseClicked();
		}
	}

	private void selectAll(tableRss jt) {
		for (int i = 0; i < jt.getRowCount(); i++)
			jt.setValueAt(true, i, 3);
	}

	/*
	 * private void jbCopySubMouseClicked(){ proxy.copyTitleFeed(jtItasa,
	 * jtMyItasa); }
	 */
	private void jbCleanMouseClicked() {
		proxy.cleanSelect(jtItasa);
		proxy.cleanSelect(jtMyItasa);
	}
}