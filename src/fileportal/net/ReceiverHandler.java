package fileportal.net;

import java.io.File;

public interface ReceiverHandler {
	/**
	 * Called when someone wants to drop a File.
	 * 
	 * @param user
	 * @param name
	 * @return whether the file should be received
	 */
	public boolean requestReceived(String user, String name);

	public void fileReceived(File file);
}
