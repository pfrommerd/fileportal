package fileportal.net;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class User {
	private String m_name;
	private BufferedImage m_icon;

	private List<UserDriver> m_drivers = new ArrayList<UserDriver>();

	public User(String name) {
		m_name = name;
	}

	public User(String name, BufferedImage icon) {
		m_name = name;
		m_icon = icon;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public BufferedImage getIcon() {
		return m_icon;
	}

	public void setIcon(BufferedImage icon) {
		m_icon = icon;
	}

	public void addDriver(UserDriver driver) {
		driver.setUser(this);
		m_drivers.add(driver);
	}

	public void removeDriver(UserDriver driver) {
		driver.setUser(null);
		m_drivers.remove(driver);
	}

	/**
	 * @param files
	 * @return a TransferTracker
	 */
	public TransferTracker sendFiles(File[] files) {
		if (m_drivers.size() == 0) {
			return null;
		}
		return m_drivers.get(0).sendFiles(files);
	}
}
