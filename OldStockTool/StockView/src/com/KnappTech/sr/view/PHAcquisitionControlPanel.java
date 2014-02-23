package com.KnappTech.sr.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class PHAcquisitionControlPanel {
	private static PHAcquisitionControlPanel instance = null;
	private JPanel panel = null;
	
	private PHAcquisitionControlPanel() { }
	
	public static final JPanel getInstance() {
		if (instance==null) {
			instance = new PHAcquisitionControlPanel();
			instance.configure();
		}
		return instance.panel;
	}
	
	private void configure() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setMinimumSize(new Dimension(200,140));
		panel.setPreferredSize(new Dimension(200,99999));
		panel.setMaximumSize(new Dimension(200,99999));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		
	}
}
