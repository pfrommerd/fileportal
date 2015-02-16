package fileportal.gui;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PortalConstants {
	public static final int TAB_WIDTH = 15;
	public static final int TAB_HEIGHT = 300;
	public static final int PANEL_WIDTH = 300;
	public static final int PANEL_HEIGHT = 300;

	public static final int Y_OFFSET = 30;

	public static final int MOVE_SPEED = 1;

	public static final int SCROLL_SPEED = 6;

	public static final int GRID_SPACING = 10;

	public static BufferedImage DEFAULT_USER_ICON = null;

	public static BufferedImage PROFILE_SETTINGS_ICON = null;

	public static final int PROFILE_BAR_HEIGHT = 32;

	public static final int PROFILE_NAME_X_OFF = 5;
	public static final int PROFILE_NAME_Y_OFF = 10;

	// Should be the same as hover radius
	public static final int PROFILE_NAME_LEADING_SPACE = 5;

	public static final int LOADING_ARC_RADIUS = 56;

	public static final float USER_FADE_RATE = 0.10f;

	// Should be the same as hover radius
	public static final int USER_ICON_TOP_SPACE = 20;

	public static final int USER_ICON_WIDTH = 50;
	public static final int USER_ICON_HEIGHT = 50;

	public static final int USER_ICON_HOVER_RADIUS = 20;

	public static final int USER_ICON_HOVER_SPEED = 3;

	public static final int USER_NAME_SPACING = 5;
	public static final int USER_MAX_NAME_WIDTH = 70;
	public static final int USER_NAME_LINE_HEIGHT = 25;
	public static final int USER_NAME_LINE_DESCENT = 5;

	public static final int USER_NAME_MAX_CHARS = 10;

	public static final int DIVIDER_THICKNESS = 1;

	public static final Font PROFILE_FONT = new Font("Dialog", Font.BOLD, 16);
	public static final Font USER_FONT = new Font("Dialog", Font.BOLD, 12);

	public static final Color DIVIDER_COLOR = Color.GRAY;

	public static final Color BACKGROUND_COLOR = Color.WHITE;
	public static final Color FONT_COLOR = Color.GRAY;

	public static final Color FILE_HOVER_COLOR = Color.GRAY;
	public static final Color TRANSFER_PROGRESS_COLOR = new Color(0, 0, 139);

	public static final Color EXIT_BUTTON_COLOR = Color.RED;

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (GraphicsDevice device : gs) {
			DisplayMode mode = device.getDisplayMode();
			PortalConstants.SCREEN_WIDTH += mode.getWidth();
			PortalConstants.SCREEN_HEIGHT += mode.getHeight();
		}

		try {
			PortalConstants.DEFAULT_USER_ICON = ImageIO.read(PortalApp.class.getResourceAsStream("/unknown-user.png"));
			PortalConstants.PROFILE_SETTINGS_ICON = ImageIO.read(PortalApp.class.getResourceAsStream("/gear.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
