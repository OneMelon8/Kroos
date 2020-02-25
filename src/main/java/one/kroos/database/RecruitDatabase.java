package one.kroos.database;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import one.kroos.utils.FileUtil;
import one.kroos.utils.LogUtil;

public class RecruitDatabase {

	private static JsonObject recruitData, operatorData;

	public static ArrayList<String> getUnion(ArrayList<RecruitTag> tags) {
		if (tags.isEmpty())
			return new ArrayList<String>(); // return empty list
		JsonObject recruitObj = getRecruitData();
		ArrayList<String> operatorsByTag = new ArrayList<String>();
		Type listType = new TypeToken<ArrayList<String>>() {
		}.getType();
		for (RecruitTag tag : tags)
			operatorsByTag.addAll(new Gson().fromJson(recruitObj.get(tag.getDisplayName()).getAsJsonArray(), listType));

		Set<String> set = new HashSet<>(operatorsByTag);
		operatorsByTag.clear();
		operatorsByTag.addAll(set);

		return operatorsByTag;
	}

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

	public static ArrayList<String> getRemoveUnderTwoStarOperators(ArrayList<String> operators) {
		if (operators.isEmpty())
			return new ArrayList<String>(); // return empty list
		JsonObject operatorObj = getOperatorData();
		ArrayList<String> output = new ArrayList<String>();
		for (String operator : operators)
			if (operatorObj.get(operator).getAsInt() > 2)
				output.add(operator);
		return output;
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
		ArrayList<RecruitTag> tags = new ArrayList<RecruitTag>(
				Arrays.asList(RecruitTag.NUKER, RecruitTag.SENIOR_OPERATOR));
		ArrayList<String> operators = getUnion(tags);
		System.out.println(RecruitTag.getDisplayNames(tags) + ":");
		for (String s : operators)
			System.out.print(s + ", ");
		System.out.println();
	}

}
