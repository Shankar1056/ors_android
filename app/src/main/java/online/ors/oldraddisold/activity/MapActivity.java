package online.ors.oldraddisold.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import online.ors.oldraddisold.R;
import online.ors.oldraddisold.service.AddressResolverService;
import online.ors.oldraddisold.util.Preference;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Activity for selecting location from Google map
 */
@SuppressWarnings("ConstantConditions")
public class MapActivity extends ORSActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, GoogleMap
    .OnMapClickListener, PlaceSelectionListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraChangeListener {
	private static final String TAG = "MapActivity";
	private static final int RC_PERM = 1024;
	private GoogleMap mMap;
	private LatLng mCurrentLocation;
	private PlaceAutocompleteFragment autocompleteFragment;
	private AddressResultReceiver mResultReceiver;
	private ProgressBar loaderPB;
	private AppCompatButton saveBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(online.ors.oldraddisold.R.layout.activity_map);
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
		    .findFragmentById(online.ors.oldraddisold.R.id.map);
		mapFragment.getMapAsync(this);
		
		// auto complete place fragment init
		autocompleteFragment = (PlaceAutocompleteFragment)
		    getFragmentManager().findFragmentById(online.ors.oldraddisold.R.id.place_autocomplete_fragment);
		autocompleteFragment.setOnPlaceSelectedListener(this);
		EditText placeET = (EditText) autocompleteFragment.getView().findViewById(online.ors.oldraddisold.R.id.place_autocomplete_search_input);
		placeET.setTextSize(14f);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
		    String.format(Locale.US, "fonts/%s", "lato-regular.ttf"));
		placeET.setTypeface(typeface);
		
		loaderPB = (ProgressBar) findViewById(R.id.pb_progress);
		findViewById(online.ors.oldraddisold.R.id.fab_current_loc).setOnClickListener(this);
		saveBtn = (AppCompatButton) findViewById(R.id.btn_save);
		saveBtn.setOnClickListener(this);
		
		// location address broadcast receiver
		mResultReceiver = new AddressResultReceiver(new Handler());
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		
		// Add a marker on the previously saved location (default: Bangalore) and move the camera
		Preference pref = new Preference(this);
		LatLng bangalore = new LatLng(pref.getLatitude(), pref.getLongitude());
		mMap.addMarker(new MarkerOptions().position(bangalore).title("Location"));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangalore, 15.0f));
		mMap.setOnCameraChangeListener(this);
		
		// Setting a click event handler for the map
		googleMap.setOnMapClickListener(this);
		
		// current location
		locationUpdate();
	}
	
	public void locationUpdate() {
		loaderPB.setVisibility(View.VISIBLE);
		saveBtn.setEnabled(false);
		// check location permission and request if not granted
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
		    != PERMISSION_GRANTED &&
		    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
			!= PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
			    new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			    },
			    RC_PERM
			);
			return;
		}
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// notify user
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage(getString(online.ors.oldraddisold.R.string.gps_network_not_enabled));
			dialog.setPositiveButton(getString(online.ors.oldraddisold.R.string.open_location_settings), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(myIntent);
				}
			});
			dialog.setNegativeButton(getString(online.ors.oldraddisold.R.string.cancel), null);
			dialog.show();
		}
		
		// GPS location request
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
		
		// Can we at least get network location?
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			// If cannot get GPS go for Network Provider's Location service
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		onMapClick(new LatLng(location.getLatitude(), location.getLongitude()));
	}
	
	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		
	}
	
	@Override
	public void onProviderEnabled(String s) {
		
	}
	
	@Override
	public void onProviderDisabled(String s) {
		
	}
	
	@Override
	public void onMapClick(LatLng latLng) {
		loaderPB.setVisibility(View.GONE);
		saveBtn.setEnabled(true);
		
		mCurrentLocation = latLng;
		
		// Clears the previously touched position
		mMap.clear();
		
		// Animating to the touched position
		mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED
		    || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
			locationUpdate();
		} else {
			Toast.makeText(this, online.ors.oldraddisold.R.string.grant_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case online.ors.oldraddisold.R.id.fab_current_loc:
				locationUpdate();
				break;
			
			case online.ors.oldraddisold.R.id.btn_save:
				if (saveBtn.isEnabled()) {
					getLocation();
				}
				break;
		}
	}
	
	private void getLocation() {
		if (mCurrentLocation != null) {
			try {
				Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
				List<Address> addresses = geo.getFromLocation(
				    mCurrentLocation.latitude, mCurrentLocation.longitude, 1
				);
				
				if (addresses.size() > 0) {
					// save the lat-long for the next init
					Preference pref = new Preference(this);
					pref.setLatLong(mCurrentLocation.latitude, mCurrentLocation.longitude);
					
					Intent result = new Intent();
					result.putParcelableArrayListExtra(EXTRA_DATA, new ArrayList<Parcelable>(addresses));
					result.putExtra(EXTRA_LAT, mCurrentLocation.latitude);
					result.putExtra(EXTRA_LONG, mCurrentLocation.longitude);
					setResult(RESULT_OK, result);
					finish();
				} else {
					Toast.makeText(this, online.ors.oldraddisold.R.string.no_location, Toast.LENGTH_SHORT).show();
					setResult(RESULT_CANCELED);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		} else {
			Toast.makeText(this, online.ors.oldraddisold.R.string.please_choose_location, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onPlaceSelected(Place place) {
		Log.i(TAG, "Place: " + place.getName());
		onMapClick(place.getLatLng());
	}
	
	@Override
	public void onError(Status status) {
		
	}
	
	@Override
	public void onCameraMove() {
		
	}
	
	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		mCurrentLocation = cameraPosition.target;
		
		mMap.clear();
		
		try {
			Location mLocation = new Location("");
			mLocation.setLatitude(mCurrentLocation.latitude);
			mLocation.setLongitude(mCurrentLocation.longitude);
			startIntentService(mLocation);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	protected void startIntentService(Location mLocation) {
		// Create an intent for passing to the intent service responsible for fetching the address.
		Intent intent = new Intent(this, AddressResolverService.class);
		
		// Pass the result receiver as an extra to the service.
		intent.putExtra(AddressResolverService.RECEIVER, mResultReceiver);
		
		// Pass the location data as an extra to the service.
		intent.putExtra(AddressResolverService.LOCATION_DATA_EXTRA, mLocation);
		
		// Start the service. If the service isn't already running, it is instantiated and started
		// (creating a process for it if needed); if it is running then it remains running. The
		// service kills itself automatically once all intents are processed.
		startService(intent);
	}
	
	private class AddressResultReceiver extends ResultReceiver {
		AddressResultReceiver(Handler handler) {
			super(handler);
		}
		
		/**
		 * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
		 */
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			String address = resultData.getString(AddressResolverService.RESULT_DATA_KEY);
			try {
				// autoComplete fragment could be null
				// if the receiver is invoked after the activity is finished
				autocompleteFragment.setText(address);
			} catch (NullPointerException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}