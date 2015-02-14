package fileportal.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class IconServer {
	private ServerSocket m_sock;
	private BufferedImage m_icon;
	private Thread m_serveThread;

	public IconServer(BufferedImage icon) {
		try {
			m_sock = new ServerSocket(ProgramConstants.ICON_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_icon = icon;
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
					ImageIO.write(m_icon, "png", sock.getOutputStream());
					sock.close();
					System.out.println("IconServer: sent icon!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}