package one.kroos.database.gacha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

public enum GachaTrait {
	// Vanguard class
	DP_RECOVERY_ON_KILL, DP_REFUND_ALL_RETREAT,
	// Guard class
	NORMAL_ATTACK_DOUBLE, NORMAL_ATTACK_ALL_BLOCKED, NORMAL_ATTACK_GUARD_RANGED, NORMAL_ATTACK_MAGIC,
	// Defender class
	SKILL_HEAL_NEARBY,
	// Sniper class
	TARGET_PRIORITY_AIR, DAMAGE_IMPROVE_AIR, NORMAL_ATTACK_AOE_PHYSICAL, TARGET_LOWEST_DEFENSE,
	// Caster class
	NORMAL_ATTACK_AOE_MAGIC,
	// Medic class
	HEAL_COUNT_3, HEAL_RANGE_UP,
	// Supporter class
	NORMAL_ATTACK_SLOW, SUMMON,
	// Specialist
	REDEPLOY_FAST, SHIFT_PULL, SHIFT_PUSH,
	// Universal
	BLOCK_COUNT_0, BLOCK_COUNT_1, BLOCK_COUNT_2, BLOCK_COUNT_3, // melee
	DODGE_PHYSICAL, DODGE_MAGICAL; // anything

	private static final GachaTrait[] POOL_VANGUARD = new GachaTrait[] { DP_RECOVERY_ON_KILL, DP_REFUND_ALL_RETREAT };
	private static final GachaTrait[] POOL_GUARD = new GachaTrait[] { NORMAL_ATTACK_DOUBLE, NORMAL_ATTACK_ALL_BLOCKED,
			NORMAL_ATTACK_GUARD_RANGED, NORMAL_ATTACK_MAGIC };
	private static final GachaTrait[] POOL_DEFENDER = new GachaTrait[] { SKILL_HEAL_NEARBY };
	private static final GachaTrait[] POOL_SNIPER = new GachaTrait[] { TARGET_PRIORITY_AIR, DAMAGE_IMPROVE_AIR,
			NORMAL_ATTACK_AOE_PHYSICAL, TARGET_LOWEST_DEFENSE };
	private static final GachaTrait[] POOL_CASTER = new GachaTrait[] { NORMAL_ATTACK_AOE_MAGIC };
	private static final GachaTrait[] POOL_MEDIC = new GachaTrait[] { HEAL_COUNT_3, HEAL_RANGE_UP };
	private static final GachaTrait[] POOL_SUPPORTER = new GachaTrait[] { NORMAL_ATTACK_SLOW, SUMMON };
	private static final GachaTrait[] POOL_SPECIALIST = new GachaTrait[] { REDEPLOY_FAST, SHIFT_PULL, SHIFT_PUSH };

	private static final GachaTrait[] POOL_MELEE = new GachaTrait[] { BLOCK_COUNT_0, BLOCK_COUNT_1, BLOCK_COUNT_2,
			BLOCK_COUNT_3 };
	private static final GachaTrait[] POOL_UNIVERSAL = new GachaTrait[] { DODGE_PHYSICAL, DODGE_MAGICAL };

	public static int size() {
		return GachaTrait.values().length;
	}

	public static ArrayList<GachaTrait> toArrayList() {
		return new ArrayList<GachaTrait>(Arrays.asList(GachaTrait.values()));
	}

	public static GachaTrait getRandomTrait(Random r, GachaClass clazz) {
		GachaTrait[] pool = null;
		switch (clazz) {
		case VANGUARD:
			pool = ArrayUtils.addAll(POOL_VANGUARD, POOL_MELEE);
			break;
		case GUARD:
			pool = ArrayUtils.addAll(POOL_GUARD, POOL_MELEE);
			break;
		case DEFENDER:
			pool = ArrayUtils.addAll(POOL_DEFENDER, POOL_MELEE);
			break;
		case SNIPER:
			pool = POOL_SNIPER;
			break;
		case CASTER:
			pool = POOL_CASTER;
			break;
		case MEDIC:
			pool = POOL_MEDIC;
			break;
		case SUPPORTER:
			pool = POOL_SUPPORTER;
			break;
		case SPECIALIST:
			pool = POOL_SPECIALIST;
			break;
		}
		// pool = ArrayUtils.addAll(pool, POOL_UNIVERSAL);
		GachaTrait output = pool[r.nextInt(pool.length)];
		return output;
	}

	public String getDisplayName() {
		switch (this) {
		// Vanguard
		case DP_RECOVERY_ON_KILL:
			return "Recovery 1 DP when killing an enemy";
		case DP_REFUND_ALL_RETREAT:
			return "Refund all DP when retreating";
		// Guard
		case NORMAL_ATTACK_DOUBLE:
			return "Attack deals damage twice";
		case NORMAL_ATTACK_ALL_BLOCKED:
			return "Attack all blocked enemies";
		case NORMAL_ATTACK_GUARD_RANGED:
			return "Can use ranged attacks that deals reduced damage";
		case NORMAL_ATTACK_MAGIC:
			return "Attack deals arts damage";
		// Defender
		case SKILL_HEAL_NEARBY:
			return "Skill heals nearby allies";
		// Sniper
		case TARGET_PRIORITY_AIR:
			return "Attack aerial targets first";
		case DAMAGE_IMPROVE_AIR:
			return "Deals increased damage to aerial targets";
		case NORMAL_ATTACK_AOE_PHYSICAL:
			return "Deals AOE physical damage";
		case TARGET_LOWEST_DEFENSE:
			return "Prioritize attacking target with lowest defense";
		// Caster
		case NORMAL_ATTACK_AOE_MAGIC:
			return "Deals AOE magic damage";
		// Medic
		case HEAL_COUNT_3:
			return "Heals 3 allies at once";
		case HEAL_RANGE_UP:
			return "Greatly increased healing range";
		// Supporter
		case NORMAL_ATTACK_SLOW:
			return "Attack slows target for a short time";
		case SUMMON:
			return "Can use summons in battle";
		// Specialist
		case REDEPLOY_FAST:
			return "Significantly reduced redeploy time";
		case SHIFT_PULL:
			return "Can pull targets toward self";
		case SHIFT_PUSH:
			return "Can push targets away from self";
		// Universal
		case BLOCK_COUNT_0:
			return "Cannot block enemies";
		case BLOCK_COUNT_1:
			return "Can block 1 enemies";
		case BLOCK_COUNT_2:
			return "Can block 2 enemies";
		case BLOCK_COUNT_3:
			return "Can block 3 enemies";
		case DODGE_PHYSICAL:
			return "Chance to evade physical damage";
		case DODGE_MAGICAL:
			return "Chance to evade magical damage";
		default:
			return null;
		}
	}
}
