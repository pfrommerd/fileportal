package fileportal.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.notification.Notification;
import com.notification.NotificationBuilder;
import com.notification.types.ComponentNotification;
import com.theme.TextTheme;
import com.theme.ThemePackage;

public class AcceptNotification extends ComponentNotification {
	private JButton m_accept;
	private JButton m_decline;
	private JLabel m_text;

	private SynchronizeObject m_obj = new SynchronizeObject();
	private boolean m_accepted = false;

	private class SynchronizeObject {

	}

	public AcceptNotification(String text) {
		JPanel customPanel = new JPanel();
		m_accept = new JButton("Accept");
		m_decline = new JButton("Decline");

		m_accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_accepted = true;
				hide();
				synchronized (m_obj) {
					m_obj.notify();
				}
			}
		});

		m_decline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_accepted = false;
				hide();
				synchronized (m_obj) {
					m_obj.notify();
				}
			}
		});

		customPanel.add(m_accept);
		customPanel.add(m_decline);
		m_text = new JLabel(text);

		addComponent(m_text, BorderLayout.NORTH);
		addComponent(customPanel, BorderLayout.CENTER);
		setSize(300, 70);
	}

	public void setText(String text) {
		m_text.setText(text);
	}

	public void setTextTheme(TextTheme theme) {
		m_text.setFont(theme.subtitle);
		m_accept.setFont(theme.subtitle);
		m_decline.setFont(theme.subtitle);
	}

	/**
	 * @return whether or not the file was accepted
	 */
	public boolean getAccept() {
		try {
			synchronized (m_obj) {
				m_obj.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return m_accepted;
	}

	public static class AcceptNotificationBuilder implements
			NotificationBuilder {
		@Override
		public Notification buildNotification(ThemePackage pack, Object[] args) {
			AcceptNotification note = null;
			if (args.length != 0) {
				note = new AcceptNotification((String) args[0]);
			} else {
				note = new AcceptNotification("");
			}
			note.setWindowTheme(pack.windowTheme);
			return note;
		}
	}
}
