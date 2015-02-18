package fileportal.gui;

import java.awt.Font;
import java.io.File;

import com.manager.NotificationManager;
import com.manager.QueueManager;
import com.notification.NotificationFactory;
import com.notification.NotificationFactory.Location;
import com.theme.ThemePackage;
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

		ThemePackage theme = ThemePackagePresets.cleanLight();
		theme.textTheme.title = new Font(theme.textTheme.title.getFontName(), theme.textTheme.title.getStyle(), 15);
		theme.textTheme.subtitle = new Font(theme.textTheme.subtitle.getFontName(), theme.textTheme.subtitle.getStyle(), 15);

		m_factory = new NotificationFactory(theme);
		m_manager = new QueueManager(Location.NORTHWEST);
		m_factory.addBuilder("accept", new FileNotificationBuilder());
	}

	@Override
	public void fileReceived(FileReceive receive) {
		User user = m_disc.getUserForName(receive.getFromUser());

		String message = null;
		if (receive.isIsSingleFile()) {
			message = receive.getFileName();
		} else {
			message = receive.getNumFiles() + " files";
		}

		final FileNotification note = (FileNotification) m_factory.build("accept", user.getIcon(),
				"Accept files from " + receive.getFromUser(), message);
		m_manager.addNotification(note, Time.infinite());

		boolean accept = note.getAccept();
		if (accept)
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

			@Override
			public void canceled() {
				note.hide();
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
