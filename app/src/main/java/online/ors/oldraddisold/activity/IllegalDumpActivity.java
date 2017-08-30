package online.ors.oldraddisold.activity;

import android.Manifest;
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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import online.ors.oldraddisold.adapter.IllegalDumpAdapter;
import online.ors.oldraddisold.api.ApiTask;
import online.ors.oldraddisold.api.ApiUrl;
import online.ors.oldraddisold.util.Constants;
import online.ors.oldraddisold.util.Util;
import online.ors.oldraddisold.view.GridSpacingItemDecoration;

/**
 * Activity for reporting illegal dumping
 */
@SuppressWarnings("ConstantConditions")
public class IllegalDumpActivity extends ORSActivity implements IllegalDumpAdapter.OnPhotoClickListener, View.OnClickListener, ApiTask.OnResponseListener {
	private static final int RC_IMAGE = 1;
	private static final int RC_PERM = 2;
	private static final String DIR_ILLEGAL_DUMP = "illegal-dumping";
	
	private AppCompatEditText descET;
	
	private int position;
	
	private IllegalDumpAdapter mAdapter;
	private TransferUtility mTransferUtil;
	private ArrayList<String> mImageUrlList = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_illegal_dump);
		
		Toolbar toolbar = (Toolbar) findViewById(online.ors.oldraddisold.R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(online.ors.oldraddisold.R.drawable.ic_back_white_18dp);
		
		mAdapter = new IllegalDumpAdapter(this);
		RecyclerView illegalRV = (RecyclerView) findViewById(online.ors.oldraddisold.R.id.rv_illegal_dump);
		int gap = getResources().getDimensionPixelSize(online.ors.oldraddisold.R.dimen.standard);
		illegalRV.addItemDecoration(new GridSpacingItemDecoration(3, gap, false));
		illegalRV.setAdapter(mAdapter);
		
		findViewById(online.ors.oldraddisold.R.id.btn_submit).setOnClickListener(this);
		descET = (AppCompatEditText) findViewById(online.ors.oldraddisold.R.id.et_desc);
		
		// Amazon S3 setups
		mTransferUtil = Util.getTransferUtility(this);
	}
	
	@Override
	public void onPhotoClick(int position) {
		this.position = position;
		
		// check for the permission to read and write on external storage, request if not granted
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
		    != PackageManager.PERMISSION_GRANTED &&
		    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
			!= PackageManager.PERMISSION_GRANTED) {
			String[] permissions = new String[]{
			    Manifest.permission.READ_EXTERNAL_STORAGE,
			    Manifest.permission.WRITE_EXTERNAL_STORAGE
			};
			
			ActivityCompat.requestPermissions(this, permissions, RC_PERM);
		} else {
			// launch camera activity to capture an image
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(takePictureIntent, RC_IMAGE);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			
			Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
			
			File finalFile = new File(getRealPathFromURI(tempUri));
			
			// upload image to s3 and save the url
			mTransferUtil.upload(Constants.BUCKET_NAME, DIR_ILLEGAL_DUMP + "/" + finalFile.getName(), finalFile);
			
			if (position + 1 < mAdapter.getItemCount()) {
				mImageUrlList.set(position, Constants.getResourceUrl(DIR_ILLEGAL_DUMP, finalFile.getName()));
			} else {
				mImageUrlList.add(Constants.getResourceUrl(DIR_ILLEGAL_DUMP, finalFile.getName()));
			}
			
			// update the adapter
			mAdapter.setItem(imageBitmap, position);
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
		    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(takePictureIntent, RC_IMAGE);
			}
		} else {
			Toast.makeText(this, online.ors.oldraddisold.R.string.grant_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onClick(View v) {
		if (mAdapter.getItemList().size() != 0) {
			JsonObject object = new JsonObject();
			object.addProperty("site_lat", 12.972442);
			object.addProperty("site_long", 77.580643);
			object.addProperty("comment", descET.getText().toString());
			JsonArray images = new JsonArray();
			for (String s : mImageUrlList) {
				images.add(s);
			}
			object.add("images", images);
			
			ApiTask.builder(this)
			    .setUrl(ApiUrl.ILLEGAL_DUMP)
			    .setRequestBody(object)
			    .setResponseListener(this)
			    .setProgressMessage(online.ors.oldraddisold.R.string.reporting_illegal_dump)
			    .exec();
		}
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
		finish();
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
