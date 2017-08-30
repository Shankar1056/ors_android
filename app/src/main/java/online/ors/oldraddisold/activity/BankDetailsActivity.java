package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.View;

import com.google.gson.JsonObject;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.RedeemModel;

/**
 * Activity for reading bank details of the customer while redeeming from wallet.
 * Auto populate bank details if customer has already redeemed from wallet
 */
@SuppressWarnings("ConstantConditions")
public class BankDetailsActivity extends ORSActivity implements View.OnClickListener, ApiTask.OnResponseListener {
	private AppCompatEditText accountET, beneficiaryET, bankNameET, branchET, ifscET, mobileET;
	private RedeemModel mAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_details);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_18dp);
		
		accountET = (AppCompatEditText) findViewById(R.id.et_account_no);
		beneficiaryET = (AppCompatEditText) findViewById(R.id.et_beneficiary);
		bankNameET = (AppCompatEditText) findViewById(R.id.et_bank_name);
		branchET = (AppCompatEditText) findViewById(R.id.et_branch_name);
		ifscET = (AppCompatEditText) findViewById(R.id.et_ifsc_code);
		mobileET = (AppCompatEditText) findViewById(R.id.et_registered_mobile);
		
		findViewById(R.id.btn_submit).setOnClickListener(this);
		
		// request for bank details of customer to auto populate
		ApiTask.builder(this)
		    .setUrl(ApiUrl.GET_USER_BANK_DETAILS)
		    .setProgressMessage(R.string.loading_bank_details)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onClick(View v) {
		if (isInputValid()) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DATA, mAccount);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	/**
	 * Validates customer bank details
	 *
	 * @return true if it's semantically correct, false otherwise
	 */
	private boolean isInputValid() {
		boolean isValid = true;
		String accountNo = accountET.getText().toString();
		String beneficiary = beneficiaryET.getText().toString();
		String bankName = bankNameET.getText().toString();
		String branch = branchET.getText().toString();
		String ifsc = ifscET.getText().toString();
		String mobile = mobileET.getText().toString();
		
		if (accountNo.isEmpty()) {
			accountET.setError(getString(R.string.invalid_account_number));
			isValid = false;
		} else {
			accountET.setError(null);
		}
		
		if (beneficiary.isEmpty()) {
			beneficiaryET.setError(getString(R.string.invalid_beneficiary_name));
			isValid = false;
		} else {
			beneficiaryET.setError(null);
		}
		
		if (bankName.isEmpty()) {
			bankNameET.setError(getString(R.string.invalid_bank_name));
			isValid = false;
		} else {
			bankNameET.setError(null);
		}
		
		if (branch.isEmpty()) {
			branchET.setError(getString(R.string.invalid_branch_name));
			isValid = false;
		} else {
			branchET.setError(null);
		}
		
		if (ifsc.isEmpty()) {
			ifscET.setError(getString(R.string.invalid_ifsc));
			isValid = false;
		} else {
			ifscET.setError(null);
		}
		
		if (!PhoneNumberUtils.isGlobalPhoneNumber(mobile) || mobile.length() != 10) {
			mobileET.setError(getString(R.string.invalid_mobile));
			return false;
		} else {
			mobileET.setError(null);
		}
		
		if (isValid) {
			mAccount.setAccountNo(accountNo);
			mAccount.setBeneficiary(beneficiary);
			mAccount.setBankName(bankName);
			mAccount.setBranch(branch);
			mAccount.setIfsc(ifsc);
			mAccount.setMobile(mobile);
		}
		
		return isValid;
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		// auto populate details from server response
		JsonObject object = response.getAsJsonObject("data").getAsJsonObject("bank_details");
		if (!object.isJsonNull()) {
			mAccount = new RedeemModel(object);
			accountET.setText(mAccount.getAccountNo());
			beneficiaryET.setText(mAccount.getBeneficiary());
			bankNameET.setText(mAccount.getBankName());
			branchET.setText(mAccount.getBranch());
			ifscET.setText(mAccount.getIfsc());
			mobileET.setText(mAccount.getMobile());
		} else {
			mAccount = new RedeemModel(object);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
