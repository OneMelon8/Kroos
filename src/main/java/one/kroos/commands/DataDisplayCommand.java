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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.config.BotConfig;
import one.kroos.database.ImgbbSpider;
import one.kroos.database.RecruitmentData;
import one.kroos.database.SqlSpider;
import one.kroos.utils.ColorUtil;
import one.kroos.utils.LogUtil;

public class DataDisplayCommand extends CommandHandler {

	public DataDisplayCommand(Bot bot) {
		super(bot, "stat", new String[] { "stats", "statistics", "disp", "display" },
				"Display the currently collected data", null, PREFIX + "stats", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		ResultSet rs = SqlSpider.query("SELECT * FROM ArknightsRecruit");
		TreeMap<String, Integer> data = parseData(rs);
		SqlSpider.close();
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
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double total = 0, max = 0;
		for (int i : data.values())
			total += i;

		for (Entry<String, Integer> entry : data.entrySet()) {
			if (entry.getValue() <= 0)
				continue;
			double v = entry.getValue() / total;
			if (v > max)
				max = v;
			dataset.addValue(v, "Chance", RecruitmentData.getDisplayName(entry.getKey()));
		}
		JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, false,
				true, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer r = plot.getRenderer();
		r.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat(" 0%")));
		r.setSeriesPaint(0, ColorUtil.fromHex("4285f4"));
		r.setSeriesItemLabelsVisible(1, true);
		r.setSeriesItemLabelFont(0, new Font("Consolas", 0, 20));
		r.setBaseItemLabelsVisible(true);
		r.setBaseSeriesVisible(true);

		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setRange(0, max + 0.02);

		return chart.createBufferedImage(800, 600);
	}

	private static MessageEmbed buildEmbed(String url) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Arknights Recruitment Tags Statistics");
		builder.setImage(url);
		return builder.build();
	}

}
