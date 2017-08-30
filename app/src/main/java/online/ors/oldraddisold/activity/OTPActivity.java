package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.UserModel;
import online.ors.oldraddisold.util.Preference;

/**
 * Validates OTP, stores user info into Shared preference {@link Preference}
 * if the user is already a member
 */
public class OTPActivity extends ORSActivity implements OnSmsCatchListener<String>, View.OnClickListener, ApiTask.OnResponseListener {
	private UserModel mUser;
	private SmsVerifyCatcher mOTPCatcher;
	
	private AppCompatEditText otpET;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_otp);
		
		mUser = (UserModel) getIntent().getSerializableExtra(EXTRA_DATA);
		mOTPCatcher = new SmsVerifyCatcher(this, this);
		
		otpET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_otp);
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mOTPCatcher.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mOTPCatcher.onStop();
	}
	
	
	@Override
	public void onClick(View v) {
		String otp = otpET.getText().toString().trim();
		
		if (!otp.matches("\\d{4}")) {
			otpET.setError(getString(online.ors.oldraddisold.R.string.invalid_otp));
			return;
		}
		
		otpET.setError(null);
		mUser.setOtp(otp);
		ApiTask.builder(this)
		    .setUrl(ApiUrl.SUBMIT_OTP)
		    .setRequestBody(mUser.toJSON())
		    .setResponseListener(this)
		    .setProgressMessage(getString(online.ors.oldraddisold.R.string.verifying_otp))
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (response.get("status").getAsInt()) {
			case -2: // invalid OTP
				otpET.setError(getString(online.ors.oldraddisold.R.string.invalid_otp));
				break;
			
			case 0: // existing user
				Toast.makeText(this, online.ors.oldraddisold.R.string.logged_in_successfully, Toast.LENGTH_SHORT).show();
				setProfileInfo(response);
				setResult(RESULT_OK);
				finish();
				break;
			
			case 1: // new user
				Intent intent = new Intent(this, SignUpActivity.class);
				intent.putExtra(EXTRA_DATA, mUser);
				startActivityForResult(intent, RC_AUTH);
				break;
			
		}
	}
	
	private void setProfileInfo(JsonObject response) {
		Preference pref = new Preference(this);
		UserModel user = new UserModel(((JsonObject) response.get("data")));
		pref.setSessionKey(user.getSessionId());
		pref.setName(user.getName());
		pref.setMobile(user.getPhone());
		pref.setEmail(user.getEmail());
		pref.setAvatar(user.getAvatar());
		pref.setLoggedIn(true);
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onSmsCatch(String message) {
		otpET.setText(message.split(" ")[0]);
		findViewById(online.ors.oldraddisold.R.id.btn_submit).performClick();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
