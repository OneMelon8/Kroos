package one.kroos.utils;

public class EnviroHandler {

	public static String getBotToken() {
		return System.getenv("token");
	}

	public static String getOcrToken() {
		return System.getenv("ocr");
	}

	public static String getImgbbToken() {
		return System.getenv("imgbb");
	}

	/**
	 * Gets the SQL database's account information
	 * 
	 * @return string array of [username, password]
	 */
	public static String[] getSqlAccount() {
		return System.getenv("sql").split(":");
	}

}
