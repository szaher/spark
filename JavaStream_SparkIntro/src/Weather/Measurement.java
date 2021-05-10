package Weather;
/**
 * 
 */

import java.io.Serializable;

/**
 * @author Saad Zaher 
 * Measurement class holds the measurement of a Weather Station at a given time
 */
public class Measurement implements Serializable{
	// Time of the measurement 
	int time;
	// temperature value
	double temperature;
	
	// Constructor
	public Measurement(int time, double temperature) {
		this.time = time;
		this.temperature = temperature;
	}
	
	// Setter Method
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	
	// Getter Method
	public double getTemperature() {
		return temperature;
	}
	
	// Setter Method	
	public void setTime(int time) {
		this.time = time;
	}
	
	// Getter Method
	public int getTime() {
		return time;
	}
	
	
}
