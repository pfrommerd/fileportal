package fileportal.net;

import java.io.File;

public interface ReceiverHandler {
	/**
	 * Called when someone wants to drop a file.
	 * 
	 * @param user
	 * @param name
	 * @return whether the file should be received
	 */
	public boolean requestReceived(String user, String name);

	/**
	 * Called when someone wants to drop a number of files.
	 * 
	 * @param user
	 * @param fileNum
	 * @return
	 */
	public boolean requestReceived(String user, int fileNum);

	public File getFileSaveLocation(String name);

	public File getFolderSaveLocation();
}
