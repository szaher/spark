import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import scala.Tuple2;

/**
 * 
 */

/**
 * @author Saad Zaher
 *
 */
public class KMeansA4 implements Serializable {

	String filePath;
	private static final Pattern FIELD_SEPARATOR = Pattern.compile(",");

	public KMeansA4(String filePath) {
		// TODO Auto-generated constructor stub
		this.filePath = filePath;
	}

	public void build() {
		// Spark Configurations
		System.setProperty("hadoop.home.dir", "C:/winutils");
		// set CPU and memory limits
		SparkConf sparkConf = new SparkConf().setAppName("K-Means").setMaster("local[4]").set("spark.executor.memory",
				"1g");
		// Build Spark Context
		JavaSparkContext ctx = new JavaSparkContext(sparkConf);

		// Load data
		JavaRDD<String> rawData = ctx.textFile(filePath);
		
		// Parse the tweets data and convert it to dense Vector
		JavaRDD<Vector> features = rawData.map(line -> {
			// split the line into multiple parts
			String[] parts = FIELD_SEPARATOR.split(line);
			// first two parts are coordinates
			double[] values = { Double.parseDouble(parts[0]), Double.parseDouble(parts[1]) };
			// convert coordinates to dense Vector
			return Vectors.dense(values);
		});
		
		// Since we have multiple iterations, caching is good (or required to minimize network overhead in multi-node deployment)
		features.cache();
		// number of clusters to build from the data
		int numClusters = 4;
		// number of iterations
		int numIterations = 20;
		
		// build the model
		KMeansModel clusters = KMeans.train(features.rdd(), numClusters, numIterations);

		// re-map the data again since we have optional field spam, so we need to check while part contains the actual tweet text
		// then build an RDD of <String, Int> representing Tweet Text and cluster number.
		// the predictions is done one point/tweet at a time.
		JavaRDD<Tuple2<String, Integer>> result = rawData.map(line -> {
			// split again
			String[] parts = FIELD_SEPARATOR.split(line);
			String text = null;
			// if we have the optional field spam
			if (parts.length == 6) {
				int spam = -1;
				// the reason we are doing parse spam however we are not going to use it, is we have a tweet which contains ',' 
				// so it might be confusing to the split method
				try {
					// try to parse spam 
					spam = Integer.parseInt(parts[4]);
					text = parts[5];
				}catch (NumberFormatException e) {
					// TODO: handle exception
					// if failed to parse the spam field, this means it's not really an integer, so it could be text.
					// re constructing text again
					text = parts[4] + " " + parts[5];
				}
			}else {
				// if no spam, get text.
				text = parts[4];
			}
			
			// build dense vector of the test data.
			double [] f = { Double.parseDouble(parts[0]), Double.parseDouble(parts[1]) };
			
			// Make prediction
			int res = clusters.predict(Vectors.dense(f));
			// Return a tuple of tweet text and cluster 
			return new Tuple2<String, Integer>(text, res);
		});
		
		// Sort result in an ascending order
		result.sortBy(new Function<Tuple2<String,Integer>, Integer>() {

			@Override
			public Integer call(Tuple2<String, Integer> arg0) throws Exception {
				// TODO Auto-generated method stub
				// return the cluster number to use it for sorting
				return arg0._2();
			}
			
		}, true, 1)
		// now printing results
		.foreach(res -> {
			System.out.println("Tweet: " + res._1() + " is cluster " + res._2());
		});
		
		// Thanks
		ctx.stop();
		ctx.close();
	}
}
