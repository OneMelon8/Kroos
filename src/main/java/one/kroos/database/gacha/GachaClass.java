package one.kroos.database.gacha;

import java.util.ArrayList;
import java.util.Arrays;

public enum GachaClass {

	GUARD, MEDIC, VANGUARD, CASTER, SNIPER, DEFENDER, SUPPORTER, SPECIALIST;

	public static int size() {
		return GachaClass.values().length;
	}

	public static ArrayList<GachaClass> toArrayList() {
		return new ArrayList<GachaClass>(Arrays.asList(GachaClass.values()));
	}
}
