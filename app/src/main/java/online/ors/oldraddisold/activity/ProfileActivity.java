package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import online.ors.oldraddisold.util.Preference;

@SuppressWarnings("ConstantConditions")
public class ProfileActivity extends ORSActivity implements View.OnClickListener {
	private static final int RC_EDIT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_profile);
		
		getWindow().setFlags(
		    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
		    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
		);
		
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		findViewById(online.ors.oldraddisold.R.id.tv_mobile_edit).setOnClickListener(this);
		
		init();
	}
	
	private void init() {
		Preference pref = new Preference(this);
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_name)).setText(pref.getName());
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_full_name)).setText(pref.getName());
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_email)).setText(pref.getEmail());
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_mobile)).setText(getString(online.ors.oldraddisold.R.string.format_phone, pref.getMobile()));
		
		Picasso.with(this)
		    .load(pref.getAvatar())
		    .fit()
		    .centerCrop()
		    .into((ImageView) findViewById(online.ors.oldraddisold.R.id.iv_avatar));
		
		Picasso.with(this)
		    .load(pref.getAvatar())
		    .fit()
		    .centerCrop()
		    .into((ImageView) findViewById(online.ors.oldraddisold.R.id.iv_bg));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(online.ors.oldraddisold.R.menu.edit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == online.ors.oldraddisold.R.id.action_edit) {
			Intent intent = new Intent(this, ProfileEditActivity.class);
			startActivityForResult(intent, RC_EDIT);
			return false;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			init();
		}
	}
	
	@Override
	public void onClick(View v) {
		startActivityForResult(new Intent(this, MobileEditActivity.class), RC_EDIT);
	}
}
