package online.ors.oldraddisold.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import online.ors.oldraddisold.R;

@SuppressWarnings("ConstantConditions")
public class NGOActivity extends ORSActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ngo);
		
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_18dp);
	}
}
