package one.kroos.commands.helpers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

public interface ReactionHandler {
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild);
}
