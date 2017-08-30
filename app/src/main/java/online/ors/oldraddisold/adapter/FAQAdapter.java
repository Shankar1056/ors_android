package online.ors.oldraddisold.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import online.ors.oldraddisold.model.FAQModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 27 Jun 2017 at 4:00 PM
 */

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {
	private ArrayList<FAQModel> mItemList = new ArrayList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(online.ors.oldraddisold.R.layout.item_faq, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		FAQModel item = mItemList.get(position);
		Context context = holder.itemView.getContext();
		
		holder.questionTV.setText(item.getQuestion());
		holder.answerTV.setText(item.getAnswer());
		
		Drawable arrow = ContextCompat.getDrawable(
		    context,
		    item.isHidden() ? online.ors.oldraddisold.R.drawable.ic_arrow_down_grey_15dp : online.ors.oldraddisold.R.drawable.ic_up_arrow_15dp
		);
		
		holder.questionTV.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
		holder.answerTV.setVisibility(item.isHidden() ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(FAQModel item) {
		mItemList.add(item);
		notifyItemChanged(mItemList.size() - 1);
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		final TextView questionTV, answerTV;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			questionTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_question);
			answerTV = (TextView) itemView.findViewById(online.ors.oldraddisold.R.id.tv_answer);
			
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			final FAQModel item = mItemList.get(getAdapterPosition());
			item.setHidden(!item.isHidden());
			notifyItemChanged(getAdapterPosition());
		}
	}
}
