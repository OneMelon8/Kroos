package one.kroos.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import one.kroos.database.gacha.GachaAssets;

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

	public static BufferedImage crop(BufferedImage original, int newWidth, int newHeight) {
		BufferedImage result = original.getSubimage((original.getWidth() - newWidth) / 2,
				(original.getHeight() - newHeight) / 2, newWidth, newHeight);
		try {
			imageToFile(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
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

	public static BufferedImage mergeInventory(BufferedImage[] images) {
		BufferedImage unknownFrame = GachaAssets.getImage(GachaAssets.FRAME_UNKNOWN);
		BufferedImage output = new BufferedImage(128 * 5, 128, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = output.createGraphics();
		for (int a = 0; a < 5; a++)
			g2d.drawImage(images[a] == null ? unknownFrame : images[a], 128 * a, 0, null);
		g2d.dispose();

		return output;
	}
}
