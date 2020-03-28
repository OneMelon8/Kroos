package one.kroos.commands;

import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.commands.helpers.ReactionDispatcher;
import one.kroos.commands.helpers.ReactionHandler;
import one.kroos.config.BotConfig;
import one.kroos.config.GachaConfig;
import one.kroos.database.Emojis;
import one.kroos.database.ImgbbSpider;
import one.kroos.database.SqlSpider;
import one.kroos.database.gacha.GachaMember;
import one.kroos.utils.ImageTools;
import one.kroos.utils.LogUtil;
import one.kroos.utils.TimeFormatter;

public class Gacha extends CommandHandler implements ReactionHandler {

	private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

	public Gacha(Bot bot) {
		super(bot, "gacha", "Try your luck?", null, PREFIX + "gacha", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("echo")) {
				// Check self member
				GachaMember m = new GachaMember(guild.getMember(author));
				bot.sendMessage(m.generateEmbedded(), channel);
				return;
			} else if (args[0].equalsIgnoreCase("inv")) {
				// Check inventory
				bot.sendMessage(getInventoryEmbedded(guild.getMember(author)), channel);
				return;
			}
		}

		String authorId = author.getId();
		if (cooldown.containsKey(authorId)) {
			long msLeft = cooldown.get(authorId) + GachaConfig.COOLDOWN_MS - System.currentTimeMillis();
			if (msLeft > 0) {
				bot.sendMessage(author.getAsMention() + " Please wait " + TimeFormatter.getCountDownSimple(msLeft)
						+ " before trying again!", channel);
				return;
			}
		}
		cooldown.put(authorId, System.currentTimeMillis());

		bot.sendThinkingPacket(channel);
		Random r = new Random();
		List<Member> members = guild.getMembers();
		Member m = members.get(r.nextInt(members.size()));

		// Update database
		SqlSpider.update("INSERT INTO GachaInventory (id,rolls) VALUES (\"" + authorId + "\",\"" + 1
				+ "\") ON DUPLICATE KEY UPDATE rolls=rolls+1");
		SqlSpider.close();

		GachaMember gm = new GachaMember(m);
		if (!isInventoryFull(authorId)) {
			bot.sendMessage(gm.generateEmbedded(), channel);
			addToInventory(authorId, gm.getMember().getUser().getId(), -1);
			return;
		}

		// Inventory full!
		bot.sendMessage("Your inventory is full! Choose which slot you would like to override:", channel);
		bot.sendMessage(getInventoryEmbedded(guild.getMember(author)), channel);
		Message reactMessage = bot.sendMessage(gm.generateEmbedded(), channel);
		bot.addReactions(reactMessage, Emojis.NUMBER_1, Emojis.NUMBER_2, Emojis.NUMBER_3, Emojis.NUMBER_4,
				Emojis.NUMBER_5, Emojis.RECYCLE);
		ReactionDispatcher.register(reactMessage, this, Emojis.NUMBER_1, Emojis.NUMBER_2, Emojis.NUMBER_3,
				Emojis.NUMBER_4, Emojis.NUMBER_5, Emojis.RECYCLE);
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
			return;
		bot.removeAllReactions(message);

		String gachaMemberId = message.getEmbeds().get(0).getFooter().getText();
		int replaceSlot = 0;
		if (emote.getEmoji().equals(Emojis.NUMBER_1))
			replaceSlot = 1;
		else if (emote.getEmoji().equals(Emojis.NUMBER_2))
			replaceSlot = 2;
		else if (emote.getEmoji().equals(Emojis.NUMBER_3))
			replaceSlot = 3;
		else if (emote.getEmoji().equals(Emojis.NUMBER_4))
			replaceSlot = 4;
		else if (emote.getEmoji().equals(Emojis.NUMBER_5))
			replaceSlot = 5;
		else {
			bot.reactCheck(message);
			return;
		}

		if (addToInventory(user.getId(), gachaMemberId, replaceSlot))
			bot.reactCheck(message);
		else
			bot.reactError(message);
	}

	private boolean addToInventory(String userId, String gachaId, int replaceIndex) {
		if (replaceIndex == -1)
			replaceIndex = getInvItemCount(userId) + 1;
		SqlSpider.update("INSERT INTO GachaInventory (id,inv1) VALUES (\"" + userId + "\",\"" + gachaId
				+ "\") ON DUPLICATE KEY UPDATE inv" + replaceIndex + "=\"" + gachaId + "\"");
		SqlSpider.close();
		return true;
	}

	private boolean isInventoryFull(String id) {
		return getInvItemCount(id) >= 5;
	}

	private int getInvItemCount(String id) {
		ResultSet rs = SqlSpider.query("SELECT * FROM GachaInventory WHERE id=\"" + id + "\"");
		int count = 0;
		try {
			if (!rs.next())
				return 0;
			for (int a = 0; a < 5; a++)
				if (rs.getString(a + 3) != null)
					count++;
		} catch (SQLException e) {
			return 0;
		} finally {
			SqlSpider.close();
		}
		return count;
	}

	private MessageEmbed getInventoryEmbedded(Member member) {
		ResultSet rs = SqlSpider.query("SELECT * FROM GachaInventory WHERE id=\"" + member.getUser().getId() + "\"");
		int rolls = 0;
		ArrayList<String> inventory = new ArrayList<String>();

		try {
			if (rs.next()) {
				rolls = rs.getInt(2);
				for (int a = 0; a < 5; a++)
					if (rs.getString(a + 3) != null)
						inventory.add(rs.getString(a + 3)); // 1st is ID, 2nd is roll count, start at 3
			}
		} catch (SQLException e) {
			LogUtil.error("SQL exception caught while getting user inventory:");
			e.printStackTrace();
		}
		SqlSpider.close();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Inventory of " + member.getEffectiveName());
		builder.setDescription(
				"*" + rolls + " gacha attempts, " + inventory.size() + " / 5 inventory slots used" + "*");

		BufferedImage[] pfps = new BufferedImage[5];
		for (int a = 0; a < inventory.size(); a++) {
			GachaMember m = new GachaMember(
					bot.getJDA().getGuildById(BotConfig.SERVER_MOE_GLOBAL_ID).getMemberById(inventory.get(a)));
			builder.addField("**Slot #" + (a + 1) + " -- " + m.getMember().getEffectiveName() + "**",
					m.getEmbeddedString(), false);
			pfps[a] = m.generateIcon();
		}

		String imgUrl = ImgbbSpider.uploadImage(ImageTools.mergeInventory(pfps));
		builder.setImage(imgUrl);
		return builder.build();
	}

}
