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
 * @created on 16 May 2017 at 5:45 PM
 */

public class TimeIntervalModel implements Serializable {
	private final int id;
	private final String timeSlot;
	private final Date startTime;
	
	public TimeIntervalModel(JsonObject time) throws ParseException {
		id = time.get("slot_id").getAsInt();
		SimpleDateFormat sdf = new SimpleDateFormat("H:m:s", Locale.UK);
		startTime = sdf.parse(time.get("slot_start").getAsString());
		Date end = sdf.parse(time.get("slot_end").getAsString());
		timeSlot = DateFormat.format("hh:mm a", startTime).toString().toUpperCase()
		    + " to " + DateFormat.format("hh:mm a", end).toString().toUpperCase();
	}
	
	public String getTimeSlot() {
		return timeSlot;
	}
	
	public int getId() {
		return id;
	}
	
	public Date getStartTime() {
		return startTime;
	}
}
