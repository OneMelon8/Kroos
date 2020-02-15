package one.kroos;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import one.kroos.commands.DataCommand;
import one.kroos.commands.DisplayDataCommand;
import one.kroos.commands.Help;
import one.kroos.commands.Ping;
import one.kroos.commands.helpers.ReactionDispatcher;
import one.kroos.database.Emojis;
import one.kroos.database.Emotes;
import one.kroos.utils.EnviroHandler;

public class Bot {
	private JDA api;

	public Timer timer;
	private static String version;

	public Bot(String ver) throws LoginException {
		JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(EnviroHandler.getBotToken());
		this.api = builder.build();
		version = ver;

		this.startTimer();
		this.registerHandlers();
	}

	public void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ReactionDispatcher.cleanUp();
			}
		}, 0, 1000);
	}

	public void schedule(TimerTask task, long delay) {
		this.timer.schedule(task, delay);
	}

	public void scheduleDelayedTask(TimerTask task, long delay) {
		this.timer.schedule(task, delay);
	}

	public void addListener(Object listener) {
		this.api.addEventListener(listener);
	}

	private void registerHandlers() {
		new Ping(this);
		new Help(this);

		new DataCommand(this);
		new DisplayDataCommand(this);
	}

	/*
	 * Messages
	 */
	public void sendThinkingPacket(MessageChannel channel) {
		channel.sendTyping().complete();
	}

	public Message sendMessage(String message, MessageChannel channel) {
		return channel.sendMessage(message).complete();
	}

	public Message sendMessage(MessageEmbed message, MessageChannel channel) {
		return channel.sendMessage(message).complete();
	}

	public void sendMessage(List<MessageEmbed> messages, MessageChannel channel) {
		for (MessageEmbed message : messages)
			channel.sendMessage(message).queue();
	}

	public Message editMessage(Message msg, String msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public Message editMessage(Message msg, Message msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public Message editMessage(Message msg, MessageEmbed msgNew) {
		return msg.editMessage(msgNew).complete();
	}

	public void deleteMessage(Message msg) {
		msg.delete().queue();
	}

	public void deleteMessage(String id, MessageChannel channel) {
		channel.deleteMessageById(id).queue();
	}

	/*
	 * Reactions
	 */
	public Emote getEmote(String emote) {
		return getJDA().getEmoteById(Emotes.getId(emote));
	}

	public void addReaction(Message msg, String reaction) {
		msg.addReaction(reaction).queue();
	}

	public void addReaction(Message msg, String... reactions) {
		for (String reaction : reactions)
			this.addReaction(msg, reaction);
	}

	public void addReaction(Message msg, Emote reaction) {
		msg.addReaction(reaction).queue();
	}

	public void addReaction(Message msg, Emote... reactions) {
		for (Emote reaction : reactions)
			this.addReaction(msg, reaction);
	}

	public void removeReaction(Message msg, String reaction) {
		for (MessageReaction r : msg.getReactions()) {
			if (!r.getReactionEmote().getName().equalsIgnoreCase(reaction))
				continue;
			r.removeReaction().queue();
			break;
		}
	}

	public void removeAllReactions(Message msg) {
		msg.clearReactions().queue();
	}

	/*
	 * Easy Reactions
	 */
	public void reactCheck(Message msg) {
		addReaction(msg, Emojis.CHECK);
	}

	public void reactCross(Message msg) {
		addReaction(msg, Emojis.CROSS);
	}

	public void reactQuestion(Message msg) {
		addReaction(msg, getJDA().getEmoteById(Emotes.getId(Emotes.KOKORO_WUT)));
	}

	public void reactWait(Message msg) {
		addReaction(msg, Emojis.HOUR_GLASS);
	}

	public void reactDetails(Message msg) {
		addReaction(msg, Emojis.MAGNIYFING_GLASS);
	}

	public void reactPrev(Message msg) {
		addReaction(msg, Emojis.ARROW_LEFT);
	}

	public void reactNext(Message msg) {
		addReaction(msg, Emojis.ARROW_RIGHT);
	}

	public void reactError(Message msg) {
		addReaction(msg, getJDA().getEmoteById(Emotes.getId(Emotes.KOKORO_ERROR)));
	}

	/*
	 * Attachments
	 */
	public Message sendFile(File file, MessageChannel channel) {
		return channel.sendFile(file).complete();
	}

	public Message sendFile(File file, String description, MessageChannel channel) {
		return channel.sendFile(file, description).complete();
	}

	/*
	 * Utilities
	 */
	public User getUserById(String id) {
		return this.getJDA().getUserById(id);
	}

	public String getUserDisplayName(String id, Guild guild) {
		return this.getUserDisplayName(guild.getMember(getUserById(id)));
	}

	public String getUserDisplayName(Member m) {
		return m.getEffectiveName();
	}

	public JDA getJDA() {
		return this.api;
	}

	public static String getVersion() {
		return version;
	}

}
