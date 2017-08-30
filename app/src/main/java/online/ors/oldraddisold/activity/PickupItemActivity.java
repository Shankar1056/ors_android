package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import online.ors.oldraddisold.adapter.PickupItemAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.ItemModel;

public class PickupItemActivity extends ORSActivity implements View.OnClickListener, ApiTask.OnResponseListener {
	private PickupItemAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_pickup_item);
		setSupportActionBar((Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar));
		
		mAdapter = new PickupItemAdapter();
		RecyclerView categoryRV = (RecyclerView) findViewById(online.ors.oldraddisold.R.id.rv_category);
		categoryRV.setAdapter(mAdapter);
		
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
		
		ApiTask.builder(this)
		    .setUrl(ApiUrl.RATE_CARD)
		    .setProgressMessage(online.ors.oldraddisold.R.string.loading_items)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(online.ors.oldraddisold.R.menu.skip, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case online.ors.oldraddisold.R.id.action_skip:
				setResult(RESULT_OK);
				finish();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATA, mAdapter.getSelectedItem());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		JsonArray data = response.getAsJsonArray("data");
		
		for (JsonElement item : data) {
			mAdapter.addItem(new ItemModel((JsonObject) item));
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
