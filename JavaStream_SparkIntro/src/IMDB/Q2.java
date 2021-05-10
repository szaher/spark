
/**
 * @author Saad Zaher
 *
 */

package IMDB;


public class Q2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SVM sv = new SVM("data/imdb_labelled.txt", 1000, 0.1);
		sv.build();
		
	}

}
