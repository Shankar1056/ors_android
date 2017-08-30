package online.ors.oldraddisold.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.model.CommentModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 24 May 2017 at 5:58 PM
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
	private final LinkedList<CommentModel> mItemList = new LinkedList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		CommentModel item = mItemList.get(position);
		
		Picasso.with(holder.avatarIV.getContext())
		    .load(item.getAvatarUrl())
		    .resizeDimen(R.dimen.huge, R.dimen.huge)
		    .centerCrop()
		    .into(holder.avatarIV);
		
		holder.nameTV.setText(item.getName());
		holder.timeTV.setReferenceTime(item.getTime());
		holder.commentTV.setText(item.getComment());
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(CommentModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public void addItemAtFirst(CommentModel item) {
		mItemList.addFirst(item);
		notifyItemInserted(0);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView avatarIV;
		TextView nameTV, commentTV;
		RelativeTimeTextView timeTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			avatarIV = (ImageView) itemView.findViewById(R.id.iv_avatar);
			nameTV = (TextView) itemView.findViewById(R.id.tv_name);
			timeTV = (RelativeTimeTextView) itemView.findViewById(R.id.tv_time);
			commentTV = (TextView) itemView.findViewById(R.id.tv_comment);
		}
	}
}
