package one.kroos.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageTools {

	public static File imageToFile(BufferedImage img) throws IOException {
		File f = new File("./temp.png");
		ImageIO.write(img, "png", f);
		return f;
	}

	public static BufferedImage getImageFromUrl(String urlStr) throws IOException {
		final URL url = new URL(urlStr);
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Chrome");
		return ImageIO.read(connection.getInputStream());
	}
}
