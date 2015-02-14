package fileportal.net;

import java.io.File;

public abstract class UserDriver {
	private User m_user;

	public void setUser(User user) {
		m_user = user;
	}

	public User getUser() {
		return m_user;
	}

	/**
	 * @param files
	 * @return true if success, false if not
	 */
	public abstract boolean sendFiles(File[] files);
}
