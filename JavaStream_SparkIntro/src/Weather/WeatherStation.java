package Weather;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.*;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

/**
 * 
 */

/**
 * @author Saad Zaher
 *
 *         WeatherStation class
 */

public final class WeatherStation implements Serializable {
	// Name of the city where the Weather Station is located
	String city;
	// List of Measurements captured by the Weather Station
	List<Measurement> StationMeasurements;
	// List of Stations
	public static List<WeatherStation> stations;

	// Constructor
	public WeatherStation(String city, Measurement[] StationMeasurements) {
		this.city = city;
		this.StationMeasurements = Arrays.asList(StationMeasurements);

	}

	// Find the maximum temperature
	public double maxTemperature(int startTime, int endTime) {
		// Retrieve the Maximum temperature using Stream
		double MaxTemperature = StationMeasurements
				// hopefully speed things a little or just process them in parallel
				.parallelStream()
				// Filter all temperature values based on the start time and end time
				.filter(m -> m.time >= startTime && m.time <= endTime)
				// Mapping Measurement to value of temperature
				.mapToDouble(temp -> temp.getTemperature())
				// Find the maximum
				.max().orElseThrow(NoSuchElementException::new);

		return MaxTemperature;
	}

	public List<Pair> countTemperature(double t1, double t2, double r) {

		// Splitting
		// Create a stream of station => Temperature Measurement
		// using a data holder class InputPair(WeatherStation ws, Stream<Measurements>
		// st)
		Stream<InputPair> Input = stations.parallelStream()
				// Input key is the station and input value is the Station Measurements
				.map(station -> new InputPair(station, station.StationMeasurements.stream()));

		// Mapping
		// for each temperature in the measurements of each station
		// if it meets t1 condition return pair of outkey, intermediate value
		// filter to remove any null or unwanted values
		Stream<Stream<Pair>> IntermediateValues = Input.map(in -> {
			return in.temperatures.map(t -> {
				if (t.getTemperature() >= (t1 - r) && t.getTemperature() <= (t1 + r)) {
					return new Pair(t1, 1);
				}
				// two if conditions instead of if else so if the temperature matches both
				// conditions we catch it
				if (t.getTemperature() >= (t2 - r) && t.getTemperature() <= (t2 + r)) {
					return new Pair(t2, 1);
				}
				return null;
			}).filter(t -> t != null);
		});

		// Convert Stream<Stream<Pair>> to Stream<Pair> by flattening results for easier
		// calculations
		// I added sequential for testing purposes to print values in order to make sure
		// my sorting was Ok
		Stream<Pair> shuffle = IntermediateValues.flatMap(iv -> iv).sorted(Comparator.comparing(Pair::getKey))
				.sequential();
//				.collect(Collectors.toList());

		// initialize Results should be outkey, outvalue
		Pair t1Result = new Pair(t1, 0);
		// initialize Results should be outkey, outvalue
		Pair t2Result = new Pair(t2, 0);

		// reduce
		// group shuffle items (two keys one for t1 and one for t2)
		shuffle
				// starting the reduce with t1Result as initial value represented by a
				.reduce(t1Result, (a, b) -> {
					if (a.Key == b.Key) {
						a.setValue(a.getValue() + b.getValue());
						return a;
					}
					// if the key didn't match t1Result, let's check t2Result
					if (t2Result.Key == b.Key) {
						t2Result.setValue(t2Result.getValue() + b.getValue());
					}
					return t2Result;
				});
		// Return a list of Pairs
		return Arrays.asList(t1Result, t2Result);

	}

	public void countTemperature(double t) {
		// Spark Configurations
		System.setProperty("hadoop.home.dir", "C:/winutils");
		// set CPU and memory limits
		SparkConf sparkConf = new SparkConf().setAppName("CountTemperature").setMaster("local[4]")
				.set("spark.executor.memory", "1g");
		// Build Spark Context
		JavaSparkContext ctx = new JavaSparkContext(sparkConf);

		// parallelize the stations collection
		List<Tuple2<Double, Integer>> output = ctx.parallelize(this.stations)
				// Flatten the list to hold only StationMeasurements instead of list[list[measurements..], list[measurements...]]
				.flatMap(station -> station.StationMeasurements.iterator())
				// map the flat map to list of pairs only if it matches the condition
				.mapToPair(measure -> {
					// if temperature falls between [t-1, t+1] return it as tuple
					if (measure.getTemperature() >= t - 1 && measure.getTemperature() <= t + 1) {
						return new Tuple2<Double, Integer>(t, 1);
					}
					// Another way I could have implemented this (probably would be easier and cleaner is to return a tuple anyway if condition is met return 1 if not met return 0
					// reduce step won't be affected anyway but might be more pairs shuffle which could just create some network traffic for nothing
					return null;
				})
				// filter out the null ones, so we only keep the ones matching our condition
				.filter(new Function<Tuple2<Double, Integer>, Boolean>() {
					@Override
					public Boolean call(Tuple2<Double, Integer> arg0) throws Exception {
						// TODO Auto-generated method stub
						return arg0 != null;
					}
				})
				// aggregate results
				.reduceByKey((Integer v1, Integer v2) -> v1 + v2)
				// collect it to a list of Tuple (t, count)
				.collect();
		
		// print it
		if (output.size() == 0) {
			System.out.println("Temperature Count: (" + t + ", 0)");
		}
		for (Tuple2<?, ?> tuple : output)
			System.out.println("Temperature Count: (" + tuple._1() + ", " + tuple._2() + ")");

		// Thanks Spark ;)
		ctx.stop();
		ctx.close();
	}
}
