package Weather;
import java.util.stream.Stream;

/**
 * 
 */

/**
 * @author Saad Zaher 
 * A class just to preapre the input for the Map Reduce
 * Split stations into WeatherStation, Stream of Measurement
 */
public class InputPair {
	// Weather station
	WeatherStation ws;
	// A stream of measures of the Weather Station
	Stream<Measurement> temperatures;
	
	// Constructor
	public InputPair(WeatherStation ws, Stream<Measurement> temStream) {
		// TODO Auto-generated constructor stub
		this.ws = ws;
		this.temperatures = temStream;
	}
	
	// Getter method
	public Stream<Measurement> getTemperatures() {
		return temperatures;
	}
	
	// Setter Method	
	public void setTemperatures(Stream<Measurement> temperatures) {
		this.temperatures = temperatures;
	}
	
	// Getter method
	public WeatherStation getWs() {
		return ws;
	}
	
	// Setter Method
	public void setWs(WeatherStation ws) {
		this.ws = ws;
	}
}
