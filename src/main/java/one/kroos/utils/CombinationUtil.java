package one.kroos.utils;

import java.util.ArrayList;

public class CombinationUtil {

	private int[] data, temp;
	private ArrayList<int[]> result;

	public CombinationUtil(int size) {
		this.data = new int[size];
		for (int a = 0; a < size; a++)
			this.data[a] = a;
		this.result = new ArrayList<int[]>();
	}

	/*
	 * arr[] ---> Input Array data[] ---> Temporary array to store current
	 * combination start & end ---> Staring and Ending indexes in arr[] index --->
	 * Current index in data[] r ---> Size of a combination to be printed
	 */
	private void combinationUtil(int[] arr, int[] data, int start, int end, int index, int r) {
		// Current combination is ready to be printed, print it
		if (index == r) {
			int[] output = new int[r];
			for (int j = 0; j < r; j++)
				output[j] = data[j];
			this.result.add(output);
			return;
		}

		// replace index with all possible elements. The condition
		// "end-i+1 >= r-index" makes sure that including one element
		// at index will make a combination with remaining elements
		// at remaining positions
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data[index] = arr[i];
			combinationUtil(arr, data, i + 1, end, index + 1, r);
		}
	}

	// The main function that prints all combinations of size r
	// in arr[] of size n. This function mainly uses combinationUtil()
	public ArrayList<int[]> getCombination(int r) {
		// A temporary array to store all combination one by one
		this.temp = new int[r];
		// Print all combination using temprary array 'data[]'
		this.combinationUtil(this.data, this.temp, 0, this.data.length - 1, 0, r);
		return this.result;
	}

	// Driver function to check for above function
	public static void main(String[] args) {
		ArrayList<int[]> result = new CombinationUtil(5).getCombination(3);
		for (int[] i : result) {
			for (int ii : i)
				System.out.print(ii + " ");
			System.out.println();
		}
	}
}
