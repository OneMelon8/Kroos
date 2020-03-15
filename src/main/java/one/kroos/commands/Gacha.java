package one.kroos.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.config.GachaConfig;
import one.kroos.database.gacha.GachaMember;
import one.kroos.utils.TimeFormatter;

public class Gacha extends CommandHandler {

	private static HashMap<String, Long> cooldown = new HashMap<String, Long>();

	public Gacha(Bot bot) {
		super(bot, "gacha", "Try your luck?", null, PREFIX + "gacha", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length > 0 && args[0].equals("echo")) {
			GachaMember m = new GachaMember(guild.getMember(author));
			bot.sendMessage(m.generateEmbedded(), channel);
			return;
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

//		StringBuilder sb = new StringBuilder(author.getAsMention() + " You received ");
//		for (Entry<Member, Integer> ent : results.entrySet())
//			sb.append((ent.getValue() == 1 ? "`" + bot.getUserDisplayName(ent.getKey()) + "`"
//					: "`" + bot.getUserDisplayName(ent.getKey()) + "` x" + ent.getValue()) + ", ");
//		sb.delete(sb.length() - 2, sb.length());
//		sb.append(" from the gacha");

		GachaMember gm = new GachaMember(m);
		bot.sendMessage(gm.generateEmbedded(), channel);
	}

}
