package fileportal.net;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import fileportal.net.lan.LANBroadcaster;
import fileportal.net.lan.LANDiscoverer;

public class Main {
	public static void main(String[] args) {
		User user = new User("test2");
		LANDiscoverer disc = new LANDiscoverer(user);
		LANBroadcaster broad = new LANBroadcaster(user);
		try {
			IconServer icon = new IconServer(ImageIO.read(Main.class
					.getResource("logo.png")));
			icon.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileReceiverServer server = new FileReceiverServer(
				new ReceiverHandler() {
					@Override
					public boolean requestReceived(String user, String name) {
						return true;
					}

					@Override
					public File getFileSaveLocation(String name) {
						return new File("/Users/sam/Desktop/" + name);
					}
				});
		server.start();
		disc.addHandler(new DiscoverHandler() {
			@Override
			public void userDiscovered(User user) {
				try {
					user.sendFiles(new File[] { new File(this.getClass()
							.getResource("test.txt").toURI()) });
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
