package fileportal.net.lan;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

import fileportal.net.NetworkConstants;
import fileportal.net.User;

public class LANIconServer {
	private ServerSocket m_sock;
	private User m_user;
	private Thread m_serveThread;

	public LANIconServer(User user) {
		try {
			m_sock = new ServerSocket(NetworkConstants.ICON_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_user = user;
	}

	public void start() {
		m_serveThread = new Thread(new IconSender());
		m_serveThread.start();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		m_serveThread.stop();
	}

	private class IconSender implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Socket sock = m_sock.accept();
					ImageIO.write(m_user.getIcon(), "png",
							sock.getOutputStream());
					sock.close();
					System.out.println("IconServer: sent icon!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}