package online.ors.oldraddisold.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberUtils;
import android.view.View;

import com.google.gson.JsonObject;

import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.UserModel;

public class AuthActivity extends ORSActivity implements View.OnClickListener, ApiTask.OnResponseListener {
	private static final int RC_PERM = 0;
	private AppCompatEditText mobileET;
	private UserModel mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_auth);
		
		mobileET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_mobile);
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
		
		mUser = new UserModel();
	}
	
	@Override
	public void onClick(View v) {
		if (isInputValid()) {
			// check the permission for auto read OTP, request them if not granted
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
			    != PackageManager.PERMISSION_GRANTED &&
			    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
				!= PackageManager.PERMISSION_GRANTED) {
				String[] permissions = new String[]{
				    Manifest.permission.RECEIVE_SMS,
				    Manifest.permission.READ_SMS
				};
				ActivityCompat.requestPermissions(this, permissions, RC_PERM);
			} else {
				// OTP auto read permission granted, send OTP from server
				ApiTask.builder(this)
				    .setResponseListener(this)
				    .setProgressMessage(online.ors.oldraddisold.R.string.authenticating)
				    .setUrl(ApiUrl.GENERATE_OTP)
				    .setRequestBody(mUser.toJSON())
				    .exec();
			}
		}
	}
	
	/**
	 * Validates mobile number
	 *
	 * @return true if and only if mobile is 10 digits in length and a global number
	 */
	private boolean isInputValid() {
		String mobile = mobileET.getText().toString().trim();
		
		if (!PhoneNumberUtils.isGlobalPhoneNumber(mobile) || mobile.length() != 10) {
			mobileET.setError(getString(online.ors.oldraddisold.R.string.invalid_mobile));
			return false;
		}
		
		mUser.setPhone(mobile);
		mobileET.setError(null);
		return true;
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		// OTP request is success, navigate to OTP activity
		if (response.get("status").getAsInt() == 0) {
			Intent intent = new Intent(this, OTPActivity.class);
			intent.putExtra(EXTRA_DATA, mUser);
			startActivityForResult(intent, RC_AUTH);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		// Request OTP after the permission dialog,
		// bother not about the permissions
		// if the user grants it then the auto read OTP, manual enter otherwise
		ApiTask.builder(this)
		    .setResponseListener(this)
		    .setProgressMessage(online.ors.oldraddisold.R.string.authenticating)
		    .setUrl(ApiUrl.GENERATE_OTP)
		    .setRequestBody(mUser.toJSON())
		    .exec();
	}
}
