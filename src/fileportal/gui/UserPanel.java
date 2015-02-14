package fileportal.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.TransferHandler;

import fileportal.net.User;

public class UserPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private boolean m_hovering = false;
	
	private int m_textHeight = 30;
	
	private User m_user;
	
	public UserPanel(User u) {
	    setTransferHandler(new FileDropHandler());
		
		m_user = u;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int halfWidth = (int) (getWidth() * 0.5f);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		
		FontMetrics fontMetrics = g2d.getFontMetrics(); 
		int textWidth = fontMetrics.stringWidth(m_user.getName());
		
		g2d.drawString(m_user.getName(), halfWidth - (textWidth >> 1), 
									PortalApp.USER_ICON_HEIGHT + 
									PortalApp.USER_NAME_SPACING +
									PortalApp.USER_NAME_LINE_HEIGHT);
		
		
		//Draw the image of the user
		BufferedImage icon = m_user.getIcon();
		
		int halfIconWidth = PortalApp.USER_ICON_WIDTH >> 1;
		
		g2d.setClip(new Ellipse2D.Double(halfWidth - halfIconWidth, 0,
										PortalApp.USER_ICON_WIDTH, PortalApp.USER_ICON_HEIGHT));
		g2d.drawImage(icon, halfWidth - halfIconWidth, 0,
						PortalApp.USER_ICON_WIDTH, PortalApp.USER_ICON_HEIGHT, null);
		g2d.setClip(null);
	}
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(Math.max(PortalApp.USER_ICON_WIDTH, PortalApp.USER_MAX_NAME_WIDTH),
							PortalApp.USER_ICON_HEIGHT + PortalApp.USER_NAME_SPACING + PortalApp.USER_NAME_LINE_HEIGHT);
	}
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	public class FileDropHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;

		@Override
        public boolean canImport(TransferHandler.TransferSupport info) {
            // we only import FileList
            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

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
    };
}
