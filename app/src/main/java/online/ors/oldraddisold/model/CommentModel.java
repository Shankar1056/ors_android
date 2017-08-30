package online.ors.oldraddisold.model;


import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import online.ors.oldraddisold.util.Preference;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 24 May 2017 at 6:13 PM
 */

public class CommentModel {
	private final String name;
	private final String comment;
	private final long time;
	private final String avatarUrl;
	
	public CommentModel(JsonObject comment) throws ParseException {
		name = comment.get("name").getAsString();
		time = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.UK)
		    .parse(comment.get("created_time").getAsString()).getTime();
		this.comment = comment.get("comment_text").getAsString();
		avatarUrl = comment.get("avatar_link").getAsString();
	}
	
	public CommentModel(Preference pref, String comment) {
		name = pref.getName();
		time = Calendar.getInstance().getTime().getTime();
		this.comment = comment;
		avatarUrl = pref.getAvatar();
	}
	
	public String getAvatarUrl() {
		return avatarUrl;
	}
	
	public String getName() {
		return name;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getComment() {
		return comment;
	}
}
