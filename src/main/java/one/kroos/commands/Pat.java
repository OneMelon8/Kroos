package one.kroos.commands;

import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;

public class Pat extends CommandHandler {

	private static final Random r = new Random();
	private static final String[] responses = new String[] { "KO-KO-DA-YO!", "Fuwa Fwah~", "Doktah~", "\\*Yawn\\*",
			"Doctor, it's such a nice day, so can't we take a little nap?",
			"Hmm...? We're about to leave? But I just got up... Wait, is this the Doctor's office? How'd I get here?",
			"Why am I always squinting? Because I don't want anyone to see my eyes. Nope, nobody~",
			"Doctor! Someone left 10 alarm clocks by my bed! Who would do something so awful?!",
			"Doctor, let's play a game~ Put an apple on your head and we'll start~" };

	public Pat(Bot bot) {
		super(bot, "pat", new String[] { "patpat" }, "Pat Kroos", null, PREFIX + "pat", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendMessage(author.getAsMention() + " " + responses[r.nextInt(responses.length)], channel);
		if (r.nextInt(20) == 0)
			bot.sendMessage("\\*pats " + author.getAsMention() + " on the head too\\*", channel);
	}

}
