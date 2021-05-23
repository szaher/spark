/**
 * 
 */
package TweetStats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.spark_project.guava.io.Files;

import scala.Tuple2;
import twitter4j.Status;

/**
 * @author Saad Zaher
 *
 */

public class PartA implements Serializable{

	private static final Pattern HASHTAG = Pattern.compile("#(\\w+)");
	
	String consumerKey;
    String consumerSecret;
    String accessToken;
    String accessTokenSecret;
    boolean verbose;
    long total = 0;
    long avgChar = 0;
    
	public PartA(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, boolean verbose) {
		// TODO Auto-generated constructor stub
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		this.verbose = verbose;
	}
	
	public void build() {
	    System.setProperty("hadoop.home.dir", "C:/winutils");
		// set CPU and memory limits
		SparkConf sparkConf = new SparkConf().setAppName("Keams").setMaster("local[4]").set("spark.executor.memory", "1g");
		
	    // Set the system properties so that Twitter4j library used by Twitter stream
	    // can use them to generate OAuth credentials
	    System.setProperty("twitter4j.oauth.consumerKey", consumerKey);
	    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret);
	    System.setProperty("twitter4j.oauth.accessToken", accessToken);
	    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret);
	    
	    // create a streaming context with 1 second duration
	    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(1000));
	  
	    // create a checkpoint directory in temp 
	    jssc.checkpoint(Files.createTempDir().getAbsolutePath());
	    
	    // fetch twitter api
	    JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(jssc);
	    	    
	    // map each tweet to it's text only
	    JavaDStream<String> statuses = stream.map(status -> {
	    	return status.getText();	
	    });
	    
	    /* Create some statistics
	     * using a string builder create <tweet></tweet> contains
	     * Main tweet body
	     * Number of Characters
	     * Number of words
	     * Hashtags in each tweet
	     */
	    JavaDStream<String> stats = stream.map(status -> {
		    
		    StringBuilder sb = new StringBuilder();
		    sb.append(" <tweet> ");
	    	sb.append(status.getText());
	    	sb.append(" || ");
	    	sb.append(" characterCount: ");
	    	sb.append(status.getText().length());
	    	sb.append(" || ");
	    	sb.append(" wordCount: ");
	    	sb.append(Integer.toString(status.getText().split(" ").length));
	    	sb.append(" || ");
	    	Matcher match = HASHTAG.matcher(status.getText());
	    	sb.append(" Tags: ");
			while(match.find()) {
				sb.append(match.group());
				sb.append(" ");
			}
			sb.append(" </tweet> ");
	    	return sb.toString();
	    	
		    });

	    
	    // Print Part A 1, 2
	    // verbose will print each and ever tweet, else will use the built-in print
	    if (verbose) {
	    	// print all statues/tweets
	    	stats.foreachRDD(st -> {
		    	for (String s: st.collect()) {
		    		System.out.println(s);
		    	}
		    	
		    });
	    }else {
	    	stats.print();
	    }
	    // get the total count of tweets all the time
	    statuses.count().foreachRDD(c -> {
	    	List<Long> arr = c.collect();
	    	total = arr.get(0);
	    	System.out.println("Total Tweets: " + total);
	    });
	    
	    // get total character count
	    JavaDStream<Integer> totalCharCount = 
	    		statuses
	    		// a simple but not perfect way to do so is to get the length of the tweet which might contain some spaces but I hope it's Ok for the assignment
	    		.map(status -> status.length())
	    		// reduce to get the final total number
	    		.reduce((a, b) -> a + b);
	    // Total word count.
	    // regex needs a complex formula as it won't match non ASCII Characters like (مرحباً)  
	    // JavaDStream<Integer> wordCount = statuses.map(status -> status.trim().split("\\w+").length );
	    JavaDStream<Integer> totalWordCount = 
	    		statuses
	    		// Trim the tweet, split, get array size/length which represents how many words
	    		.map(status -> status.trim().split(" ").length )
	    		// reduce to get total number
	    		.reduce((a, b) -> a + b);
	    
	    // print total number of Characters
	    totalCharCount.foreachRDD(chr -> {
	    	List<Integer> tChr = chr.collect();
	    	if (total != 0) {
	    		System.out.println("Average Charachter per tweet: " +  tChr.get(0)/total);
	    	}
	    });
	    
	    //print total number of word count
	    totalWordCount.foreachRDD(wrd -> {
	    	List<Integer> tWrd = wrd.collect();
	    	if (total != 0) {
	    		System.out.println("Average Word per tweet: " +  tWrd.get(0)/total);
	    	}
	    });
	    
	    // top 10 hash tags
	    // tokenize tweets by flattening the tweets
	    JavaDStream<String> words = statuses.flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterator<String> call(String status) throws Exception {
				// TODO Auto-generated method stub
				return Arrays.asList(status.split(" ")).iterator();
			}
	    	
		});
	    
	    // filter only hashtags words that start with #
	    JavaDStream<String> hashTags = words.filter(new Function<String, Boolean>() {
	        @Override
	        public Boolean call(String word) {
	          return word.startsWith("#");
	        }
	      });
	    
	    // Mapping hashtags to pairs
	    // so recalling map reduce stuff each hashtag will be (HashTag, 1)  
	    // then reduce by Key; grouping all hashtags together and count the value to get the total occurrence of each hashtag
	    // to sort the hashtags with the highest value first we need to re-map from <String, Long> TO <Long, String> 
	    // then we can only print the first n tags
	    JavaPairDStream<Long, String> toptags = 
	    	hashTags
	    	// map each hashtag to (Hashtag, 1)
	    	.countByValue()
	    	// group by Hashtag name and sum the values (reduce phase)
	    	.reduceByKey(new Function2<Long, Long, Long>() {

				@Override
				public Long call(Long v1, Long v2) throws Exception {
					// TODO Auto-generated method stub
					return v1 + v2;
				}
			})
	    	// Map  to (long, HashTag) to sort by the occurrence 
	    	.mapToPair(new PairFunction<Tuple2<String,Long>, Long, String>() {

	    			@Override
	    			public Tuple2<Long, String> call(Tuple2<String, Long> t) throws Exception {
	    				// TODO Auto-generated method stub
	    				return new Tuple2<>(t._2, t._1);
	    			}
	    	    	
	    		})
	    	// Sort ...
	    	.transformToPair(new Function<JavaPairRDD<Long,String>, JavaPairRDD<Long, String>>() {

	    			@Override
	    			public JavaPairRDD<Long, String> call(JavaPairRDD<Long, String> v1) throws Exception {
	    				// TODO Auto-generated method stub
	    				// sort in descending order 
	    				return v1.sortByKey(false);
	    			}
	    			
	    	});
	    // Print top 10 hashtags
	    
	    toptags.foreachRDD(tg -> {
//	    	System.out.println("Tag Count: " + tg.collect());
	    	List<Tuple2<Long, String>> arr = tg.collect();
	    	int stop = 10;
	    	// if number of tags is less than 10, print everything
	    	if (arr.size() < 10) {
	    		stop = arr.size();
	    	}
	    	System.out.print("Top Tags > ");
	    	// print nothing if no tweets
	    	if (arr.size() > 0) {
	    		for (int i = 0; i < stop; i++) {
		    		System.out.print(arr.get(i));
		    	}
	    	}
	    	// make it look good
	    	System.out.println();
	    	
	    });
	    
	    // repeat every 30 seconds for the last 5 minutes
	    // apply window on the statuses 
	    JavaDStream<String> windowedStatues = statuses.window(Durations.seconds(300), Durations.seconds(30));
	    
	    // get total character count
	    windowedStatues
	    // a simple but not perfect way to do so is to get the length of the tweet which might contain some spaces but I hope it's Ok for the assignment
	    .map(status -> status.length())
	    // reduce to get the final total number
	    .reduce((a, b) -> a + b)
	    .foreachRDD(chr -> {
	    	List<Integer> tChr = chr.collect();
	    	if (total != 0) {
	    		System.out.println("Average Charachter per tweet (Ever 30 seconds): " +  tChr.get(0)/total);
	    	}
	    });
	    
	    // Average Word per tweet (ever 30 seconds)
	    windowedStatues
	    // Trim the tweet, split, get array size/length which represents how many words
	    .map(status -> status.trim().split(" ").length )
	    // reduce to get total number
	    .reduce((a, b) -> a + b)
	    .foreachRDD(wrd -> {
	    	List<Integer> tWrd = wrd.collect();
	    	if (total != 0) {
	    		System.out.println("Average Word per tweet (Ever 30 seconds): " +  tWrd.get(0)/total);
	    	}
	    });
	    
	    // get top hashtags every 30 seconds for the last 5 minutes
	    hashTags
	    // re-calculate every 30 seconds for the last 5 minutes
	    .window(Durations.seconds(300), Durations.seconds(30))
    	// map each hashtag to (Hashtag, 1)
	 // map each hashtag to (Hashtag, 1)
    	.countByValue()
    	// group by Hashtag name and sum the values (reduce phase)
    	.reduceByKey(new Function2<Long, Long, Long>() {

			@Override
			public Long call(Long v1, Long v2) throws Exception {
				// TODO Auto-generated method stub
				return v1 + v2;
			}
		})
    	// Map  to (long, HashTag) to sort by the occurrence 
    	.mapToPair(new PairFunction<Tuple2<String,Long>, Long, String>() {

    			@Override
    			public Tuple2<Long, String> call(Tuple2<String, Long> t) throws Exception {
    				// TODO Auto-generated method stub
    				return new Tuple2<>(t._2, t._1);
    			}
    	    	
    		})
    	// Sort ...
    	.transformToPair(new Function<JavaPairRDD<Long,String>, JavaPairRDD<Long, String>>() {

    			@Override
    			public JavaPairRDD<Long, String> call(JavaPairRDD<Long, String> v1) throws Exception {
    				// TODO Auto-generated method stub
    				// sort in descending order 
    				return v1.sortByKey(false);
    			}
    			
    	})
	    .foreachRDD(tg -> {
	    	// System.out.println("Tag Count: " + tg.collect());
	    	List<Tuple2<Long, String>> arr = tg.collect();
	    	int stop = 10;
	    	// if number of tags is less than 10, print everything
	    	if (arr.size() < 10) {
	    		stop = arr.size();
	    	}
	    	System.out.print("Top Tags (Every 30 seconds) > ");
	    	// print nothing if no tweets
	    	if (arr.size() > 0) {
	    		for (int i = 0; i < stop; i++) {
	    			System.out.print(arr.get(i));
	    		}
	    	}
	    	// make it look good
	    	System.out.println();
	    });
	    
	    
	    // Let it rain! Galway Style :) 
	    jssc.start();
	    try {
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
