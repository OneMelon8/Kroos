package one.kroos.database.gacha;

import java.util.Random;

import net.dv8tion.jda.api.entities.Member;
import one.kroos.database.RecruitTag;

public class GachaMember {

	private Member member;
	private String id;

	private int rarity;
	private GachaClass clazz;
	private RecruitTag affix;

	/**
	 * Generate card based on member
	 */
	public GachaMember(Member m) {
		this.member = m;
		this.id = m.getId();

		// Generate
		Random r = new Random(m.getIdLong());
		this.rarity = r.nextInt(6) + 1; // 1-6 stars
		this.clazz = GachaClass.values()[r.nextInt(GachaClass.size())]; // class
		this.affix = RecruitTag.getAffixTags().get(r.nextInt(RecruitTag.getAffixTags().size())); // affix
	}

	public void debug() {
		System.out.println(this.member.getEffectiveName() + " [R" + this.rarity + "]: \n- Class: "
				+ this.clazz.toString() + "\nAffix: " + this.affix.getDisplayName());
	}

}
