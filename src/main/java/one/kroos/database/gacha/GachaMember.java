package one.kroos.database.gacha;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import one.kroos.config.BotConfig;
import one.kroos.database.Emotes;
import one.kroos.database.ImgbbSpider;
import one.kroos.database.RecruitTag;
import one.kroos.utils.ColorUtil;
import one.kroos.utils.ImageTools;

public class GachaMember {

	private Member member;

	private int rarity;
	private GachaClass clazz;
	private RecruitTag affix;
	private ArrayList<GachaTrait> traits;

	/**
	 * Generate card based on member
	 */
	public GachaMember(Member m) {
		this.member = m;

		// Generate
		Random r = new Random(m.getIdLong());
		this.clazz = GachaClass.values()[r.nextInt(GachaClass.size())]; // class
		this.rarity = this.randomRarity(r); // rarity
		this.affix = RecruitTag.getAffixTags().get(r.nextInt(RecruitTag.getAffixTags().size())); // affix
		this.traits = new ArrayList<GachaTrait>();
		if (r.nextInt(4) == 0) // 1/4 chance to get traits
			this.traits.add(GachaTrait.getRandomTrait(r, this.getClazz()));
	}

	public MessageEmbed generateEmbedded(String extras) {
		EmbedBuilder builder = new EmbedBuilder();
		try {
			builder.setColor(
					ColorUtil.getDominantColor(ImageTools.getImageFromUrl(this.member.getUser().getAvatarUrl())));
		} catch (Exception e) {
			builder.setColor(BotConfig.COLOR_MISC);
		}
		builder.setAuthor(this.member.getEffectiveName());
		builder.setThumbnail(ImgbbSpider.uploadImage(this.generateIcon()));
		builder.addField(new Field("**Information:**", this.getEmbeddedString(), false));
		builder.setFooter(extras == null ? this.member.getId() : (extras + " : " + this.member.getId()));
		return builder.build();
	}

	public String getEmbeddedString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Rarity: " + this.getRarityEmoteStr());
		sb.append("\nClass: **" + this.clazz.getEmote() + " " + this.clazz.getDisplayName() + "**");
		sb.append("\nAffix: **" + this.affix.getDisplayName() + "**");
		if (this.traits.size() > 0)
			sb.append("\nTrait: **" + this.traits.get(0).getDisplayName() + "**");
		return sb.toString();
	}

	private int randomRarity(Random r) {
		int gen = r.nextInt(100);
		// 10% chance of 1 star
		if (gen < 10)
			return 1;
		// 15% chance of 2 stars
		if (gen < 25)
			return 2;
		// 35% chance of 3 stars
		if (gen < 50)
			return 3;
		// 30% chance of 4 stars
		if (gen < 90)
			return 4;
		// 8% chance of 5 stars
		if (gen < 98)
			return 5;
		// 2% chance of 6 stars
		return 6;
	}

	public String getRarityEmoteStr() {
		StringBuilder sb = new StringBuilder();
		if (this.rarity >= 4)
			for (int a = 0; a < this.rarity; a++)
				sb.append(Emotes.BANDORI_STAR_PREMIUM);
		else
			for (int a = 0; a < this.rarity; a++)
				sb.append(Emotes.BANDORI_STAR);
		return sb.toString();
	}

	public BufferedImage generateIcon() {
		BufferedImage icon = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = icon.createGraphics();

		// LAYER 0 -- Universal card background (for transparent images)
		if (this.rarity <= 3)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.BACKGROUND), 0, 0, null);
		else if (this.rarity == 4)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.BACKGROUND_4), 0, 0, null);
		else if (this.rarity == 5)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.BACKGROUND_5), 0, 0, null);
		else if (this.rarity == 6)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.BACKGROUND_6), 0, 0, null);

		// LAYER 1 -- User profile picture
		String url = this.member.getUser().getAvatarUrl();
		if (url == null)
			url = BotConfig.URL_DISCORD;
		g2d.drawImage(ImageTools.getImageFromUrl(url).getScaledInstance(128, 128, Image.SCALE_DEFAULT), 0, 0, null);

		// LAYER 2 -- Frame overlay
		if (this.rarity <= 3)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.FRAME), 0, 0, null);
		else if (this.rarity == 4)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.FRAME_4), 0, 0, null);
		else if (this.rarity == 5)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.FRAME_5), 0, 0, null);
		else if (this.rarity == 6)
			g2d.drawImage(GachaAssets.getImage(GachaAssets.FRAME_6), 0, 0, null);
		g2d.dispose();
		return icon;
	}

	public Member getMember() {
		return member;
	}

	public int getRarity() {
		return rarity;
	}

	public GachaClass getClazz() {
		return clazz;
	}

	public RecruitTag getAffix() {
		return affix;
	}

	public ArrayList<GachaTrait> getTraits() {
		return traits;
	}

}
