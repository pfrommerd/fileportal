package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fileportal.net.Discoverer;
import fileportal.net.FileReceiverServer;
import fileportal.net.User;
import fileportal.net.UserUtils;
import fileportal.net.lan.LanBroadcaster;
import fileportal.net.lan.LanDiscoverer;
import fileportal.net.lan.LanIconServer;

public class PortalApp extends JFrame {
	private static final long serialVersionUID = 1L;



	private User m_user;
	private DiscoveryPanel m_discoveryPanel;

	public PortalApp(User user) {
		m_user = user;

		setLocation(PortalConstants.SCREEN_WIDTH - PortalConstants.PANEL_WIDTH - PortalConstants.TAB_WIDTH,
				PortalConstants.Y_OFFSET);
		setSize(PortalConstants.PANEL_WIDTH + PortalConstants.TAB_WIDTH, PortalConstants.PANEL_HEIGHT);

		setUndecorated(true);
		setAlwaysOnTop(true);

		JPanel main = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				RenderUtils.s_setupFineRender(g);
				super.paintComponent(g);
			}
		};

		main.setLayout(new BorderLayout());

		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BorderLayout());

		m_discoveryPanel = new DiscoveryPanel(this);

		JScrollPane scroll = new JScrollPane();
		scroll.getVerticalScrollBar().setUnitIncrement(PortalConstants.SCROLL_SPEED);
		scroll.setBorder(null);

		scroll.getViewport().add(m_discoveryPanel);

		subPanel.add(scroll, BorderLayout.CENTER);
		subPanel.add(new ProfileBar(m_user), BorderLayout.NORTH);

		main.add(new PulloutTabPanel(this), BorderLayout.WEST);
		main.add(subPanel, BorderLayout.CENTER);

		add(main);
		setVisible(true);
	}

	public User getUser() {
		return m_user;
	}

	public boolean isPanelShowing() {
		return this.getWidth() > 100;
	}

	public void addDiscoverer(Discoverer d) {
		d.addHandler(m_discoveryPanel);
	}

	public void removeDiscoverer(Discoverer d) {
		d.removeHandler(m_discoveryPanel);
	}

	public void hidePanel() {
		//The width of the tab + the height of the image(Profile bar height) + 1/2 height the bar(exit button width)
		this.setSize(PortalConstants.TAB_WIDTH + (int) (1.5 * PortalConstants.PROFILE_BAR_HEIGHT), 30);
		this.setLocation(PortalConstants.SCREEN_WIDTH - getWidth(), 0);
	}

	public void showPanel() {
		this.setSize(PortalConstants.PANEL_WIDTH, PortalConstants.PANEL_HEIGHT);
		this.setLocation(PortalConstants.SCREEN_WIDTH - PortalConstants.PANEL_WIDTH, 0);
	}

	public static void main(String[] args) {
		User user = null;

		try {
			user = UserUtils.s_readUser(new File(System.getProperty("user.home") + "/.fileportal"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (user == null) {
			user = new User(System.getProperty("user.name"));
			user.setIcon(PortalConstants.DEFAULT_USER_ICON);
		}

		final User u = user;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (u != null) {
					try {
						UserUtils.s_saveUser(u, new File(System.getProperty("user.home") + "/.fileportal"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		// Now start the main app
		LanDiscoverer disc = new LanDiscoverer(user);
		LanBroadcaster broad = new LanBroadcaster(user);

		LanIconServer icon = new LanIconServer(user);
		icon.start();

		FileReceiverServer server = new FileReceiverServer(new GuiReceiverHandler());
		server.start();

		disc.start();
		broad.start();

		PortalApp app = new PortalApp(user);
		app.addDiscoverer(disc);
	}
}