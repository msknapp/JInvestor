package com.KnappTech.sr.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persist.PersistenceRegister;

public class ModelSelectionPanel {
	private static final String CHANGESELECTIONACTIONCOMMAND = "change";
	private static final String VIEWACTIONCOMMAND = "view";
	private static final String[] options = {"Company","PriceHistory",
		"FinancialHistory","EconomicRecord","Investor"};
	private static ModelSelectionPanel instance = null;
	private JPanel panel = null;
	private JPanel innerPanel = null;
	private JComboBox cb = null;
	private ActionListener al = null;
	private JScrollPane scroller = null;
	private JList itemList = null;
//	private ListModel listModel = null;
	
	private ModelSelectionPanel() {
		
	}
	
	public static final JPanel getInstance() {
		if (instance==null) {
			instance = new ModelSelectionPanel();
			instance.configure();
		}
		return instance.panel;
	}
	
	/**
	 * Builds the panel.
	 */
	private void configure() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panel.setMinimumSize(new Dimension(200,140));
		panel.setPreferredSize(new Dimension(200,99999));
		panel.setMaximumSize(new Dimension(200,Integer.MAX_VALUE));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.Y_AXIS));
		innerPanel.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		innerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		innerPanel.setPreferredSize(new Dimension(99999,99999));
		innerPanel.setMinimumSize(new Dimension(190,130));
		innerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cb = new JComboBox(options);
		cb.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		cb.setActionCommand(CHANGESELECTIONACTIONCOMMAND);
		al = new ChangeSelectionActionListener();
		cb.addActionListener(al);
		cb.setMinimumSize(new Dimension(190,30));
		cb.setPreferredSize(new Dimension(190,30));
		cb.setMaximumSize(new Dimension(190,30));//		listModel = new DefaultListModel();
		itemList = new JList();
		itemList.setBackground(Color.WHITE);
		itemList.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		itemList.setMinimumSize(new Dimension(170,60));
		itemList.setMaximumSize(new Dimension(170,Integer.MAX_VALUE));
		itemList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		itemList.setLayoutOrientation(JList.VERTICAL);
		scroller = new JScrollPane(itemList);
		scroller.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		scroller.setMinimumSize(new Dimension(190,60));
		scroller.setPreferredSize(new Dimension(190,99999));
		scroller.setMaximumSize(new Dimension(190,Integer.MAX_VALUE));

		JButton viewButton = new JButton("View");
		viewButton.setActionCommand(VIEWACTIONCOMMAND);
		viewButton.addActionListener(new ViewSelectionActionListener());
		viewButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(innerPanel);
		innerPanel.add(cb);
		innerPanel.add(Box.createRigidArea(new Dimension(0,5)));
		innerPanel.add(scroller);
		innerPanel.add(Box.createRigidArea(new Dimension(0,5)));
		innerPanel.add(viewButton);
	}
	
	private class ViewSelectionActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String id = (String)itemList.getSelectedValue();
			String sel = (String)cb.getSelectedItem();
			try {
				if (sel!=null) {
					if (sel.equals("Company")) {
						Company company =PersistenceRegister.company().getIfStored(id,false);
						MainDisplay.setModelObject(company);
					} else if (sel.equals("PriceHistory")) {
						PriceHistory ph = PersistenceRegister.ph().getIfStored(id,false);
						MainDisplay.setModelObject(ph);
					} else if (sel.equals("FinancialHistory")) {
						FinancialHistory fh = PersistenceRegister.financial().getIfStored(id,false);
						MainDisplay.setModelObject(fh);
					} else if (sel.equals("EconomicRecord")) {
						EconomicRecord er = PersistenceRegister.er().getIfStored(id,false);
						MainDisplay.setModelObject(er);
					} else if (sel.equals("Investor")) {
	//					ids = InvestorManager.getAllStoredIDs();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ChangeSelectionActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String sel = (String)cb.getSelectedItem();
			if (sel!=null) {
//				options = {"Company","PriceHistory",
//						"FinancialHistory","EconomicRecord","Investor"};
				// must populate the item list.
				Set<String> ids = null;
				if (sel.equals("Company")) {
					ids = PersistenceRegister.company().getAllStoredIDs();
				} else if (sel.equals("PriceHistory")) {
					ids = PersistenceRegister.ph().getAllStoredIDs();
				} else if (sel.equals("FinancialHistory")) {
					ids = PersistenceRegister.financial().getAllStoredIDs();
				} else if (sel.equals("EconomicRecord")) {
					ids = (Set<String>) PersistenceRegister.er().getAllStoredIDs();
				} else if (sel.equals("Investor")) {
//					ids = InvestorManager.getAllStoredIDs();
				}
				if (ids!=null) {
					ArrayList<String> idsList = new ArrayList<String>(ids);
					Collections.sort(idsList);
					String[] idsa = idsList.toArray(new String[idsList.size()]);
					itemList.setListData(idsa);
					MainDisplay.refreshView();
				}
			}
		}
	}
}