package one.kroos.utils;

import java.awt.Graphics2D;
import java.awt.Image;
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

	public static BufferedImage getImageFromUrl(String urlStr) {
		try {
			final URL url = new URL(urlStr);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Chrome");
			return ImageIO.read(connection.getInputStream());
		} catch (IOException e) {
			return null;
		}
	}

	public static BufferedImage resize(BufferedImage img, double scale) {
		return resize(img, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
	}

	public static BufferedImage resize(BufferedImage img, int width, int height) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}
}
