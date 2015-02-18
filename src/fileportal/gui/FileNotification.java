package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.notification.Notification;
import com.notification.NotificationBuilder;
import com.notification.types.BorderLayoutNotification;
import com.theme.TextTheme;
import com.theme.ThemePackage;
import com.theme.WindowTheme;

/**
 * An IconNotification displays text, but with an icon.
 */
public class FileNotification extends BorderLayoutNotification {
	private TextTheme m_theme;

	private JLabel m_titleLabel;
	private JLabel m_subtitleLabel;

	private JLabel m_iconLabel;
	private JButton m_accept;
	private JButton m_decline;

	private JPanel m_buttonPanel;

	private JProgressBar m_progressBar;

	private boolean m_accepted = false;

	public static final int ICON_PADDING = 10;

	public FileNotification(BufferedImage icon, String msg, String subMsg) {
		Image img = icon;
		if (icon.getWidth() != 40 || icon.getHeight() != 40) {
			img = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		}
		m_iconLabel = new JLabel();
		m_iconLabel.setIcon(new ImageIcon(img));
		m_titleLabel = new JLabel(msg);
		m_subtitleLabel = new JLabel(subMsg);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(m_titleLabel, BorderLayout.NORTH);
		panel.add(m_subtitleLabel, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(0, ICON_PADDING, 0, 0));

		m_buttonPanel = new JPanel();
		m_buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));

		m_accept = new JButton("Accept");
		m_decline = new JButton("Decline");
		m_accept.setPreferredSize(new Dimension(100, 20));
		m_decline.setPreferredSize(new Dimension(100, 20));

		m_accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_accepted = true;
				synchronized (FileNotification.this) {
					FileNotification.this.notifyAll();
				}
			}
		});
		m_decline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_accepted = false;
				synchronized (FileNotification.this) {
					FileNotification.this.notifyAll();
				}
			}
		});

		m_buttonPanel.add(m_decline);
		m_buttonPanel.add(m_accept);
		m_buttonPanel.setPreferredSize(m_buttonPanel.getPreferredSize());

		addComponent(m_iconLabel, BorderLayout.WEST);
		addComponent(panel, BorderLayout.CENTER);
		addComponent(m_buttonPanel, BorderLayout.SOUTH);

		setSize(300, 150);
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
		m_accept.setFont(theme.subtitle);
		m_decline.setFont(theme.subtitle);

		m_titleLabel.setForeground(m_theme.titleColor);
		m_subtitleLabel.setForeground(m_theme.subtitleColor);
		m_accept.setForeground(m_theme.subtitleColor);
		m_decline.setForeground(m_theme.subtitleColor);
	}

	public String getTitle() {
		return m_titleLabel.getText();
	}

	public void setTitle(String title) {
		m_titleLabel.setText(title);
	}

	public String getSubtitle() {
		return m_subtitleLabel.getText();
	}

	public void setSubtitle(String subtitle) {
		m_subtitleLabel.setText(subtitle);
	}

	public boolean getAccept() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		return m_accepted;
	}

	// Will switch to transfer mode
	public void showTransfer() {
		m_progressBar = new JProgressBar(0, 100);
		m_progressBar.setPreferredSize(new Dimension(200, 25));

		removeComponent(m_buttonPanel);
		addComponent(m_progressBar, BorderLayout.SOUTH);
	}

	public void setTransferPercentage(float percent) {
		m_progressBar.setValue((int) percent);
	}

	@Override
	public void themeSet(WindowTheme theme) {
		super.themeSet(theme);

		if (m_theme != null) {
			m_titleLabel.setForeground(m_theme.titleColor);
			m_subtitleLabel.setForeground(m_theme.subtitleColor);
			m_accept.setForeground(m_theme.subtitleColor);
			m_decline.setForeground(m_theme.subtitleColor);
		}
	}

	public static class FileNotificationBuilder implements NotificationBuilder {
		@Override
		public Notification buildNotification(ThemePackage pack, Object[] args) {
			FileNotification note = null;
			if (args.length != 0) {
				note = new FileNotification((BufferedImage) args[0], (String) args[1], (String) args[2]);
			} else {
				note = new FileNotification(PortalConstants.DEFAULT_USER_ICON, "Accept", "");
			}
			note.setWindowTheme(pack.windowTheme);
			note.setTextTheme(pack.textTheme);
			return note;
		}
	}
}
