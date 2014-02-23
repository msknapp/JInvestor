package com.KnappTech.sr.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.KnappTech.model.KTObject;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.user.Investor;

public class MainDisplay {
	private static final MainDisplay instance = new MainDisplay();
	
	private JFrame mainFrame = null;
	private JPanel mainPanel = null;
	private JPanel bodyPanel = null;
	private JPanel leftPanel = null;
	private JPanel bottomLeftPanel = null;
	private JPanel controlPanel = null;
	private ActionListener controlListener = null;
	
	private static final String SEEMODELACTIONCOMMAND = "seemodel";
	private static final String UPDATEPRICEHISTORIESCOMMAND = "updatePH";
	private static final String UPDATEECONOMICRECORDSCOMMAND = "updateER";
	private static final String UPDATEFINANCIALHISTORIESCOMMAND = "updateFH";
	private static final String RUNREGRESSIONSCOMMAND = "regress";
	private static final String VIEWPORTFOLIOREPORTCOMMAND = "viewPortfolio";
	private static final String VIEWRECOMMENDEDLONGSSCOMMAND = "viewLongs";
	private static final String VIEWRECOMMENDEDSHORTSSCOMMAND = "viewShorts";
	
	private MainDisplay() {
		// does nothing. don't want to create a bunch 
		// of variables unless I know they will be used.
	}
	
	/**
	 * Creates the display and presents it to the user.
	 */
	public static void start() {
		instance.iStart();
	}
	
	private void iStart() {
		mainFrame = new JFrame("Stockradamus");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		leftPanel.setMaximumSize(new Dimension(300,Integer.MAX_VALUE));
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		createControlPanel();
		mainFrame.setContentPane(mainPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.add(leftPanel);
		leftPanel.add(controlPanel);
		leftPanel.add(Box.createRigidArea(new Dimension(0,5)));
		mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
		mainFrame.pack();
		mainFrame.setMinimumSize(new Dimension(400,400));
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.setVisible(true);
	}
	
	private JPanel createControlPanel() {
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		controlPanel.setAlignmentX(Panel.LEFT_ALIGNMENT);
		controlPanel.setAlignmentY(Panel.TOP_ALIGNMENT);
		controlPanel.setMaximumSize(new Dimension(200,300));
		controlPanel.setMinimumSize(new Dimension(200,300));
		controlPanel.setPreferredSize(new Dimension(200,300));
//		controlPanel.add(Box.createRigidArea(new Dimension(0,5)));
		controlListener = new ControlListener();
		addControlButton("See Model Data", SEEMODELACTIONCOMMAND);
		addControlButton("Update Price Histories", UPDATEPRICEHISTORIESCOMMAND);
		addControlButton("Update Econ. Records", UPDATEECONOMICRECORDSCOMMAND);
		addControlButton("Update Fin. Histories", UPDATEFINANCIALHISTORIESCOMMAND);
		addControlButton("Run Regressions", RUNREGRESSIONSCOMMAND);
		addControlButton("View Portfolio Report", VIEWPORTFOLIOREPORTCOMMAND);
		addControlButton("View Rec. Longs", VIEWRECOMMENDEDLONGSSCOMMAND);
		addControlButton("View Rec. Shorts", VIEWRECOMMENDEDSHORTSSCOMMAND);
		
		return controlPanel;
	}
	
	private void addControlButton(String name,String actionCommand) {
		JButton button = new JButton(name);
		button.setAlignmentX(Frame.CENTER_ALIGNMENT);
		button.setPreferredSize(new Dimension(185,30));
		button.setMaximumSize(new Dimension(185,30));
		button.setMinimumSize(new Dimension(185,30));
		button.setActionCommand(actionCommand);
		button.addActionListener(controlListener);
		controlPanel.add(button);
		controlPanel.add(Box.createRigidArea(new Dimension(0,5)));
	}
	
	private class ControlListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (bottomLeftPanel!=null) {
				try {
					leftPanel.remove(bottomLeftPanel);
				} catch (Exception ex) {}
			}
			if (action.equals(SEEMODELACTIONCOMMAND)) {
				bottomLeftPanel=ModelSelectionPanel.getInstance();
				leftPanel.add(bottomLeftPanel);
			} else if (action.equals(UPDATEPRICEHISTORIESCOMMAND)) {
				
			} else if (action.equals(UPDATEECONOMICRECORDSCOMMAND)) {
				
			} else if (action.equals(UPDATEFINANCIALHISTORIESCOMMAND)) {
				
			} else if (action.equals(RUNREGRESSIONSCOMMAND)) {
				
			} else if (action.equals(VIEWPORTFOLIOREPORTCOMMAND)) {
				
			} else if (action.equals(VIEWRECOMMENDEDLONGSSCOMMAND)) {
				
			} else if (action.equals(VIEWRECOMMENDEDSHORTSSCOMMAND)) {
				
			}
			refresh();
		}
	}
	
	public static void refreshView() {
		instance.refresh();
	}
	
	private void refresh() {
		Runnable r = new Runnable() {
			public void run() {
				mainFrame.validate();
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
//	private JPanel createModelViewPanel() {
//		JPanel lcp = new JPanel();
//		
//		
//		return lcp;
//	}

	public static void setModelObject(KTObject object) {
		if (object instanceof Company) {
			JPanel p = CompanyBodyPanel.getInstance((Company)object);
			instance.setBodyPanel(p);
		} else if (object instanceof PriceHistory) {
			JPanel p = RecordListBodyPanel.getInstance((PriceHistory)object);
			instance.setBodyPanel(p);
		} else if (object instanceof FinancialHistory) {
			
		} else if (object instanceof EconomicRecord) {
			JPanel p = RecordListBodyPanel.getInstance((EconomicRecord)object);
			instance.setBodyPanel(p);
		} else if (object instanceof Investor) {
			
		}
		refreshView();
	}
	
	private void setBodyPanel(JPanel panel) {
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		panel.setPreferredSize(new Dimension(99999,99999));
//		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		if (bodyPanel!=null) {
			mainPanel.remove(bodyPanel);
		}
		bodyPanel = panel;
		mainPanel.add(bodyPanel);
	}
}