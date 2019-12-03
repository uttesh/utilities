package utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import utility.bitbucket.BitbucketService;

public class BitbucketServiceSample {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, ParseException {
		BitbucketService bitbucketService = new BitbucketService();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = dateFormat.parse("2019-11-13T05:45:52.726567");
        long createdOnInMillis = date.getTime();
        System.out.println("datec ::: "+date);
		//bitbucketService.getLatestBuild();
		//bitbucketService.getServiceSpec("2.2");
//		List<String> tags = bitbucketService.getAllTags();
//		System.out.println("tags :: " + tags.size());
//		for (String releaseVersion : tags) {
//			System.out.println("release version :: "+ releaseVersion);
//			//bitbucketService.getServiceSpec(releaseVersion);
//			bitbucketService.getServiceSpec("2.3");
//		}
	}
}
