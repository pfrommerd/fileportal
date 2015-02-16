package fileportal.net.lan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.Timer;

import fileportal.net.Broadcaster;
import fileportal.net.NetworkConstants;
import fileportal.net.User;

public class LanBroadcaster implements Broadcaster {
	private User m_user;
	private DatagramSocket m_sock;
	private Timer m_timer;

	public LanBroadcaster(User user) {
		m_user = user;
	}

	@Override
	public void start() {
		try {
			m_sock = new DatagramSocket();
			m_sock.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		m_timer = new Timer(NetworkConstants.BROADCAST_DELAY, new Broadcaster());
		m_timer.start();
	}

	@Override
	public void stop() {
		m_timer.stop();
		m_sock.close();
	}

	private class Broadcaster implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			byte[] sendData = ("User: " + m_user.getName()).getBytes();

			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName("255.255.255.255"), NetworkConstants.BROADCAST_LISTEN_PORT);

				m_sock.send(sendPacket);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
