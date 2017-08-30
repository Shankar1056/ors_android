package online.ors.oldraddisold.model;

import android.text.format.DateFormat;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 22 May 2017 at 6:37 PM
 */

public class FeedModel implements Serializable {
	private final int articleId;
	private final String title;
	//private final String desc;
	private final String imageLink;
	//private final String createdDate;
	private final String modifiedDate;
	private final int commentCount;
	
	public FeedModel(JsonObject item) throws ParseException {
		articleId = item.get("article_id").getAsInt();
		title = item.get("article_title").getAsString();
		//desc = item.get("short_desc").getAsString();
		imageLink = item.get("image_link").getAsString();
		//createdDate = item.get("created_date").getAsString();
		modifiedDate = DateFormat.format(
		    "MMM dd",
		    new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.US)
			.parse(item.get("modified_date").getAsString())
		).toString();
		commentCount = item.get("num_comments").getAsInt();
	}
	
	public String getImageLink() {
		return imageLink;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getModifiedDate() {
		return modifiedDate;
	}
	
	public int getCommentCount() {
		return commentCount;
	}
	
	public JsonObject toJson(int offset) {
		JsonObject object = toJson();
		object.addProperty("offset", offset);
		return object;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("article_id", articleId);
		return object;
	}
	
	public JsonObject toJson(String comment) {
		JsonObject object = toJson();
		object.addProperty("comment_text", comment);
		return object;
	}
}
