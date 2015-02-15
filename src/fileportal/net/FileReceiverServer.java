package fileportal.net;

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

	public class ClientHandler implements Runnable {
		private Socket m_sock;
		private BufferedReader m_reader;
		private PrintWriter m_writer;
		private FileReceive m_receive;

		public ClientHandler(Socket sock) {
			m_sock = sock;
			try {
				m_reader = new BufferedReader(new InputStreamReader(
						m_sock.getInputStream()));
				m_writer = new PrintWriter(m_sock.getOutputStream());
				m_receive = new FileReceive(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private long readFile(ZipInputStream zip, File file, long totalSize,
				long read, List<TransferTracker> trackers) throws IOException {
			FileOutputStream fos = new FileOutputStream(file);

			byte[] buffer = new byte[1024];

			int len;
			while ((len = zip.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
				read += 1024;

				for (TransferTracker tracker : trackers) {
					tracker.setPercentage((double) 100 * read / totalSize);
				}
				System.out.println("Read: " + read);
				System.out.println("Setting percentage: " + (double) 100 * read
						/ totalSize);
			}

			fos.close();
			return read;
		}

		private void readFiles(long totalSize, File saveLoc,
				List<TransferTracker> trackers) throws IOException {
			ZipInputStream zip = new ZipInputStream(m_sock.getInputStream());
			ZipEntry entry = zip.getNextEntry();

			long read = 0;

			while (entry != null) {
				File saveFile = new File(saveLoc, entry.getName());
				System.out.println("FileReceiver: Unzipping: "
						+ entry.getName());
				System.out.println("Total bytes read: " + read);

				File parent = new File(saveFile.getParent());

				if (!parent.exists())
					parent.mkdirs();

				read = readFile(zip, saveFile, totalSize, read, trackers);

				zip.closeEntry();

				entry = zip.getNextEntry();
			}
			zip.close();
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
				readFiles(m_receive.getSize(), directory,
						m_receive.getTrackers());
				m_sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void decline() {
			m_writer.write("denied\n");
			m_writer.flush();

			try {
				m_sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * public class ClientHandler implements Runnable { private Socket m_sock;
	 * 
	 * public ClientHandler(Socket sock) { m_sock = sock; }
	 * 
	 * private long readFile(ZipInputStream zip, File file, long totalSize, long
	 * read, TransferTracker tracker) throws IOException { FileOutputStream fos
	 * = new FileOutputStream(file);
	 * 
	 * byte[] buffer = new byte[1024];
	 * 
	 * int len; while ((len = zip.read(buffer)) > 0) { fos.write(buffer, 0,
	 * len); read += 1024;
	 * 
	 * tracker.setPercentage((double) 100 * read / totalSize);
	 * System.out.println("Read: " + read);
	 * System.out.println("Setting percentage: " + tracker.getPercentage()); }
	 * 
	 * fos.close(); return read; }
	 * 
	 * private void readFiles(long totalSize, TransferTracker tracker) throws
	 * IOException { File saveLoc = m_handler.getFolderSaveLocation();
	 * 
	 * ZipInputStream zip = new ZipInputStream(m_sock.getInputStream());
	 * ZipEntry entry = zip.getNextEntry();
	 * 
	 * long read = 0;
	 * 
	 * while (entry != null) { File saveFile = new File(saveLoc,
	 * entry.getName()); System.out.println("FileReceiver: Unzipping: " +
	 * entry.getName()); System.out.println("Total bytes read: " + read);
	 * 
	 * File parent = new File(saveFile.getParent());
	 * 
	 * if (!parent.exists()) parent.mkdirs();
	 * 
	 * read = readFile(zip, saveFile, totalSize, read, tracker);
	 * 
	 * zip.closeEntry();
	 * 
	 * entry = zip.getNextEntry(); } zip.close(); }
	 * 
	 * @Override public void run() { try { BufferedReader reader = new
	 * BufferedReader( new InputStreamReader(m_sock.getInputStream()));
	 * PrintWriter writer = new PrintWriter(m_sock.getOutputStream());
	 * 
	 * String request = reader.readLine();
	 * System.out.println("FileReceiver: Got request: " + request); boolean
	 * accept = false; String[] parts = new String[3]; if
	 * (request.indexOf("Single: ") == 0) { parts =
	 * request.substring(8).split("---div---"); } else if
	 * (request.indexOf("Multiple: ") == 0) { parts =
	 * request.substring(10).split("---div---"); } String user = parts[0];
	 * String fileNameOrNum = parts[1]; final long size =
	 * Long.parseLong(parts[2]); if (request.indexOf("Single: ") == 0) { accept
	 * = m_handler.shouldAccept(user, fileNameOrNum); } else if
	 * (request.indexOf("Multiple: ") == 0) { accept =
	 * m_handler.shouldAccept(user, Integer.parseInt(fileNameOrNum)); }
	 * 
	 * if (accept) { writer.write("accept\n"); writer.flush();
	 * 
	 * final TransferTracker tracker = new TransferTracker(0);
	 * m_handler.setProgressTracker(tracker);
	 * 
	 * readFiles(size, tracker); } else { writer.write("denied\n");
	 * writer.flush(); }
	 * 
	 * m_sock.close(); } catch (IOException e) { e.printStackTrace(); } } }
	 */
}
