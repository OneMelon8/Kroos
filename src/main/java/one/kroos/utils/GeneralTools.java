package one.kroos.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import one.kroos.database.Emotes;

public class GeneralTools {

	public static String getBar(double current, double max, double length) {
		length -= 2;
		final double percentage = Math.min(1.0, Math.round(current / max * 100D) / 100D);
		final int hashCount = (int) (percentage * length);
		final int dashCount = (int) (length - hashCount);

		StringBuilder sb = new StringBuilder("[");
		for (int a = 0; a < hashCount; a++)
			sb.append("#");
		for (int a = 0; a <= dashCount; a++)
			sb.append("-");
		sb.append("]");
		return sb.toString();
	}

	public static String getPercentage(double current, double max) {
		return round(100D * current / max, 2) + "%";
	}

	public static String getPercentage(double decimal) {
		return round(decimal * 100, 2) + "%";
	}

	public static String signNumber(double d) {
		return (d < 0 ? "" : "+") + d;
	}

	public static String signNumberEmotes(double d, boolean displayNumber) {
		if (d == 0)
			return Emotes.EQUALS + (displayNumber ? "0" : "");
		if (d > 0)
			return Emotes.PLUS + (displayNumber ? d : "");
		return Emotes.MINUS + (displayNumber ? Math.abs(d) : "");
	}

	public static double round(double d, int places) {
		return Math.round(d * Math.pow(10, places)) / Math.pow(10, places);
	}

	public static int round(double d) {
		return (int) Math.round(d);
	}

	public static String getTime() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	}

	public static void logError(Exception ex) {
		System.out.println(getTime() + " >> " + ex.getClass().getSimpleName() + " : " + ex.getMessage());
		ex.printStackTrace();
	}
}
