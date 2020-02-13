package one.kroos.config;

import java.awt.Color;

public class BotConfig {
	public static final String BOT_ID = "677380717008781322";
	public static final String ONE_ID = "233735408737976320";

	/*
	 * Reaction time-out time in ms
	 */
	public static final long REACTION_TIME_OUT = 30;
	public static final long REACTION_TIME_OUT_MS = REACTION_TIME_OUT * 1000;

	/*
	 * Messages
	 */
	public static final String PREFIX = "k ";
	public static final String[] UNKNOWN_COMMAND = { "Hmm, that's not a valid command. Try **" + PREFIX + "help**",
			"I don't understand, check out **" + PREFIX + "help**" };
	public static final String[] COMMAND_SUGGESTION = { "Do you mean by **" + PREFIX + "#**?" };

	/*
	 * Miscellaneous
	 */
	public static final Color COLOR_MISC = new Color(66, 244, 66);

	/*
	 * URLs
	 */
	public static final String URL_404 = "https://i.imgur.com/nYJtkaS.png";
	public static final String URL_RHODES = "https://i.imgur.com/mXnN6vZ.png";

}
