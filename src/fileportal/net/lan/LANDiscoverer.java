package fileportal.net.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import fileportal.net.DiscoverHandler;
import fileportal.net.Discoverer;
import fileportal.net.NetworkConstants;
import fileportal.net.User;

public class LANDiscoverer implements Discoverer {
	private List<DiscoverHandler> m_handlers = new ArrayList<DiscoverHandler>();
	private List<User> m_connected = new ArrayList<User>();
	private Map<User, LANTimeout> m_timeouts = new HashMap<User, LANTimeout>();
	private DatagramSocket m_sock;
	private Thread m_listener;

	public LANDiscoverer(User thisUser) {
		try {
			m_sock = new DatagramSocket(NetworkConstants.BROADCAST_LISTEN_PORT,
					InetAddress.getByName("0.0.0.0"));
			m_sock.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		m_connected.add(thisUser);
	}

	/**
	 * @param userName
	 * @return whether there is a user with this name connected
	 */
	public boolean isConnected(String userName) {
		for (User user : m_connected) {
			if (user.getName().equals(userName)) {
				return true;
			}
		}
		return false;
	}

	public User getUserForName(String userName) {
		for (User user : m_connected) {
			if (user.getName().equals(userName)) {
				return user;
			}
		}
		return null;
	}

	protected void userTimeout(User user) {
		System.out.println("User timeout: " + user);
		m_connected.remove(user);
		for (DiscoverHandler handler : m_handlers) {
			handler.userDisconnected(user);
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
						String name = message.substring(6);
						if (isConnected(name)) {
							User user = getUserForName(name);
							if (m_timeouts.containsKey(user)) {
								m_timeouts.get(user).reset();
							}

							System.out.println("LANDiscoverer: User " + name
									+ " is already connected!");
							continue;
						}
						System.out.println("LANDiscoverer: User " + name
								+ " is not connected!");

						User user = new User(name);
						LANDriver driver = new LANDriver(packet.getAddress());
						user.addDriver(driver);
						m_timeouts.put(user, new LANTimeout(user,
								LANDiscoverer.this));

						Socket iconSock = new Socket(packet.getAddress(),
								NetworkConstants.ICON_PORT);
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
