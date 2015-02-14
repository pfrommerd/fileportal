package fileportal.net;

import java.awt.image.BufferedImage;
import java.io.File;

public interface User {
	public String getName();

	public void setName(String name);

	public BufferedImage getIcon();

	public void setIcon(BufferedImage icon);

	public void sendFiles(File[] files);
}
