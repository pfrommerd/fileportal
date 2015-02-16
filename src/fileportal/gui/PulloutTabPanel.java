package fileportal.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PulloutTabPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;

	private PortalApp m_app;

	public PulloutTabPanel(PortalApp app) {
		m_app = app;
		setForeground(PortalConstants.BACKGROUND_COLOR);
		setBackground(PortalConstants.BACKGROUND_COLOR);
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, PortalConstants.DIVIDER_THICKNESS, PortalConstants.DIVIDER_COLOR));

		addMouseListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		int halfWidth = (int) (getWidth() * 0.5f);
		// int halfHeight = (int) (getHeight() * 0.5f);
		int halfHeight = (int) (Math.min(PortalConstants.TAB_HEIGHT, getHeight()) * 0.5f);
		if (m_app.isPanelShowing()) {
			g2d.drawLine(5, halfHeight + 10, halfWidth, halfHeight);
			g2d.drawLine(5, halfHeight - 10, halfWidth, halfHeight);
		} else {
			g2d.drawLine(5, halfHeight, halfWidth, halfHeight + 10);
			g2d.drawLine(5, halfHeight, halfWidth, halfHeight - 10);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (m_app.isPanelShowing())
			m_app.hidePanel();
		else
			m_app.showPanel();
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PortalConstants.TAB_WIDTH, PortalConstants.TAB_HEIGHT);
	}

}
