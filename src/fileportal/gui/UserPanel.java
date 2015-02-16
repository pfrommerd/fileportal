package fileportal.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import fileportal.net.TransferTracker;
import fileportal.net.User;
import fileportal.net.User.UserListener;

public class UserPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private PortalApp m_app;

	// Clip image of the user, updated whenever the user image changes
	private BufferedImage m_clippedImg = null;
	// The last user image that has been clipped
	private Image m_lastUserImg = null;

	private String m_userName;
	private Image m_userImg = null;


	private boolean m_hovering = false;
	private int m_hoverCircleWidth = PortalConstants.USER_ICON_WIDTH;
	private int m_hoverCircleHeight = PortalConstants.USER_ICON_HEIGHT;

	private TransferTracker m_currentTransfer = null;
	private float m_alpha = 0f;

	private Timer m_hoverRepaintTimer;
	
	private User m_user;

	public UserPanel(PortalApp app, User u) {
		m_app = app;

		new DropTarget(this, new MyDragDropListener());

		setForeground(PortalConstants.BACKGROUND_COLOR);
		setBackground(PortalConstants.BACKGROUND_COLOR);

		m_user = u;
		
		m_userName = m_user.getName();
		
		// Downscale the icon
		m_userImg = m_user.getIcon().getScaledInstance(PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_ICON_HEIGHT,
														Image.SCALE_SMOOTH);
		
		m_user.addListener(new UserListener() {
			@Override
			public void nameChanged(String name) {
				m_userName = name;
			}

			@Override
			public void iconChanged(BufferedImage icon) {
				// Downscale the icon
				m_userImg = icon.getScaledInstance(PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_ICON_HEIGHT,
													 Image.SCALE_SMOOTH);
			}
		});
		
		m_hoverRepaintTimer = new Timer(16, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_hovering) animateHoverCircleGrow();
				else animateHoverCircleShrink();
				
				boolean shrinkWidth = m_hoverCircleWidth > PortalConstants.USER_ICON_WIDTH;
				boolean shrinkHeight = m_hoverCircleHeight > PortalConstants.USER_ICON_HEIGHT;
				
				if (!m_hovering && !shrinkWidth && !shrinkHeight) {
					m_hoverRepaintTimer.stop();
				}
				repaint();
			}
		});
	}

	public void updateClippedImage(Graphics2D g2d, Image m_userImg2) {
		// Create a translucent intermediate image in which we can perform
		// the soft clipping
		GraphicsConfiguration gc = g2d.getDeviceConfiguration();
		BufferedImage img = gc.createCompatibleImage(PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_ICON_HEIGHT,
				Transparency.TRANSLUCENT);
		Graphics2D g2 = img.createGraphics();

		// Clear the image so all pixels have zero alpha
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_ICON_HEIGHT);

		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(PortalConstants.BACKGROUND_COLOR);
		g2.fillOval(0, 0, PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_ICON_HEIGHT);

		g2.setComposite(AlphaComposite.SrcAtop);
		
		// Draw the icon to the image
		g2.drawImage(m_userImg2, 0, 0, PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_ICON_HEIGHT, null);
		g2.dispose();

		m_clippedImg = img;
	}

	@Override
	public void paintComponent(Graphics g) {
		//For when we just call repaint() from this panel
		RenderUtils.s_setupFineRender(g);
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		if (m_alpha < 1) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(m_alpha, 0));
			g2d.setComposite(ac);
		}

		int halfWidth = (int) (getWidth() * 0.5f);

		g2d.setColor(Color.BLACK);

		String text = m_userName;
		if (text.length() > PortalConstants.USER_NAME_MAX_CHARS) {
			text = text.substring(0, PortalConstants.USER_NAME_MAX_CHARS - 3) + "...";
		}

		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textWidth = fontMetrics.stringWidth(text);

		g2d.setColor(PortalConstants.FONT_COLOR);
		g2d.setFont(PortalConstants.USER_FONT);

		g2d.drawString(text, halfWidth - (textWidth >> 1), PortalConstants.USER_ICON_TOP_SPACE + PortalConstants.USER_ICON_HEIGHT
						+ PortalConstants.USER_NAME_SPACING + PortalConstants.USER_NAME_LINE_HEIGHT);

		BufferedImage icon = m_clippedImg;
		if (icon == null)
			icon = PortalConstants.DEFAULT_USER_ICON;
		int halfIconWidth = PortalConstants.USER_ICON_WIDTH >> 1;

		// Translate to the pos of the icon on the screen
		g2d.translate(halfWidth - halfIconWidth, PortalConstants.USER_ICON_TOP_SPACE);

		g2d.setColor(PortalConstants.FILE_HOVER_COLOR);

		// Draw the hover animation background if the animation is present
		if (m_hoverCircleWidth > PortalConstants.USER_ICON_WIDTH || m_hoverCircleWidth > PortalConstants.USER_ICON_HEIGHT)
			g2d.fillOval(-(m_hoverCircleWidth - PortalConstants.USER_ICON_WIDTH >> 1),
					-((m_hoverCircleHeight - PortalConstants.USER_ICON_HEIGHT) >> 1), m_hoverCircleWidth, m_hoverCircleHeight);

		// Draw the sending percent animation if there is a file transfer
		if (m_currentTransfer != null) {
			g2d.setColor(PortalConstants.TRANSFER_PROGRESS_COLOR);
			int deg = (int) (m_currentTransfer.getPercentage() / 100 * 360);
			g2d.fillArc(-((PortalConstants.LOADING_ARC_RADIUS - PortalConstants.USER_ICON_WIDTH) >> 1),
					-((PortalConstants.LOADING_ARC_RADIUS - PortalConstants.USER_ICON_HEIGHT) >> 1),
					PortalConstants.LOADING_ARC_RADIUS, PortalConstants.LOADING_ARC_RADIUS, 90, -deg);
			if (m_currentTransfer.getPercentage() >= 100) {
				m_currentTransfer = null;
			}
		}

		if (m_lastUserImg != m_userImg) {
			updateClippedImage(g2d, m_userImg);
			m_lastUserImg = m_userImg;
		}

		// Copy our intermediate image to the screen
		g2d.drawImage(m_clippedImg, 0, 0, null);
	}
	
	public void animateHoverCircleShrink() {
		boolean shrinkWidth = m_hoverCircleWidth > PortalConstants.USER_ICON_WIDTH;
		boolean shrinkHeight = m_hoverCircleHeight > PortalConstants.USER_ICON_HEIGHT;
		if (shrinkWidth) {
			m_hoverCircleWidth -= PortalConstants.USER_ICON_HOVER_SPEED;
		}
		if (shrinkHeight) {
			m_hoverCircleHeight -= PortalConstants.USER_ICON_HOVER_SPEED;
		}
		repaint();
	}
	
	public void animateHoverCircleGrow() {
		boolean growWidth = m_hoverCircleWidth < 
				PortalConstants.USER_ICON_WIDTH + PortalConstants.USER_ICON_HOVER_RADIUS;
		boolean growHeight = m_hoverCircleHeight <
				PortalConstants.USER_ICON_HEIGHT + PortalConstants.USER_ICON_HOVER_RADIUS;
		if (growWidth) {
			m_hoverCircleWidth += PortalConstants.USER_ICON_HOVER_SPEED;
		}
		if (growHeight) {
			m_hoverCircleHeight += PortalConstants.USER_ICON_HOVER_SPEED;
		}
		repaint();
	}
	
	public void fadeIn() {
		while (m_alpha < 1) {
			m_alpha += PortalConstants.USER_FADE_RATE;
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
	public void fadeOut() {
		while (m_alpha > 0) {
			m_alpha -= PortalConstants.USER_FADE_RATE;
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(Math.max(PortalConstants.USER_ICON_WIDTH, PortalConstants.USER_MAX_NAME_WIDTH),
				PortalConstants.USER_ICON_TOP_SPACE + PortalConstants.USER_ICON_HEIGHT + PortalConstants.USER_NAME_SPACING
						+ PortalConstants.USER_NAME_LINE_HEIGHT + PortalConstants.USER_NAME_LINE_DESCENT);
	}

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public class MyDragDropListener implements DropTargetListener {

		@Override
		public void drop(DropTargetDropEvent event) {
			Dimension dim = UserPanel.this.getPreferredSize();
			Point loc = event.getLocation();
			if (loc.getY() > dim.getHeight() || loc.getX() > dim.getWidth()) {
				return;
			}
			
			// Accept copy drops
			event.acceptDrop(DnDConstants.ACTION_COPY);
			
			m_hovering = false;

			// Get the transfer which can provide the dropped item data
			Transferable transferable = event.getTransferable();

			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			for (DataFlavor flavor : flavors) {
				try {
					// If the drop items are files
					if (flavor.isFlavorJavaFileListType()) {

						// Get all of the dropped files
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) transferable.getTransferData(flavor);
						m_currentTransfer = m_user.sendFiles(files.toArray(new File[0]), m_app.getUser());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			event.dropComplete(true);
		}

		@Override
		public void dragEnter(DropTargetDragEvent event) {
		}

		@Override
		public void dragExit(DropTargetEvent event) {
			m_hovering = false;
		}

		@Override
		public void dragOver(DropTargetDragEvent event) {
			Dimension dim = UserPanel.this.getPreferredSize();
			Point loc = event.getLocation();
			if (loc.getY() < dim.getHeight() && loc.getX() < dim.getWidth()) {
				if (!m_hovering) {					
					m_hovering = true;
					m_hoverRepaintTimer.start();
				}
			} else if (m_hovering) {
				m_hovering = false;
			}
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent event) {
		}

	}

	/*public class FileDropHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;

		@Override
	    public boolean canImport(TransferHandler.TransferSupport info) {
			info.getDropLocation().getDropPoint();
	        // we only import FileList
	        if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	            return false;
	        }
	        System.out.println("Hovering...");
	        m_hovering = true;
	        
	        return true;
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public boolean importData(TransferHandler.TransferSupport info) {
	        if (!info.isDrop()) {
	            return false;
	        }

	        // Check for FileList flavor
	        if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	            displayDropLocation("List doesn't accept a drop of this type.");
	            return false;
	        }

	        // Get the fileList that is being dropped.
	        Transferable t = info.getTransferable();
	        List<File> data;
	        try {
	            data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
	        } 
	        catch (Exception e) { return false; }
	        
	        m_user.sendFiles(data.toArray(new File[0]));
	        
	        return true;
	    }

	    private void displayDropLocation(String string) {
	    	System.out.println("Foo " + string);
	    }
	};*/
}
