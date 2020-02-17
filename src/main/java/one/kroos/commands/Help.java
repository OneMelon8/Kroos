package one.kroos.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandDispatcher;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.config.BotConfig;

public class Help extends CommandHandler {

	public Help(Bot bot) {
		super(bot, "help", new String[] { "?" }, "Show the help information for Kroos", null,
				PREFIX + "help [specfic command]", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		// Send general help commands
		if (args.length != 1) {
			bot.sendMessage(getAllHelpEmbeded(), channel);
			return;
		}

		// Send help for specific commands
		String sub = args[0];
		CommandHandler commandHandler = CommandDispatcher.registeredListeners.get(sub);
		if (commandHandler == null)
			for (String key : CommandDispatcher.registeredCommands.keySet()) {
				String[] lstAliases = CommandDispatcher.registeredCommands.get(key);
				for (String aliase : lstAliases)
					if (sub.equalsIgnoreCase(aliase))
						sub = key;
			}
		commandHandler = CommandDispatcher.registeredListeners.get(sub);
		if (commandHandler == null) {
			bot.sendMessage(author.getAsMention()
					+ " Hmm, I'm not sure what that means. Check out a list of available commands with **"
					+ BotConfig.PREFIX + "help**", channel);
			return;
		}
		bot.sendMessage(commandHandler.getHelpEmbeded(), channel);
	}

	public MessageEmbed getAllHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Doctor? Do you need help? *Yawn*");
		builder.setDescription(this.description);

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-4s >> %s", "data", "Gather data from attached screenshot\n"
				+ "- NOTE: simply upload screenshot(s), no command required\n"));
		sb.append(String.format("%-4s >> %s", "disp", "Display the current data as a graph\n"));
		sb.append("```");
		builder.addField(new Field("**Recruitment Commands:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-4s >> %s", "ping", "Check my heartbeat\n"));
		sb.append(String.format("%-4s >> %s", "help", "Show this image\n"));
		sb.append("```");
		builder.addField(new Field("**Other Commands:**", sb.toString(), false));
		return builder.build();
	}
}
