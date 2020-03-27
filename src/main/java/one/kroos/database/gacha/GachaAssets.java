package one.kroos.database.gacha;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GachaAssets {
	public static final String PREFIX_PATH = "./assets/gacha/";
	public static final String TKON = "tkon.png";

	public static final String BACKGROUND = "bg.png";
	public static final String BACKGROUND_4 = "bg_4.png";
	public static final String BACKGROUND_5 = "bg_5.png";
	public static final String BACKGROUND_6 = "bg_6.png";

	public static final String FRAME = "SilverFrame.png";
	public static final String FRAME_4 = "Silver4Frame.png";
	public static final String FRAME_5 = "GoldFrame.png";
	public static final String FRAME_6 = "RainbowFrame.png";
	public static final String FRAME_UNKNOWN = "UnknownFrame.png";

	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(new File(PREFIX_PATH + name));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
