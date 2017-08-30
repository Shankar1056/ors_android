package online.ors.oldraddisold.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.text.ParseException;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.adapter.PickupItemAdapter;
import online.ors.oldraddisold.adapter.PickupRateAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.PickupDetailsModel;
import online.ors.oldraddisold.model.PickupModel;
import online.ors.oldraddisold.view.CenteredImageSpan;


@SuppressWarnings("ConstantConditions")
public class ScheduleDetailsActivity extends ORSActivity implements ApiTask.OnResponseListener, View.OnClickListener {
	private static final String TAG = "ScheduleDetailsActivity";
	private static final int RC_CANCEL = 1;
	private static final int RC_DETAILS = 2;
	private PickupModel mPickup;
	private boolean isCancelled = false;
	private PickupItemAdapter mAdapter;
	private PickupRateAdapter mRateAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_details);
		
		mPickup = (PickupModel) getIntent().getSerializableExtra(EXTRA_DATA);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_18dp);
		getSupportActionBar().setTitle(String.valueOf(mPickup.getPickupId()));
		
		// expected items
		mAdapter = new PickupItemAdapter();
		mAdapter.setSelectable(false);
		RecyclerView itemsRV = (RecyclerView) findViewById(R.id.rv_items);
		itemsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		itemsRV.setAdapter(mAdapter);
		
		// pickup rate adapter
		mRateAdapter = new PickupRateAdapter();
		RecyclerView rateRV = (RecyclerView) findViewById(R.id.rv_pickup_rate);
		rateRV.setNestedScrollingEnabled(false);
		rateRV.setAdapter(mRateAdapter);
		
		ApiTask.builder(this)
		    .setUrl(ApiUrl.SCHEDULE_DETAILS)
		    .setRequestBody(mPickup.toJson())
		    .setRequestCode(RC_DETAILS)
		    .setProgressMessage(R.string.loading_history_details)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finishWithStatus();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void finishWithStatus() {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATA, isCancelled);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		finishWithStatus();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_DETAILS:
				try {
					PickupDetailsModel pickup = new PickupDetailsModel(response.getAsJsonObject("data"));
					init(pickup);
				} catch (ParseException e) {
					Log.e(TAG, e.getMessage(), e);
				}
				break;
			
			case RC_CANCEL:
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				ApiTask.builder(this)
				    .setUrl(ApiUrl.SCHEDULE_DETAILS)
				    .setRequestBody(mPickup.toJson())
				    .setRequestCode(RC_DETAILS)
				    .setProgressMessage(R.string.loading_history_details)
				    .setResponseListener(this)
				    .exec();
				isCancelled = true;
				break;
		}
	}
	
	private void init(PickupDetailsModel pickup) {
		((TextView) findViewById(R.id.tv_schedule_id)).setText(String.valueOf(pickup.getPickupId()));
		((TextView) findViewById(R.id.tv_schedule_date)).setText(pickup.getDate());
		((TextView) findViewById(R.id.tv_schedule_time)).setText(pickup.getTime());
		((TextView) findViewById(R.id.tv_schedule_address)).setText(pickup.getAddress());
		
		SpannableString status = new SpannableString("   " + pickup.getStatus());
		CenteredImageSpan is = new CenteredImageSpan(
		    this,
		    getResources().getIdentifier(pickup.getStatus(), "drawable", getPackageName())
		);
		status.setSpan(is, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		TextView statusTV = (TextView) findViewById(R.id.tv_schedule_status);
		statusTV.setText(status);
		statusTV.setTextColor(
		    getResources().getColor(
			getResources().getIdentifier(pickup.getStatus(), "color", getPackageName())
		    )
		);
		
		if (pickup.getStatus().equals("pending")) {
			findViewById(R.id.tv_cancel_schedule).setOnClickListener(this);
		} else {
			findViewById(R.id.tv_cancel_schedule).setVisibility(View.GONE);
		}
		
		if (pickup.getAgentContact() != null && !pickup.getStatus().equals("cancelled")
		    && !pickup.getStatus().equals("completed")) {
			((TextView) findViewById(R.id.tv_agent_contact)).setText(pickup.getAgentContact());
		} else {
			findViewById(R.id.cv_contact).setVisibility(View.GONE);
		}
		
		if (pickup.getStatus().equals("completed")) {
			mRateAdapter.addItems(pickup.getPickupItemList());
			((TextView) findViewById(R.id.tv_total_price))
			    .setText(getString(R.string.format_price, pickup.getTotalPrice()));
		} else {
			findViewById(R.id.ll_price).setVisibility(View.GONE);
		}
		
		mAdapter.setItemList(pickup.getItemList());
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onClick(View v) {
		final AppCompatEditText noteET = new AppCompatEditText(this);
		noteET.setHint(R.string.hint_reason_for_cancelling);
		new AlertDialog.Builder(this)
		    .setView(noteET)
		    .setTitle(R.string.title_cancel_schedule)
		    .setMessage(R.string.hint_cancel_schedule)
		    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    String note = noteET.getText().toString();
				    ApiTask.builder(ScheduleDetailsActivity.this)
					.setUrl(ApiUrl.SCHEDULE_CANCEL)
					.setRequestBody(mPickup.toJson(note))
					.setProgressMessage(R.string.canceling_schedule)
					.setResponseListener(ScheduleDetailsActivity.this)
					.setRequestCode(RC_CANCEL)
					.exec();
			    }
		    })
		    .setNegativeButton(R.string.cancel, null)
		    .create()
		    .show();
	}
}
