package fileportal.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fileportal.net.User;
import fileportal.net.User.UserListener;


public class ProfileBar extends JPanel {
	private static final long serialVersionUID = 1L;

	private User m_user;
	
	private JLabel m_icon;
	private JLabel m_name;
	
	public ProfileBar(User u) {
		m_user = u;
		
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createMatteBorder(0, 0, PortalApp.DIVIDER_THICKNESS, 0, PortalApp.DIVIDER_COLOR));
		setForeground(PortalApp.BACKGROUND_COLOR);
		setBackground(PortalApp.BACKGROUND_COLOR);
		
		//Downscale the icon
		Image img = u.getIcon().getScaledInstance(PortalApp.PROFILE_BAR_HEIGHT, PortalApp.PROFILE_BAR_HEIGHT, Image.SCALE_SMOOTH);
		
		m_icon = new JLabel(new ImageIcon(img));
		m_icon.addMouseListener(new IconMouseListener());
		
		m_name = new JLabel(" " + u.getName());
		
		m_name.setForeground(PortalApp.FONT_COLOR);
		m_name.setFont(PortalApp.PROFILE_FONT);

		add(m_name, BorderLayout.CENTER);
		
		m_name.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					final JTextField field = new JTextField(m_user.getName());
					field.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							m_user.setName(field.getText());
							remove(field);
							add(m_name, BorderLayout.CENTER);
							revalidate();
						}
					});
					remove(m_name);
					add(field, BorderLayout.CENTER);
					revalidate();
				}
			} 
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		add(m_icon, BorderLayout.WEST);
		add(m_name, BorderLayout.CENTER);
		add(new ExitPanel(), BorderLayout.EAST);

		u.addListener(new UserListener() {
			@Override
			public void nameChanged(String name) {
				m_name.setText(" " + name);
			}

			@Override
			public void iconChanged(BufferedImage icon) {
				System.out.println("Changing icon");
				//Downscale the icon
				Image img = icon.getScaledInstance(PortalApp.PROFILE_BAR_HEIGHT, 
								PortalApp.PROFILE_BAR_HEIGHT, Image.SCALE_SMOOTH);
				
				remove(m_icon);
				
				m_icon = new JLabel(new ImageIcon(img));
				m_icon.addMouseListener(new IconMouseListener());
				
				add(m_icon, BorderLayout.WEST);
				
				revalidate();
				repaint();
			}
		});
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(PortalApp.PROFILE_BAR_HEIGHT,
							 PortalApp.PROFILE_BAR_HEIGHT);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	public class IconMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				//Select the next image
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file != null) {
						try {
							BufferedImage img = ImageIO.read(file);
							m_user.setIcon(img);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	public class ExitPanel extends JPanel implements MouseListener {
		private static final long serialVersionUID = 1L;
		
		public ExitPanel() {
			setForeground(PortalApp.BACKGROUND_COLOR);
			setBackground(PortalApp.BACKGROUND_COLOR);
			
			addMouseListener(this);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			/*g.drawLine(0, getHeight() / 2, getWidth(), 0);
			g.drawLine(0, 0, getWidth(), getHeight() / 2);
			
			//And the border
			g.drawLine(0, 0, 0, getHeight() / 2);
			g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);*/

			g.setColor(PortalApp.EXIT_BUTTON_COLOR);
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			g2d.setComposite(ac);
			g2d.fillRect(0, 0, getWidth(), getHeight() / 2);
		}
		@Override
		public Dimension getMinimumSize() {
			return new Dimension(PortalApp.PROFILE_BAR_HEIGHT / 2,
								 PortalApp.PROFILE_BAR_HEIGHT);
		}
		@Override
		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.exit(0);
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
	}

}
