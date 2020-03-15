package one.kroos.database;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import one.kroos.config.BotConfig;
import one.kroos.utils.EnviroHandler;

public class ImgbbSpider {
	private static final String url = "https://api.imgbb.com/1/upload";
	private static final String key = EnviroHandler.getImgbbToken();

	public static String uploadImage(BufferedImage image) {
		try {
			if (image == null)
				return BotConfig.URL_RHODES;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);

			String dataImg = Base64.getEncoder().encodeToString(baos.toByteArray());

			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(getUrl());
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("image", dataImg));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpEntity entity = httpclient.execute(httppost).getEntity();

			if (entity == null)
				return null;
			InputStream instream = entity.getContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(instream, writer, "UTF-8");
			String jsonString = writer.toString();
			JsonObject json = (JsonObject) JsonParser.parseString(jsonString);
			JsonObject data = json.get("data").getAsJsonObject();
			String url = data.get("display_url").getAsString().replace("\\/", "/");
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return BotConfig.URL_404;
		}
	}

	public static String uploadImage(String imageUrl) {
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(getUrl());
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("image", imageUrl));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpEntity entity = httpclient.execute(httppost).getEntity();

			if (entity == null)
				return null;
			InputStream instream = entity.getContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(instream, writer, "UTF-8");
			String jsonString = writer.toString();
			JsonObject json = (JsonObject) JsonParser.parseString(jsonString);
			JsonObject data = json.get("data").getAsJsonObject();
			String url = data.get("display_url").getAsString().replace("\\/", "/");
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return BotConfig.URL_404;
		}
	}

	private static String getUrl() {
		return url + "?key=" + key;
	}
}
