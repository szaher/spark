/**
 * 
 */
package Q2Q3;

import java.util.*;

/**
 * @author Saad Zaher
 *
 */
public class MergeSort<T> {
	// A modified sort algorithm that accepts generic type parameter, it should be data type agnostic.
	// generic type array 
	T[] array;
	// generic type comparator to compare two objects of T type
	Comparator<T> comparer;
	
	public void sort(T[] array, Comparator<T> comparer) {
		// Generic type parameter function
		// instead of working only with int[], function changed to work with generic types
		this.array = array;
		this.comparer = comparer;
		sort(0, array.length);
	}

	private void sort(int low, int n) {
		if (n > 1) {
			int mid = n >> 1;
			sort(low, mid);
			sort(low + mid, mid);
			combine(low, n, 1);
		}
	}
	
	public T[] getArray() {
		// return the generic type array after sort 
		return array;
	}

	private void combine(int low, int n, int st) {
		int m = st << 1;
		if (m < n) {
			combine(low, n, m);
			combine(low + st, n, m);
			for (int i = low + st; i + st < low + n; i += m)
				compareAndSwap(i, i + st);
		} else
			compareAndSwap(low, low + st);
	}

	private void compareAndSwap(int i, int j) {
		// use Comparator object to compare two instances of type T
		// the comparator is expected to return values >= 1 if array[i] > array[j]
		// and if so, we should swap entries within the array
		if (comparer.compare(array[i], array[j]) > 0) {
			swap(i, j);
		}
		
	}

	private void swap(int i, int j) {
		// create a temporary object h of generic type T to swap values
		T h = array[i];
		array[i] = array[j];
		array[j] = h;
	}
}
