package one.kroos.utils;

public class LogUtil {

	public static void info(String msg) {
		System.out.println("[I] " + GeneralTools.getTime() + " >> " + msg);
	}

	public static void debug(String msg) {
		System.out.println("[D] " + GeneralTools.getTime() + " >> " + msg);
	}

	public static void warning(String msg) {
		System.out.println("[W] " + GeneralTools.getTime() + " >> " + msg);
	}

	public static void error(String msg) {
		System.err.println("[E] " + GeneralTools.getTime() + " >> " + msg);
	}

}
