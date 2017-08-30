package online.ors.oldraddisold.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 11 May 2017 at 05:23 PM
 */

public class IllegalDumpAdapter extends RecyclerView.Adapter<IllegalDumpAdapter.ViewHolder> {
	private final ArrayList<Bitmap> mItemList = new ArrayList<>();
	private final OnPhotoClickListener mListener;
	
	public IllegalDumpAdapter(OnPhotoClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(online.ors.oldraddisold.R.layout.item_illegal_dumping, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (position < mItemList.size()) {
			Bitmap bitmap = mItemList.get(position);
			holder.imageIV.setImageBitmap(bitmap);
			holder.addTV.setVisibility(View.GONE);
		} else {
			holder.addTV.setVisibility(View.VISIBLE);
			holder.imageIV.setImageBitmap(null);
		}
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size() + 1;
	}
	
	public void setItem(Bitmap imageBitmap, int position) {
		if (position < mItemList.size()) {
			mItemList.set(position, imageBitmap);
			notifyItemChanged(position);
		} else {
			mItemList.add(imageBitmap);
			notifyItemInserted(position);
		}
	}
	
	public ArrayList<Bitmap> getItemList() {
		return mItemList;
	}
	
	public interface OnPhotoClickListener {
		void onPhotoClick(int position);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView imageIV;
		TextView addTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (ImageView) itemView.findViewById(online.ors.oldraddisold.R.id.iv_image);
			addTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_add);
			imageIV.setOnClickListener(this);
			addTV.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			mListener.onPhotoClick(getAdapterPosition());
		}
	}
}
