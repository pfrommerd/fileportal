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
	 * @param fromUser
	 *            the User that is sending the file
	 * @return a status tracker
	 */
	public abstract TransferTracker sendFiles(File[] files, User fromUser);
}
