package one.kroos.database;

import java.util.ArrayList;
import java.util.Arrays;

public class RecruitmentData {

	public static final ArrayList<String> TAGS = new ArrayList<String>(
			Arrays.asList("starter", "senior operator", "top operator", // qualification
					"melee", "ranged", // position
					"guard", "medic", "vanguard", "caster", "sniper", "defender", "supporter", "specialist", // class
					"healing", "support", "dps", "aoe", "slow", "survival", "defense", "debuff", "shift", // affix
					"crowd control", "nuker", "summon", "fast-redeploy", "dp-recovery", "robot"));

	// WARNING: only use for displaying to the user!
	public static final ArrayList<String> TAGS_DISPLAY = new ArrayList<String>(
			Arrays.asList("Starter", "Senior Operator", "Top Operator", // qualification
					"Melee", "Ranged", // position
					"Guard", "Medic", "Vanguard", "Caster", "Sniper", "Defender", "Supporter", "Specialist", // class
					"Healing", "Support", "DPS", "AOE", "Slow", "Survival", "Defense", "Debuff", "Shift", // affix
					"Crowd Control", "Nuker", "Summon", "Fast-Redeploy", "DP-Recovery", "Robot"));

	public static String getDisplayName(ArrayList<String> tags) {
		if (tags.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder();
		for (String tag : tags)
			sb.append("**" + TAGS_DISPLAY.get(TAGS.indexOf(tag.toLowerCase())) + "**, ");
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public static String getDisplayName(String tag) {
		return TAGS_DISPLAY.get(TAGS.indexOf(tag.toLowerCase()));
	}

	public static String getNameFromIndex(int index) {
		return TAGS.get(index);
	}

	public static int getIndexFromName(String name) {
		return TAGS.indexOf(name.toLowerCase());
	}

}
