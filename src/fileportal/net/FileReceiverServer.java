package fileportal.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileReceiverServer {
	private ServerSocket m_serverSock;
	private ReceiverHandler m_handler;
	private Thread m_serverThread;

	public FileReceiverServer(ReceiverHandler handler) {
		m_handler = handler;

		try {
			m_serverSock = new ServerSocket(NetworkConstants.FILE_PORT);
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
					Socket sock = m_serverSock.accept();
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

		private void readSingleFile(String name) throws IOException {
			File saveLoc = m_handler.getFileSaveLocation(name);

			ZipInputStream zip = new ZipInputStream(m_sock.getInputStream());

			ZipEntry entry = zip.getNextEntry();

			while (entry != null) {
				FileOutputStream fos = new FileOutputStream(saveLoc);

				byte[] buffer = new byte[1024];

				int len;
				while ((len = zip.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
			}
			zip.close();
		}

		private void readMultipleFiles() throws IOException {
			File saveLoc = m_handler.getFolderSaveLocation();

			ZipInputStream zip = new ZipInputStream(m_sock.getInputStream());
			ZipEntry entry = zip.getNextEntry();

			while (entry != null) {
				System.out.println("got entry: " + entry);
				File saveFile = new File(saveLoc, entry.getName());
				FileOutputStream fos = new FileOutputStream(saveFile);

				byte[] buffer = new byte[1024];

				int len;
				while ((len = zip.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				zip.closeEntry();

				entry = zip.getNextEntry();
			}
			zip.close();
		}

		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(m_sock.getInputStream()));
				PrintWriter writer = new PrintWriter(m_sock.getOutputStream());

				String request = reader.readLine();
				if (request.indexOf("Single: ") == 0) {
					String[] parts = request.substring(8).split("---div---");
					String user = parts[0];
					String fileName = parts[1];
					System.out
							.println("FileReceiver: somebody gave this request: "
									+ request);

					boolean accept = m_handler.shouldAccept(user, fileName);
					if (accept) {
						writer.write("accept\n");
						writer.flush();

						readSingleFile(fileName);
					} else {
						writer.write("denied\n");
						writer.flush();
						System.out.println("FileReceiver: Denying file");
					}
				} else if (request.indexOf("Multiple: ") == 0) {
					String[] parts = request.substring(10).split("---div---");
					String user = parts[0];
					String fileNum = parts[1];
					System.out
							.println("FileReceiver: somebody gave this request: "
									+ request);

					boolean accept = m_handler.shouldAccept(user, fileNum);
					if (accept) {
						writer.write("accept\n");
						writer.flush();

						System.out.println("Reading multiple files");

						readMultipleFiles();
					} else {
						writer.write("denied\n");
						writer.flush();
						System.out.println("FileReceiver: Denying file");
					}
				}

				m_sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
