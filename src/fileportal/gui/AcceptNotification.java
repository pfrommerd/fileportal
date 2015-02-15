package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.notification.Notification;
import com.notification.NotificationBuilder;
import com.notification.types.ComponentNotification;
import com.theme.TextTheme;
import com.theme.ThemePackage;
import com.theme.WindowTheme;

/**
 * An IconNotification displays text, but with an icon.
 */
public class AcceptNotification extends ComponentNotification {
	private TextTheme m_theme;

	private String m_title;
	private String m_subtitle;

	private JLabel m_titleLabel;
	private JLabel m_subtitleLabel;

	private JLabel m_iconLabel;

	private boolean m_accepted = false;
	
	public static final int ICON_PADDING = 10;

	public AcceptNotification(BufferedImage icon, String msg, String subMsg) {
		m_iconLabel = new JLabel();
		m_iconLabel.setIcon(new ImageIcon(icon));
		m_titleLabel = new JLabel(msg);
		m_subtitleLabel = new JLabel(subMsg);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(m_titleLabel, BorderLayout.NORTH);
		panel.add(m_subtitleLabel, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(0, ICON_PADDING, 0, 0));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		JButton accept = new JButton("Accept");
		JButton decline = new JButton("Decline");

		accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_accepted = true;
				synchronized(AcceptNotification.this) {
					AcceptNotification.this.notifyAll();
				}
			}
		});
		decline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_accepted = false;
				synchronized(AcceptNotification.this) {
					AcceptNotification.this.notifyAll();
				}
			}
		});
		
		buttonPanel.add(decline);
		buttonPanel.add(accept);
		
		addComponent(m_iconLabel, BorderLayout.WEST);
		addComponent(panel, BorderLayout.CENTER);
		addComponent(buttonPanel, BorderLayout.SOUTH);
		
		setSize(300, 125);
	}

	/**
	 * Sets the icon to use.
	 * 
	 * @param icon
	 */
	public void setIcon(ImageIcon icon) {
		m_iconLabel.setIcon(icon);
	}

	protected TextTheme getTextTheme() {
		return m_theme;
	}

	/**
	 * @param theme
	 *            the two Fonts that should be used.
	 */
	public void setTextTheme(TextTheme theme) {
		m_theme = theme;
		m_titleLabel.setFont(theme.title);
		m_subtitleLabel.setFont(theme.subtitle);
	}

	public String getTitle() {
		return m_title;
	}

	public void setTitle(String title) {
		m_titleLabel.setText(title);
		m_title = title;
	}

	public String getSubtitle() {
		return m_subtitle;
	}

	public void setSubtitle(String subtitle) {
		m_subtitleLabel.setText(subtitle);
		m_subtitle = subtitle;
	}
	
	public boolean getAccept() {
		synchronized(this) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		return m_accepted;
	}

	public static class AcceptNotificationBuilder implements
							NotificationBuilder {
		@Override
		public Notification buildNotification(ThemePackage pack, Object[] args) {
			AcceptNotification note = null;
			if (args.length != 0) {
				note = new AcceptNotification((BufferedImage) args[0], (String) args[1], (String) args[2]);
			} else {
				note = new AcceptNotification(PortalApp.DEFAULT_USER_ICON, "Accept", "");
			}
			note.setWindowTheme(pack.windowTheme);
			return note;
		}
	}
	
	@Override
	public void themeSet(WindowTheme theme) {
		super.themeSet(theme);
		m_titleLabel.setBackground(theme.background);
		m_subtitleLabel.setBackground(theme.background);

		m_titleLabel.setForeground(theme.foreground);
		m_subtitleLabel.setForeground(theme.foreground);
	}
}
