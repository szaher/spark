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
public class Q2 {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// First list of measurement
		Measurement[] measures1 = {
				new Measurement(1, 10.4), 
				new Measurement(2, 10.3),
				new Measurement(2, 56.3),
				new Measurement(2, 10.3),
				new Measurement(3, 190.3),
				new Measurement(2, 100.4),
				new Measurement(3, 17.3),
				new Measurement(3, 19.3),
				new Measurement(4, 10.3),
				new Measurement(5, 220.3)
		};
		// Second list of measurement
		Measurement[] measures2 = {
				new Measurement(1, 9.4), 
				new Measurement(2, 11.3),
				new Measurement(2, 16.5),
				new Measurement(2, 18.2),
				new Measurement(3, 10.6),
				new Measurement(2, 10.5),
				new Measurement(3, 8.7),
				new Measurement(3, 13.),
				new Measurement(4, 12.4),
				new Measurement(5, 22.9)
		};
		// Third list of measurement
		Measurement[] measures3 = {
				new Measurement(5, 9.4), 
				new Measurement(3, 11.3),
				new Measurement(7, 18.5),
				new Measurement(5, 8.2),
				new Measurement(3, 8.1),
				new Measurement(1, 10.5),
				new Measurement(3, 8.7),
				new Measurement(3, 19.),
				new Measurement(4, 20.4),
				new Measurement(5, 22.9)
		};
		// Three Weather stations at three different Cities
		WeatherStation ws1 = new WeatherStation("Galway", measures1);
		WeatherStation ws2 = new WeatherStation("Dublin", measures2);
		WeatherStation ws3 = new WeatherStation("Cairo", measures3);
		// updating the stations field
		ws1.stations = Arrays.asList(ws1, ws2, ws3);
		// Counting Temperature
		List<Pair> counts = ws1.countTemperature(10., 20., 2.);
		// Print the outkey, outvalue (The result of the count)
		for (Pair pair : counts) {
			System.out.println("(" + pair.Key + ", " + pair.Value + ")");
		}
		
		System.getenv().entrySet().forEach(System.out::println);
		
		
	}

}
