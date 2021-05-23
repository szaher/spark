/**
 * 
 */
package TweetStats;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Saad Zaher
 *
 */
public class Main {

	/**
	 * @param args
	 */
//	private static final Pattern HASHTAG = Pattern.compile("#(\\w+)");
	public static void main(String[] args) {
		// credentials required to communicate with Twitter API
		// please enter your own values for testing ...
		String consumerKey = "";
	    String consumerSecret = "";
	    String accessToken = "";
	    String accessTokenSecret = "";
	    
	    if (consumerKey == "" || consumerSecret == "" || accessToken == "" || accessTokenSecret == "") {
	    	System.out.println("Missing required api keys to run this application");
	    	System.exit(1);
	    }
	    
	    // Create an instance of class PartA
		PartA q = new PartA(consumerKey, consumerSecret, accessToken, accessTokenSecret, false);
		// run the core function
		q.build();
		

	}

}
