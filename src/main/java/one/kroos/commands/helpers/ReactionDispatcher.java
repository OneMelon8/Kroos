package one.kroos.commands.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import one.kroos.App;
import one.kroos.config.BotConfig;
import one.kroos.utils.LogUtil;

public class ReactionDispatcher {

	private static ConcurrentHashMap<Object[], ReactionHandler> dynamicRegisteredListeners = new ConcurrentHashMap<Object[], ReactionHandler>();
	public static HashMap<Message, Long> purgeReactions = new HashMap<Message, Long>();

	/*
	 * Register reaction for event + clean-up
	 */
	public static void register(Message msg, ReactionHandler event, String... emoteName) {
		register(msg, event, Arrays.asList(emoteName), BotConfig.REACTION_TIME_OUT);
	}

	public static void register(Message msg, ReactionHandler event, long timeoutInSeconds, String... emoteName) {
		register(msg, event, Arrays.asList(emoteName), timeoutInSeconds);
	}

	public static void register(Message msg, ReactionHandler event, List<String> emoteNames, long timeoutInSeconds) {
		final Object[] info = new Object[] { msg, new ArrayList<String>(emoteNames) };
		dynamicRegisteredListeners.put(info, event);
		App.bot.scheduleDelayedTask(new TimerTask() {
			@Override
			public void run() {
				if (!dynamicRegisteredListeners.containsKey(info))
					return;
				dynamicRegisteredListeners.remove(info);
			}
		}, timeoutInSeconds * 1000);

		// Register for clean-up
		purgeReactions.put(msg, System.currentTimeMillis() / 1000 + timeoutInSeconds);
	}

	public static void cleanUp() {
		long now = System.currentTimeMillis() / 1000;
		Message[] messages = (Message[]) purgeReactions.keySet().toArray(new Message[0]);
		for (Message m : messages) {
			if (m == null) {
				purgeReactions.remove(m);
				continue;
			}
			long purgeTime = purgeReactions.get(m);
			if (now < purgeTime)
				continue;
			App.bot.removeAllReactions(m);
			purgeReactions.remove(m);
		}
	}

	@SuppressWarnings("unchecked")
	public static void fire(User user, ReactionEmote emo, String messageId, MessageChannel channel, Guild guild) {
		if (user.isBot())
			return;
		Message msg = channel.retrieveMessageById(messageId).complete();

		// Check if bot has reacted too
		// This is ugly because JDA's .isSelf() framework is incomplete
		boolean botReacted = false;
		for (MessageReaction reaction : msg.getReactions()) {
			if (!reaction.getReactionEmote().getName().equals(emo.getName()))
				continue;
			botReacted = reaction.isSelf();
			break;
		}
		// If bot haven't, cancel event
		if (!botReacted)
			return;

		// ===================================================================================================================
		// TODO: check unused
		// ===================================================================================================================
		// Check timeout
//		long time = msg.getTimeCreated().toEpochSecond();
//		if (msg.getTimeEdited() != null)
//			time = msg.getTimeEdited().toEpochSecond();
//		boolean timedOut = System.currentTimeMillis() / 1000 - time > BotConfig.REACTION_TIME_OUT;
		// If timed out (which shouldn't happen cause cleanup, but safety), cancel event
//		if (timedOut)
//			return;

		String emoteName = emo.getName();
		LogUtil.info(user.getAsTag() + " reacted with " + emo.getName() + " on message " + msg.getContentRaw());

		// Use iterator to avoid concurrent deleting exceptions
		Iterator<Map.Entry<Object[], ReactionHandler>> iter = dynamicRegisteredListeners.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Object[], ReactionHandler> entry = iter.next();
			Object[] info = entry.getKey();
			Message storedMessage = (Message) info[0];
			ArrayList<String> emoteMatchers = (ArrayList<String>) info[1];
			if (!storedMessage.getId().equals(messageId) || !emoteMatchers.contains(emoteName))
				continue;

			// Fire event
			ReactionHandler event = entry.getValue();
			event.onReact(user, emo, msg, channel, guild);

			// Remove event registry from database
			iter.remove();
			// Remove event registry from clean-up queue
			if (purgeReactions.containsKey(msg))
				purgeReactions.remove(msg);
		}
		// No event found (shouldn't happen)

	}
}
