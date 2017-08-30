package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Locale;

/**
 * Activity for reaching out to ORS
 */
@SuppressWarnings("ConstantConditions")
public class ContactUsActivity extends ORSActivity implements View.OnClickListener {
	/**
	 * Recover Habitat Geo location constance
	 */
	private static final double ORS_LATITUDE = 13.0206016;
	private static final double ORS_LONGITUDE = 77.6566887;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_contact_us);
		
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		findViewById(online.ors.oldraddisold.R.id.tv_address).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// open google map to show the Recover Habitat premises location
		String uri = String.format(
		    Locale.ENGLISH,
		    "geo:%f,%f?q=%f,%f(Recover Habitat)", ORS_LATITUDE, ORS_LONGITUDE, ORS_LATITUDE, ORS_LONGITUDE
		);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(intent);
	}
}
