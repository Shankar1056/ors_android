package online.ors.oldraddisold.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import online.ors.oldraddisold.activity.BankDetailsActivity;
import online.ors.oldraddisold.activity.NGOActivity;
import online.ors.oldraddisold.activity.ORSActivity;
import online.ors.oldraddisold.activity.WalletTransactionActivity;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.RedeemModel;

import static android.app.Activity.RESULT_OK;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 May 2017 at 3:35 PM
 */

@SuppressWarnings("ConstantConditions")
public class WalletFragment extends Fragment implements View.OnClickListener, ApiTask.OnResponseListener {
	private static final int RC_BALANCE = 1;
	private static final int RC_DONATE = 2;
	private static final int RC_BANK_DETAILS = 4;
	private static final int RC_REDEEM = 8;
	private double mBalance;
	private AppCompatEditText amountET;
	private TextView[] donateTVs = new TextView[4];
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case online.ors.oldraddisold.R.id.tv_donate_0:
			case online.ors.oldraddisold.R.id.tv_donate_1:
			case online.ors.oldraddisold.R.id.tv_donate_2:
			case online.ors.oldraddisold.R.id.tv_donate_3:
				selectSpeedDial(v);
				break;
			
			case online.ors.oldraddisold.R.id.btn_donate:
				if (isInputValid(false)) {
					makeDonation();
				}
				break;
			
			case online.ors.oldraddisold.R.id.btn_redeem:
				if (isInputValid(true)) {
					startActivityForResult(
					    new Intent(getContext(), BankDetailsActivity.class),
					    RC_BANK_DETAILS
					);
				}
				break;
			
			case online.ors.oldraddisold.R.id.tv_redeem_history:
				startActivity(new Intent(getContext(), WalletTransactionActivity.class));
				break;
			
			case online.ors.oldraddisold.R.id.tv_donate_know_more:
				startActivity(new Intent(getContext(), NGOActivity.class));
				break;
		}
	}
	
	private void makeDonation() {
		final double amount = Double.parseDouble(amountET.getText().toString());
		new AlertDialog.Builder(getContext())
		    .setTitle(online.ors.oldraddisold.R.string.title_donate)
		    .setMessage(getString(online.ors.oldraddisold.R.string.donate_message, amount))
		    .setPositiveButton(online.ors.oldraddisold.R.string.donate, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    JsonObject object = new JsonObject();
				    object.addProperty("amount", amount);
				    
				    ApiTask.builder(getContext())
					.setUrl(ApiUrl.DONATE)
					.setRequestBody(object)
					.setProgressMessage(online.ors.oldraddisold.R.string.making_donation)
					.setRequestCode(RC_DONATE)
					.setResponseListener(WalletFragment.this)
					.exec();
			    }
		    })
		    .setNegativeButton(online.ors.oldraddisold.R.string.cancel, null)
		    .create()
		    .show();
	}
	
	private void selectSpeedDial(View v) {
		for (TextView donateTV : donateTVs) {
			donateTV.setSelected(false);
		}
		
		v.setSelected(true);
		double selectedAmount = Double.parseDouble(v.getTag().toString());
		this.amountET.setText(getString(online.ors.oldraddisold.R.string.format_float, selectedAmount));
	}
	
	private boolean isInputValid(boolean isRedeem) {
		try {
			double amount = Double.parseDouble(amountET.getText().toString());
			
			if (amount == 0) {
				Toast.makeText(getContext(), online.ors.oldraddisold.R.string.invalid_amount, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			if (amount > mBalance) {
				Toast.makeText(getContext(), online.ors.oldraddisold.R.string.insufficient_balance, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			if (isRedeem && (mBalance < 500 || amount < 500)) {
				Toast.makeText(getContext(), online.ors.oldraddisold.R.string.minimum_redeem, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			return true;
		} catch (NumberFormatException e) {
			Toast.makeText(getContext(), online.ors.oldraddisold.R.string.invalid_amount, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	private void init() {
		((TextView) getView().findViewById(online.ors.oldraddisold.R.id.tv_balance)).setText(getString(online.ors.oldraddisold.R.string.format_rupee_float, mBalance));
		amountET.setText(getString(online.ors.oldraddisold.R.string.format_float, mBalance));
		
		/*if (mBalance < 500) {
			getView().findViewById(R.id.btn_redeem).setEnabled(false);
		}
		
		if (mBalance == 0) {
			getView().findViewById(R.id.btn_donate).setEnabled(false);
		}*/
		
		int difference = (int) mBalance / 4;
		for (int i = 0; i < 4; i++) {
			int amount = difference * (i + 1);
			if (difference > 0 && amount <= mBalance) {
				donateTVs[i].setText(getString(online.ors.oldraddisold.R.string.format_rupee_int, (difference * (i + 1))));
				donateTVs[i].setOnClickListener(this);
				donateTVs[i].setTag(amount);
			} else {
				donateTVs[i].setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_BALANCE:
				mBalance = response.getAsJsonObject("data").get("wallet_balance").getAsDouble();
				init();
				break;
			
			case RC_DONATE:
				onSuccess(response, RC_BALANCE, savedData);
				Toast.makeText(getContext(), response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				break;
			
			case RC_REDEEM:
				onSuccess(response, RC_BALANCE, savedData);
				Toast.makeText(getContext(), response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_BANK_DETAILS && resultCode == RESULT_OK) {
			RedeemModel redeem = (RedeemModel) data.getSerializableExtra(ORSActivity.EXTRA_DATA);
			redeem.setAmount(amountET.getText().toString());
			
			ApiTask.builder(getContext())
			    .setUrl(ApiUrl.REDEEM)
			    .setRequestBody(redeem.toJson())
			    .setResponseListener(this)
			    .setRequestCode(RC_REDEEM)
			    .setProgressMessage(online.ors.oldraddisold.R.string.redeeming_from_wallet)
			    .exec();
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(online.ors.oldraddisold.R.string.wallet);
		
		View view = inflater.inflate(online.ors.oldraddisold.R.layout.fragment_wallet, container, false);
		
		view.findViewById(online.ors.oldraddisold.R.id.btn_donate).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.btn_redeem).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_redeem_history).setOnClickListener(this);
		view.findViewById(online.ors.oldraddisold.R.id.tv_donate_know_more).setOnClickListener(this);
		
		getActivity().findViewById(online.ors.oldraddisold.R.id.iv_wallet).setVisibility(View.VISIBLE);
		
		amountET = (AppCompatEditText) view.findViewById(online.ors.oldraddisold.R.id.et_amount);
		donateTVs[0] = (TextView) view.findViewById(online.ors.oldraddisold.R.id.tv_donate_0);
		donateTVs[1] = (TextView) view.findViewById(online.ors.oldraddisold.R.id.tv_donate_1);
		donateTVs[2] = (TextView) view.findViewById(online.ors.oldraddisold.R.id.tv_donate_2);
		donateTVs[3] = (TextView) view.findViewById(online.ors.oldraddisold.R.id.tv_donate_3);
		
		ApiTask.builder(getContext())
		    .setUrl(ApiUrl.WALLET_BALANCE)
		    .setResponseListener(this)
		    .setRequestCode(RC_BALANCE)
		    .setProgressMessage(online.ors.oldraddisold.R.string.loading_balance)
		    .exec();
		
		return view;
	}
}
