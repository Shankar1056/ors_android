package online.ors.oldraddisold.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import online.ors.oldraddisold.adapter.RateCardAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.ItemModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 May 2017 at 3:38 PM
 */

@SuppressWarnings("ConstantConditions")
public class RateCardFragment extends Fragment implements ApiTask.OnResponseListener {
	private RateCardAdapter mAdapter;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(online.ors.oldraddisold.R.string.rate_card);
		
		mAdapter = new RateCardAdapter();
		RecyclerView rateCardRV = (RecyclerView) inflater.inflate(online.ors.oldraddisold.R.layout.fragment_rate_card, container, false);
		rateCardRV.setAdapter(mAdapter);
		
		ApiTask.builder(getContext())
		    .setUrl(ApiUrl.RATE_CARD)
		    .setProgressMessage(online.ors.oldraddisold.R.string.loading_rate_card)
		    .setResponseListener(this)
		    .exec();
		
		return rateCardRV;
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
