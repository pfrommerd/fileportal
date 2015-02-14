package fileportal.net.lan;

import java.util.Timer;
import java.util.TimerTask;

import fileportal.net.NetworkConstants;

public class LANTimeout {
	private LANUser m_user;
	private LANDiscoverer m_disc;
	private Timer m_timer;

	public LANTimeout(LANUser user, LANDiscoverer discoverer) {
		m_user = user;
		m_disc = discoverer;
		m_timer = new Timer();
		m_timer.schedule(new DisconnectTask(),
				(int) (NetworkConstants.BROADCAST_DELAY * 2.5));
	}

	public void reset() {
		m_timer.cancel();
		m_timer = new Timer();
		m_timer.schedule(new DisconnectTask(),
				(int) (NetworkConstants.BROADCAST_DELAY * 2.5));
	}

	private class DisconnectTask extends TimerTask {
		@Override
		public void run() {
			m_disc.userTimeout(m_user);
		}
	}
}
