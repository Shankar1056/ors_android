package online.ors.oldraddisold.util;

import java.util.Locale;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 19 Jun 2017 at 4:22 PM
 */

public class Constants {
	/*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
	public static final String COGNITO_POOL_ID = "ap-south-1:87036e73-412a-40fc-b71a-be478bd17d78";
	
	/*
	 * Region of your Cognito identity pool ID.
	 */
	public static final String COGNITO_POOL_REGION = "ap-south-1";
	
	/*
	 * Note, you must first create a bucket using the S3 console before running
	 * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
	 * put it's name in the field below.
	 */
	public static final String BUCKET_NAME = "orsgallery";
	
	/*
	 * Region of your bucket.
	 */
	public static final String BUCKET_REGION = "ap-southeast-1";
	
	//https://s3-ap-southeast-1.amazonaws.com/orsgallery/illegal-dumping/1497936352360.jpg
	public static String getResourceUrl(String dir, String fileName) {
		return String.format(
		    Locale.UK,
		    "https://s3-%s.amazonaws.com/%s/%s/%s",
		    BUCKET_REGION,
		    BUCKET_NAME,
		    dir,
		    fileName
		);
	}
}
