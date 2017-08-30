package online.ors.oldraddisold.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.model.ItemModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 May 2017 at 3:41 PM
 */

public class RateCardAdapter extends RecyclerView.Adapter<RateCardAdapter.ViewHolder> {
	private final ArrayList<ItemModel> mItemList = new ArrayList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_rate_card, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ItemModel item = mItemList.get(position);
		holder.titleTV.setText(item.getTitle());
		holder.priceTV.setText(
		    holder.priceTV.getResources().getString(
			online.ors.oldraddisold.R.string.format_price_unit, item.getPrice(), item.getUnit()
		    )
		);
		
		Picasso.with(holder.itemView.getContext())
		    .load(item.getIconUrl())
		    .fit()
		    .into(holder.iconIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(ItemModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView titleTV, priceTV;
		ImageView iconIV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			titleTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_title);
			priceTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_price);
			iconIV = (ImageView) itemView.findViewById(online.ors.oldraddisold.R.id.iv_icon);
			iconIV.setSelected(true);
			iconIV.setColorFilter(Color.WHITE);
		}
	}
}
