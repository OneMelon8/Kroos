package one.kroos.database;

public class Emotes {

	public static final String RODY_BEAT = "<:rodybeat:552017882905575424>";
	public static final String KOKORO_WUT = "<:kokoron_wut:598387497155821579>";
	public static final String KOKORO_WUT_3 = "<:kokoron_wut_3:599596808930459649>";
	public static final String KOKORO_ERROR = "<:kokoron_error:598824542978441232>";
	public static final String KOKORO_SPARKLE = "<:kokoron_sparkle:601045739203854364>";
	public static final String SAAYA_MELT = "<:saaya_melt:600851841152188426>";

	public static final String ATTR_PURE = "<:attr_pure:578337887146213382>";
	public static final String ATTR_POWER = "<:attr_power:578337887112658954>";
	public static final String ATTR_HAPPY = "<:attr_happy:578337887238488074>";
	public static final String ATTR_COOL = "<:attr_cool:578337887142281216>";
	public static final String BANDORI_STAR_PREMIUM = "<:bandori_star:578806302864572427>";
	public static final String BANDORI_STAR = "<:bandori_star2:580847904508346378>";
	public static final String LIVE_BOOST = "<:live_boost:603455894994550787>";

	public static final String BANDORI_RARITY_1 = BANDORI_STAR;
	public static final String BANDORI_RARITY_2 = "<:bandori_rarity_2:599082647699980298>";
	public static final String BANDORI_RARITY_3 = "<:bandori_rarity_3:599082647636934696>";
	public static final String BANDORI_RARITY_4 = "<:bandori_rarity_4:599085915205271582>";

	// Arknights
	public static final String ARKNIGHTS_CLASS_CASTER = "<:class_caster:679391191975985162>";
	public static final String ARKNIGHTS_CLASS_DEFENDER = "<:class_defender:679391191544102924>";
	public static final String ARKNIGHTS_CLASS_GUARD = "<:class_guard:679391191711875083>";
	public static final String ARKNIGHTS_CLASS_MEDIC = "<:class_medic:679391191657349162>";
	public static final String ARKNIGHTS_CLASS_SNIPER = "<:class_sniper:679391191980310546>";
	public static final String ARKNIGHTS_CLASS_SPECIALIST = "<:class_specialist:679391191997087765>";
	public static final String ARKNIGHTS_CLASS_SUPPORTER = "<:class_supporter:679391192034574347>";
	public static final String ARKNIGHTS_CLASS_VANGUARD = "<:class_vanguard:679391191980310588>";
	public static final String ARKNIGHTS_RHODES_ISLAND = "<:rhodes_island:679392679603339285>";
	public static final String ARKNIGHTS_ELITE_2 = "<:elite:681943093166997647>";
	public static final String ARKNIGHTS_AFFIX = "<:affix:681951480844255314>";

	// Utilities
	public static final String PLUS = "<:plus:621873710940553237>";
	public static final String MINUS = "<:minus:621873698898706433>";
	public static final String EQUALS = "<:equals:621873698982330388>";

	/*
	 * Generic Getters
	 */
	public static String getId(String emote) {
		emote = emote.replace(">", "");
		return emote.split(":")[2];
	}

	public static String getName(String emote) {
		emote = emote.replace(">", "");
		return emote.split(":")[1];
	}

}
