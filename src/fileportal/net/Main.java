package fileportal.net;

import fileportal.net.lan.LANBroadcaster;
import fileportal.net.lan.LANDiscoverer;
import fileportal.net.lan.LANUser;

public class Main {
	public static void main(String[] args) {
		LANDiscoverer disc = new LANDiscoverer();
		LANBroadcaster broad = new LANBroadcaster(new LANUser("test"));

		disc.start();
		broad.start();
	}
}
