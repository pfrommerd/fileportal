package fileportal.net;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import fileportal.net.lan.LANBroadcaster;
import fileportal.net.lan.LANDiscoverer;
import fileportal.net.lan.LANUser;

public class Main {
	public static void main(String[] args) {
		LANDiscoverer disc = new LANDiscoverer();
		LANBroadcaster broad = new LANBroadcaster(new LANUser("test"));
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
					public void fileReceived(File file) {

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
