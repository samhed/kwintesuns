package ida.liu.se.kwintesuns.client;

import java.util.ArrayList;

public class Quicksort  {
	private ArrayList<Post> list;
	private int number;

	public void sort(ArrayList<Post> postList) {
		// Check for empty or null array
		if (postList == null || postList.size()==0){
			return;
		}
		this.list = postList;
		number = postList.size();
		quicksort(0, number - 1);
	}

	/**
	 * Sort the list using the quicksort algorithm
	 * @param low the start of the list to sort
	 * @param high the end of the list to sort
	 */
	private void quicksort(int low, int high) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		Post pivot = list.get(low + (high-low)/2);

		// Divide into two lists
		while (i <= j) {
			// If the current date from the left list is after the pivot
			// element's date then get the next element from the left list
			while (list.get(i).getDate().after(pivot.getDate())) {
				i++;
			}
			// If the current date from the right list is before the pivot
			// element's date then get the next element from the right list
			while (list.get(j).getDate().before(pivot.getDate())) {
				j--;
			}

			// If we have found a date in the left list which is before 
			// the pivot element's date and if we have found a date in the 
			// right list which is after the pivot element's date then we 
			// swap the posts.
			if (i <= j) {
				swap(i, j);
				i++;
				j--;
			}
		}
		// Recursion
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}

	private void swap(int i, int j) {
		Post temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}
}