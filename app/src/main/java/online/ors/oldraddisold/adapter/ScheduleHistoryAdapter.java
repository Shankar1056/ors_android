package online.ors.oldraddisold.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.model.PickupModel;
import online.ors.oldraddisold.view.CenteredImageSpan;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 May 2017 at 5:33 PM
 */

public class ScheduleHistoryAdapter extends RecyclerView.Adapter<ScheduleHistoryAdapter.ViewHolder> {
	private final ArrayList<PickupModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	
	public ScheduleHistoryAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_history, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PickupModel item = mItemList.get(position);
		Resources res = holder.statusTV.getResources();
		
		holder.idTV.setText(String.valueOf(item.getPickupId()));
		ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);
		
		SpannableString date = new SpannableString(res.getString(R.string.format_schedule_date, item.getDate()));
		date.setSpan(fcs, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.dateTV.setText(date);
		
		SpannableString time = new SpannableString(res.getString(R.string.format_schedule_time, item.getTime()));
		time.setSpan(fcs, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.timeTV.setText(time);
		
		// TODO: change the "on the way" status icon and color
		SpannableString status = new SpannableString(res.getString(R.string.format_schedule_status, item
		    .getStatus()));
		CenteredImageSpan is = new CenteredImageSpan(
		    holder.itemView.getContext(),
		    res.getIdentifier(
			item.getStatus(),
			"drawable",
			holder.statusTV.getContext().getPackageName()
		    )
		);
		status.setSpan(is, 21, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		status.setSpan(fcs, 0, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.statusTV.setText(status);
		holder.statusTV.setTextColor(
		    res.getColor(
			res.getIdentifier(
			    item.getStatus(),
			    "color",
			    holder.statusTV.getContext().getPackageName()
			)
		    )
		);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(PickupModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public void clearAll() {
		mItemList.clear();
		notifyDataSetChanged();
	}
	
	public interface OnItemClickListener {
		void onItemClick(PickupModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView idTV, dateTV, timeTV, statusTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			idTV = (TextView) itemView.findViewById(R.id.tv_schedule_id);
			dateTV = (TextView) itemView.findViewById(R.id.tv_schedule_date);
			timeTV = (TextView) itemView.findViewById(R.id.tv_schedule_time);
			statusTV = (TextView) itemView.findViewById(R.id.tv_schedule_status);
			
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			mListener.onItemClick(mItemList.get(getAdapterPosition()));
		}
	}
}
