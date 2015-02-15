package fileportal.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;

import fileportal.net.TransferTracker;
import fileportal.net.User;

public class UserPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private PortalApp m_app;
	
	//Clip image of the user, updated whenever the user image changes
	private BufferedImage m_clippedImg = null;
	//The last user image
	private BufferedImage m_userImg = null;
	
	private boolean m_hovering = false;
	private int m_hoverCircleWidth = PortalApp.USER_ICON_WIDTH;
	private int m_hoverCircleHeight = PortalApp.USER_ICON_HEIGHT;
	
	private TransferTracker m_currentTransfer = null;
	private float m_alpha = 0f;
	
	private User m_user;
	
	public UserPanel(PortalApp app, User u) {
		m_app = app;
		
	    new DropTarget(this, new MyDragDropListener());

	    setForeground(PortalApp.BACKGROUND_COLOR);
	    setBackground(PortalApp.BACKGROUND_COLOR);
	    
		m_user = u;
	}
	
	public void updateClippedImage(Graphics2D g2d, BufferedImage icon) {
		// Create a translucent intermediate image in which we can perform
		// the soft clipping
		GraphicsConfiguration gc = g2d.getDeviceConfiguration();
		BufferedImage img = gc.createCompatibleImage(PortalApp.USER_ICON_WIDTH, PortalApp.USER_ICON_HEIGHT, Transparency.TRANSLUCENT);
		Graphics2D g2 = img.createGraphics();

		// Clear the image so all pixels have zero alpha
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, PortalApp.USER_ICON_WIDTH, PortalApp.USER_ICON_HEIGHT);

		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(PortalApp.BACKGROUND_COLOR);
		g2.fillOval(0, 0, PortalApp.USER_ICON_WIDTH, PortalApp.USER_ICON_HEIGHT);

		g2.setComposite(AlphaComposite.SrcAtop);

		//Draw the icon to the image
		g2.drawImage(icon, 0, 0, PortalApp.USER_ICON_WIDTH, PortalApp.USER_ICON_HEIGHT, null);
		g2.dispose();
		
		m_clippedImg = img;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		if (m_alpha < 1) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(m_alpha, 0));
			g2d.setComposite(ac);
		}
		
		int halfWidth = (int) (getWidth() * 0.5f);

		g2d.setColor(Color.BLACK);
		
		String text = m_user.getName();
		if (text.length() > PortalApp.USER_NAME_MAX_CHARS) {
			text = text.substring(0, PortalApp.USER_NAME_MAX_CHARS - 3) + "...";
		}
		
		FontMetrics fontMetrics = g2d.getFontMetrics(); 
		int textWidth = fontMetrics.stringWidth(text);
		
		g2d.setColor(PortalApp.FONT_COLOR);
		g2d.setFont(PortalApp.USER_FONT);

		g2d.drawString(text, halfWidth - (textWidth >> 1), 
									PortalApp.USER_ICON_TOP_SPACE +
									PortalApp.USER_ICON_HEIGHT + 
									PortalApp.USER_NAME_SPACING +
									PortalApp.USER_NAME_LINE_HEIGHT);
		
		BufferedImage icon = m_user.getIcon();
		if (icon == null) icon = PortalApp.DEFAULT_USER_ICON;
		int halfIconWidth = PortalApp.USER_ICON_WIDTH >> 1;
		
		//Translate to the pos of the icon on the screen
		g2d.translate(halfWidth - halfIconWidth, PortalApp.USER_ICON_TOP_SPACE);

		g2d.setColor(PortalApp.FILE_HOVER_COLOR);
		
		//Draw the hover animation background if the animation is present
		if (m_hoverCircleWidth > PortalApp.USER_ICON_WIDTH ||
				m_hoverCircleWidth > PortalApp.USER_ICON_HEIGHT) 
			g2d.fillOval(-(m_hoverCircleWidth - PortalApp.USER_ICON_WIDTH >> 1), -((m_hoverCircleHeight - PortalApp.USER_ICON_HEIGHT) >> 1), m_hoverCircleWidth, m_hoverCircleHeight);

		//Draw the sending percent animation if there is a file transfer
		if (m_currentTransfer != null) {
			g2d.setColor(PortalApp.TRANSFER_PROGRESS_COLOR);
			int deg = (int) (m_currentTransfer.getPercentage() / 100 * 360);
			g2d.fillArc(-((PortalApp.LOADING_ARC_RADIUS - PortalApp.USER_ICON_WIDTH) >> 1),
						-((PortalApp.LOADING_ARC_RADIUS - PortalApp.USER_ICON_HEIGHT) >> 1), 
						PortalApp.LOADING_ARC_RADIUS, PortalApp.LOADING_ARC_RADIUS, 
						90, 
						-deg);
			if (m_currentTransfer.getPercentage() >= 100) {
				m_currentTransfer = null;
			}
		}
		
		if (m_userImg != m_user.getIcon()) {
			updateClippedImage(g2d, m_user.getIcon());
			m_userImg = m_user.getIcon();
		}
		
		// Copy our intermediate image to the screen
		g2d.drawImage(m_clippedImg, 0, 0, null);
		
		//Do animating
		if (m_alpha < 1) {
			m_alpha += PortalApp.USER_FADE_RATE;
		}
		if (m_hovering && m_hoverCircleWidth < PortalApp.USER_ICON_WIDTH + PortalApp.USER_ICON_HOVER_RADIUS) {
			m_hoverCircleWidth += PortalApp.USER_ICON_HOVER_SPEED;
		}
		if (m_hovering && m_hoverCircleHeight < PortalApp.USER_ICON_HEIGHT + PortalApp.USER_ICON_HOVER_RADIUS) {
			m_hoverCircleHeight += PortalApp.USER_ICON_HOVER_SPEED;
		}
		if (!m_hovering && m_hoverCircleWidth > PortalApp.USER_ICON_WIDTH) {
			m_hoverCircleWidth -= PortalApp.USER_ICON_HOVER_SPEED;
		}
		if (!m_hovering && m_hoverCircleHeight > PortalApp.USER_ICON_HEIGHT) {
			m_hoverCircleHeight -= PortalApp.USER_ICON_HOVER_SPEED;
		}
	}

	public void fadeOut() {
		while(m_alpha > 0) {
			m_alpha -= PortalApp.USER_FADE_RATE;
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(Math.max(PortalApp.USER_ICON_WIDTH, PortalApp.USER_MAX_NAME_WIDTH),
							PortalApp.USER_ICON_TOP_SPACE + PortalApp.USER_ICON_HEIGHT + 
							PortalApp.USER_NAME_SPACING + PortalApp.USER_NAME_LINE_HEIGHT + 
							PortalApp.USER_NAME_LINE_DESCENT);
	}
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	public class MyDragDropListener implements DropTargetListener {

	    @Override
	    public void drop(DropTargetDropEvent event) {
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
	    		if (!m_hovering) m_hovering = true;
	    	} else if(m_hovering) m_hovering = false;
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
