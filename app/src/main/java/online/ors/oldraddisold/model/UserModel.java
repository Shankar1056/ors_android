package online.ors.oldraddisold.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 Apr 2017 at 3:22 PM
 */

public class UserModel implements Serializable {
	private String phone;
	private String otp;
	private String name;
	private String email;
	private String sessionId;
	private String avatar;
	
	public UserModel() {
		
	}
	
	public UserModel(JsonObject user) {
		sessionId = user.get("session_id").getAsString();
		name = user.get("name").getAsString();
		email = user.get("email").getAsString();
		phone = user.get("phone").getAsString();
		avatar = user.get("avatar_link").getAsString();
	}
	
	public JsonObject toJSON() {
		JsonObject object = new JsonObject();
		object.addProperty("phone", phone);
		object.addProperty("otp", otp);
		object.addProperty("name", name);
		object.addProperty("email", email);
		object.addProperty("avatar_link", avatar);
		return object;
	}
	
	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}

