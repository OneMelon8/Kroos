package one.kroos.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import one.kroos.App;
import one.kroos.config.BotConfig;
import one.kroos.utils.LogUtil;
import one.kroos.utils.StringTools;

public class CommandDispatcher {

	public static HashMap<String, String[]> registeredCommands = new HashMap<String, String[]>();
	public static HashMap<String, CommandHandler> registeredListeners = new HashMap<String, CommandHandler>();

	private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

	/**
	 * Tries to process the command (if registered)
	 * 
	 * @param e - the {@link MessageReceivedEvent} instance
	 */
	public static void fire(MessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();
		if (!msg.startsWith(BotConfig.PREFIX))
			return;

		String[] msgArr = msg.substring(BotConfig.PREFIX.length()).split(" ");
		String userCmd = msgArr[0].toLowerCase();
		LogUtil.info(e.getAuthor().getAsTag() + " executed " + msg);

		// Check cool-down to stop spamming
		String authorId = e.getAuthor().getId();
		int cooldownTime = 2500; // Milliseconds
		if (cooldown.containsKey(authorId)) {
			long msLeft = cooldown.get(authorId) + cooldownTime - System.currentTimeMillis();
			if (msLeft > 0) {
				App.bot.reactWait(e.getMessage());
				return;
			}
		}
		cooldown.put(authorId, System.currentTimeMillis());

		String[] args = Arrays.copyOfRange(msgArr, 1, msgArr.length);

		// Loop thru all the registered commands
		for (String cmd : registeredCommands.keySet()) {
			ArrayList<String> aliases = new ArrayList<String>(Arrays.asList(registeredCommands.get(cmd)));
			if ((userCmd.equals(cmd) || aliases.contains(userCmd))) {
				// Dispatch command
				registeredListeners.get(cmd).onCommand(e.getAuthor(), userCmd, args, e.getMessage(), e.getChannel(),
						e.getGuild());
				return;
			}
		}
		// Disabled cause.. spam i guess?
		// unknownCommand(userCmd, event);
		App.bot.reactQuestion(e.getMessage());
//		ReactionDispatcher.register(e.getMessage(), new Help(App.bot), "kokoron_wut");
	}

	/**
	 * Register a command for the bot <br>
	 * Commands that are not registered will not be executed!
	 * 
	 * @param command  - command to register
	 * @param instance
	 */
	public static void register(String command, CommandHandler instance) {
		registeredCommands.put(command, new String[0]);
		registeredListeners.put(command, instance);
	}

	/**
	 * Register a command for the bot <br>
	 * Commands that are not registered will not be executed!
	 * 
	 * @param command - command to register
	 * @param aliases - command's aliases
	 */
	public static void register(String command, String[] aliases, CommandHandler instance) {
		registeredCommands.put(command, aliases);
		registeredListeners.put(command, instance);

	}

	// UNUSED FUNCTIONALITY
	@SuppressWarnings("unused")
	private static void unknownCommand(String cmd, User author, String command, String[] args, Message message,
			MessageChannel channel, Guild guild) {
		// No command found => attempt to auto correct
		Object[] help = helpAttempt(cmd);
		String attempt = (String) help[0];
		double sim = (Double) help[1];
		if (sim >= 0.85) {
			registeredListeners.get(attempt).onCommand(author, command, args, message, channel, guild);
			return;
		}
		// No attempts are valid => send help message
		String helpMsg = BotConfig.UNKNOWN_COMMAND[new Random().nextInt(BotConfig.UNKNOWN_COMMAND.length)];
		if (sim >= 0.65)
			helpMsg = BotConfig.COMMAND_SUGGESTION[new Random().nextInt(BotConfig.COMMAND_SUGGESTION.length)]
					.replace("#", attempt);
		App.bot.sendMessage(helpMsg, channel);
	}

	/**
	 * Using the string similarity function to try to "save" the typo
	 * 
	 * @param userCmd - command that user typed
	 * @return Object[] of [(String) "corrected" command, (double) similarity]
	 */
	private static Object[] helpAttempt(String userCmd) {
		String bestMatch = null;
		double bestDist = 0;
		for (String cmd : registeredCommands.keySet()) {
			double dist = StringTools.getSimilarity(userCmd, cmd);
			if (dist > bestDist) {
				bestDist = dist;
				bestMatch = cmd;
			}
			for (String alias : registeredCommands.get(cmd)) {
				dist = StringTools.getSimilarity(userCmd, alias);
				if (dist > bestDist) {
					bestDist = dist;
					bestMatch = cmd;
				}
			}
		}
		return new Object[] { bestMatch, bestDist };
	}
}
