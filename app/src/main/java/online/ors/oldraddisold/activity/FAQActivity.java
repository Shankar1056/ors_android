package online.ors.oldraddisold.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.adapter.FAQAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.FAQModel;

/**
 * Activity for FAQ
 */
@SuppressWarnings("ConstantConditions")
public class FAQActivity extends ORSActivity implements ApiTask.OnResponseListener {
	private FAQAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_18dp);
		
		mAdapter = new FAQAdapter();
		RecyclerView faqRV = (RecyclerView) findViewById(R.id.rv_faq);
		faqRV.setAdapter(mAdapter);
		
		// load FAQs from server
		ApiTask.builder(this)
		    .setUrl(ApiUrl.FAQ)
		    .setProgressMessage(R.string.loading_faq)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		JsonArray data = response.getAsJsonArray("data");
		
		for (JsonElement item : data) {
			mAdapter.addItem(new FAQModel((JsonObject) item));
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
