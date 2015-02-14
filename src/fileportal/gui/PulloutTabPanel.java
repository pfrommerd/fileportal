package fileportal.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class PulloutTabPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;

	private PortalApp m_app;
	
	public PulloutTabPanel(PortalApp app) {
		m_app = app;
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		addMouseListener(this);
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		int halfWidth = (int) (getWidth() * 0.5f);
		int halfHeight = (int) (getHeight() * 0.5f);
		if (m_app.isPanelShowing()) {
			g2d.drawLine(5, halfHeight + 10, halfWidth, halfHeight);
			g2d.drawLine(5, halfHeight - 10, halfWidth, halfHeight);
		} else {
			g2d.drawLine(5, halfHeight, halfWidth, halfHeight + 10);
			g2d.drawLine(5, halfHeight, halfWidth, halfHeight - 10);
		}
	}
	public void mouseClicked(MouseEvent e) {
		if (m_app.isPanelShowing()) m_app.hidePanel();
		else m_app.showPanel();
	}
	public void mousePressed(MouseEvent e) {

	}
	public void mouseReleased(MouseEvent e) {

	}
	public void mouseEntered(MouseEvent e) {

	}
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PortalApp.TAB_WIDTH, PortalApp.TAB_HEIGHT);
	}

}

