package online.ors.oldraddisold.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.model.TimeIntervalModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 May 2017 at 7:02 PM
 */

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
	private final ArrayList<TimeIntervalModel> mItemList = new ArrayList<>();
	private int mSelectedPosition = 0;
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_time, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.timeTV.setText(mItemList.get(position).getTimeSlot());
		holder.timeTV.setSelected(position == mSelectedPosition);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void setSelectedPosition(int position) {
		int pos = mSelectedPosition;
		mSelectedPosition = position;
		notifyItemChanged(pos);
		notifyItemChanged(mSelectedPosition);
	}
	
	public TimeIntervalModel getSelectedTimeInterval() {
		return mItemList.get(mSelectedPosition);
	}
	
	public void addItem(TimeIntervalModel item) {
		mItemList.add(item);
	}
	
	public void addPaddingItems() {
		mItemList.add(0, mItemList.get(mItemList.size() - 1));
		mItemList.add(mItemList.get(1));
		notifyDataSetChanged();
	}
	
	public Date getSelectedTime() {
		return mItemList.get(mSelectedPosition).getStartTime();
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView timeTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			timeTV = (TextView) itemView;
		}
	}
}
