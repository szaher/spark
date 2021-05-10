/**
 * 
 */
package Q2Q3;

import java.util.Comparator;

/**
 * @author Saad Zaher
 *
 */
public class DoubleComparator implements Comparator<Double>{
	// A Child class that implements interface Comparator for Double data type
	@Override
	public int compare(Double o1, Double o2) {
		// Implementation of the compare method
		// it compares two instances of the data type Double
		if (o1.equals(o2)) {
			// if both are equal return 0
			return 0;
		}
		
		if (o1 > o2) {
			// this is the main check, if the first element is bigger than the second element
			// return 1 so the sort algorithm knows we should swap
			return 1;
		}
		// otherwise return -1 which means second element > first element
		return -1;
	}
	

}
