/**
 * 
 */
package IMDB;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.optimization.L1Updater;
import scala.Tuple2;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;

/**
 * @author Saad Zaher
 *
 */
public class SVM implements Serializable {

	// path to the data file
	String filePath;
	// how many iterations for training
	int numOfIterations;
	// Regularization Parameter
	double regularization;
	// Line & Label separator
	private static final Pattern LABEL_STOP = Pattern.compile("  	");

	// allow some parameters to customize the model
	public SVM(String path, int numOfIterations, double regularization) {
		// TODO Auto-generated constructor stub
		filePath = path;
		this.numOfIterations = numOfIterations;
		this.regularization = regularization;
	}

	public void build() {
		// Spark Configurations
		System.setProperty("hadoop.home.dir", "C:/winutils");
		// set CPU and memory limits
		SparkConf sparkConf = new SparkConf().setAppName("SVM").setMaster("local[4]").set("spark.executor.memory",
				"1g");
		// Build Spark Context
		JavaSparkContext ctx = new JavaSparkContext(sparkConf); 

		final HashingTF tf = new HashingTF(100);
		
		//JavaRDD<LabeledPoint> rawData = MLUtils.loadLabeledPoints(this.ctx, "data/imdb_labelled.txt").toJavaRDD();
		
		//Load data
		JavaRDD<String> rawData = ctx.textFile(filePath);
		// Split lines into (label, words)
		JavaRDD<LabeledPoint> labeledData = rawData.map(new Function<String, LabeledPoint>() {

			@Override
			public LabeledPoint call(String lineReview) throws Exception {
				// TODO Auto-generated method stub
				// split using the label stop separator (3 spaces)
				String[] parts = LABEL_STOP.split(lineReview);
				// return a labeled point
				return new LabeledPoint(Double.parseDouble(parts[1]), tf.transform(Arrays.asList(parts[0].split(" "))));
			}
		});
		// Split Training and testing 
		// Sample with 60% of the data and no duplicates
		JavaRDD<LabeledPoint> train = labeledData.sample(false, 0.6, 1251L);
		// subtract the data - train = test ( the remaining 40 %)
		JavaRDD<LabeledPoint> test = labeledData.subtract(train);
		// Cache the training data since it will be used in multiple iterations
		train.cache();
		// However the SVMWithSGD is static but I'll create my own object to customize the SVM configuration
		SVMWithSGD svml = new SVMWithSGD();
		// set the parameters we obtained from the class constructor
		svml.optimizer().setNumIterations(this.numOfIterations).setRegParam(this.regularization)
				.setUpdater(new L1Updater());
		
		// Build the model
		final SVMModel model = svml.train(train.rdd(), this.numOfIterations);

		// loop over test data and predict each point and rebuild the RDD list of (score, label)
		JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test.map(new Function<LabeledPoint, Tuple2<Object, Object>>() {
			public Tuple2<Object, Object> call(LabeledPoint p) {
				Double score = model.predict(p.features());
				return new Tuple2<Object, Object>(score, p.label());
			}
		});

		// Get evaluation metrics.
		BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
		
		model.clearThreshold();
		
		// Calculate the ROC
		double auROC = metrics.areaUnderROC();

		System.out.println("Area under ROC = " + auROC);
//		System.out.println(metrics.scoreAndLabels());
		// Thanks Spark!
		ctx.stop();
		ctx.close();
	}

}
