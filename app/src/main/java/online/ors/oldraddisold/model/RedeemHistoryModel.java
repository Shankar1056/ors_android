package online.ors.oldraddisold.model;

import android.text.format.DateFormat;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 20 May 2017 at 6:14 PM
 */

public class RedeemHistoryModel {
	private final String type;
	private final String status;
	private final String date;
	private final double amount;
	private final boolean isCredit;
	
	public RedeemHistoryModel(JsonObject item) throws ParseException {
		String type = item.get("origin").getAsString();
		this.type = type.substring(0, 1).toUpperCase() + type.substring(1);
		
		switch (this.type) {
			case "Pick Up":
			case "Item Sale":
				status = "#OD" + item.get("schedule_id").getAsString();
				break;
			
			case "Redeem":
				status = item.get("redeem_status").getAsString();
				break;
			
			case "Refund":
				status = this.type;
				break;
			
			default:
				status = item.get("description").getAsString();
				break;
		}
		
		date = DateFormat.format(
		    "MMM dd, yyyy",
		    new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.US)
			.parse(item.get("date").getAsString())
		).toString();
		
		amount = item.get("transaction_amount").getAsDouble();
		isCredit = item.get("transaction_type").getAsInt() == 1;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDate() {
		return date;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public boolean isCredit() {
		return isCredit;
	}
}
