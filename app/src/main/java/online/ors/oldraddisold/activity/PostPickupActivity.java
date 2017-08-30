package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PostPickupActivity extends ORSActivity implements View.OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_post_pickup);
		
		findViewById(online.ors.oldraddisold.R.id.btn_schedule).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		startActivity(new Intent(this, ScheduleHistoryActivity.class));
		finish();
	}
}
