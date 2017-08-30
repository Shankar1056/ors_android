package online.ors.oldraddisold.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.adapter.WalletTransactionAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.RedeemHistoryModel;

@SuppressWarnings("ConstantConditions")
public class WalletTransactionActivity extends ORSActivity implements ApiTask.OnResponseListener {
	private static final String TAG = "WalletTransaction";
	private WalletTransactionAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_transaction);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_18dp);
		
		mAdapter = new WalletTransactionAdapter();
		RecyclerView redeemHistoryRV = (RecyclerView) findViewById(R.id.rv_redeem_history);
		redeemHistoryRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		redeemHistoryRV.setAdapter(mAdapter);
		
		ApiTask.builder(this)
		    .setUrl(ApiUrl.REDEEM_HISTORY)
		    .setProgressMessage(R.string.loading_transaction_details)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		if (!isFinishing()) {
			JsonArray histories = response.getAsJsonObject("data").getAsJsonArray("transaction_history");
			
			if (histories.size() > 0) {
				for (JsonElement item : histories) {
					try {
						mAdapter.addItem(new RedeemHistoryModel((JsonObject) item));
					} catch (ParseException e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			} else {
				findViewById(R.id.tv_not_found).setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
