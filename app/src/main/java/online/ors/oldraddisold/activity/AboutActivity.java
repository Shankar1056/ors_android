package online.ors.oldraddisold.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import online.ors.oldraddisold.R;

/**
 * About ORS services activity
 */
@SuppressWarnings("ConstantConditions")
public class AboutActivity extends ORSActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		TextView aboutTV = (TextView) findViewById(R.id.tv_about_ors);
		aboutTV.setText(Html.fromHtml(getString(R.string.desc_about_us)));
	}
}
