package fileportal.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fileportal.net.DiscoverHandler;
import fileportal.net.User;

public class DiscoveryPanel extends JPanel implements DiscoverHandler {
	private static final long serialVersionUID = 1L;
	
	private HashMap<User, UserPanel> m_panels = new HashMap<User, UserPanel>();
	
	public DiscoveryPanel() {
		setBorder(BorderFactory.createEmptyBorder(PortalApp.GRID_SPACING, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING));
		setLayout(new GridLayout(0, 3, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING));
		
		setForeground(Color.WHITE);
		setBackground(Color.WHITE);
		
		userDiscovered(new User("James", PortalApp.DEFAULT_USER_ICON));
		userDiscovered(new User("Komal", PortalApp.DEFAULT_USER_ICON));
		userDiscovered(new User("Samuel", PortalApp.DEFAULT_USER_ICON));
		userDiscovered(new User("Andrew", PortalApp.DEFAULT_USER_ICON));
		userDiscovered(new User("Lol", PortalApp.DEFAULT_USER_ICON));
	}
	
	public Collection<User> getUsers() {
		return m_panels.keySet();
	}
	
	@Override
	public void userDiscovered(User user) {
		UserPanel p = new UserPanel(user);
		m_panels.put(user, p);
		add(p);
		
		revalidate();
		repaint();
	}
	
	@Override
	public void userDisconnected(User user) {
		remove(m_panels.get(user));
		m_panels.remove(user);
		
		System.out.println(user);
		
		revalidate();
		repaint();
	}
}
