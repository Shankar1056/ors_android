package online.ors.oldraddisold.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Method;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.util.Preference;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 May 2017 at 5:29 PM
 */

public abstract class ORSActivity extends AppCompatActivity {
	public static final String EXTRA_DATA = "extra_data";
	protected static final String EXTRA_LAT = "extra_lat";
	protected static final String EXTRA_LONG = "extra_long";
	protected static final int RC_AUTH = 4;
	static final int RESULT_INVALID = 954;
	private static final String TAG = "ORSActivity";
	private Menu menu;
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			
			case R.id.action_rate:
				goToMarket();
				return true;
			
			case R.id.action_logout:
				new AlertDialog.Builder(this)
				    .setTitle(R.string.title_logout)
				    .setMessage(R.string.confirm_logout)
				    .setNegativeButton(R.string.cancel, null)
				    .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
						    new Preference(ORSActivity.this).clear();
						    Intent homeIntent = new Intent(ORSActivity.this, MainActivity.class);
						    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						    startActivity(homeIntent);
					    }
				    })
				    .create()
				    .show();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void goToMarket() {
		Uri uri = Uri.parse("market://details?id=online.ors.oldraddisold");
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,
			    Uri.parse("http://play.google.com/store/apps/details?id=online.ors.oldraddisold")));
		}
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu1) {
		if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
			try {
				Method m = menu.getClass().getDeclaredMethod(
				    "setOptionalIconsVisible", Boolean.TYPE);
				m.setAccessible(true);
				m.invoke(menu, true);
			} catch (NoSuchMethodException e) {
				Log.e(TAG, "onMenuOpened", e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return super.onMenuOpened(featureId, menu);
	}
}
