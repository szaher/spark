/**
 * 
 */
package Q4;

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

	/**
	 * Since we divide the array into two half, we can sort each half in a separate thread
	 * using lambda expression to create runnable objects and pass it on to thread object to run it
	 * @param low
	 * @param n
	 */
	private void sort(int low, int n) {
		if (n > 1) {
			int mid = n >> 1;
			// create a lambda expression of Runnable to sort the first half of the array as separate thread
			Runnable r1 = ()->sort(low, mid);
			// create a lambda expression of Runnable to sort the second half of the array as separate thread
			Runnable r2 = ()->sort(low + mid, mid);
			
			Thread t1 = new Thread(r1);
			Thread t2 = new Thread(r2);
			
			// start the first thread
			t1.start();
			// start the second thread
			t2.start();
			// the t1.join and t2.join will instruct the main thread not exit before t1 and t2 are complete
			try {
				t1.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				t2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Now time to combine
			combine(low, n, 1);
		}
	}

	public int[] getArray() {
		// A getter function to return the array
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
