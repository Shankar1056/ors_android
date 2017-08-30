package online.ors.oldraddisold.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.FeedModel;

/**
 * Activity for displaying feeds
 */
public class FeedActivity extends ORSActivity implements ApiTask.OnResponseListener, View.OnClickListener {
	private FeedModel mFeed;
	private WebView descWV;
	private String mContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_feed);
		
		mFeed = (FeedModel) getIntent().getSerializableExtra(EXTRA_DATA);
		
		final ImageView featureIV = (ImageView) findViewById(online.ors.oldraddisold.R.id.iv_image);
		featureIV.post(new Runnable() {
			@Override
			public void run() {
				// load featured image using Picasso
				Picasso.with(FeedActivity.this)
				    .load(mFeed.getImageLink())
				    .resize(featureIV.getWidth(), featureIV.getHeight())
				    .centerCrop()
				    .into(featureIV);
			}
		});
		
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_title)).setText(mFeed.getTitle());
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_label_comments)).setText(
		    mFeed.getCommentCount() == 0 ? online.ors.oldraddisold.R.string.be_the_first : online.ors.oldraddisold.R.string.view_comments
		);
		
		descWV = (WebView) findViewById(online.ors.oldraddisold.R.id.wv_description);
		WebSettings settings = descWV.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
		settings.setBuiltInZoomControls(false);
		settings.setDisplayZoomControls(false);
		
		((TextView) findViewById(online.ors.oldraddisold.R.id.tv_comment_count)).setText(
		    getResources().getQuantityString(online.ors.oldraddisold.R.plurals.comments, mFeed.getCommentCount(), mFeed.getCommentCount())
		);
		
		findViewById(online.ors.oldraddisold.R.id.ib_fb).setOnClickListener(this);
		findViewById(online.ors.oldraddisold.R.id.ib_twitter).setOnClickListener(this);
		findViewById(online.ors.oldraddisold.R.id.ib_mail).setOnClickListener(this);
		findViewById(online.ors.oldraddisold.R.id.ib_whats_app).setOnClickListener(this);
		findViewById(online.ors.oldraddisold.R.id.ib_message).setOnClickListener(this);
		findViewById(online.ors.oldraddisold.R.id.ll_comments).setOnClickListener(this);
		
		// get description from server
		ApiTask.builder(this)
		    .setUrl(ApiUrl.FEED_BY_ID)
		    .setRequestBody(mFeed.toJson())
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		// load description onto web view
		mContent = response.getAsJsonObject("data").get("html_content").getAsString();
		descWV.loadData(mContent, "text/html", "UTF-8");
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case online.ors.oldraddisold.R.id.ll_comments:
				// navigate to comment activity
				Intent intent = new Intent(this, CommentActivity.class);
				intent.putExtra(EXTRA_DATA, mFeed);
				startActivity(intent);
				break;
			
			case online.ors.oldraddisold.R.id.ib_fb:
				// share on facebook
				launchIntent("com.facebook.katana");
				break;
			
			case online.ors.oldraddisold.R.id.ib_twitter:
				// share on twitter
				launchIntent("com.twitter.android");
				break;
			
			case online.ors.oldraddisold.R.id.ib_mail:
				// share it by email
				launchIntent("com.google.android.gm");
				break;
			
			case online.ors.oldraddisold.R.id.ib_whats_app:
				// share it on Whats App
				launchIntent("com.whatsapp");
				break;
			
			case online.ors.oldraddisold.R.id.ib_message:
				// share it by default SMS app
				launchIntent(Telephony.Sms.getDefaultSmsPackage(this));
				break;
		}
		
	}
	
	/**
	 * Launches apps to share the content
	 *
	 * @param packageName application package name to be shared with
	 */
	private void launchIntent(String packageName) {
		try {
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mContent);
			shareIntent.setPackage(packageName);
			startActivity(shareIntent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, online.ors.oldraddisold.R.string.app_not_installed, Toast.LENGTH_SHORT).show();
		}
	}
}
