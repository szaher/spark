/**
 * 
 */
package Q1;

/**
 * @author Saad Zaher
 *
 */
public class Q1 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// entry point for the application
		// create an array of 16 integers to be sorted
		int[] a1 = {1, 2, 3, 2, 34, 33, 21, 12, 21, 65, 10, 2, 3, 0, -1, 16};
		// print array before sorting
		Q1.printArray(a1);
		
		MergeSort ms = new MergeSort();
		// run the sort algorithm
		ms.sort(a1);
		// print the array after sorting, 
		// since java defaults to pass by value so we need to get the sorted array
		Q1.printArray(ms.getArray());
	}
	
	public static void printArray(int[] array) {
		// a simple function to print the array
		for (int i: array) {
			System.out.print(i);
			System.out.print(" ");
		}
		System.out.println();
	}

}
