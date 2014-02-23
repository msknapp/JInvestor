package com.KnappTech.sr.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUIFactory {
	
	public static JPanel createSimplePropertiesPanel(Map<String,String> props) {
		JPanel panel = new JPanel();
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(leftPanel);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		panel.add(rightPanel);
		for (String key : props.keySet()) {
			JLabel keyLabel = new JLabel(key);
			String value = props.get(key);
			JLabel valueLabel = new JLabel(value);
			int keyw = Math.min(200, key.length()*8+5);
			keyLabel.setPreferredSize(new Dimension(keyw,30));
			int valw = Math.min(200, key.length()*8+5);
			valueLabel.setPreferredSize(new Dimension(valw,30));
			leftPanel.add(keyLabel);
			rightPanel.add(valueLabel);
			leftPanel.add(Box.createRigidArea(new Dimension(0,5)));
			rightPanel.add(Box.createRigidArea(new Dimension(0,5)));
			leftPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		}
		return panel;
	}
}
