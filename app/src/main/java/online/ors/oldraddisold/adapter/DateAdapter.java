package online.ors.oldraddisold.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 May 2017 at 5:45 PM
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
	private static final String TAG = "DateAdapter";
	private int selectedPosition;
	
	public DateAdapter() {
		selectedPosition = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(online.ors.oldraddisold.R.layout.item_date, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, position);
		holder.dateTV.setText(DateFormat.format("dd", calendar));
		
		if (position == selectedPosition) {
			holder.dateTV.setSelected(true);
			holder.dayTV.setText(DateFormat.format("EEEE", calendar));
			holder.dayTV.setVisibility(View.VISIBLE);
			
			holder.monthTV.setText(DateFormat.format("MMMM", calendar));
			holder.monthTV.setVisibility(View.VISIBLE);
		} else {
			holder.dateTV.setSelected(false);
			holder.dayTV.setVisibility(View.INVISIBLE);
			holder.monthTV.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public int getItemCount() {
		// provide dates from tomorrow to current DAY_OF_MONTH till next month
		return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) + 2;
	}
	
	public String getSelectedDate() {
		Calendar selectedDate = Calendar.getInstance();
		selectedDate.add(Calendar.DAY_OF_MONTH, selectedPosition);
		return DateFormat.format("yyyy-MM-dd", selectedDate).toString();
	}
	
	public void setSelectedPosition(int position) {
		int pos = selectedPosition;
		selectedPosition = position;
		notifyItemChanged(pos);
		notifyItemChanged(selectedPosition);
	}
	
	public Calendar getSelectedDateTime(Date selectedTime) {
		Calendar time = Calendar.getInstance();
		time.setTime(selectedTime);
		
		Calendar then = Calendar.getInstance();
		then.add(Calendar.DAY_OF_MONTH, selectedPosition);
		then.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		then.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		then.set(Calendar.SECOND, time.get(Calendar.SECOND));
		
		return then;
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView monthTV, dayTV, dateTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			monthTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_month);
			dayTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_day);
			dateTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_date);
		}
	}
}