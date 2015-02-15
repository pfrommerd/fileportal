package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fileportal.net.Discoverer;
import fileportal.net.User;

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
	
	public static final int USER_ICON_WIDTH = 50;
	public static final int USER_ICON_HEIGHT = 50;
	public static final int USER_NAME_SPACING = 10;
	public static final int USER_MAX_NAME_WIDTH = 70;
	public static final int USER_NAME_LINE_HEIGHT = 20;
	
	public static final int DIVIDER_THICKNESS = 1;
	public static final Color DIVIDER_COLOR = Color.GRAY;

	public static final Font FONT = new Font("Dialog", Font.BOLD, 16);
	public static final Color FONT_COLOR = Color.GRAY;
	
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	
	
	
	static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        
		SCREEN_WIDTH = (int) rect.getWidth();
		SCREEN_HEIGHT = (int) rect.getHeight();
		
		try {
			DEFAULT_USER_ICON = ImageIO.read(PortalApp.class.getResourceAsStream("/unknown-user.png"));
			PROFILE_SETTINGS_ICON = ImageIO.read(PortalApp.class.getResourceAsStream("/gear.png"));
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
		

		JPanel main = new JPanel();
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
	
	public void hidePanel() {
		while(isPanelShowing()) {
			//Move to the right
			setLocation(getX()  + MOVE_SPEED, getY());
			repaint();
		}
	}
	
	public void showPanel() {
		while(getX() > SCREEN_WIDTH - PANEL_WIDTH - TAB_WIDTH) {
			//Move to the left
			setLocation(getX() - MOVE_SPEED, getY());
			repaint();
		}
		repaint();
	}
	
	public static void main(String[] args) {
		User user = new User("Unknown");
		
		@SuppressWarnings("unused")
		PortalApp app = new PortalApp(user);
		//app.addDiscoverer(new LANDiscoverer());
	}
}