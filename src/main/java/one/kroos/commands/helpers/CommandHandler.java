package one.kroos.commands.helpers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;

public abstract class CommandHandler {
	protected String command;
	protected String[] aliases;

	protected String helpMsg;

	protected Bot bot;

	/**
	 * Constructor
	 * 
	 * @param command - the command string
	 * @param helpMsg - the help message
	 */
	public CommandHandler(Bot bot, String command, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = new String[0];
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, this);
	}

	/**
	 * Constructor
	 * 
	 * @param command - the command string
	 * @param aliases - aliases to the command
	 * @param helpMsg - the help message
	 */
	public CommandHandler(Bot bot, String command, String[] aliases, String helpMsg) {
		this.bot = bot;
		this.command = command;
		this.aliases = aliases;
		this.helpMsg = helpMsg;

		CommandDispatcher.register(command, aliases, this);
	}

	/**
	 * Executes the command
	 * 
	 * @param guild
	 */
	public abstract void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild);

	/**
	 * @return (String) help message
	 */
	public String getHelp() {
		return this.helpMsg;
	}

	public abstract MessageEmbed getHelpEmbeded();
}
