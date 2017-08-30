package online.ors.oldraddisold.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.ors.oldraddisold.activity.AboutActivity;
import online.ors.oldraddisold.activity.ContactUsActivity;
import online.ors.oldraddisold.activity.FAQActivity;
import online.ors.oldraddisold.activity.IllegalDumpActivity;
import online.ors.oldraddisold.activity.ProfileActivity;
import online.ors.oldraddisold.activity.ScheduleHistoryActivity;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 09 May 2017 at 11:30 AM
 */

@SuppressWarnings("ConstantConditions")
public class ExploreFragment extends Fragment implements View.OnClickListener {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(online.ors.oldraddisold.R.string.explore);
		
		View view = inflater.inflate(online.ors.oldraddisold.R.layout.fragment_explore, container, false);
		
		view.findViewById(online.ors.oldraddisold.R.id.tv_my_schedule).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_my_profile).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_about_ors).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_contact_us).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_faq).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_illegal_dumping).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case online.ors.oldraddisold.R.id.tv_my_schedule:
				startActivity(new Intent(getContext(), ScheduleHistoryActivity.class));
				break;
			
			case online.ors.oldraddisold.R.id.tv_my_profile:
				startActivity(new Intent(getContext(), ProfileActivity.class));
				break;
			
			case online.ors.oldraddisold.R.id.tv_about_ors:
				startActivity(new Intent(getContext(), AboutActivity.class));
				break;
			
			case online.ors.oldraddisold.R.id.tv_contact_us:
				startActivity(new Intent(getContext(), ContactUsActivity.class));
				break;
			
			case online.ors.oldraddisold.R.id.tv_faq:
				startActivity(new Intent(getContext(), FAQActivity.class));
				break;
			
			case online.ors.oldraddisold.R.id.tv_illegal_dumping:
				startActivity(new Intent(getContext(), IllegalDumpActivity.class));
				break;
		}
	}
}
