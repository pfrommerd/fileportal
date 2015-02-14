package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	
	public static final int USER_ICON_WIDTH = 50;
	public static final int USER_ICON_HEIGHT = 50;
	public static final int USER_NAME_SPACING = 10;
	public static final int USER_MAX_NAME_WIDTH = 70;
	public static final int USER_NAME_LINE_HEIGHT = 20;
	
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	
	
	static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        
		SCREEN_WIDTH = (int) rect.getWidth();
		SCREEN_HEIGHT = (int) rect.getHeight();
	}	
	
	public PortalApp() {
		setLocation(SCREEN_WIDTH - PANEL_WIDTH - TAB_WIDTH, Y_OFFSET);
		setSize(PANEL_WIDTH + TAB_WIDTH, PANEL_HEIGHT);

		setUndecorated(true);
		setAlwaysOnTop(true);
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		
		main.add(new PulloutTabPanel(this), BorderLayout.WEST);
		
		JScrollPane scroll = new JScrollPane();
		scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
		scroll.setBorder(null);
		
		scroll.getViewport().add(new DiscoveryPanel());
		
		main.add(scroll, BorderLayout.CENTER);
		
		
		add(main);
		setVisible(true);
	}
	
	public boolean isPanelShowing() {
		return getX() + TAB_WIDTH < SCREEN_WIDTH;
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
		PortalApp app = new PortalApp();
	}
}