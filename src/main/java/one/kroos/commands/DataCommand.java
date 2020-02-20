package one.kroos.commands;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.commands.helpers.ReactionHandler;
import one.kroos.database.Emojis;
import one.kroos.database.ImgbbSpider;
import one.kroos.database.RecruitDatabase;
import one.kroos.database.RecruitTag;
import one.kroos.database.SqlSpider;
import one.kroos.utils.CombinationUtil;
import one.kroos.utils.GeneralTools;
import one.kroos.utils.ImageHistogram;
import one.kroos.utils.LogUtil;
import one.kroos.utils.OcrUtil;

public class DataCommand extends CommandHandler implements ReactionHandler {

	private static final String RECRUIT_IMAGE_URL = "https://i.imgur.com/mzVdsiP.jpg";

	public DataCommand(Bot bot) {
		super(bot, "data", "Gather data for the recruitment function", null,
				PREFIX + "data\nOR simply upload a relevant screenshot", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		List<Attachment> attachments = message.getAttachments();
		if (attachments.isEmpty() || !attachments.get(0).isImage()) {
			bot.sendMessage(getHelpEmbeded(), channel);
			return;
		}

		for (int a = 0; a < attachments.size(); a++) {
			// Only accepts recruit images
			if (!attachments.get(a).isImage()) {
				bot.sendMessage("Invalid file type in attachment #" + (a + 1), channel);
				continue;
			}
			String url = attachments.get(a).getProxyUrl();
			if (!isRecruitmentScreenshot(url)) {
				bot.sendMessage(
						"It seems that image #" + (a + 1) + " is not a screenshot of the recruitment interface..?",
						channel);
				continue;
			}

			ArrayList<RecruitTag> result = OcrUtil.ocrRecruitment(url);
			if (result == null || result.isEmpty()) {
				bot.sendMessage("I don't see any tags in image #" + (a + 1) + "..?", channel);
				continue;
			}

			// Upload tag data into database
			StringBuilder sb = new StringBuilder();
			for (RecruitTag tag : result) {
				// Upload data for each tag
				SqlSpider.update("UPDATE ArknightsRecruit SET count=count+1 WHERE `index`=" + tag.getIndex());
				LogUtil.info("Updated " + tag.getDisplayName() + " in the database!");

				// Build string
				sb.append("**" + tag.getDisplayName() + "**, ");
			}
			sb.delete(sb.length() - 2, sb.length());

			// Upload data for bulk tags
			SqlSpider.execute("INSERT INTO RecruitData(`data`) VALUES (\"" + sb.toString().replace("*", "") + "\")");
			LogUtil.info("Updated bulk recruit tags in the database!");
			bot.sendMessage("Database updated for image #" + (a + 1) + " with " + sb.toString() + "!", channel);

			MessageEmbed emb = getRecruitResultEmbeded(result, a + 1);
			if (emb == null)
				continue;
			bot.sendMessage(emb, channel);
		}
		SqlSpider.close();
	}

	private static MessageEmbed getRecruitResultEmbeded(ArrayList<RecruitTag> tags, int index) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setAuthor("Warning: Rare tag combination found in recruitment!");
		builder.setDescription("*Operator(s) of 4☆ or higher found in image #" + index + "*");
		boolean doReturn = false;

		for (int a = tags.size(); a > 0; a--) {
			ArrayList<int[]> combos = new CombinationUtil(tags.size()).getCombination(a);
			for (int[] combo : combos) {
				ArrayList<RecruitTag> comboTags = new ArrayList<RecruitTag>();
				for (int c : combo)
					comboTags.add(tags.get(c));
				ArrayList<String> operators = RecruitDatabase.getIntersection(comboTags);
				if (operators.isEmpty())
					continue;
				if (RecruitDatabase.hasLowRarityOperators(operators))
					continue;

				builder.addField(RecruitTag.getDisplayNames(comboTags) + ":", "> " + stringArrayListToString(operators),
						false);
				doReturn = true;
			}
		}

		if (!doReturn)
			return null;
		return builder.build();
	}

	private static String stringArrayListToString(ArrayList<String> arr) {
		if (arr.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		for (String s : arr)
			sb.append(s + ", ");
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	/*
	 * Unused because of direct database implementation
	 */
	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		bot.removeAllReactions(message);

		if (emote.getName().equals(Emojis.CROSS)) {
			bot.editMessage(message, "Hmm, I must have closed my eyes on that one, try again?");
			return;
		}
		// Only possibility here is Emojis.CHECK because there's only 2 reaction emojis
		String text = message.getContentDisplay();
		String[] tagArr = text.split("\\*\\*");
		for (String tagStr : tagArr) {
			RecruitTag tag = RecruitTag.fromString(tagStr);
			if (tag == null)
				continue;
			// Upload data for each tag
			SqlSpider.update("UPDATE ArknightsRecruit SET count=count+1 WHERE `index`=" + tag.getIndex());
			LogUtil.info("Updated " + tagStr + " in the database!");
		}
		bot.sendMessage("Database updated, thank you for contributing!", channel);
	}

	public static boolean isDataIntent(Message message) {
		List<Attachment> attachments = message.getAttachments();
		if (attachments.isEmpty() || !attachments.get(0).isImage())
			return false;
		return isRecruitmentScreenshot(attachments.get(0).getProxyUrl());
	}

	public static boolean isRecruitmentScreenshot(String url) {
		url = ImgbbSpider.uploadImage(url);
		try {
			double sim = new ImageHistogram().match(RECRUIT_IMAGE_URL, url);
			LogUtil.info("Recruitment data similarity: " + GeneralTools.getPercentage(sim));
			return sim > 0.98;
		} catch (IOException e) {
			LogUtil.error("IOException caught when comparing recruiting images...");
			e.printStackTrace();
			return false;
		}
	}

}
