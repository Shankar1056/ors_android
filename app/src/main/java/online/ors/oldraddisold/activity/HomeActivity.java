package online.ors.oldraddisold.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.fragment.ExploreFragment;
import online.ors.oldraddisold.fragment.FeedFragment;
import online.ors.oldraddisold.fragment.PickupFragment;
import online.ors.oldraddisold.fragment.RateCardFragment;
import online.ors.oldraddisold.fragment.WalletFragment;
import online.ors.oldraddisold.util.Preference;

/**
 * User home activity
 */
public class HomeActivity extends ORSActivity implements View.OnClickListener, ApiTask.OnResponseListener {
	private LinearLayout bottomLL;
	private int currentTabPosition = -1;
	private boolean isBackPressed = false;
	private Toolbar toolbar;
	private View pickupView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		// bottom navigation custom implementation click listeners
		bottomLL = (LinearLayout) findViewById(R.id.ll_bottom);
		pickupView = findViewById(R.id.tv_pickup);
		pickupView.setOnClickListener(this);
		findViewById(R.id.tv_wallet).setOnClickListener(this);
		findViewById(R.id.tv_rate_card).setOnClickListener(this);
		findViewById(R.id.tv_explore).setOnClickListener(this);
		findViewById(R.id.tv_feed).setOnClickListener(this);
		
		// show pickup fragment initially
		onClick(pickupView);
		
		// check for app updates
		checkForAppUpdate();
	}
	
	private void checkForAppUpdate() {
		if (new Preference(this).isTimeToCheckForUpdate()) {
			ApiTask.builder(this)
			    .setUrl(ApiUrl.CHECK_FOR_UPDATES)
			    .setResponseListener(this)
			    .exec();
		}
	}
	
	@Override
	public void onClick(View v) {
		// bottom navigation custom implementation due to the limitations on no.of items
		for (int i = 0; i < bottomLL.getChildCount(); i++) {
			bottomLL.getChildAt(i).setSelected(false);
		}
		v.setSelected(true);
		
		switch (v.getId()) {
			case R.id.tv_pickup:
				replaceFragmentWithAnimation(new PickupFragment(), 0, R.color.green, R.color.darkGreen);
				break;
			
			case R.id.tv_wallet:
				replaceFragmentWithAnimation(new WalletFragment(), 1, R.color.walletPrimary, R.color.walletPrimaryDark);
				break;
			
			case R.id.tv_rate_card:
				replaceFragmentWithAnimation(new RateCardFragment(), 2, R.color.ratePrimary, R.color
				    .ratePrimaryDark);
				break;
			
			case R.id.tv_feed:
				replaceFragmentWithAnimation(new FeedFragment(), 3, R.color.colorPrimary, R.color
				    .colorPrimaryDark);
				break;
			
			case R.id.tv_explore:
				replaceFragmentWithAnimation(new ExploreFragment(), 4, R.color.explorePrimary, R.color
				    .explorePrimaryDark);
				break;
		}
	}
	
	/**
	 * Replaces the existing fragment with this fragment in the container
	 *
	 * @param fragment   to be replaced with
	 * @param position   position of the bottom navigation item pointing to the fragment to be replaced with
	 * @param primaryRes toolbar color of the fragment
	 * @param darkRes    status bar color of the fragment
	 */
	private void replaceFragmentWithAnimation(Fragment fragment, int position, int primaryRes, int darkRes) {
		if (position == currentTabPosition) {
			return;
		}
		
		findViewById(R.id.iv_wallet).setVisibility(View.GONE);
		
		// changing status bar color is only allowed from Lollipop onwards
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(darkRes));
		}
		
		toolbar.setBackgroundColor(getResources().getColor(primaryRes));
		
		// replace fragment with animation
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (position < currentTabPosition) {
			transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
		} else {
			transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
		}
		
		transaction.replace(R.id.fl_container, fragment);
		transaction.commit();
		
		// set the current tab position as the current fragment position
		currentTabPosition = position;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		// if not on the pickup fragment, show them pickup fragment
		if (currentTabPosition != 0) {
			onClick(pickupView);
			isBackPressed = false;
		} else if (!isBackPressed) {
			// exist from the app after tapping on the back button twist
			Toast.makeText(this, R.string.tap_to_exit, Toast.LENGTH_SHORT).show();
			isBackPressed = true;
			
			// clear out back pressed flag after 3 seconds
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isBackPressed = false;
				}
			}, 3000);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		if (!isFinishing()) {
			new Preference(this).setLastUpdateCheckedTimeAsNow();
			
			if (response.get("status").getAsInt() == 404) {
				updateApp();
			}
		}
	}
	
	private void updateApp() {
		new AlertDialog.Builder(this)
		    .setTitle(R.string.title_update_app)
		    .setMessage(R.string.message_optional_update)
		    .setNegativeButton(R.string.cancel, null)
		    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    Intent goToMarket = new Intent(Intent.ACTION_VIEW)
					.setData(Uri.parse("market://details?id=" + getPackageName()));
				    startActivity(goToMarket);
			    }
		    })
		    .create()
		    .show();
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
