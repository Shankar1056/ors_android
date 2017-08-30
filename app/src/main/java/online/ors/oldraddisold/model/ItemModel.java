package online.ors.oldraddisold.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 16 May 2017 at 3:38 PM
 */

public class ItemModel implements Serializable {
	private final int id;
	private final double price;
	private final String title;
	private final String unit;
	private final String iconUrl;
	private boolean isSelected;
	
	public ItemModel(JsonObject item) {
		id = item.get("item_id").getAsInt();
		title = item.get("item_name").getAsString();
		price = item.has("item_rate") ? item.get("item_rate").getAsDouble() : 0;
		unit = item.has("unit") ? item.get("unit").getAsString() : null;
		iconUrl = item.get("icon_url").getAsString();
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getId() {
		return id;
	}
	
	public double getPrice() {
		return price;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
}
