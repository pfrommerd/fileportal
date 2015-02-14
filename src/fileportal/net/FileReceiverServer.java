package fileportal.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiverServer {
	private ServerSocket m_sock;
	private ReceiverHandler m_handler;
	private Thread m_serverThread;

	public FileReceiverServer(ReceiverHandler handler) {
		m_handler = handler;

		try {
			m_sock = new ServerSocket(ProgramConstants.FILE_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		m_serverThread = new Thread(new Server());
		m_serverThread.start();
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		m_serverThread.stop();
	}

	private class Server implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Socket sock = m_sock.accept();
					System.out
							.println("FileReceiver: somebody wants to drop a file");
					Thread t = new Thread(new ClientHandler(sock));
					t.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ClientHandler implements Runnable {
		private Socket m_sock;

		public ClientHandler(Socket sock) {
			m_sock = sock;
		}

		@Override
		public void run() {
			try {
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						m_sock.getInputStream()));
				PrintWriter writer = new PrintWriter(m_sock.getOutputStream());

				String request = buf.readLine();
				String[] parts = request.split("---div---");
				String user = parts[0];
				String fileName = parts[1];
				System.out.println("FileReceiver: somebody gave this request: "
						+ request);

				boolean accept = m_handler.requestReceived(user, fileName);
				if (accept) {
					writer.write("accept\n");
					writer.flush();
					System.out.println("FileReceiver: Accepting file");
				} else {
					writer.write("denied\n");
					writer.flush();
					System.out.println("FileReceiver: Denying file");
				}

				m_sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
