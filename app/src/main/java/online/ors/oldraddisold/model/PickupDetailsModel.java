package online.ors.oldraddisold.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 17 May 2017 at 2:49 PM
 */

public class PickupDetailsModel extends PickupModel {
	private final String address;
	private final String agentContact;
	private final ArrayList<ItemModel> itemList = new ArrayList<>();
	private final ArrayList<PickupItemModel> pickupItemList = new ArrayList<>();
	private double totalPrice = 0;
	
	public PickupDetailsModel(JsonObject pickup) throws ParseException {
		super(pickup);
		address = pickup.get("schedule_address").getAsString();
		agentContact = pickup.has("phone") ? pickup.get("phone").getAsString() : null;
		
		JsonArray items = pickup.getAsJsonArray("selected_items");
		for (JsonElement item : items) {
			itemList.add(new ItemModel((JsonObject) item));
		}
		
		if (pickup.has("transaction_items")) {
			JsonArray pickItems = pickup.getAsJsonArray("transaction_items");
			for (JsonElement item : pickItems) {
				PickupItemModel pickupItem = new PickupItemModel((JsonObject) item);
				pickupItemList.add(pickupItem);
				totalPrice += pickupItem.getTotalPrice();
			}
		}
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getAgentContact() {
		return agentContact;
	}
	
	public ArrayList<ItemModel> getItemList() {
		return itemList;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public ArrayList<PickupItemModel> getPickupItemList() {
		return pickupItemList;
	}
}
