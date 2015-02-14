package fileportal.net.lan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;

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
	public BufferedImage getIcon() {
		return m_icon;
	}

	@Override
	public void sendFile(File file) {

	}

}
