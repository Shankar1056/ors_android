package online.ors.oldraddisold.model;

import com.google.gson.JsonObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 27 Jun 2017 at 4:14 PM
 */

public class FAQModel {
	private final String question;
	private final String answer;
	private boolean hidden = true;
	
	public FAQModel(JsonObject item) {
		question = item.get("question").getAsString();
		answer = item.get("answer").getAsString();
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}
