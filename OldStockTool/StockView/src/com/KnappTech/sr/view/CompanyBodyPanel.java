package com.KnappTech.sr.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.RegressionResults;
import com.KnappTech.sr.persist.PersistenceRegister;

public class CompanyBodyPanel {
	private static final String SEEPRICEHISTORYCOMMAND = "ph";
	private static final String SEEFINANCIALHISTORYCOMMAND = "fh";
	private static final String REGRESSCOMMAND = "r";
	private static CompanyBodyPanel instance = null;
	private JPanel panel = null;
	private JPanel ipanel = null;
	private JPanel controlPanel = null;
	private JPanel metaPanel = null;
	private JPanel innerMetaPanel = null;
	private JPanel regressionPanel = null;
	private JPanel innerRegressionPanel = null;
	private Company company = null;
	private JPanel innerControlPanel = null;
	private ActionListener controlListener = null;
	
	private CompanyBodyPanel() {
		
	}
	
	public static final JPanel getInstance(Company company) {
		instance = new CompanyBodyPanel();
		instance.company = company;
		instance.build();
		return instance.panel;
	}
	
	private void build() {
		panel = new JPanel();
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
//		ipanel = new JPanel();
//		ipanel.setAlignmentY(Component.TOP_ALIGNMENT);
//		ipanel.setLayout(new BoxLayout(ipanel,BoxLayout.Y_AXIS));
//		ipanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//		panel.add(ipanel);
		JPanel topPanel = new JPanel();
		buildControlPanel();
		buildMetaPanel();
		buildRegressionPanel();
		topPanel.add(controlPanel);
		topPanel.add(Box.createRigidArea(new Dimension(5,0)));
		topPanel.add(metaPanel);
		topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.X_AXIS));
		topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,300));
		panel.add(topPanel);
		panel.add(Box.createRigidArea(new Dimension(0,5)));
		panel.add(regressionPanel);
	}

	private void buildRegressionPanel() {
		regressionPanel = new JPanel();
		regressionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		innerRegressionPanel = new JPanel();
		innerRegressionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		regressionPanel.add(innerRegressionPanel);
		regressionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		innerRegressionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		innerRegressionPanel.setLayout(new BoxLayout(innerRegressionPanel,BoxLayout.Y_AXIS));
		JLabel titleLabel = new JLabel("Regression Results:");
		innerRegressionPanel.add(titleLabel);
		innerRegressionPanel.add(Box.createRigidArea(new Dimension(0,5)));
		innerRegressionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		RegressionResults rr = company.getRegressionResultsSet();
		if (rr!=null) {
//			String[] columnNames = rr.getEntryColumnNames();
//			String[][] values = rr.getEntries();
//			JTable t = new JTable(values,columnNames);
//			innerRegressionPanel.add(t);
		}
	}

	private void buildMetaPanel() {
		metaPanel = new JPanel();
		metaPanel.setLayout(new BoxLayout(metaPanel, BoxLayout.Y_AXIS));
		metaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,300));
		metaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		metaPanel.setAlignmentX(Component.TOP_ALIGNMENT);
		innerMetaPanel = new JPanel();
		innerMetaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		innerMetaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,300));
		innerMetaPanel.setAlignmentX(Component.TOP_ALIGNMENT);
		innerMetaPanel.setLayout(new BoxLayout(innerMetaPanel, BoxLayout.Y_AXIS));
		metaPanel.add(innerMetaPanel);
		addMetaData("Ticker:",company.getID());
		addMetaData("Industry:",company.getIndustryName());
		addMetaData("Sector:", company.getSectorName());
		Double lkp = null;
		try {
			lkp = company.getLastKnownPrice();
		} catch (Exception e) {}
		if (lkp!=null) {
			addMetaData("Last Price:",String.valueOf(lkp));
		}
	}
	
	private void addMetaData(String name,String value) {
		JLabel nameLabel = new JLabel(name);
		JLabel valueLabel = new JLabel(value);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(nameLabel);
		p.add(Box.createRigidArea(new Dimension(5,0)));
		p.add(valueLabel);
		p.setAlignmentX(Component.LEFT_ALIGNMENT);
		innerMetaPanel.add(p);
		innerMetaPanel.add(Box.createRigidArea(new Dimension(0,5)));
	}

	private void buildControlPanel() {
		controlPanel = new JPanel();
		controlListener = new ControlClickListener();
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
		controlPanel.setMinimumSize(new Dimension(300,300));
		controlPanel.setMaximumSize(new Dimension(300,300));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		innerControlPanel = new JPanel();
		innerControlPanel.setLayout(new BoxLayout(innerControlPanel,BoxLayout.Y_AXIS));
		innerControlPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		innerControlPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		addControlButton("See Price History", SEEPRICEHISTORYCOMMAND);
		addControlButton("See Financial History", SEEFINANCIALHISTORYCOMMAND);
		addControlButton("Regress", REGRESSCOMMAND);
		controlPanel.add(innerControlPanel);
	}
	
	private void addControlButton(String name,String actionCommand) {
		JButton b = new JButton(name);
//		b.setAlignmentX(Component.LEFT_ALIGNMENT);
		b.setMinimumSize(new Dimension(290,30));
		b.setMaximumSize(new Dimension(290,30));
		b.setActionCommand(actionCommand);
		b.addActionListener(controlListener);
		innerControlPanel.add(b);
		innerControlPanel.add(Box.createRigidArea(new Dimension(0,5)));
	}
	
	private class ControlClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String actionCommand = arg0.getActionCommand();
			if (actionCommand.equals(SEEPRICEHISTORYCOMMAND)) {
				PriceHistory ph = loadPriceHistory(company);
				MainDisplay.setModelObject(ph);
			} else if (actionCommand.equals(SEEFINANCIALHISTORYCOMMAND)) {
				FinancialHistory fh = loadFinancialHistory(company);
				MainDisplay.setModelObject(fh);
			} else if (actionCommand.equals(REGRESSCOMMAND)) {
//				FinancialHistory fh = company.loadFinancialHistory();
//				MainDisplay.setModelObject(fh);
			}
		}
		
		public PriceHistory loadPriceHistory(Company company) {
			if (!company.isLocked() && !company.isPriceHistorySet()) 
				try {
					company.setPriceHistory(PersistenceRegister.ph().getIfStored(company.getID(),false));
				} catch (InterruptedException e) {
					
				}
			return company.getPriceHistory();
		}
		
		public FinancialHistory loadFinancialHistory(Company company) {
			if (!company.isLocked() && !company.isFinancialHistorySet()) 
				try {
					company.setFinancialHistory(PersistenceRegister.financial().getIfStored(company.getID(),false));
				} catch (InterruptedException e) {
					
				}
			return company.getFinancialHistory();
		}
	}
	
}