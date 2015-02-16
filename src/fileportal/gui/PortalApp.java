package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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

	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();

		PortalConstants.SCREEN_WIDTH = (int) rect.getWidth();
		PortalConstants.SCREEN_HEIGHT = (int) rect.getHeight();

		try {
			PortalConstants.DEFAULT_USER_ICON = ImageIO.read(PortalApp.class.getResourceAsStream("/unknown-user.png"));
			PortalConstants.PROFILE_SETTINGS_ICON = ImageIO.read(PortalApp.class.getResourceAsStream("/gear.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

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
		return getX() + PortalConstants.TAB_WIDTH < PortalConstants.SCREEN_WIDTH;
	}

	public void addDiscoverer(Discoverer d) {
		d.addHandler(m_discoveryPanel);
	}

	public void removeDiscoverer(Discoverer d) {
		d.removeHandler(m_discoveryPanel);
	}

	public void run() {
		while (true) {
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void hidePanel() {
		while (isPanelShowing()) {
			int panelX = getX() + PortalConstants.TAB_WIDTH;
			int desX = PortalConstants.SCREEN_WIDTH;
			int delta = desX - panelX;
			int moveX = (int) (delta * PortalConstants.MOVE_SPEED / 5f);
			if (delta < 5) {
				moveX = 1;
			}
			// Move to the right
			setLocation(getX() + moveX, getY());
			repaint();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void showPanel() {
		while (getX() > PortalConstants.SCREEN_WIDTH - PortalConstants.PANEL_WIDTH - PortalConstants.TAB_WIDTH) {
			int panelX = getX() + PortalConstants.TAB_WIDTH;
			int desX = PortalConstants.SCREEN_WIDTH - PortalConstants.PANEL_WIDTH;
			int delta = desX - panelX;
			int moveX = (int) (delta * PortalConstants.MOVE_SPEED / 5f);
			if (delta > -5) {
				moveX = -1;
			}

			// Move to the left
			setLocation(getX() + moveX, getY());
			repaint();
		}
		repaint();
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

		// will run the main loop
		app.run();
	}
}