package one.kroos.database;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import one.kroos.utils.CombinationUtil;
import one.kroos.utils.FileUtil;
import one.kroos.utils.LogUtil;

public class RecruitDatabase {

	private static JsonObject recruitData, operatorData;

	public static ArrayList<String> getIntersection(ArrayList<RecruitTag> tags) {
		if (tags.isEmpty())
			return new ArrayList<String>(); // return empty list
		JsonObject recruitObj = getRecruitData();
		ArrayList<ArrayList<String>> operatorsByTag = new ArrayList<ArrayList<String>>();
		Type listType = new TypeToken<ArrayList<String>>() {
		}.getType();
		for (RecruitTag tag : tags)
			operatorsByTag.add(new Gson().fromJson(recruitObj.get(tag.getDisplayName()).getAsJsonArray(), listType));

		// Calculate intersections
		ArrayList<String> output = operatorsByTag.get(0);
		for (int a = 1; a < operatorsByTag.size(); a++)
			output.retainAll(operatorsByTag.get(a));
		return output;
	}

	public static ArrayList<String> getHighRarityOperators(ArrayList<String> operators) {
		if (operators.isEmpty())
			return new ArrayList<String>(); // return empty list
		JsonObject operatorObj = getOperatorData();
		ArrayList<String> highRarityOperators = new ArrayList<String>();
		for (String operator : operators)
			if (operatorObj.get(operator).getAsInt() >= 4)
				highRarityOperators.add(operator);
		return highRarityOperators;
	}

	public static int getRarity(String operator) {
		return getOperatorData().get(operator).getAsInt();
	}

	public static boolean hasLowRarityOperators(ArrayList<String> operators) {
		if (operators.isEmpty())
			return false;
		JsonObject operatorObj = getOperatorData();
		for (String operator : operators)
			if (operatorObj.get(operator).getAsInt() < 4)
				return true;
		return false;
	}

	public static JsonObject getRecruitData() {
		if (recruitData != null)
			return recruitData;
		try {
			recruitData = FileUtil.read("database/recruit");
			return recruitData;
		} catch (FileNotFoundException e) {
			LogUtil.error("FileNotFoundException caught when getting local recruit data...");
			e.printStackTrace();
			return null;
		}
	}

	public static JsonObject getOperatorData() {
		if (operatorData != null)
			return operatorData;
		try {
			operatorData = FileUtil.read("database/operators");
			return operatorData;
		} catch (FileNotFoundException e) {
			LogUtil.error("FileNotFoundException caught when getting local operator data...");
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<RecruitTag> tags = new ArrayList<RecruitTag>(Arrays.asList(RecruitTag.SENIOR_OPERATOR,
				RecruitTag.VANGUARD, RecruitTag.CASTER, RecruitTag.MELEE, RecruitTag.DP_RECOVERY));

		for (int b = tags.size(); b > 0; b--) {
			ArrayList<int[]> combos = new CombinationUtil(tags.size()).getCombination(b);
			for (int[] combo : combos) {
				ArrayList<RecruitTag> comboTags = new ArrayList<RecruitTag>();
				for (int c : combo)
					comboTags.add(tags.get(c));
				ArrayList<String> operators = getIntersection(comboTags);
				if (operators.isEmpty())
					continue;
				if (hasLowRarityOperators(operators))
					continue;
				System.out.println(RecruitTag.getDisplayNames(comboTags) + ":");
				for (String s : operators)
					System.out.print(s + ", ");
				System.out.println();
			}
		}

	}

	public static void printIntArr(int[] arr) {
		for (int a : arr)
			System.out.print(a + ", ");
		System.out.println();
	}

}
