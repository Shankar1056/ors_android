package online.ors.oldraddisold.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.ors.oldraddisold.activity.PickupActivity;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 May 2017 at 12:38 PM
 */

@SuppressWarnings("ConstantConditions")
public class PickupFragment extends Fragment implements View.OnClickListener {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(online.ors.oldraddisold.R.string.pickup);
		
		View view = inflater.inflate(online.ors.oldraddisold.R.layout.fragment_pickup, container, false);
		view.findViewById(online.ors.oldraddisold.R.id.btn_pickup).setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onClick(View v) {
		startActivity(new Intent(getContext(), PickupActivity.class));
	}
}
