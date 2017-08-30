package online.ors.oldraddisold.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.text.ParseException;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.adapter.CommentAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.CommentModel;
import online.ors.oldraddisold.model.FeedModel;
import online.ors.oldraddisold.util.Preference;

/**
 * Activity for viewing and making comments on posts
 */
@SuppressWarnings("ConstantConditions")
public class CommentActivity extends ORSActivity
    implements ApiTask.OnResponseListener, View.OnClickListener, Paginate.Callbacks {
	private static final String TAG = "CommentActivity";
	private static final int RC_COMMENTS = 1;
	private static final int RC_POST = 2;
	private static final String DATA_COMMENT = "data_comment";
	
	private RecyclerView commentRV;
	private AppCompatEditText commentET;
	
	private CommentAdapter mAdapter;
	private Paginate mPaginate;
	private FeedModel mFeed;
	private Preference mPref;
	
	private int mIndex = 0;
	private boolean isLoading = false, hasLoadedAll = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		mFeed = (FeedModel) getIntent().getSerializableExtra(EXTRA_DATA);
		mPref = new Preference(this);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_18dp);
		
		mAdapter = new CommentAdapter();
		commentRV = (RecyclerView) findViewById(R.id.rv_comment);
		commentRV.setAdapter(mAdapter);
		mPaginate = Paginate.with(commentRV, this)
		    .setLoadingTriggerThreshold(2)
		    .addLoadingListItem(true)
		    .setLoadingListItemCreator(new CustomLoadingListItemCreator())
		    .setLoadingListItemSpanSizeLookup(new CustomLoadingListItemSpanLookup())
		    .build();
		
		commentET = (AppCompatEditText) findViewById(R.id.et_comment);
		findViewById(R.id.iv_send).setOnClickListener(this);
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_COMMENTS:
				// load comments page wise (Pagination).
				isLoading = false;
				mIndex += 10;
				JsonArray comments = response.getAsJsonArray("data");
				
				if (comments.size() > 0) {
					for (JsonElement comment : comments) {
						try {
							mAdapter.addItem(new CommentModel((JsonObject) comment));
						} catch (ParseException e) {
							Log.e(TAG, e.getMessage(), e);
						}
					}
				} else {
					hasLoadedAll = true;
					mPaginate.setHasMoreDataToLoad(false);
				}
				break;
			
			case RC_POST:
				// show message and clear text
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				commentET.setText(null);
				
				// add comment to the list
				mAdapter.addItemAtFirst(new CommentModel(mPref, savedData.getString(DATA_COMMENT)));
				commentRV.scrollToPosition(0);
				break;
		}
		
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
	}
	
	@Override
	public void onClick(View v) {
		String comment = commentET.getText().toString().trim();
		
		if (!comment.isEmpty()) {
			Bundle savedData = new Bundle();
			savedData.putString(DATA_COMMENT, comment);
			
			// send comments to server
			ApiTask.builder(this)
			    .setUrl(ApiUrl.POST_COMMENT)
			    .setRequestCode(RC_POST)
			    .setResponseListener(this)
			    .setSavedData(savedData)
			    .setRequestBody(mFeed.toJson(comment))
			    .exec();
		}
	}
	
	@Override
	public void onLoadMore() {
		if (!isFinishing()) {
			// load more pages as user scrolls over to top
			isLoading = true;
			ApiTask.builder(this)
			    .setUrl(ApiUrl.COMMENTS)
			    .setRequestBody(mFeed.toJson(mIndex))
			    .setRequestCode(RC_COMMENTS)
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
			View view = getLayoutInflater().inflate(R.layout.loading_row, parent, false);
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
			return 1;
		}
	}
}
