package one.kroos.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.config.BotConfig;

public class Help extends CommandHandler {

	public Help(Bot bot) {
		super(bot, "help", "Show the help information for Kroos");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		bot.sendMessage(this.getHelpEmbeded(), channel);
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Doctor? Do you need help? *Yawn*");
		builder.setDescription(this.helpMsg);

		StringBuilder sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "data", "Gather data from attached screenshot\n"
				+ "- NOTE: simply uploading a screenshot, no command required\n"));
		sb.append(String.format("%-8s >> %s", "disp", "Display the current data as a graph\n"));
		sb.append("```");
		builder.addField(new Field("**Recruitment Commands:**", sb.toString(), false));

		sb = new StringBuilder("```");
		sb.append(String.format("%-8s >> %s", "ping", "Check my heartbeat\n"));
		sb.append(String.format("%-8s >> %s", "help", "Show this image\n"));
		sb.append("```");
		builder.addField(new Field("**Other Commands:**", sb.toString(), false));
		return builder.build();
	}
}
