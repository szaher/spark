package Weather;
import java.util.Arrays;
import java.util.List;

/**
 * 
 */

/**
 * @author Saad Zaher 
 *
 */
public class Q1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Example list of Measurements
		Measurement[] measures = {
				new Measurement(1, 10.4), 
				new Measurement(2, 10.3),
				new Measurement(2, 56.3),
				new Measurement(2, 10.3),
				new Measurement(3, 190.3),
				new Measurement(2, 100.4),
				new Measurement(3, 10.3),
				new Measurement(3, 10.3),
				new Measurement(4, 10.3),
				new Measurement(5, 220.3)
		};
		// Example Weather Station
		WeatherStation ws = new WeatherStation("Galway", measures);
		// Print the Maximum result
		System.out.println("Maximum Temperature: " + ws.maxTemperature(1, 3));
	}

}
