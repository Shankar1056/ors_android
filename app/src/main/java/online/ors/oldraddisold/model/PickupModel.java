package online.ors.oldraddisold.model;

import android.text.format.DateFormat;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 17 May 2017 at 11:54 AM
 */

public class PickupModel implements Serializable {
	private final String date;
	private final String time;
	private final String status;
	private final int pickupId;
	
	public PickupModel(JsonObject item) throws ParseException {
		pickupId = item.get("schedule_id").getAsInt();
		status = item.get("schedule_status").getAsString();
		
		SimpleDateFormat sdf = new SimpleDateFormat("H:m:s", Locale.UK);
		Date start = sdf.parse(item.get("slot_start").getAsString());
		Date end = sdf.parse(item.get("slot_end").getAsString());
		time = DateFormat.format("hh:mm a", start).toString().toUpperCase()
		    + " to " + DateFormat.format("hh:mm a", end).toString().toUpperCase();
		
		sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.UK);
		date = DateFormat.format("dd MMM yyyy", sdf.parse(item.get("scheduled_date")
		    .getAsString())).toString().toUpperCase();
	}
	
	public String getDate() {
		return date;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getPickupId() {
		return pickupId;
	}
	
	public JsonObject toJson(String note) {
		JsonObject object = toJson();
		object.addProperty("notes", note);
		return object;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("schedule_id", pickupId);
		return object;
	}
}
