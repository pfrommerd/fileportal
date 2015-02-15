package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fileportal.net.User;
import fileportal.net.User.UserListener;


public class ProfileBar extends JPanel {
	private static final long serialVersionUID = 1L;

	private User m_user;
	
	private JLabel m_icon;
	private JLabel m_name;
	
	public ProfileBar(User u) {
		m_user = u;
		
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createMatteBorder(0, 0, PortalApp.DIVIDER_THICKNESS, 0, PortalApp.DIVIDER_COLOR));
		setForeground(Color.WHITE);
		setBackground(Color.WHITE);
		
		//Downscale the icon
		Image img = u.getIcon().getScaledInstance(PortalApp.PROFILE_BAR_HEIGHT, PortalApp.PROFILE_BAR_HEIGHT, Image.SCALE_SMOOTH);
		
		m_icon = new JLabel(new ImageIcon(img));
		m_icon.addMouseListener(new IconMouseListener());
		
		m_name = new JLabel(" " + u.getName());
		
		m_name.setForeground(PortalApp.FONT_COLOR);
		m_name.setFont(PortalApp.PROFILE_FONT);

		add(m_name, BorderLayout.CENTER);
		
		m_name.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					final JTextField field = new JTextField(m_user.getName());
					field.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							m_user.setName(field.getText());
							remove(field);
							add(m_name, BorderLayout.CENTER);
							revalidate();
						}
					});
					remove(m_name);
					add(field, BorderLayout.CENTER);
					revalidate();
				}
			} 
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		add(m_icon, BorderLayout.WEST);
		add(m_name, BorderLayout.CENTER);

		u.addListener(new UserListener() {
			@Override
			public void nameChanged(String name) {
				m_name.setText(" " + name);
			}

			@Override
			public void iconChanged(BufferedImage icon) {
				//Downscale the icon
				Image img = icon.getScaledInstance(PortalApp.PROFILE_BAR_HEIGHT, 
								PortalApp.PROFILE_BAR_HEIGHT, Image.SCALE_SMOOTH);
				
				m_icon = new JLabel(new ImageIcon(img));
				m_icon.addMouseListener(new IconMouseListener());
				
				add(m_icon, BorderLayout.WEST);
				revalidate();
			}
		});
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(PortalApp.PROFILE_BAR_HEIGHT,
							 PortalApp.PROFILE_BAR_HEIGHT);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	public class IconMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				//Select the next image
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file != null) {
						try {
							BufferedImage img = ImageIO.read(file);
							m_user.setIcon(img);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
}
