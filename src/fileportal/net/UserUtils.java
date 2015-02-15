package fileportal.net;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class UserUtils {
	public static void s_saveUser(User u, File dir) throws IOException {
		if (!dir.exists()) dir.mkdirs();
		
		//Write the username
		PrintWriter writer = new PrintWriter(new FileWriter(new File(dir, "username")));
		writer.println(u.getName());
		writer.flush();
		writer.close();
		
		//Write the image
		ImageIO.write(u.getIcon(), "PNG", new File(dir, "icon.png"));
	}
	public static User s_readUser(File dir) throws IOException {
		if (!dir.exists()) return null;
		
		User u = new User();
		
		File file = new File(dir, "username");
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		String userName = new String(data, "UTF-8").trim();
		
		u.setName(userName);
		
		BufferedImage img = ImageIO.read(new File(dir, "icon.png"));
		u.setIcon(img);
		
		return u;
	}
}
