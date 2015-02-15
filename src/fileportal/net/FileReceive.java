package fileportal.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fileportal.net.FileReceiverServer.ClientHandler;

public class FileReceive {
	private List<TransferTracker> m_trackers = new ArrayList<TransferTracker>();

	private String m_fromUser;

	private long m_size;
	private boolean m_isSingleFile;
	private String m_name;
	private int m_numFiles;

	private ClientHandler m_handler;

	public FileReceive(ClientHandler handler) {
		m_handler = handler;
	}

	public void accept(File directory) {
		m_handler.accept(directory);
	}

	public void decline() {
		m_handler.decline();
	}

	public String getFromUser() {
		return m_fromUser;
	}

	public void setFromUser(String fromUser) {
		m_fromUser = fromUser;
	}

	public long getSize() {
		return m_size;
	}

	public void setSize(long size) {
		m_size = size;
	}

	public boolean isIsSingleFile() {
		return m_isSingleFile;
	}

	public void setIsSingleFile(boolean m_isSingleFile) {
		this.m_isSingleFile = m_isSingleFile;
	}

	public String getFileName() {
		return m_name;
	}

	public void setFileName(String m_name) {
		this.m_name = m_name;
	}

	public int getNumFiles() {
		return m_numFiles;
	}

	public void setNumFiles(int m_numFiles) {
		this.m_numFiles = m_numFiles;
	}

	public void addProgressTracker(TransferTracker tracker) {
		m_trackers.add(tracker);
	}

	public void removeProgressTracker(TransferTracker tracker) {
		m_trackers.remove(tracker);
	}

	public List<TransferTracker> getTrackers() {
		return m_trackers;
	}
}
