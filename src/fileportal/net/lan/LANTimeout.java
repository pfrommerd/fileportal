package fileportal.net.lan;

import java.util.Timer;
import java.util.TimerTask;

import fileportal.net.NetworkConstants;
import fileportal.net.User;

public class LANTimeout {
	private User m_user;
	private LANDiscoverer m_disc;
	private Timer m_timer;

	public LANTimeout(User user, LANDiscoverer discoverer) {
		m_user = user;
		m_disc = discoverer;
		m_timer = new Timer();
		m_timer.schedule(
				new DisconnectTask(),
				(int) (NetworkConstants.BROADCAST_DELAY * NetworkConstants.TIMEOUT_MULTIPLE));
	}

	public void reset() {
		m_timer.cancel();
		m_timer = new Timer();
		m_timer.schedule(
				new DisconnectTask(),
				(int) (NetworkConstants.BROADCAST_DELAY * NetworkConstants.TIMEOUT_MULTIPLE));
	}

	private class DisconnectTask extends TimerTask {
		@Override
		public void run() {
			m_disc.userTimeout(m_user);
		}
	}
}
