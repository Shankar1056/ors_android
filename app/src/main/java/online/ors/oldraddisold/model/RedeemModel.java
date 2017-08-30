package online.ors.oldraddisold.model;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 20 May 2017 at 12:21 PM
 */

public class RedeemModel implements Serializable {
	private String accountNo;
	private String beneficiary;
	private String bankName;
	private String branch;
	private String ifsc;
	private String mobile;
	private String amount;
	
	public RedeemModel(JsonObject object) {
		accountNo = object.get("account_no") != JsonNull.INSTANCE ? object.get("account_no").getAsString() : null;
		beneficiary = object.get("account_holders_name") != JsonNull.INSTANCE
		    ? object.get("account_holders_name").getAsString() : null;
		ifsc = object.get("ifsc_code") != JsonNull.INSTANCE ? object.get("ifsc_code").getAsString() : null;
		bankName = object.get("bank_name") != JsonNull.INSTANCE ? object.get("bank_name").getAsString() : null;
		branch = object.get("branch_name") != JsonNull.INSTANCE ? object.get("branch_name").getAsString() : null;
		mobile = object.get("bank_registered_mobile") != JsonNull.INSTANCE
		    ? object.get("bank_registered_mobile").getAsString() : null;
	}
	
	public String getAccountNo() {
		return accountNo;
	}
	
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	
	public String getBeneficiary() {
		return beneficiary;
	}
	
	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public String getBranch() {
		return branch;
	}
	
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	public String getIfsc() {
		return ifsc;
	}
	
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("account_no", accountNo);
		object.addProperty("bank_name", bankName);
		object.addProperty("ifsc_code", ifsc);
		object.addProperty("branch_name", branch);
		object.addProperty("bank_registered_mobile", mobile);
		object.addProperty("account_holders_name", beneficiary);
		object.addProperty("redeem_amount", amount);
		return object;
	}
}
