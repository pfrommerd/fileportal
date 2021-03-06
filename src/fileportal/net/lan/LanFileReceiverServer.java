package fileportal.net.lan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fileportal.net.FileReceive;
import fileportal.net.NetworkConstants;
import fileportal.net.ReceiverHandler;
import fileportal.net.TransferTracker;

public class LanFileReceiverServer {
	private ServerSocket m_serverSock;
	private ReceiverHandler m_handler;
	private Thread m_serverThread;

	public LanFileReceiverServer(ReceiverHandler handler) {
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
					System.out.println("FileReceiver: somebody wants to drop a file");
					Thread t = new Thread(new ClientHandler(sock));
					t.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class ClientHandler implements Runnable {
		private Socket m_sock;
		private BufferedReader m_reader;
		private PrintWriter m_writer;
		private FileReceive m_receive;

		public ClientHandler(Socket sock) {
			m_sock = sock;
			try {
				m_reader = new BufferedReader(new InputStreamReader(m_sock.getInputStream()));
				m_writer = new PrintWriter(m_sock.getOutputStream());
				m_receive = new FileReceive(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private long readFile(ZipInputStream zip, File file, long totalSize, long read, List<TransferTracker> trackers)
				throws IOException {
			FileOutputStream fos = new FileOutputStream(file);

			byte[] buffer = new byte[1024];

			int len;
			while ((len = zip.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
				read += len;

				for (TransferTracker tracker : trackers) {
					tracker.setPercentage((double) 100 * read / totalSize);
				}
			}

			fos.close();
			return read;
		}

		private void readFiles(long totalSize, File saveLoc, List<TransferTracker> trackers) throws IOException {
			ZipInputStream zip = new ZipInputStream(m_sock.getInputStream());
			ZipEntry entry = zip.getNextEntry();

			long read = 0;

			while (entry != null) {
				File saveFile = new File(saveLoc, entry.getName());

				File parent = new File(saveFile.getParent());

				if (!parent.exists())
					parent.mkdirs();

				read = readFile(zip, saveFile, totalSize, read, trackers);

				zip.closeEntry();

				entry = zip.getNextEntry();
			}
			zip.close();

			for (TransferTracker tracker : trackers) {
				tracker.setPercentage(100);
			}
		}

		@Override
		public void run() {
			try {
				String request = m_reader.readLine();
				System.out.println("FileReceiver: Got request: " + request);
				String[] parts = new String[3];
				if (request.indexOf("Single: ") == 0) {
					parts = request.substring(8).split("---div---");
				} else if (request.indexOf("Multiple: ") == 0) {
					parts = request.substring(10).split("---div---");
				}
				String user = parts[0];
				String fileNameOrNum = parts[1];
				final long size = Long.parseLong(parts[2]);

				if (request.indexOf("Single: ") == 0) {
					m_receive.setIsSingleFile(true);
					m_receive.setFileName(fileNameOrNum);
				} else if (request.indexOf("Multiple: ") == 0) {
					m_receive.setIsSingleFile(false);
					m_receive.setNumFiles(Integer.parseInt(fileNameOrNum));
				}

				m_receive.setFromUser(user);
				m_receive.setSize(size);

				m_handler.fileReceived(m_receive);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void accept(File directory) {
			m_writer.write("accept\n");
			m_writer.flush();

			try {
				try {
					readFiles(m_receive.getSize(), directory, m_receive.getTrackers());
					m_sock.close();
				} catch (IOException e) {

				}
			} finally {
				for (TransferTracker tracker : m_receive.getTrackers()) {
					tracker.canceled();
				}
				try {
					m_sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void decline() {
			m_writer.write("denied\n");
			m_writer.flush();

			try {
				m_sock.close();
			} catch (IOException e) {
				System.err.println("Failed read file...");

				// e.printStackTrace();
			}
		}
	}
}
