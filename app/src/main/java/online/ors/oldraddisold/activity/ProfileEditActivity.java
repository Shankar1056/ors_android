package online.ors.oldraddisold.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.model.UserModel;
import online.ors.oldraddisold.util.Constants;
import online.ors.oldraddisold.util.Preference;
import online.ors.oldraddisold.util.Util;


@SuppressWarnings("ConstantConditions")
public class ProfileEditActivity extends ORSActivity
    implements View.OnClickListener, ApiTask.OnResponseListener {
	private static final String TAG = "ProfileEditActivity";
	private static final int RC_GALLERY = 0;
	private static final int RC_STORAGE_PERM = 1;
	private static final int RC_CAMERA = 2;
	private static final String DIR_USER_AVATAR = "user-avatars";
	
	private TransferUtility mTransferUtil;
	private UserModel mUser;
	
	private AppCompatEditText nameET, emailET;
	private CircleImageView avatarIV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_profile_edit);
		
		mUser = new UserModel();
		
		avatarIV = (CircleImageView) findViewById(online.ors.oldraddisold.R.id.iv_avatar);
		nameET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_name);
		emailET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_email);
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
		findViewById(online.ors.oldraddisold.R.id.fab_camera).setOnClickListener(this);
		
		// Amazon S3 setups
		mTransferUtil = Util.getTransferUtility(this);
		
		init();
	}
	
	private void init() {
		Preference pref = new Preference(this);
		mUser.setAvatar(pref.getAvatar());
		mUser.setName(pref.getName());
		mUser.setEmail(pref.getEmail());
		
		nameET.setText(pref.getName());
		emailET.setText(pref.getEmail());
		
		Picasso.with(this)
		    .load(pref.getAvatar())
		    .fit()
		    .centerCrop()
		    .into(avatarIV);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case online.ors.oldraddisold.R.id.fab_camera:
				getAvatar();
				break;
			
			case online.ors.oldraddisold.R.id.btn_submit:
				if (isInputValid()) {
					ApiTask.builder(this)
					    .setUrl(ApiUrl.UPDATE_USER_PROFILE)
					    .setRequestBody(mUser.toJSON())
					    .setResponseListener(this)
					    .setProgressMessage(online.ors.oldraddisold.R.string.updating_info)
					    .exec();
				}
				break;
		}
	}
	
	private void getAvatar() {
		final Dialog supportDialog = new Dialog(this);
		supportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View supportView = getLayoutInflater().inflate(online.ors.oldraddisold.R.layout.dialog_image, null);
		supportDialog.setContentView(supportView);
		supportDialog.show();
		supportDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
		    WindowManager.LayoutParams.WRAP_CONTENT);
		
		supportView.findViewById(online.ors.oldraddisold.R.id.ll_exit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//dismiss the dialog
				supportDialog.dismiss();
			}
		});
		
		supportDialog.findViewById(online.ors.oldraddisold.R.id.ll_camera).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				supportDialog.dismiss();
				
				if (ContextCompat.checkSelfPermission(ProfileEditActivity.this, Manifest.permission
				    .WRITE_EXTERNAL_STORAGE) ==
				    PackageManager.PERMISSION_GRANTED) {
					Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
						startActivityForResult(takePictureIntent, RC_CAMERA);
					}
				} else {
					ActivityCompat.requestPermissions(
					    ProfileEditActivity.this,
					    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					    RC_CAMERA
					);
				}
			}
		});
		
		supportDialog.findViewById(online.ors.oldraddisold.R.id.ll_gallery).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				supportDialog.dismiss();
				
				if (ContextCompat.checkSelfPermission(
				    ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
				    PackageManager.PERMISSION_GRANTED &&
				    ContextCompat.checkSelfPermission(
					ProfileEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
					PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_GALLERY);
				} else {
					ActivityCompat.requestPermissions(
					    ProfileEditActivity.this,
					    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
						.WRITE_EXTERNAL_STORAGE},
					    RC_STORAGE_PERM
					);
				}
			}
		});
	}
	
	
	private boolean isInputValid() {
		mUser.setName(nameET.getText().toString().trim());
		mUser.setEmail(emailET.getText().toString().trim());
		boolean isValid = true;
		
		if (mUser.getName().length() < 3) {
			nameET.setError("Invalid Name");
			isValid = false;
		} else {
			nameET.setError(null);
		}
		
		if (TextUtils.isEmpty(mUser.getEmail()) ||
		    !Patterns.EMAIL_ADDRESS.matcher(mUser.getEmail()).matches()) {
			emailET.setError("Invalid Email");
			isValid = false;
		} else {
			emailET.setError(null);
		}
		
		return isValid;
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		if (response.get("status").getAsInt() == 0) {
			Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
			Preference pref = new Preference(this);
			pref.setName(mUser.getName());
			pref.setEmail(mUser.getEmail());
			pref.setAvatar(mUser.getAvatar());
			setResult(RESULT_OK);
			finish();
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case RC_GALLERY:
					if (data == null) {
						Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
						return;
					}
					
					try {
						Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
						avatarIV.setImageBitmap(bitmap);
						
						Uri tempUri = getImageUri(getApplicationContext(), bitmap);
						
						File finalFile = new File(getRealPathFromURI(tempUri));
						mTransferUtil.upload(Constants.BUCKET_NAME, DIR_USER_AVATAR + "/" + finalFile.getName(), finalFile);
						
						mUser.setAvatar(Constants.getResourceUrl(DIR_USER_AVATAR, finalFile.getName()));
					} catch (IOException e) {
						Log.e(TAG, e.getMessage(), e);
					}
					break;
				
				case RC_CAMERA:
					Bundle extras = data.getExtras();
					Bitmap imageBitmap = (Bitmap) extras.get("data");
					avatarIV.setImageBitmap(imageBitmap);
					
					Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
					
					File finalFile = new File(getRealPathFromURI(tempUri));
					mTransferUtil.upload(Constants.BUCKET_NAME, DIR_USER_AVATAR + "/" + finalFile.getName(), finalFile);
					
					mUser.setAvatar(Constants.getResourceUrl(DIR_USER_AVATAR, finalFile.getName()));
					break;
			}
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}
	
	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		String realPath = cursor.getString(idx);
		cursor.close();
		return realPath;
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case RC_STORAGE_PERM:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_GALLERY);
				} else {
					Toast.makeText(this, online.ors.oldraddisold.R.string.permission_denied, Toast.LENGTH_LONG).show();
				}
				break;
			
			case RC_CAMERA:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
						startActivityForResult(takePictureIntent, RC_CAMERA);
					}
				} else {
					Toast.makeText(this, online.ors.oldraddisold.R.string.permission_denied, Toast.LENGTH_LONG).show();
				}
				break;
		}
	}
}
