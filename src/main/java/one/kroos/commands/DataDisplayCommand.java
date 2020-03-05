package one.kroos.commands;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import one.kroos.Bot;
import one.kroos.commands.helpers.CommandHandler;
import one.kroos.commands.helpers.ReactionDispatcher;
import one.kroos.commands.helpers.ReactionHandler;
import one.kroos.config.BotConfig;
import one.kroos.database.Emotes;
import one.kroos.database.ImgbbSpider;
import one.kroos.database.RecruitDatabase;
import one.kroos.database.RecruitTag;
import one.kroos.database.SqlSpider;
import one.kroos.utils.ColorUtil;
import one.kroos.utils.LogUtil;

public class DataDisplayCommand extends CommandHandler implements ReactionHandler {

	public DataDisplayCommand(Bot bot) {
		super(bot, "stat", new String[] { "stats", "statistics", "disp", "display" },
				"Display the currently collected data", null, PREFIX + "stats", null);
	}

	@Override
	public void onCommand(User author, String command, String[] args, Message message, MessageChannel channel,
			Guild guild) {
		// DELETE WHEN DONE
		if (true) {
			bot.sendMessage(
					"Kroos will be down for a while since the MySQL server host is shit, I'll need to transfer hosts now -- 🍉",
					channel);
			return;
		}

		bot.sendThinkingPacket(channel);
		ResultSet rs = SqlSpider.query("SELECT * FROM ArknightsRecruit");
		HashMap<RecruitTag, Integer> data = parseData(rs);
		SqlSpider.close();

		BufferedImage img = generateBarChart(data);
		String url = ImgbbSpider.uploadImage(img);
		Message reactMessage = bot.sendMessage(
				buildEmbed(getTotal(data.values()), "Recruit Tags General Probability Statistics", url), channel);

		bot.addEmoteReactions(reactMessage, Emotes.ARKNIGHTS_ELITE_2, Emotes.ARKNIGHTS_CLASS_SPECIALIST,
				Emotes.ARKNIGHTS_AFFIX);
		ReactionDispatcher.register(reactMessage, this, Emotes.ARKNIGHTS_ELITE_2, Emotes.ARKNIGHTS_CLASS_SPECIALIST,
				Emotes.ARKNIGHTS_AFFIX);
	}

	@Override
	public void onReact(User user, ReactionEmote emote, Message message, MessageChannel channel, Guild guild) {
		if (message.getEmbeds() == null || message.getEmbeds().size() == 0)
			return;
		bot.removeAllReactions(message);

		ResultSet rs = SqlSpider.query("SELECT * FROM ArknightsRecruit");
		HashMap<RecruitTag, Integer> data = parseData(rs);
		SqlSpider.close();

		String emoteName = bot.getEmoteName(emote);
		Message messageEdited = null;
		ArrayList<String> reactions = new ArrayList<String>();

		int total = getTotal(data.values());
		if (emoteName.equals(Emotes.ARKNIGHTS_RHODES_ISLAND)) {
			// General graph
			messageEdited = bot.editMessage(message, buildEmbed(total, "Recruit Tags General Probability Statistics",
					ImgbbSpider.uploadImage(generateBarChart(data))));
			reactions.addAll(
					Arrays.asList(Emotes.ARKNIGHTS_ELITE_2, Emotes.ARKNIGHTS_CLASS_SPECIALIST, Emotes.ARKNIGHTS_AFFIX));
		} else if (emoteName.equals(Emotes.ARKNIGHTS_ELITE_2)) {
			// Graph by operators
			messageEdited = bot.editMessage(message, buildEmbed(total, "Recruit Operators Probability Statistics",
					ImgbbSpider.uploadImage(generateOperatorChart(parseOperatorData()))));
			reactions.addAll(Arrays.asList(Emotes.ARKNIGHTS_RHODES_ISLAND, Emotes.ARKNIGHTS_CLASS_SPECIALIST,
					Emotes.ARKNIGHTS_AFFIX));
		} else if (emoteName.equals(Emotes.ARKNIGHTS_CLASS_SPECIALIST)) {
			// Graph by classes
			messageEdited = bot.editMessage(message, buildEmbed(total, "Recruit Tags by Class Probability Statistics",
					ImgbbSpider.uploadImage(generateBarChart(filterData(data, RecruitTag.getClassTags())))));
			reactions.addAll(
					Arrays.asList(Emotes.ARKNIGHTS_RHODES_ISLAND, Emotes.ARKNIGHTS_ELITE_2, Emotes.ARKNIGHTS_AFFIX));
		} else if (emoteName.equals(Emotes.ARKNIGHTS_AFFIX)) {
			// Graph by affixes
			messageEdited = bot.editMessage(message, buildEmbed(total, "Recruit Tags by Affix Probability Statistics",
					ImgbbSpider.uploadImage(generateBarChart(filterData(data, RecruitTag.getAffixTags())))));
			reactions.addAll(Arrays.asList(Emotes.ARKNIGHTS_RHODES_ISLAND, Emotes.ARKNIGHTS_ELITE_2,
					Emotes.ARKNIGHTS_CLASS_SPECIALIST));
		} else {
			// Not really possible to get here, but still a fail-safe
			bot.reactCross(message);
			return;
		}

		bot.addEmoteReactions(messageEdited, reactions);
		ReactionDispatcher.register(messageEdited, this, reactions);
	}

