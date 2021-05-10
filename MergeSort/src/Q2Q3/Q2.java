/**
 * 
 */
package Q2Q3;

import java.util.Comparator;

/**
 * @author Saad Zaher
 *
 */
public class Q2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a String Comparator
		StringComparator sc = new StringComparator();
		// Create a MergeSort Instance for String data type
		MergeSort<String> StringSort = new MergeSort<>();
		// Example String[] to test String sorting
		String[] a1 = {"Hello", "World", "Ireland", ",", "From", "An", "Egyptian", "Living", "In", "galway", "or", "Galway", "What", "is", "the", "craic"};
		// Print array before sorting
		Q2.printArray(a1);
		// Carry on the sort
		StringSort.sort(a1, sc);
		// Print array after sorting
		Q2.printArray(StringSort.getArray());
		
		System.out.println("\n");
		
		// Double sort
		// Create a Comparator for Double data type
		DoubleComparator dc = new DoubleComparator();
		// Create an Instance of MergeSort for Double data type
		MergeSort<Double> DoubleSort = new MergeSort<>();
		// Example unsorted Double[]
		Double[] d1 = {1.6, 1.3, 1.0, 2., 3., 3.3, 2.1, 1.2, 2.1, 6.5, 1.0, .2, 3.1, 0.2, -1., 1.6};
		// Print array before sorting
		Q2.printArray(d1);
		// Sort the array
		DoubleSort.sort(d1, dc);
		// Print array after sorting
		Q2.printArray(DoubleSort.getArray());
		
	}
	
	public static void printArray(Object[] array) {
		// Print array
		for (Object i : array) {
			System.out.print(i);
			System.out.print(" ");
		}
		System.out.println();
	}

}
