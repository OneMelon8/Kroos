package one.kroos.database.gacha;

import java.util.ArrayList;
import java.util.Arrays;

import one.kroos.database.Emotes;

public enum GachaClass {

	GUARD, MEDIC, VANGUARD, CASTER, SNIPER, DEFENDER, SUPPORTER, SPECIALIST;

	public static int size() {
		return GachaClass.values().length;
	}

	public static ArrayList<GachaClass> toArrayList() {
		return new ArrayList<GachaClass>(Arrays.asList(GachaClass.values()));
	}

	public String getEmote() {
		switch (this) {
		case GUARD:
			return Emotes.ARKNIGHTS_CLASS_GUARD;
		case MEDIC:
			return Emotes.ARKNIGHTS_CLASS_MEDIC;
		case VANGUARD:
			return Emotes.ARKNIGHTS_CLASS_VANGUARD;
		case CASTER:
			return Emotes.ARKNIGHTS_CLASS_CASTER;
		case SNIPER:
			return Emotes.ARKNIGHTS_CLASS_SNIPER;
		case DEFENDER:
			return Emotes.ARKNIGHTS_CLASS_DEFENDER;
		case SUPPORTER:
			return Emotes.ARKNIGHTS_CLASS_SUPPORTER;
		case SPECIALIST:
			return Emotes.ARKNIGHTS_CLASS_SPECIALIST;
		}
		return null;
	}
}
