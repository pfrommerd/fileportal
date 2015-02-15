package fileportal.net;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import fileportal.net.lan.LANBroadcaster;
import fileportal.net.lan.LANDiscoverer;
import fileportal.net.lan.LANIconServer;

public class Main {
	public static void main(String[] args) {
		User user = new User("test2");
		try {
			user.setIcon(ImageIO.read(Main.class.getResource("logo.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		LANDiscoverer disc = new LANDiscoverer(new User("foobar"));
		LANBroadcaster broad = new LANBroadcaster(user);
		LANIconServer icon = new LANIconServer(user);
		icon.start();

		FileReceiverServer server = new FileReceiverServer(
				new ReceiverHandler() {
					@Override
					public boolean shouldAccept(String user, String name) {
						return true;
					}

					@Override
					public File getFileSaveLocation(String name) {
						return new File("/Users/sam/Desktop/" + name);
					}

					@Override
					public File getFolderSaveLocation() {
						return new File("/Users/sam/Desktop/");
					}

					@Override
					public boolean shouldAccept(String user, int fileNum) {
						return true;
					}
				});
		server.start();
		disc.addHandler(new DiscoverHandler() {
			@Override
			public void userDiscovered(User user) {
				System.out.println("User discovered");
				try {
					File test = new File(this.getClass()
							.getResource("tron.dmg").toURI());
					File logo = new File(this.getClass()
							.getResource("logo.png").toURI());
					TransferTracker track = user.sendFiles(new File[] { test });
					while (!track.isFinished()) {
						System.out.println(track.getPercentage());
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void userDisconnected(User user) {

			}
		});

		disc.start();
		broad.start();
	}
}
