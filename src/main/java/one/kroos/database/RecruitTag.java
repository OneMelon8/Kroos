package one.kroos.database;

import java.util.ArrayList;
import java.util.Arrays;

public enum RecruitTag {
	// Qualification
	STARTER, SENIOR_OPERATOR, TOP_OPERATOR,
	// Position
	MELEE, RANGED,
	// Class
	GUARD, MEDIC, VANGUARD, CASTER, SNIPER, DEFENDER, SUPPORTER, SPECIALIST,
	// Affix
	HEALING, SUPPORT, DPS, AOE, SLOW, SURVIVAL, DEFENSE, DEBUFF, SHIFT, CROWD_CONTROL, NUKER, SUMMON, FAST_REDEPLOY,
	DP_RECOVERY, ROBOT;

	private static final ArrayList<String> TAGS_DISPLAY = new ArrayList<String>(
			Arrays.asList("Starter", "Senior Operator", "Top Operator", // qualification
					"Melee", "Ranged", // position
					"Guard", "Medic", "Vanguard", "Caster", "Sniper", "Defender", "Supporter", "Specialist", // class
					"Healing", "Support", "DPS", "AOE", "Slow", "Survival", "Defense", "Debuff", "Shift", // affix
					"Crowd Control", "Nuker", "Summon", "Fast-Redeploy", "DP-Recovery", "Robot"));

	public static ArrayList<RecruitTag> getQualificationTags() {
		return new ArrayList<RecruitTag>(Arrays.asList(STARTER, SENIOR_OPERATOR, TOP_OPERATOR));
	}

	public static ArrayList<RecruitTag> getPositionTags() {
		return new ArrayList<RecruitTag>(Arrays.asList(MELEE, RANGED));
	}

	public static ArrayList<RecruitTag> getClassTags() {
		return new ArrayList<RecruitTag>(
				Arrays.asList(GUARD, MEDIC, VANGUARD, CASTER, SNIPER, DEFENDER, SUPPORTER, SPECIALIST));
	}

	public static ArrayList<RecruitTag> getAffixTags() {
		return new ArrayList<RecruitTag>(Arrays.asList(HEALING, SUPPORT, DPS, AOE, SLOW, SURVIVAL, DEFENSE, DEBUFF,
				SHIFT, CROWD_CONTROL, NUKER, SUMMON, FAST_REDEPLOY, DP_RECOVERY, ROBOT));
	}

	public static RecruitTag fromString(String tag) {
		for (int a = 0; a < RecruitTag.values().length; a++)
			if (tag.equalsIgnoreCase(TAGS_DISPLAY.get(a)))
				return fromIndex(a);
		return null;
	}

	public static RecruitTag fromIndex(int index) {
		return RecruitTag.values()[index];
	}

	public String toString() {
		return this.getDisplayName().toLowerCase();
	}

	public int getIndex() {
		return Arrays.asList(RecruitTag.values()).indexOf(this);
	}

	public String getDisplayName() {
		return TAGS_DISPLAY.get(this.getIndex());
	}

	public static String getDisplayNames(ArrayList<RecruitTag> tags) {
		if (tags.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder();
		for (RecruitTag tag : tags)
			sb.append("**" + tag.getDisplayName() + "**, ");
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	public static boolean contains(String tag) {
		return fromString(tag) != null;
	}
}
