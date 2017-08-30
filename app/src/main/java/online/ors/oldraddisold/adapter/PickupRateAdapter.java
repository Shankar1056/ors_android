package online.ors.oldraddisold.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.model.PickupItemModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 27 Jun 2017 at 2:18 PM
 */

public class PickupRateAdapter extends RecyclerView.Adapter<PickupRateAdapter.ViewHolder> {
	private ArrayList<PickupItemModel> mItemList = new ArrayList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_pickup_rate, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PickupItemModel item = mItemList.get(position);
		Resources res = holder.itemView.getResources();
		
		holder.titleQtyTV.setText(
		    res.getString(R.string.format_title_qty, item.getTitle(), item.getQty(), item.getUnit())
		);
		holder.priceTV.setText(res.getString(R.string.format_price, item.getTotalPrice()));
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItems(ArrayList<PickupItemModel> pickupItemList) {
		mItemList = pickupItemList;
		notifyDataSetChanged();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView titleQtyTV, priceTV;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			titleQtyTV = (TextView) itemView.findViewById(R.id.tv_title_qty);
			priceTV = (TextView) itemView.findViewById(R.id.tv_price);
		}
	}
}
