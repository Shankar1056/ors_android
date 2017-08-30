package online.ors.oldraddisold.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.text.ParseException;

import online.ors.oldraddisold.activity.FeedActivity;
import online.ors.oldraddisold.activity.ORSActivity;
import online.ors.oldraddisold.adapter.FeedAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.FeedModel;
import online.ors.oldraddisold.view.GridSpacingHeaderItemDecoration;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 22 May 2017 at 6:18 PM
 */

@SuppressWarnings("ConstantConditions")
public class FeedFragment extends Fragment implements ApiTask.OnResponseListener,
    FeedAdapter.OnFeedItemClickListener, Paginate.Callbacks {
	private static final String TAG = "FeedFragment";
	
	private FeedAdapter mAdapter;
	private Paginate mPaginate;
	
	private int mFeedIndex = 0;
	private boolean isLoading = false, hasLoadedAll = false;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(online.ors.oldraddisold.R.string.feed);
		
		mAdapter = new FeedAdapter(this);
		RecyclerView feedRV = (RecyclerView) inflater.inflate(online.ors.oldraddisold.R.layout.fragment_feed, container, false);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return position == 0 ? 2 : 1;
			}
		});
		feedRV.setLayoutManager(gridLayoutManager);
		feedRV.setAdapter(mAdapter);
		int thick = getResources().getDimensionPixelSize(online.ors.oldraddisold.R.dimen.thick);
		feedRV.addItemDecoration(new GridSpacingHeaderItemDecoration(2, thick, true));
		
		mPaginate = Paginate.with(feedRV, this)
		    .setLoadingTriggerThreshold(2)
		    .addLoadingListItem(true)
		    .setLoadingListItemCreator(new CustomLoadingListItemCreator())
		    .setLoadingListItemSpanSizeLookup(new CustomLoadingListItemSpanLookup())
		    .build();
		
		return feedRV;
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		isLoading = false;
		mFeedIndex += 10;
		JsonArray data = response.getAsJsonArray("data");
		
		if (data.size() > 0) {
			for (JsonElement item : data) {
				try {
					mAdapter.addItem(new FeedModel((JsonObject) item));
				} catch (ParseException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} else {
			hasLoadedAll = true;
			mPaginate.setHasMoreDataToLoad(false);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onItemClick(FeedModel item) {
		Intent intent = new Intent(getContext(), FeedActivity.class);
		intent.putExtra(ORSActivity.EXTRA_DATA, item);
		startActivity(intent);
	}
	
	@Override
	public void onShare(FeedModel item) {
		
	}
	
	@Override
	public void onLoadMore() {
		if (isAdded()) {
			isLoading = true;
			JsonObject object = new JsonObject();
			object.addProperty("offset", mFeedIndex);
			ApiTask.builder(getContext())
			    .setUrl(ApiUrl.FEEDS)
			    .setRequestBody(object)
			    .setResponseListener(this)
			    .exec();
		}
	}
	
	@Override
	public boolean isLoading() {
		return isLoading;
	}
	
	@Override
	public boolean hasLoadedAllItems() {
		return hasLoadedAll;
	}
	
	private class CustomLoadingListItemCreator implements LoadingListItemCreator {
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = getActivity().getLayoutInflater().inflate(online.ors.oldraddisold.R.layout.loading_row, parent, false);
			return new ViewHolder(view);
		}
		
		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			
		}
		
		class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View itemView) {
				super(itemView);
			}
		}
	}
	
	private class CustomLoadingListItemSpanLookup implements LoadingListItemSpanLookup {
		@Override
		public int getSpanSize() {
			return 2;
		}
	}
}
