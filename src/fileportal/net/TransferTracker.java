package fileportal.net;

import java.util.ArrayList;
import java.util.List;

public class TransferTracker {
	private int m_currentFile;
	private int m_numFiles;
	private double m_percentage;

	private List<TransferListener> m_listeners;

	public TransferTracker() {
		m_percentage = 0;
		m_listeners = new ArrayList<TransferListener>();
	}

	public void addListener(TransferListener listener) {
		m_listeners.add(listener);
	}

	public void removeListener(TransferListener listener) {
		m_listeners.remove(listener);
	}

	public int getCurrentFile() {
		return m_currentFile;
	}

	public void setCurrentFile(int file) {
		m_currentFile = file;
	}

	public int getTotalFiles() {
		return m_numFiles;
	}

	public void setTotalFiles(int files) {
		m_numFiles = files;
	}

	public boolean isFinished() {
		return m_currentFile == m_numFiles;
	}

	public double getPercentage() {
		return m_percentage;
	}

	public void setPercentage(double percentage) {
		m_percentage = percentage;

		for (TransferListener listener : m_listeners) {
			listener.percentageChanged(percentage);
		}
	}

	public void canceled() {
		for (TransferListener listener : m_listeners) {
			listener.canceled();
		}
	}

	public static interface TransferListener {
		public void percentageChanged(double newPercentage);

		public void canceled();
	}
}
