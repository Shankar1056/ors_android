package online.ors.oldraddisold.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.model.RedeemHistoryModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 May 2017 at 11:03 AM
 */

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder> {
	private final ArrayList<RedeemHistoryModel> mItemList = new ArrayList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_redeem_history, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		RedeemHistoryModel item = mItemList.get(position);
		Resources res = holder.amountTV.getResources();
		
		holder.typeTV.setText(item.getType());
		holder.statusTV.setText(item.getStatus());
		holder.dateTV.setText(item.getDate());
		holder.amountTV.setText(res.getString(R.string.format_rupee_float, item.getAmount()));
		holder.amountTV.setTextColor(res.getColor(item.isCredit() ? R.color.green : R.color.black));
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(RedeemHistoryModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView typeTV, statusTV, dateTV, amountTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			typeTV = (TextView) itemView.findViewById(R.id.tv_type);
			statusTV = (TextView) itemView.findViewById(R.id.tv_status);
			dateTV = (TextView) itemView.findViewById(R.id.tv_date);
			amountTV = (TextView) itemView.findViewById(R.id.tv_amount);
		}
	}
}
