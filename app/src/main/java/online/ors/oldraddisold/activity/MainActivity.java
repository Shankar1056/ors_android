package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.service.RegistrationIntentService;
import online.ors.oldraddisold.util.Preference;

/**
 * Entry point of the application
 */
public class MainActivity extends ORSActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// check if the app should exit
		if (!getIntent().getBooleanExtra(EXTRA_DATA, false)) {
			// show the splash screen for 3 seconds
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					startTask();
				}
			}, 3000);
			
			// force fullscreen
			getWindow().setFlags(
			    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
			    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
			);
		} else {
			finish();
		}
	}
	
	/**
	 * Navigates the user to {@link HomeActivity} if the user is logged in
	 * {@link AuthActivity} otherwise
	 */
	private void startTask() {
		if (new Preference(this).isLoggedIn()) {
			startActivity(new Intent(this, HomeActivity.class));
			finish();
		} else {
			startActivityForResult(new Intent(this, AuthActivity.class), RC_AUTH);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_AUTH && resultCode == RESULT_OK) {
			// user logged in successfully, navigate to HomeActivity
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
			    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			startActivity(new Intent(this, HomeActivity.class));
			
			// Send GCM token to server
			startService(new Intent(this, RegistrationIntentService.class));
		}
		
		finish();
	}
}
