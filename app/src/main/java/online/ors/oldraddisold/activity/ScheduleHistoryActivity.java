package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;

import online.ors.oldraddisold.adapter.ScheduleHistoryAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.PickupModel;

@SuppressWarnings("ConstantConditions")
public class ScheduleHistoryActivity extends ORSActivity implements ScheduleHistoryAdapter.OnItemClickListener, ApiTask.OnResponseListener {
	private static final String TAG = "ScheduleHistoryActivity";
	private static final int RC_DETAILS = 1;
	private ScheduleHistoryAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_schedule_history);
		
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		mAdapter = new ScheduleHistoryAdapter(this);
		RecyclerView scheduleRV = (RecyclerView) findViewById(online.ors.oldraddisold.R.id.rv_schedule_history);
		scheduleRV.setAdapter(mAdapter);
		
		ApiTask.builder(this)
		    .setUrl(ApiUrl.PICKUP_LIST)
		    .setProgressMessage(online.ors.oldraddisold.R.string.loading_pickup_history)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onItemClick(PickupModel item) {
		Intent intent = new Intent(this, ScheduleDetailsActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivityForResult(intent, RC_DETAILS);
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		mAdapter.clearAll();
		JsonArray data = response.getAsJsonArray("data");
		
		if (data.size() > 0) {
			for (JsonElement item : data) {
				try {
					mAdapter.addItem(new PickupModel((JsonObject) item));
				} catch (ParseException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} else {
			findViewById(online.ors.oldraddisold.R.id.tv_not_found).setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data.getBooleanExtra(EXTRA_DATA, false)) {
			ApiTask.builder(this)
			    .setUrl(ApiUrl.PICKUP_LIST)
			    .setProgressMessage(online.ors.oldraddisold.R.string.loading_pickup_history)
			    .setResponseListener(this)
			    .exec();
		}
	}
}
