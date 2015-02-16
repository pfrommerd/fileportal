package fileportal.gui;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fileportal.net.DiscoverHandler;
import fileportal.net.User;

public class DiscoveryPanel extends JPanel implements DiscoverHandler {
	private static final long serialVersionUID = 1L;

	private PortalApp m_app;
	private HashMap<User, UserPanel> m_panels = new HashMap<User, UserPanel>();

	public DiscoveryPanel(PortalApp app) {
		m_app = app;
		setBorder(BorderFactory.createEmptyBorder(PortalApp.GRID_SPACING, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING,
				PortalApp.GRID_SPACING));
		setLayout(new GridLayout(0, 3, PortalApp.GRID_SPACING, PortalApp.GRID_SPACING));

		setForeground(PortalApp.BACKGROUND_COLOR);
		setBackground(PortalApp.BACKGROUND_COLOR);
	}

	public Collection<User> getUsers() {
		return m_panels.keySet();
	}

	@Override
	public void userDiscovered(User user) {
		UserPanel p = new UserPanel(m_app, user);
		m_panels.put(user, p);
		add(p);

		revalidate();
		repaint();
	}

	@Override
	public void userDisconnected(User user) {
		UserPanel panel = m_panels.get(user);
		if (panel != null) {
			panel.fadeOut();
			remove(panel);
		}
		m_panels.remove(user);

		revalidate();
		repaint();
	}
}
