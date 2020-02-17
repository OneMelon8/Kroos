package one.kroos.commands;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
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
		bot.sendThinkingPacket(channel);
		ResultSet rs = SqlSpider.query("SELECT * FROM ArknightsRecruit");
		HashMap<String, Integer> data = parseData(rs);
		SqlSpider.close();
		BufferedImage img = generatePieChart(data);
		String url = ImgbbSpider.uploadImage(img);
		bot.sendMessage(buildEmbed(getTotal(data), url), channel);
	}

	private static HashMap<String, Integer> parseData(ResultSet rs) {
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		try {
			while (rs.next())
				data.put(rs.getString(2), rs.getInt(3));
		} catch (SQLException e) {
			LogUtil.error("SQLException caught when parsing data...");
			e.printStackTrace();
		}
		return data;
	}

	private static int getTotal(HashMap<String, Integer> data) {
		int total = 0;
		for (int i : data.values())
			total += i;
		return total;
	}

	private static BufferedImage generatePieChart(HashMap<String, Integer> rawData) {
		LinkedHashMap<String, Integer> data = sortByValue(rawData);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double total = getTotal(data), max = 0;

		for (Entry<String, Integer> entry : data.entrySet()) {
			double v = entry.getValue() / total;
			if (v > max)
				max = v;
			dataset.addValue(v, "Chance", RecruitmentData.getDisplayName(entry.getKey()));
		}
		JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, false,
				true, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer r = plot.getRenderer();
		r.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat(" #.##%")));
		r.setSeriesPaint(0, ColorUtil.fromHex("4285f4"));
		r.setSeriesItemLabelsVisible(1, true);
		r.setSeriesItemLabelFont(0, new Font("Consolas", 0, 28));
		r.setBaseItemLabelsVisible(true);
		r.setBaseSeriesVisible(true);

		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setRange(0, max + 0.015);
		range.setMinorTickCount(1);
		range.setNumberFormatOverride(new DecimalFormat("#.#%"));
		range.setTickLabelFont(new Font("Consolas", 0, 30));

		CategoryAxis category = plot.getDomainAxis();
		category.setTickLabelFont(new Font("Roboto", 0, 32));

		return chart.createBufferedImage(1600, 1200);
	}

	private static MessageEmbed buildEmbed(int count, String url) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor("Arknights Recruitment Tags Statistics");
		builder.setDescription("*Collected a total of **" + (count / 5) + "** data samples*");
		builder.setImage(url);
		return builder.build();
	}

	private static LinkedHashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list)
			temp.put(aa.getKey(), aa.getValue());
		return temp;
	}
}