	private static BufferedImage generateBarChart(HashMap<RecruitTag, Integer> rawData) {
		LinkedHashMap<RecruitTag, Integer> data = sortByValue(rawData);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double total = getTotal(data.values()), max = 0;

		for (Entry<RecruitTag, Integer> entry : data.entrySet()) {
			double v = entry.getValue() / total;
			if (v > max)
				max = v;
			dataset.addValue(v, "Chance", entry.getKey().getDisplayName());
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

	private static BufferedImage generateOperatorChart(HashMap<String, Integer> operatorRawData) {
		LinkedHashMap<String, Integer> data = sortByValueOperators(operatorRawData);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double total = getTotal(data.values()), max = 0;

		for (Entry<String, Integer> entry : data.entrySet()) {
			double v = entry.getValue() / total;
			if (v > max)
				max = v;
			dataset.addValue(v, "Chance", entry.getKey());
		}
		JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, false,
				true, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer r = plot.getRenderer();
		r.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat(" #.##%")));
		r.setSeriesPaint(0, ColorUtil.fromHex("4285f4"));
		r.setSeriesItemLabelsVisible(1, true);
		r.setSeriesItemLabelFont(0, new Font("Consolas", 0, 20));
		r.setBaseItemLabelsVisible(true);
		r.setBaseSeriesVisible(true);

		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setNumberFormatOverride(new DecimalFormat("#.#%"));
		range.setTickLabelFont(new Font("Consolas", 0, 30));

		CategoryAxis category = plot.getDomainAxis();
		category.setTickLabelFont(new Font("Roboto", 0, 20));

		return chart.createBufferedImage(1600, 1600);
	}

	private static MessageEmbed buildEmbed(int count, String title, String url) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(BotConfig.COLOR_MISC);
		builder.setAuthor(title);
		builder.setDescription("*Collected a total of **" + (count / 5) + "** data samples*");
		builder.setImage(url);
		return builder.build();
	}

	private static HashMap<RecruitTag, Integer> parseData(ResultSet rs) {
		HashMap<RecruitTag, Integer> data = new HashMap<RecruitTag, Integer>();
		try {
			while (rs.next())
				data.put(RecruitTag.fromString(rs.getString(2)), rs.getInt(3));
		} catch (SQLException e) {
			LogUtil.error("SQLException caught when parsing recruit data...");
			e.printStackTrace();
		}
		return data;
	}

	private static HashMap<String, Integer> parseOperatorData() {
		ResultSet rs = SqlSpider.query("SELECT * FROM RecruitData");
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		try {
			while (rs.next()) {
				ArrayList<RecruitTag> tags = new ArrayList<RecruitTag>();
				for (String s : rs.getString(2).split(", "))
					if (RecruitTag.contains(s))
						tags.add(RecruitTag.fromString(s));
				for (String operator : RecruitDatabase.getUnion(tags))
					if (data.containsKey(operator))
						data.put(operator, data.get(operator) + 1);
					else
						data.put(operator, 1);
			}
		} catch (SQLException e) {
			LogUtil.error("SQLException caught when parsing bulk recruit data...");
			e.printStackTrace();
		} finally {
			SqlSpider.close();
		}
		return data;
	}

	private static HashMap<RecruitTag, Integer> filterData(HashMap<RecruitTag, Integer> data,
			ArrayList<RecruitTag> tags) {
		HashMap<RecruitTag, Integer> output = new HashMap<RecruitTag, Integer>();
		for (RecruitTag tag : tags)
			if (data.containsKey(tag))
				output.put(tag, data.get(tag));
		return output;
	}

	private static int getTotal(Collection<Integer> collection) {
		int total = 0;
		for (int i : collection)
			total += i;
		return total;
	}

	private static LinkedHashMap<RecruitTag, Integer> sortByValue(HashMap<RecruitTag, Integer> hm) {
		List<Map.Entry<RecruitTag, Integer>> list = new LinkedList<Map.Entry<RecruitTag, Integer>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<RecruitTag, Integer>>() {
			@Override
			public int compare(Map.Entry<RecruitTag, Integer> o1, Map.Entry<RecruitTag, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		LinkedHashMap<RecruitTag, Integer> temp = new LinkedHashMap<RecruitTag, Integer>();
		for (Map.Entry<RecruitTag, Integer> aa : list)
			temp.put(aa.getKey(), aa.getValue());
		return temp;
	}

	private static LinkedHashMap<String, Integer> sortByValueOperators(HashMap<String, Integer> hm) {
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
