package one.kroos.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;

import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.commands.helpers.ReactionDispatcher;
import one.kroos.commands.helpers.ReactionHandler;
import one.kroos.config.BotConfig;
import one.kroos.database.Emojis;
import one.kroos.database.ImgbbSpider;
import one.kroos.database.RecruitmentData;
import one.kroos.database.SqlSpider;
import one.kroos.utils.GeneralTools;
import one.kroos.utils.ImageHistogram;
import one.kroos.utils.LogUtil;
import one.kroos.utils.OcrUtil;

public class DataCommand extends CommandHandler implements ReactionHandler {

	private static final String RECRUIT_IMAGE_URL = "https://i.imgur.com/mzVdsiP.jpg";

	public DataCommand(Bot bot) {
		super(bot, "data", "Gather data for the recruitment function");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		List<Attachment> attachments = message.getAttachments();
		if (attachments.isEmpty() || !attachments.get(0).isImage()) {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}
		String url = attachments.get(0).getProxyUrl();

		bot.sendMessage("Let me see what's in this image, hmm...", channel);
		ArrayList<String> result = OcrUtil.ocrRecruitment(url);
		if (result.isEmpty()) {
			bot.sendMessage("I don't see any tags..?", channel);
			return;
		}

		message = bot.sendMessage("I found " + RecruitmentData.getDisplayName(result) + " in the image. Is that right?",
				channel);
		bot.reactCheck(message);
		bot.reactCross(message);

		// Register dynamic reaction handler
		ReactionDispatcher.register(message, this, Emojis.CHECK, Emojis.CROSS);
	}

	public static boolean isDataIntent(Message message) {
		List<Attachment> attachments = message.getAttachments();
		if (attachments.isEmpty() || !attachments.get(0).isImage())
			return false;
		String url = ImgbbSpider.uploadImage(attachments.get(0).getProxyUrl());
		try {
			double sim = new ImageHistogram().match(RECRUIT_IMAGE_URL, url);
			LogUtil.info("Recruitment data similarity: " + GeneralTools.getPercentage(sim));
			return sim > 0.97;
		} catch (IOException e) {
			LogUtil.error("IOException caught when comparing recruiting images...");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		bot.removeAllReactions(message);

		if (emote.getName().equals(Emojis.CROSS)) {
			bot.sendMessage("Hmm, I must have closed my eyes on that one, try again?", channel);
			return;
		}
		// Only possibility here is Emojis.CHECK because there's only 2 reaction emojis
		String text = message.getContentDisplay();
		String[] tagArr = text.split("\\*\\*");
		for (String tag : tagArr) {
			if (!RecruitmentData.TAGS_DISPLAY.contains(tag))
				continue;

			// Upload data for each tag
			SqlSpider.update("UPDATE ArknightsRecruit SET count=count+1 WHERE `index`="
					+ RecruitmentData.TAGS_DISPLAY.indexOf(tag));
			LogUtil.info("Updated " + tag + " in the database!");
		}
		bot.sendMessage("Database updated, thank you for contributing!", channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Arknights Recruitment Data");
		builder.setDescription(this.helpMsg);
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```\n"
				+ "Be sure to attach a screenshot of the recruitment interface", false));
		return builder.build();
	}

}
