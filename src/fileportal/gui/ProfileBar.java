package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fileportal.net.User;

public class ProfileBar extends JPanel {
	private static final long serialVersionUID = 1L;

	private User m_user;
	
	public ProfileBar(User u) {
		m_user = u;
		
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createMatteBorder(0, 0, PortalApp.DIVIDER_THICKNESS, 0, PortalApp.DIVIDER_COLOR));
		setForeground(Color.WHITE);
		setBackground(Color.WHITE);
		
		add(new GearPanel(), BorderLayout.EAST);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		
		BufferedImage img = m_user.getIcon();
		if (img == null) img = PortalApp.DEFAULT_USER_ICON;
		
		g2d.drawImage(img, 0, 0, getHeight(), getHeight(), null);
		
		g2d.setColor(PortalApp.FONT_COLOR);
		g2d.setFont(PortalApp.FONT);
		g2d.drawString(m_user.getName(), getHeight() + PortalApp.PROFILE_NAME_X_OFF, getHeight() - PortalApp.PROFILE_NAME_Y_OFF);
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
	
	public class GearPanel extends JPanel implements MouseListener {
		public GearPanel() {
			setForeground(Color.WHITE);
			setBackground(Color.WHITE);
			
			addMouseListener(this);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(PortalApp.PROFILE_SETTINGS_ICON, 0, 0, 
									getWidth(), getHeight(), null);
			
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

		@Override
		public void mouseClicked(MouseEvent e) {
			new UserEditor(ProfileBar.this, m_user);
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
