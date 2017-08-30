package online.ors.oldraddisold.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import online.ors.oldraddisold.adapter.PickupItemAdapter;
import online.ors.oldraddisold.model.ScheduleModel;

@SuppressWarnings("ConstantConditions")
public class PickupConfirmActivity extends ORSActivity implements View.OnClickListener {
	private ScheduleModel mSchedule;
	private AppCompatEditText noteET;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_pickup_confirm);
		
		mSchedule = (ScheduleModel) getIntent().getSerializableExtra(EXTRA_DATA);
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		PickupItemAdapter mAdapter = new PickupItemAdapter();
		mAdapter.setItemList(mSchedule.getItemList());
		mAdapter.setSelectable(false);
		RecyclerView itemsRV = (RecyclerView) findViewById(online.ors.oldraddisold.R.id.rv_items);
		itemsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		itemsRV.setAdapter(mAdapter);
		
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_schedule_date)).setText(mSchedule.getDate());
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_schedule_time)).setText(mSchedule.getTime());
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_schedule_address)).setText(mSchedule.getAddress());
		
		noteET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_note);
		noteET.setText(mSchedule.getNote());
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		new AlertDialog.Builder(this)
		    .setTitle(online.ors.oldraddisold.R.string.title_schedule_pickup)
		    .setMessage(online.ors.oldraddisold.R.string.msg_schedule_pickup)
		    .setNegativeButton(online.ors.oldraddisold.R.string.cancel, null)
		    .setPositiveButton(online.ors.oldraddisold.R.string.schedule, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    mSchedule.setNote(noteET.getText().toString());
				    Intent intent = new Intent();
				    intent.putExtra(EXTRA_DATA, mSchedule);
				    setResult(RESULT_OK, intent);
				    finish();
			    }
		    })
		    .create()
		    .show();
	}
}
