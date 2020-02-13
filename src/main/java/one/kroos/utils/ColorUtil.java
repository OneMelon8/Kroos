package one.kroos.utils;

import java.awt.Color;

public class ColorUtil {

	public static Color fromHex(String color) {
		color = color.replace("#", "");
		return new Color(Integer.valueOf(color.substring(0, 2), 16), Integer.valueOf(color.substring(2, 4), 16),
				Integer.valueOf(color.substring(4, 6), 16));
	}

}