package fileportal.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.notification.ComponentNotification;

public class AcceptNotification extends ComponentNotification {
	public AcceptNotification(String text) {
		JPanel customPanel = new JPanel();
		JButton accept = new JButton("Accept");
		JButton decline = new JButton("Decline");
		
		customPanel.add(accept);
		customPanel.add(decline);
		
		addComponent(new JLabel(text), BorderLayout.NORTH);
		addComponent(customPanel, BorderLayout.CENTER);
		setSize(300, 70);
	}
}
