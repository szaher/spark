package Weather;
/**
 * 
 */

/**
 * @author Saad Zaher 
 * Pair class will hold Outkey, Intermediate value and Outkey, OutValue results
 * It will be used to create pairs of key and value during different operations on the streams
 */
public class Pair {
	// Key has to be double since it's usually the temperature
	double Key;
	// how often it occurred
	int Value;
	
	// Constructor
	public Pair(double key, int value) {
		// TODO Auto-generated constructor stub
		this.Key = key;
		this.Value = value;
	}
	
	// Getter Method
	public Double getKey() {
		return Key;
	}
	
	// Setter Method
	public void setKey(double key) {
		Key = key;
	}
	
	// Getter Method
	public int getValue() {
		return Value;
	}
	
	// Setter Method
	public void setValue(int value) {
		Value = value;
	}
	
	
}
