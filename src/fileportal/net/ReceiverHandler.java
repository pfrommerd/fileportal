package fileportal.net;

public interface ReceiverHandler {
	public void fileReceived(FileReceive receive);

	/**
	 * Called when someone wants to drop a file.
	 * 
	 * @param user
	 * @param name
	 * @return whether the file should be received
	 */
	// public boolean shouldAccept(String user, String name);

	/**
	 * Called when someone wants to drop a number of files.
	 * 
	 * @param user
	 * @param fileNum
	 * @return
	 */
	// public boolean shouldAccept(String user, int fileNum);

	/*
	 * public File getFileSaveLocation(String name);
	 * 
	 * public File getFolderSaveLocation();
	 * 
	 * public void setProgressTracker(TransferTracker tracker);
	 */
}
