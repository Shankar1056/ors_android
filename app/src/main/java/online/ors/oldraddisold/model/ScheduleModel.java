package online.ors.oldraddisold.model;

import android.text.format.DateFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 16 May 2017 at 4:34 PM
 */

public class ScheduleModel implements Serializable {
	private ArrayList<ItemModel> itemList;
	private double latitude;
	private double longitude;
	private String date;
	private TimeIntervalModel timeInterval;
	private String address;
	private String note;
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setTimeInterval(TimeIntervalModel timeInterval) {
		this.timeInterval = timeInterval;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("scheduled_date", date);
		object.addProperty("slot_id", timeInterval.getId());
		object.addProperty("pick_up_lat", latitude);
		object.addProperty("pick_up_long", longitude);
		object.addProperty("pick_up_address", address);
		object.addProperty("notes", note);
		
		if (itemList != null) {
			JsonArray items = new JsonArray();
			for (ItemModel item : itemList) {
				items.add(item.getId());
			}
			
			object.add("selected_items", items);
		}
		
		return object;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public ArrayList<ItemModel> getItemList() {
		return itemList;
	}
	
	public void setItemList(ArrayList<ItemModel> itemList) {
		this.itemList = itemList;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getTime() {
		return timeInterval.getTimeSlot();
	}
	
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
		try {
			return DateFormat.format("dd MMM yyyy", sdf.parse(date)).toString();
		} catch (ParseException e) {
			return date;
		}
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
}
