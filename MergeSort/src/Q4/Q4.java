/**
 * 
 */
package Q4;

/**
 * @author Saad Zaher
 *
 */
public class Q4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// main method
		
		// example array to be sorted
		int[] a1 = { 1, 2, 3, 2, 34, 33, 21, 12, 21, 65, 10, 2, 3, 0, -1, 16 };
		// print array before sorting
		Q4.printArray(a1);
		// create an instance of the MergeSort
		MergeSort ms = new MergeSort();
		// Sort the array
		ms.sort(a1);
		// Print the array after sorting
		Q4.printArray(ms.getArray());
	}

	public static void printArray(int[] array) {
		for (int i : array) {
			System.out.print(i);
			System.out.print(" ");
		}
		System.out.println();
	}

}
