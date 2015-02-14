package fileportal.net;

import java.awt.image.BufferedImage;
import java.io.File;

public interface User {
	public String getName();
	public BufferedImage getIcon();
	public void sendFile(File file);
}
