package one.kroos.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.database.gacha.GachaMember;

public class Gacha extends CommandHandler {

	public Gacha(Bot bot) {
		super(bot, "gacha", "=w=", null, PREFIX + "gacha", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		if (args.length > 0 && args[0].equals("echo")) {
			GachaMember m = new GachaMember(guild.getMember(author));
			m.debug();
			bot.reactCheck(message);
			return;
		}

		bot.sendThinkingPacket(channel);
		Random r = new Random();
		List<Member> members = guild.getMembers();
		HashMap<Member, Integer> results = new HashMap<Member, Integer>();
		for (int a = 0; a < 10; a++) {
			Member m = members.get(r.nextInt(members.size()));
			results.put(m, results.getOrDefault(m, 0) + 1);
		}

		StringBuilder sb = new StringBuilder(author.getAsMention() + " You received ");
		for (Entry<Member, Integer> ent : results.entrySet())
			sb.append((ent.getValue() == 1 ? "`" + bot.getUserDisplayName(ent.getKey()) + "`"
					: "`" + bot.getUserDisplayName(ent.getKey()) + "` x" + ent.getValue()) + ", ");
		sb.delete(sb.length() - 2, sb.length());
		sb.append(" from the gacha");
		bot.sendMessage(sb.toString(), channel);
	}

}
