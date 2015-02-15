package fileportal.net;

public class TransferTracker {
	private int m_currentFile;
	private int m_numFiles;
	private double m_percentage;

	public TransferTracker(double percentage) {
		m_percentage = percentage;
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
	}
}
