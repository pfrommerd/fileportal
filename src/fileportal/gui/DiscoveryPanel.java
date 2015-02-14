package fileportal.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fileportal.net.DiscoverHandler;
import fileportal.net.User;

public class DiscoveryPanel extends JPanel implements DiscoverHandler {
	private static final long serialVersionUID = 1L;
	
	private HashMap<User, UserPanel> m_panels = new HashMap<User, UserPanel>();
	
	public DiscoveryPanel() {
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(PortalApp.GRID_SPACING, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING));
		setLayout(new GridLayout(0, 3, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING));	
		
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
		userDiscovered(new DummyUser("Foo Bar"));
	}
	
	@Override
	public void userDiscovered(User user) {
		UserPanel p = new UserPanel(user);
		m_panels.put(user, p);
		add(p);
	}
	
	@Override
	public void userDisconnected(User user) {
		remove(m_panels.get(user));
		m_panels.remove(user);
	}
}
