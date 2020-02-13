package one.kroos.commands;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

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
import one.kroos.database.ImgbbSpider;
import one.kroos.database.RecruitmentData;
import one.kroos.database.SqlSpider;
import one.kroos.utils.LogUtil;

public class DisplayDataCommand extends CommandHandler {

	public DisplayDataCommand(Bot bot) {
		super(bot, "disp", "Display the currently collected data");
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		ResultSet rs = SqlSpider.query("SELECT * FROM ArknightsRecruit");
		TreeMap<String, Integer> data = parseData(rs);
		BufferedImage img = generatePieChart(data);
		String url = ImgbbSpider.uploadImage(img);
		bot.sendMessage(buildEmbed(url), channel);
	}

	private static TreeMap<String, Integer> parseData(ResultSet rs) {
		TreeMap<String, Integer> data = new TreeMap<String, Integer>();
		try {
			while (rs.next())
				data.put(rs.getString(2), rs.getInt(3));
		} catch (SQLException e) {
			LogUtil.error("SQLException caught when parsing data...");
			e.printStackTrace();
		}
		return data;
	}

	private static BufferedImage generatePieChart(TreeMap<String, Integer> data) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Entry<String, Integer> entry : data.entrySet()) {
			if (entry.getValue() <= 0)
				continue;
			dataset.setValue(RecruitmentData.getDisplayName(entry.getKey()), entry.getValue());
		}
		JFreeChart chart = ChartFactory.createPieChart(null, dataset, false, false, false);
		PiePlot plot = (PiePlot) chart.getPlot();
//		plot.setLabelGenerator(null);
		plot.setLabelBackgroundPaint(null);
		plot.setLabelFont(new Font("Consolas", 0, 20));
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0}\n{1} ({2})", new DecimalFormat("0"),
				new DecimalFormat("0%"));
		plot.setLabelGenerator(gen);

		return chart.createBufferedImage(800, 600);
	}

	private static MessageEmbed buildEmbed(String url) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Arknights Recruitment Tags Statistics");
		builder.setImage(url);
		return builder.build();
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
