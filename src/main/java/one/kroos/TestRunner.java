package one.kroos;

import one.kroos.utils.ImageHistogram;

public class TestRunner {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello happy world!");

		String url1 = "https://i.imgur.com/04g8Fb1.jpg", url2 = "https://i.imgur.com/0fwutOO.jpg",
				url3 = "https://i.imgur.com/j8Vi3P8.jpg";
		ImageHistogram i = new ImageHistogram();
		System.out.println(i.match(url1, url2) + ":" + i.match(url1, url3) + ":" + i.match(url3, url2));
		System.out.println(i.match(url2, url1) + ":" + i.match(url1, url1));

		System.out.println("Goodbye happy world!");
	}

}
