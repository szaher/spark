/**
 * 
 */
package Q1;

/**
 * @author Saad Zaher
 *
 */
public class MergeSort {
	
	int[] array;
	
	public void sort(int[] array) {
		// array length must be a power of 2
		this.array = array;
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
	
	public int[] getArray() {
		// return the int[] array 
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
		if (array[i] > array[j])
			swap(i, j);
	}

	private void swap(int i, int j) {
		int h = array[i];
		array[i] = array[j];
		array[j] = h;
	}
}
