package fileportal.net.lan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fileportal.net.NetworkConstants;
import fileportal.net.UserDriver;

public class LANDriver extends UserDriver {
	private InetAddress m_address;

	public LANDriver(InetAddress address) {
		m_address = address;
	}

	private void sendSingleFile(Socket sock, File file) throws IOException {
		System.out.println("LANUser: sending single file");
		PrintWriter writer = new PrintWriter(sock.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		writer.write("Single: " + getUser().getName() + "---div---"
				+ file.getName() + "\n");
		writer.flush();

		String response = reader.readLine();
		System.out.println("LANUser: Got response: " + response);

		if (response.equals("accept")) {
			System.out.println("LANUser: Writing file");
			FileInputStream fis = new FileInputStream(file);
			ZipEntry entry = new ZipEntry(file.getName());
			ZipOutputStream out = new ZipOutputStream(sock.getOutputStream());
			out.putNextEntry(entry);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			fis.close();
			out.closeEntry();
			out.close();
		}
		writer.close();
		reader.close();
	}

	private void sendMultipleFiles(Socket sock, File[] files)
			throws IOException {
		System.out.println("LANUser: sending multi file");
		PrintWriter writer = new PrintWriter(sock.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				sock.getInputStream()));
		writer.write("Multiple: " + getUser().getName() + "---div---"
				+ files.length + "\n");
		writer.flush();

		String response = reader.readLine();
		System.out.println("LANUser: Got response: " + response);

		if (response.equals("accept")) {
			System.out.println("LANUser: Writing multifiles");

			ZipOutputStream out = new ZipOutputStream(sock.getOutputStream());

			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				ZipEntry entry = new ZipEntry(file.getName());
				FileInputStream fis = new FileInputStream(file);

				out.putNextEntry(entry);

				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}

				fis.close();

				out.closeEntry();
			}
		}
		writer.close();
		reader.close();
	}

	@Override
	public boolean sendFiles(File[] files) {
		if (getUser() == null) {
			throw new RuntimeException("No user set on driver!");
		}

		try {
			Socket sock = new Socket(m_address, NetworkConstants.FILE_PORT);
			if (files.length == 1) {
				sendSingleFile(sock, files[0]);
			} else {
				sendMultipleFiles(sock, files);
			}
			sock.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
