package online.ors.oldraddisold.activity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.adapter.DateAdapter;
import online.ors.oldraddisold.adapter.TimeAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.ItemModel;
import online.ors.oldraddisold.model.ScheduleModel;
import online.ors.oldraddisold.model.TimeIntervalModel;
import online.ors.oldraddisold.view.ScrollControlLinearLayoutManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

@SuppressWarnings("ConstantConditions")
public class PickupActivity extends ORSActivity implements View.OnTouchListener, View.OnClickListener, ApiTask.OnResponseListener {
	private static final String TAG = "PickupActivity";
	private static final int RC_MAP = 16;
	private static final int RC_SUCCESS = 32;
	private static final int RC_TIME_SLOT = 64;
	private static final int RC_SCHEDULE = 128;
	private static final int RC_PICKUP_ITEM = 256;
	private static final int RC_CONFIRM = 512;
	
	private AppCompatEditText addressET, areaZipCodeET, landmarkET;
	private DateAdapter mDateAdapter;
	private TimeAdapter mTimeAdapter;
	private ScheduleModel mSchedule;
	private RecyclerView timeRV;
	private boolean isTimeLoaded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_pickup);
		
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		mSchedule = new ScheduleModel();
		
		// init date recycler view
		final RecyclerView dateRV = (RecyclerView) findViewById(online.ors.oldraddisold.R.id.rv_date);
		final ScrollControlLinearLayoutManager dateLayoutManager = new ScrollControlLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
		dateRV.setLayoutManager(dateLayoutManager);
		mDateAdapter = new DateAdapter();
		dateRV.setAdapter(mDateAdapter);
		dateRV.smoothScrollToPosition(1);
		final LinearSnapHelper dateSnapHelper = new LinearSnapHelper();
		dateSnapHelper.attachToRecyclerView(dateRV);
		dateRV.setOnFlingListener(dateSnapHelper);
		dateRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				
				if (newState == SCROLL_STATE_IDLE) {
					View view = dateSnapHelper.findSnapView(dateLayoutManager);
					int position = dateRV.getChildAdapterPosition(view);
					mDateAdapter.setSelectedPosition(position);
				}
			}
			
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				
			}
		});
		mDateAdapter.setSelectedPosition(1);
		
		// init time recycler view
		timeRV = (RecyclerView) findViewById(online.ors.oldraddisold.R.id.rv_time);
		final ScrollControlLinearLayoutManager timeLayoutManager = new ScrollControlLinearLayoutManager(this);
		timeRV.setLayoutManager(timeLayoutManager);
		mTimeAdapter = new TimeAdapter();
		timeRV.setAdapter(mTimeAdapter);
		final LinearSnapHelper timeSnapHelper = new LinearSnapHelper();
		timeSnapHelper.attachToRecyclerView(timeRV);
		timeRV.setOnFlingListener(timeSnapHelper);
		timeRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				
				if (newState == SCROLL_STATE_IDLE) {
					View view = timeSnapHelper.findSnapView(timeLayoutManager);
					int position = timeRV.getChildAdapterPosition(view);
					mTimeAdapter.setSelectedPosition(position);
				}
			}
			
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				
			}
		});
		
		addressET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_address);
		addressET.setOnTouchListener(this);
		
		areaZipCodeET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_area);
		landmarkET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_landmark);
		
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
		
		startActivityForResult(new Intent(this, PickupItemActivity.class), RC_PICKUP_ITEM);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getRawX() >= (addressET.getRight() - addressET.getCompoundDrawables()[2].getBounds().width())) {
				startActivityForResult(new Intent(this, MapActivity.class), RC_MAP);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		if (mSchedule.getLatitude() != 0 && isAddressValid()) {
			// check if the date is in the future
			Calendar now = Calendar.getInstance();
			Calendar then = mDateAdapter.getSelectedDateTime(mTimeAdapter.getSelectedTime());
			
			if (now.compareTo(then) < 0) {
				mSchedule.setDate(mDateAdapter.getSelectedDate());
				mSchedule.setTimeInterval(mTimeAdapter.getSelectedTimeInterval());
				Intent intent = new Intent(this, PickupConfirmActivity.class);
				intent.putExtra(EXTRA_DATA, mSchedule);
				startActivityForResult(intent, RC_CONFIRM);
			} else {
				Toast.makeText(this, R.string.select_future, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, online.ors.oldraddisold.R.string.please_choose_location, Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean isAddressValid() {
		boolean isValid = true;
		String address1 = addressET.getText().toString().trim();
		String address2 = areaZipCodeET.getText().toString().trim();
		String address3 = landmarkET.getText().toString().trim();
		
		if (address1.length() < 5) {
			addressET.setError(getString(R.string.invalid_address));
			isValid = false;
		} else {
			addressET.setError(null);
		}
		
		if (address2.length() < 5) {
			areaZipCodeET.setError(getString(R.string.invalid_address));
			isValid = false;
		} else {
			areaZipCodeET.setError(null);
		}
		
		if (isValid) {
			if (address3.length() > 0) {
				mSchedule.setAddress(address1 + ", " + address2 + ", " + address3);
			} else {
				mSchedule.setAddress(address1 + ", " + address2);
			}
		}
		
		return isValid;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			case RC_PICKUP_ITEM:
				if (resultCode == RESULT_OK) {
					mSchedule.setItemList(data == null ? null : (ArrayList<ItemModel>) data.getSerializableExtra(EXTRA_DATA));
					startActivityForResult(new Intent(this, MapActivity.class), RC_MAP);
				} else {
					finish();
				}
				break;
			
			case RC_MAP:
				if (resultCode == RESULT_OK) {
					saveMapData(data);
				}
				
				if (!isTimeLoaded) {
					ApiTask.builder(this)
					    .setUrl(ApiUrl.TIME_SLOTS)
					    .setProgressMessage(online.ors.oldraddisold.R.string.fetch_time_slots)
					    .setResponseListener(this)
					    .setRequestCode(RC_TIME_SLOT)
					    .exec();
				}
				break;
			
			case RC_CONFIRM: // Confirm the schedule
				if (resultCode == RESULT_OK) {
					mSchedule = (ScheduleModel) data.getSerializableExtra(EXTRA_DATA);
					
					ApiTask.builder(this)
					    .setUrl(ApiUrl.SCHEDULE)
					    .setRequestBody(mSchedule.toJson())
					    .setProgressMessage(online.ors.oldraddisold.R.string.scheduling_pickup)
					    .setResponseListener(this)
					    .setRequestCode(RC_SCHEDULE)
					    .exec();
				}
				break;
			
			case RC_SUCCESS:
				finish();
				break;
		}
	}
	
	private void saveMapData(Intent data) {
		List<Address> addressList = data.getParcelableArrayListExtra(EXTRA_DATA);
		addressET.setText(addressList.get(0).getAddressLine(0) + ", "
		    + addressList.get(0).getAddressLine(1));
		areaZipCodeET.setText(addressList.get(0).getAddressLine(2));
		mSchedule.setLatitude(data.getDoubleExtra(EXTRA_LAT, 0));
		mSchedule.setLongitude(data.getDoubleExtra(EXTRA_LONG, 0));
		addressET.setError(null);
		areaZipCodeET.setError(null);
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_TIME_SLOT:
				for (JsonElement time : response.getAsJsonArray("data")) {
					try {
						mTimeAdapter.addItem(new TimeIntervalModel((JsonObject) time));
					} catch (ParseException e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
				mTimeAdapter.addPaddingItems();
				mTimeAdapter.setSelectedPosition(1);
				isTimeLoaded = true;
				
				timeRV.post(new Runnable() {
					@Override
					public void run() {
						ViewGroup.LayoutParams timeLP = timeRV.getLayoutParams();
						View itemView = timeRV.getChildAt(1);
						timeLP.height = itemView.getHeight() * 3;
						timeRV.setLayoutParams(timeLP);
					}
				});
				break;
			
			case RC_SCHEDULE:
				if (response.get("status").getAsInt() == 0) {
					startActivityForResult(new Intent(this, PostPickupActivity.class), RC_SUCCESS);
				} else {
					Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_LONG).show();
				}
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
