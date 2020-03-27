package one.kroos.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.utils.LogUtil;

public class Echo extends CommandHandler {

	public Echo(Bot bot) {
		super(bot, "echo", "Testing command", null, PREFIX + "echo", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		LogUtil.debug("Echoed by " + author.getAsTag() + ": " + message.getContentRaw());
		bot.reactCheck(message);
	}

}
