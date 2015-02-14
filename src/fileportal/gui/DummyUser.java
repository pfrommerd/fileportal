package fileportal.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import fileportal.net.User;

public class DummyUser implements User {
	private String m_name;
	private BufferedImage m_image;
	
	public DummyUser(String name) {
		m_name = name;
		try {
			m_image = ImageIO.read(DummyUser.class.getResourceAsStream("/unknown-user.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public BufferedImage getIcon() {
		return m_image;
	}

	@Override
	public void sendFile(File file) {
		System.out.println("Sending file: " + file.getAbsolutePath());
	}

}
