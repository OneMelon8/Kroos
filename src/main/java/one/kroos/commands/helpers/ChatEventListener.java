package one.kroos.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import one.kroos.config.BotConfig;

public class ChatEventListener extends ListenerAdapter {

	public static final ArrayList<String> WHITELISTED_CHANNELS = new ArrayList<String>(
			Arrays.asList("581608190907514922", "667314115827597312"));

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("KO-KO-DA-YO!");
		event.getJDA().getPresence().setPresence(Activity.playing("with @One 🍉#0001"), false);
//		App.bot.sendMessage("KO-KO-DA-YO!", App.bot.getJDA().getTextChannelById("667314115827597312"));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!channelCheck(e.getChannel()) || e.getAuthor().isBot())
			return;
		if (!e.getMessage().getContentRaw().startsWith(BotConfig.PREFIX))
			return;
		CommandDispatcher.fire(e);
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (!channelCheck(e.getChannel()) || e.getUser().isBot())
			return;
		ReactionDispatcher.fire(e.getUser(), e.getReactionEmote(), e.getMessageId(), e.getChannel(), e.getGuild());
	}

	private boolean channelCheck(MessageChannel channel) {
		return WHITELISTED_CHANNELS.contains(channel.getId());
	}
}
