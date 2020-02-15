package one.kroos.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.database.Emotes;

public class Ping extends CommandHandler {

	public Ping(Bot bot) {
		super(bot, "ping", "Check if the bot is currently active", null, PREFIX + "ping", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendThinkingPacket(channel);
		bot.sendMessage(Emotes.RODY_BEAT + " KO-KO-DA-YO! " + Math.round(bot.getJDA().getGatewayPing()) + "ms",
				channel);
	}

}
