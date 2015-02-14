package fileportal.net.lan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import fileportal.net.NetworkConstants;
import fileportal.net.UserDriver;

public class LANDriver extends UserDriver {
	private InetAddress m_address;

	public LANDriver(InetAddress address) {
		m_address = address;
	}

	@Override
	public boolean sendFiles(File[] files) {
		if (getUser() == null) {
			throw new RuntimeException("No user set on driver!");
		}

		try {
			Socket sock = new Socket(m_address, NetworkConstants.FILE_PORT);
			for (File f : files) {
				System.out.println("LANUser: sending files");
				PrintWriter writer = new PrintWriter(sock.getOutputStream());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));
				writer.write(getUser().getName() + "---div---" + f.getName()
						+ "\n");
				writer.flush();

				String response = reader.readLine();
				System.out.println("LANUser: Got response: " + response);

				if (response.equals("accept")) {
					System.out.println("LANUser: Writing file");
					FileInputStream fis = new FileInputStream(f);
					byte[] fileBytes = new byte[fis.available()];
					// int bytesRead = fis.read(fileByte);
					fis.read(fileBytes);
					ObjectOutputStream oos = new ObjectOutputStream(
							sock.getOutputStream());
					oos.writeObject(fileBytes);
					fis.close();
				}
			}
			sock.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
