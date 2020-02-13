package one.kroos;

import one.kroos.commands.helpers.ChatEventListener;
import one.kroos.database.SqlSpider;

public class App {

	public static Bot bot;

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Doctor~ *Yawn*...");
			bot = new Bot("1");
			bot.addListener(new ChatEventListener());

			initShutDown();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private static void initShutDown() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				SqlSpider.close();
			}
		}));
	}
}
