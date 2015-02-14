package fileportal.net.lan;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import fileportal.net.ProgramConstants;
import fileportal.net.User;

public class LANUser implements User {
	private String m_name;
	private BufferedImage m_icon;
	private InetAddress m_address;

	public LANUser(String name) {
		m_name = name;
	}

	public LANUser(String name, BufferedImage icon) {
		m_name = name;
		m_icon = icon;
	}

	public void setAddress(InetAddress address) {
		m_address = address;
	}

	public InetAddress getAddress() {
		return m_address;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	@Override
	public BufferedImage getIcon() {
		return m_icon;
	}

	@Override
	public void setIcon(BufferedImage icon) {
		m_icon = icon;
	}

	@Override
	public void sendFiles(File[] files) {
		try {
			Socket sock = new Socket(m_address, ProgramConstants.FILE_PORT);
			for (File f : files) {
				System.out.println("LANUser: sending files");
				PrintWriter writer = new PrintWriter(sock.getOutputStream());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));
				writer.write(m_name + "---div---" + f.getName() + "\n");
				writer.flush();

				String response = reader.readLine();
				System.out.println("LANUser: Got response: " + response);
			}
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
