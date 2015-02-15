package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.notification.NotificationFactory;
import com.notification.NotificationFactory.PopupLocation;
import com.notification.NotificationManager;
import com.notification.QueueManager;
import com.notification.Time;
import com.theme.ThemePackagePresets;

import fileportal.gui.AcceptNotification.AcceptNotificationBuilder;
import fileportal.net.Discoverer;
import fileportal.net.FileReceiverServer;
import fileportal.net.ReceiverHandler;
import fileportal.net.User;
import fileportal.net.lan.LANBroadcaster;
import fileportal.net.lan.LANDiscoverer;
import fileportal.net.lan.LANIconServer;

public class PortalApp extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final int TAB_WIDTH = 30;
	public static final int TAB_HEIGHT = 100;
	public static final int PANEL_WIDTH = 300;
	public static final int PANEL_HEIGHT = 300;

	public static final int Y_OFFSET = 30;

	public static final int MOVE_SPEED = 20;

	public static final int SCROLL_SPEED = 6;

	public static final int GRID_SPACING = 10;

	public static BufferedImage DEFAULT_USER_ICON = null;

	public static BufferedImage PROFILE_SETTINGS_ICON = null;

	public static final int PROFILE_BAR_HEIGHT = 32;

	public static final int PROFILE_NAME_X_OFF = 5;
	public static final int PROFILE_NAME_Y_OFF = 10;

	// Should be the same as hover radius
	public static final int PROFILE_NAME_LEADING_SPACE = 5;

	public static final float USER_FADE_RATE = 0.10f;
	
	// Should be the same as hover radius
	public static final int USER_ICON_TOP_SPACE = 20;

	public static final int USER_ICON_WIDTH = 50;
	public static final int USER_ICON_HEIGHT = 50;

	public static final int USER_ICON_HOVER_RADIUS = 20;

	public static final int USER_ICON_HOVER_SPEED = 6;

	public static final int USER_NAME_SPACING = 5;
	public static final int USER_MAX_NAME_WIDTH = 70;
	public static final int USER_NAME_LINE_HEIGHT = 25;
	public static final int USER_NAME_LINE_DESCENT = 5;

	public static final int USER_NAME_MAX_CHARS = 10;

	public static final int DIVIDER_THICKNESS = 1;
	public static final Color DIVIDER_COLOR = Color.GRAY;

	public static final Font PROFILE_FONT = new Font("Dialog", Font.BOLD, 16);
	public static final Font USER_FONT = new Font("Dialog", Font.BOLD, 12);

	public static final Color FONT_COLOR = Color.GRAY;

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	static {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();

		SCREEN_WIDTH = (int) rect.getWidth();
		SCREEN_HEIGHT = (int) rect.getHeight();

		try {
			DEFAULT_USER_ICON = ImageIO.read(PortalApp.class
					.getResourceAsStream("/unknown-user.png"));
			PROFILE_SETTINGS_ICON = ImageIO.read(PortalApp.class
					.getResourceAsStream("/gear.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private User m_user;
	private DiscoveryPanel m_discoveryPanel;

	public PortalApp(User user) {
		m_user = user;

		setLocation(SCREEN_WIDTH - PANEL_WIDTH - TAB_WIDTH, Y_OFFSET);
		setSize(PANEL_WIDTH + TAB_WIDTH, PANEL_HEIGHT);

		setUndecorated(true);
		setAlwaysOnTop(true);

		JPanel main = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);

				super.paintComponent(g);
			}
		};
		main.setLayout(new BorderLayout());
		JPanel subPanel = new JPanel();

		subPanel.setLayout(new BorderLayout());

		m_discoveryPanel = new DiscoveryPanel();

		JScrollPane scroll = new JScrollPane();
		scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
		scroll.setBorder(null);

		scroll.getViewport().add(m_discoveryPanel);

		subPanel.add(scroll, BorderLayout.CENTER);
		subPanel.add(new ProfileBar(m_user), BorderLayout.NORTH);

		main.add(new PulloutTabPanel(this), BorderLayout.WEST);
		main.add(subPanel, BorderLayout.CENTER);

		add(main);
		setVisible(true);
	}

	public boolean isPanelShowing() {
		return getX() + TAB_WIDTH < SCREEN_WIDTH;
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
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void hidePanel() {
		while (isPanelShowing()) {
			int panelX = getX() + TAB_WIDTH;
			int desX = SCREEN_WIDTH;
			int delta = desX - panelX;
			int moveX = delta / 5;
			System.out.println(delta);
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
		System.out.println("Done hiding");
	}

	public void showPanel() {
		while (getX() > SCREEN_WIDTH - PANEL_WIDTH - TAB_WIDTH) {
			// Move to the left
			setLocation(getX() - MOVE_SPEED, getY());
			repaint();
		}
		repaint();
	}

	public static void main(String[] args) {
		User user = new User(System.getProperty("user.name"));
		user.setIcon(DEFAULT_USER_ICON);

		final NotificationFactory noteFactory = new NotificationFactory(
				ThemePackagePresets.cleanLight());
		final NotificationManager noteManager = new QueueManager(
				PopupLocation.NORTHWEST);
		noteFactory.addBuilder("accept", new AcceptNotificationBuilder());

		final LANDiscoverer disc = new LANDiscoverer(user);
		LANBroadcaster broad = new LANBroadcaster(user);

		LANIconServer icon = new LANIconServer(user);
		icon.start();

		FileReceiverServer server = new FileReceiverServer(
				new ReceiverHandler() {
					@Override
					public boolean shouldAccept(String userName, String name) {
						User user = disc.getUserForName(userName);

						AcceptNotification note = (AcceptNotification) noteFactory
								.build("accept", user.getIcon(),"Accept file from " + userName, name + " from " + userName);
						noteManager.addNotification(note, Time.infinite());
						boolean accept = note.getAccept();
						note.hide();
						return accept;
					}

					@Override
					public boolean shouldAccept(String userName, int fileNum) {
						User user = disc.getUserForName(userName);
						
						AcceptNotification note = (AcceptNotification) noteFactory
								.build("accept", user.getIcon(), "Accept files form " + userName, fileNum + " files from " + userName);
						noteManager.addNotification(note, Time.infinite());
						boolean accept = note.getAccept();
						note.hide();
						return accept;
					}

					@Override
					public File getFileSaveLocation(String name) {
						return new File(System.getProperty("user.home")
								+ "/Desktop/" + name);
					}

					@Override
					public File getFolderSaveLocation() {
						return new File(System.getProperty("user.home")
								+ "/Desktop/");
					}
				});
		server.start();

		disc.start();
		broad.start();

		PortalApp app = new PortalApp(user);
		app.addDiscoverer(disc);

		// will run the main loop
		app.run();
	}
}