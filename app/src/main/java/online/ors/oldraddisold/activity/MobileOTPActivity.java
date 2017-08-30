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

public class MobileOTPActivity extends ORSActivity
    implements OnSmsCatchListener<String>, View.OnClickListener, ApiTask.OnResponseListener {
	
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
		    .setUrl(ApiUrl.MODIFY_MOBILE)
		    .setRequestBody(mUser.toJSON())
		    .setResponseListener(this)
		    .setProgressMessage(getString(online.ors.oldraddisold.R.string.verifying_otp))
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (response.get("status").getAsInt()) {
			case -3: // mobile already registered
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DATA, response.get("message").getAsString());
				setResult(RESULT_INVALID, intent);
				finish();
				break;
			
			case -2: // invalid OTP
				otpET.setError(getString(online.ors.oldraddisold.R.string.invalid_otp));
				break;
			
			case 0: // mobile changed successfully
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				new Preference(this).setMobile(mUser.getPhone());
				setResult(RESULT_OK);
				finish();
				break;
		}
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
