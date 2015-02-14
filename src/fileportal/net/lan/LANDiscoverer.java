package fileportal.net.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fileportal.net.DiscoverHandler;
import fileportal.net.Discoverer;
import fileportal.net.ProgramConstants;

public class LANDiscoverer implements Discoverer {
	private List<DiscoverHandler> m_handlers = new ArrayList<DiscoverHandler>();
	private List<LANUser> m_connected = new ArrayList<LANUser>();
	private DatagramSocket m_sock;
	private Thread m_listener;

	public LANDiscoverer() {
		try {
			m_sock = new DatagramSocket(ProgramConstants.BROADCAST_LISTEN_PORT,
					InetAddress.getByName("0.0.0.0"));
			m_sock.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addHandler(DiscoverHandler handler) {
		m_handlers.add(handler);
	}

	@Override
	public void removeHandler(DiscoverHandler handler) {
		m_handlers.remove(handler);
	}

	@Override
	public void start() {
		m_listener = new Thread(new SocketListener());
		m_listener.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void stop() {
		m_listener.stop();
	}

	private class SocketListener implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					byte[] recvBuf = new byte[15000];
					DatagramPacket packet = new DatagramPacket(recvBuf,
							recvBuf.length);
					m_sock.receive(packet);

					String message = new String(packet.getData()).trim();
					if (message.contains("User: ")) {
						String name = message.substring(5);
						LANUser user = new LANUser(name);
						user.setAddress(packet.getAddress());

						Socket iconSock = new Socket(user.getAddress(),
								ProgramConstants.ICON_PORT);
						user.setIcon(ImageIO.read(iconSock.getInputStream()));
						iconSock.close();

						System.out
								.println("User discovered: " + user.getName());

						m_connected.add(user);
						for (DiscoverHandler handler : m_handlers) {
							handler.userDiscovered(user);
						}
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
