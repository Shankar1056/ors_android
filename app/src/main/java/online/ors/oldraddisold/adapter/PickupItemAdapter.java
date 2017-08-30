package online.ors.oldraddisold.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import online.ors.oldraddisold.model.ItemModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 09 May 2017 at 6:58 PM
 */

public class PickupItemAdapter extends RecyclerView.Adapter<PickupItemAdapter.ViewHolder> {
	private ArrayList<ItemModel> mItemList = new ArrayList<>();
	private boolean selectable = true;
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(selectable ? online.ors.oldraddisold.R.layout.item_category : online.ors.oldraddisold.R.layout.item_pickup, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ItemModel item = mItemList.get(position);
		Context context = holder.itemView.getContext();
		
		holder.titleTV.setText(item.getTitle());
		holder.imageView.setSelected(!selectable || item.isSelected());
		holder.imageView.setColorFilter(
		    !selectable || item.isSelected() ? Color.WHITE :
			ContextCompat.getColor(context, online.ors.oldraddisold.R.color.green)
		);
		
		Picasso.with(holder.itemView.getContext())
		    .load(item.getIconUrl())
		    .fit()
		    .into(holder.imageView);
	}
	
	@Override
	public int getItemCount() {
		return mItemList == null ? 0 : mItemList.size();
	}
	
	public void addItem(ItemModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public ArrayList<ItemModel> getSelectedItem() {
		ArrayList<ItemModel> itemList = new ArrayList<>();
		
		for (ItemModel item : mItemList) {
			if (item.isSelected()) {
				itemList.add(item);
			}
		}
		
		return itemList;
	}
	
	public void setItemList(ArrayList<ItemModel> itemList) {
		mItemList = itemList;
		notifyDataSetChanged();
	}
	
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView imageView;
		TextView titleTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			titleTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_title);
			imageView = (ImageView) itemView.findViewById(online.ors.oldraddisold.R.id.iv_image);
			
			if (selectable) {
				itemView.setOnClickListener(this);
			}
		}
		
		@Override
		public void onClick(View v) {
			ItemModel item = mItemList.get(getAdapterPosition());
			item.setSelected(!item.isSelected());
			notifyItemChanged(getAdapterPosition());
		}
	}
}
