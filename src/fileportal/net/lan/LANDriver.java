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

	private void recursiveZip(String root, File file, ZipOutputStream zos,
			TransferTracker tracker) throws FileNotFoundException {
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

				long length = file.length();
				long read = 0;
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
					read += 1024;

					tracker.setPercentage((double) 100 * read / length);
				}

				fis.close();
				zos.closeEntry();
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
							writer.write("Single: " + from.getName()
									+ "---div---" + files[0].getName() + "\n");
							writer.flush();
						} else {
							writer.write("Multiple: " + from.getName()
									+ "---div---" + files.length + "\n");
							writer.flush();
						}

						String response = reader.readLine();
						if (response.equals("accept")) {
							for (File f : files) {
								recursiveZip("", f, zos, tracker);
							}
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
