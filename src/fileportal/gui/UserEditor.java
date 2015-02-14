package fileportal.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fileportal.net.User;

public class UserEditor extends JFrame {
	private BufferedImage m_selected = null;
		
	public UserEditor(final ProfileBar bar, final User u) {
		JPanel labelPanel = new JPanel(new GridLayout(3, 1));
	    JPanel inputPanel = new JPanel(new GridLayout(3, 1));
	    
	    getContentPane().add(labelPanel, BorderLayout.WEST);
	    getContentPane().add(inputPanel, BorderLayout.EAST);
		
		labelPanel.add(new JLabel("   User Icon"));
		labelPanel.add(new JLabel("   Username "));
		
		JButton selectButton = new JButton("Select...");
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file != null) {
						try {
							m_selected = ImageIO.read(file);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		
		final JTextField userName = new JTextField(u.getName());
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				u.setName(userName.getText());
				if (m_selected != null) u.setIcon(m_selected);
				UserEditor.this.dispose();
				bar.repaint();
			}
		});
		
		inputPanel.add(selectButton);
		inputPanel.add(userName);
		inputPanel.add(okButton);
		
		pack();
		setSize(getWidth() + 50, getHeight());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, 
						 dim.height / 2 - this.getSize().height / 2);
		setVisible(true);
	}
}
