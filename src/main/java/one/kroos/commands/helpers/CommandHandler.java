package one.kroos.commands.helpers;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.config.BotConfig;

public abstract class CommandHandler {
	protected final static String PREFIX = BotConfig.PREFIX;

	protected String command;
	protected String[] aliases;

	protected String description;
	protected LinkedHashMap<String, String> longDescription;
	protected String usage;
	protected String example;

	protected Bot bot;

	/**
	 * Constructor
	 * 
	 * @param command         - the command string
	 * @param description     - the description of the command
	 * @param longDescription - the long description of the command {title : info}
	 * @param usage           - the usage of the command
	 * @param example         - the example of the command
	 */
	public CommandHandler(Bot bot, String command, String description, LinkedHashMap<String, String> longDescription,
			String usage, String example) {
		this.bot = bot;
		this.command = command;
		this.aliases = new String[0];
		this.description = description;
		this.longDescription = longDescription;
		this.usage = usage;
		this.example = example;

		CommandDispatcher.register(command, this);
	}

	/**
	 * Constructor
	 * 
	 * @param command         - the command string
	 * @param aliases         - aliases to the command
	 * @param description     - the description of the command
	 * @param longDescription - the long description of the command {title : info}
	 * @param usage           - the usage of the command
	 * @param example         - the example of the command
	 */
	public CommandHandler(Bot bot, String command, String[] aliases, String description,
			LinkedHashMap<String, String> longDescription, String usage, String example) {
		this.bot = bot;
		this.command = command;
		this.aliases = aliases;
		this.description = description;
		this.longDescription = longDescription;
		this.usage = usage;
		this.example = example;

		CommandDispatcher.register(command, aliases, this);
	}

	/**
	 * Executes the command, override in sub-classes
	 */
	public abstract void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild);

	/**
	 * Get the help in embedded format
	 * 
	 * @return help {@link MessageEmbed} message
	 */
	public final MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Help for " + this.command + ":");
		builder.setDescription("*" + this.description + "*");

		// Long description
		if (this.longDescription != null && !this.longDescription.isEmpty())
			for (Entry<String, String> entry : this.longDescription.entrySet())
				builder.addField(new Field("**" + entry.getKey() + ":**", entry.getValue(), false));

		// Aliases
		if (this.aliases != null && this.aliases.length != 0) {
			StringBuilder sb = new StringBuilder("```");
			for (String aliase : aliases)
				sb.append(aliase + ", ");
			sb.delete(sb.length() - 2, sb.length());
			sb.append("```");
			builder.addField(new Field("**Aliases:**", sb.toString(), false));
		}

		// Usages
		if (this.usage != null)
			builder.addField(new Field("**Usage:**", "```" + this.usage + "```", false));

		// Examples
		if (this.example != null)
			builder.addField(new Field("**Example:**", "```" + this.example + "```", false));

		return builder.build();
	}
}
