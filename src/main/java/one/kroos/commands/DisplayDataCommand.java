package one.kroos.commands;

import java.sql.ResultSet;

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
import one.kroos.database.SqlSpider;

public class DisplayDataCommand extends CommandHandler {

	public DisplayDataCommand(Bot bot) {
		super(bot, "disp", "Display the currently collected data");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		ResultSet rs = SqlSpider.query("SELECT * FROM ArknightsRecruit");
	}

	@Override
	public MessageEmbed getHelpEmbeded() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Display Data");
		builder.setDescription(this.helpMsg);
		builder.addField(new Field("Copy & Paste:", "```" + BotConfig.PREFIX + command + "```", false));
		return builder.build();
	}
}
