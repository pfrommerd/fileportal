package fileportal.net;

public interface Discoverer {
	public void addHandler(DiscoverHandler handler);

	public void removeHandler(DiscoverHandler handler);

	public void start();

	public void stop();
}
