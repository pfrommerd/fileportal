package fileportal.net;

public interface Discoverer {
	public void addHandler(DiscoverHandler handler);

	public void removeHandler(DiscoverHandler handler);

	/**
	 * @param userName
	 * @return whether there is a user with this name connected
	 */
	public boolean isConnected(String userName);

	public User getUserForName(String userName);

	public void start();

	public void stop();
}
