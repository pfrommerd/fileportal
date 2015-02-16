package fileportal.gui;

import java.io.File;

import com.manager.NotificationManager;
import com.manager.QueueManager;
import com.notification.NotificationFactory;
import com.notification.NotificationFactory.Location;
import com.theme.ThemePackagePresets;
import com.utils.Time;

import fileportal.gui.FileNotification.FileNotificationBuilder;
import fileportal.net.Discoverer;
import fileportal.net.FileReceive;
import fileportal.net.ReceiverHandler;
import fileportal.net.TransferTracker;
import fileportal.net.TransferTracker.TransferListener;
import fileportal.net.User;

public class GuiReceiverHandler implements ReceiverHandler {
	private Discoverer m_disc;
	private NotificationFactory m_factory;
	private NotificationManager m_manager;

	public GuiReceiverHandler(Discoverer disc) {
		m_disc = disc;

		m_factory = new NotificationFactory(ThemePackagePresets.cleanLight());
		m_manager = new QueueManager(Location.NORTHWEST);
		m_factory.addBuilder("accept", new FileNotificationBuilder());
	}

	@Override
	public void fileReceived(FileReceive receive) {
		User user = m_disc.getUserForName(receive.getFromUser());

		String message = null;
		if (receive.isIsSingleFile()) {
			message = receive.getFileName() + " from " + receive.getFromUser();
		} else {
			message = receive.getNumFiles() + " files from " + receive.getFromUser();
		}

		final FileNotification note = (FileNotification) m_factory.build("accept", user.getIcon(),
				"Accept files from " + receive.getFromUser(), message);
		m_manager.addNotification(note, Time.infinite());

		boolean accept = note.getAccept();
		note.showTransfer();

		TransferTracker tracker = new TransferTracker();
		tracker.addListener(new TransferListener() {
			private int lastPercent = 0;

			@Override
			public void percentageChanged(double percentage) {
				int percent = (int) percentage;
				if (percent - lastPercent < 1)
					return;

				if (percent >= 99)
					note.hide();
				else
					note.setTransferPercentage(percent);

				lastPercent = percent;
			}
		});

		receive.addProgressTracker(tracker);

		if (accept) {
			receive.accept(new File(System.getProperty("user.home") + "/Desktop/"));
		} else {
			receive.decline();
			note.hide();
		}
	}
}
