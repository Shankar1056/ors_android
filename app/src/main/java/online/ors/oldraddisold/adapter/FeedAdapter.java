package online.ors.oldraddisold.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import online.ors.oldraddisold.model.FeedModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 22 May 2017 at 6:20 PM
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
	private final ArrayList<FeedModel> mItemList = new ArrayList<>();
	private final OnFeedItemClickListener mListener;
	
	public FeedAdapter(OnFeedItemClickListener listener) {
		this.mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(online.ors.oldraddisold.R.layout.item_feed, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		FeedModel item = mItemList.get(position);
		Resources res = holder.bottomTitleTV.getResources();
		
		holder.topTitleTV.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
		holder.bottomTitleTV.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
		holder.topTitleTV.setText(item.getTitle());
		holder.bottomTitleTV.setText(item.getTitle());
		
		holder.dateTV.setText(item.getModifiedDate());
		
		Picasso.with(holder.imageIV.getContext())
		    .load(item.getImageLink())
		    .resizeDimen(
			online.ors.oldraddisold.R.dimen.image_exp,
			position == 0 ? online.ors.oldraddisold.R.dimen.image_height_large : online.ors.oldraddisold.R.dimen.image_height_regular
		    )
		    .centerCrop()
		    .into(holder.imageIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(FeedModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size());
	}
	
	public interface OnFeedItemClickListener {
		void onItemClick(FeedModel item);
		
		void onShare(FeedModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView topTitleTV, bottomTitleTV, dateTV;
		ImageView imageIV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (ImageView) itemView.findViewById(online.ors.oldraddisold.R.id.iv_image);
			topTitleTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_top_title);
			bottomTitleTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_bottom_title);
			dateTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_date);
			
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			mListener.onItemClick(mItemList.get(getAdapterPosition()));
		}
	}
}
