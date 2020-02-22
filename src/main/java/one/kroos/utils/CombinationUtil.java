package one.kroos.utils;

import java.util.ArrayList;

public class CombinationUtil {

	private int size;
	private int[] data, temp;
	private ArrayList<int[]> result;

	public CombinationUtil(int size) {
		this.size = size;
	}

	private void init() {
		this.data = new int[size];
		for (int a = 0; a < size; a++)
			this.data[a] = a;
		this.result = new ArrayList<int[]>();
	}

	public ArrayList<int[]> getCombination(int length) {
		this.init();
		this.temp = new int[length];
		this.combinationUtil(this.data, this.temp, 0, this.data.length - 1, 0, length);
		return this.result;
	}

	private void combinationUtil(int[] input, int[] data, int start, int end, int index, int length) {
		if (index == length) {
			this.result.add(data.clone());
			return;
		}

		// replace index with all possible elements. The condition
		// "end-i+1 >= r-index" makes sure that including one element
		// at index will make a combination with remaining elements
		// at remaining positions
		for (int i = start; i <= end && end - i + 1 >= length - index; i++) {
			data[index] = input[i];
			combinationUtil(input, data, i + 1, end, index + 1, length);
		}
	}
}
