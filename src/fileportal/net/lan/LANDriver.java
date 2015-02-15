package fileportal.net.lan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fileportal.net.NetworkConstants;
import fileportal.net.TransferTracker;
import fileportal.net.User;
import fileportal.net.UserDriver;

public class LANDriver extends UserDriver {
	private InetAddress m_address;

	public LANDriver(InetAddress address) {
		m_address = address;
	}

	public static long sizeOf(File file) {
		if (!file.isDirectory())
			return file.length();

		long length = 0;
		for (File f : file.listFiles()) {
			length += sizeOf(f);
		}
		return length;
	}

	private long m_totalSize = 0;
	private long m_totalRead = 0;

	private void recursiveZip(String root, File file, ZipOutputStream zos,
			TransferTracker tracker) throws FileNotFoundException {
		System.out.println("LANDriver: zipping file: " + file.getName());

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for (File f : files) {
				recursiveZip(root + file.getName() + "/", f, zos, tracker);
			}
		} else {
			FileInputStream fis = new FileInputStream(file);
			ZipEntry entry = new ZipEntry(root + file.getName());
			try {
				zos.putNextEntry(entry);

				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
					m_totalRead += 1024;

					tracker.setPercentage((double) 100 * m_totalRead
							/ m_totalSize);
				}

				fis.close();
				zos.closeEntry();
				zos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public TransferTracker sendFiles(final File[] files, final User from) {
		if (getUser() == null) {
			throw new RuntimeException("No user set on driver!");
		}
		final TransferTracker tracker = new TransferTracker(0);

		try {
			final Socket sock = new Socket(m_address,
					NetworkConstants.FILE_PORT);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						PrintWriter writer = new PrintWriter(
								sock.getOutputStream());
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(sock.getInputStream()));
						ZipOutputStream zos = new ZipOutputStream(
								sock.getOutputStream());
						if (files.length == 1) {
							System.out.println(from.getName());
							writer.write("Single: " + from.getName()
									+ "---div---" + files[0].getName() + "\n");
							writer.flush();
						} else {
							writer.write("Multiple: " + from.getName()
									+ "---div---" + files.length + "\n");
							writer.flush();
						}

						String response = reader.readLine();
						if (response != null && response.equals("accept")) {
							m_totalRead = 0;

							for (File f : files) {
								m_totalSize += sizeOf(f);
							}
							for (File f : files) {
								recursiveZip("", f, zos, tracker);
							}

							m_totalRead = 0;
							m_totalSize = 0;
						}

						sock.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tracker;
	}
}
