package one.kroos.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FileUtil {

	public static void write(String fileName, String key, JsonElement value) throws IOException {
		JsonObject obj;
		try {
			obj = read(fileName);
		} catch (Exception e) {
			obj = new JsonObject();
		}
		obj.add(key, value);

		write(fileName, obj.toString());
	}

	public static void write(String fileName, String value) throws IOException {
		FileWriter writer = new FileWriter("./" + fileName + ".json");
		writer.write(value);
		writer.close();
	}

	public static void write(String fileName, JsonElement value) throws IOException {
		write(fileName, value.toString());
	}

	public static JsonObject read(String fileName) throws FileNotFoundException {
		FileReader reader = new FileReader("./" + fileName + ".json");
		return (JsonObject) JsonParser.parseReader(reader);
	}

	public static JsonElement get(String fileName, String key) throws FileNotFoundException {
		return read(fileName).get(key);
	}
}
