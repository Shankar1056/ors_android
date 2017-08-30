package online.ors.oldraddisold.model;

import com.google.gson.JsonObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 27 Jun 2017 at 2:29 PM
 */

public class PickupItemModel {
	private final double qty;
	private final double totalPrice;
	private final String title;
	private final String unit;
	
	public PickupItemModel(JsonObject item) {
		title = item.get("item_description").getAsString();
		unit = item.get("unit").getAsString();
		qty = item.get("item_quantity").getAsDouble();
		totalPrice = item.get("item_total").getAsDouble();
	}
	
	public double getQty() {
		return qty;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getUnit() {
		return unit;
	}
}
