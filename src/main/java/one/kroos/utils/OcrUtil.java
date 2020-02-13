package one.kroos.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import one.kroos.database.RecruitmentData;

public class OcrUtil {
	private static final String URL = "https://api.ocr.space/parse/imageurl?apikey=";

	public static ArrayList<String> ocrRecruitment(String imageUrl) {
		String queryUrl = URL + EnviroHandler.getOcrToken() + "&url=" + imageUrl;
		String result;
		try {
			result = get(queryUrl);
		} catch (IOException e) {
			LogUtil.error("IOException caught when OCR-ing...");
			e.printStackTrace();
			return null;
		}
		JsonObject resultObj = (JsonObject) JsonParser.parseString(result);
		String[] parsedText = resultObj.get("ParsedResults").getAsJsonArray().get(0).getAsJsonObject().get("ParsedText")
				.getAsString().toLowerCase().split("\\r\\n");
		ArrayList<String> output = new ArrayList<String>();
		for (String text : parsedText)
			for (String tag : RecruitmentData.TAGS) {
				if (!text.equals(tag))
					continue;
				output.add(tag);
				break;
			}
		return output;
	}

	private static String get(String urlToRead) throws IOException {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Chrome");
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}

}
