package com.liteon.icampusguardian.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.liteon.icampusguardian.LoginActivity;
import com.liteon.icampusguardian.MainActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

public class SafetyFragment extends Fragment {

	private MapView mMapView;
	private GoogleMap mGoogleMap;
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private static LatLng mLastPosition = new LatLng(25.077877, 121.571141);
	private static ArrayList<GeoEventItem> myDataset = new ArrayList<>();
	private GeoEventItem mCurrentItem;
	private static final int DURATION = 3000;
	private ValueAnimator mValueAnimator;
	private FloatingActionButton mLocationOnMap;
	private boolean isAlerted = false;
	private TextView mUpdateText;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private GradientDrawable mGradientDrawable;
	private Bitmap mBitmap;
	private Context mContext;
	
	public SafetyFragment(Intent intent) {
		String latlng = intent.getStringExtra(Def.EXTRA_SOS_LOCATION);
		latlng = "25.070108, 121.611435";
		if (!TextUtils.isEmpty(latlng)) {
			String parse[] = latlng.split(","); 
			mLastPosition = new LatLng(Double.parseDouble(parse[0]), Double.parseDouble(parse[1]));
			isAlerted = true;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf_item = new SimpleDateFormat("HH:mm");
			String currentDate = sdf.format(Calendar.getInstance().getTime());
			String currentTime = sdf_item.format(Calendar.getInstance().getTime());
			for (GeoEventItem item : myDataset) {
				if (TextUtils.equals(item.getDate(), currentDate)) {
					mCurrentItem = item;
				}
			}
			if (mCurrentItem == null) {
				mCurrentItem = new GeoEventItem();
			}
			mCurrentItem.setDate(currentDate);
			mCurrentItem.setEnterSchool("");
			mCurrentItem.setLeaveSchool("");
			mCurrentItem.setEmergency(currentTime);
			mCurrentItem.setEmergencyRelease("");
		}
	}
	
	public SafetyFragment() {
	}

	private void showRipples(LatLng latLng) {


	    // Convert the drawable to bitmap
	    Canvas canvas = new Canvas(mBitmap);
	    mGradientDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    mGradientDrawable.draw(canvas);

	    // Radius of the circle
	    final int radius = 70;

	    // Add the circle to the map
	    final GroundOverlay circle = mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
	            .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(mBitmap)));

	    // Prep the animator   
	    PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 0, radius);
	    PropertyValuesHolder transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0, 1);
	    mValueAnimator = new ValueAnimator();
	    mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
	    mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
	    mValueAnimator.setValues(radiusHolder, transparencyHolder);
	    mValueAnimator.setDuration(DURATION);
	    mValueAnimator.setEvaluator(new FloatEvaluator());
	    mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
	    mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	        @Override
	        public void onAnimationUpdate(ValueAnimator valueAnimator) {
	            float animatedRadius = (float) valueAnimator.getAnimatedValue("radius");
	            float animatedAlpha = (float) valueAnimator.getAnimatedValue("transparency");
	            circle.setDimensions(animatedRadius * 2);
	            circle.setTransparency(animatedAlpha);
	        }
	    });

	    // start the animation
	    mValueAnimator.start();
	}
	
	public void stopRipples() {
		mValueAnimator.end();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_safty, container, false);
		findView(rootView);
		setListener();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		mMapView.onCreate(savedInstanceState);
		mLocationOnMap.setVisibility(View.INVISIBLE);
		initMapComponent();
		initRecycleView();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		init();
		mContext = getContext();
		return rootView;
	}

	private void init() {
	    mGradientDrawable = new GradientDrawable();
	    mGradientDrawable.setShape(GradientDrawable.OVAL);
	    mGradientDrawable.setSize(500,500);
	    mGradientDrawable.setColor(getResources().getColor(R.color.md_red_700));
	    mGradientDrawable.setStroke(0, Color.TRANSPARENT);

	    mBitmap = Bitmap.createBitmap(mGradientDrawable.getIntrinsicWidth()
	            , mGradientDrawable.getIntrinsicHeight()
	            , Bitmap.Config.ARGB_8888);
	}
	private void setListener() {
		mLocationOnMap.setOnClickListener(mOnLocateClickListener);
	}
	
	private View.OnClickListener mOnLocateClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//TODO get location from device
			
			//Update map
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastPosition, 16);
			mGoogleMap.animateCamera(cameraUpdate);			
		}
	};
	private void initMapComponent() {

		mMapView.getMapAsync(mOnMapReadyCallback);
	}

	private void initRecycleView() {

		mRecyclerView.setHasFixedSize(true);

		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new GeoEventAdapter(myDataset);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void testData() {
		if (myDataset.size() == 0) {
			for (int i = 30; i > 11; i--) {
				GeoEventItem item = new GeoEventItem();
				item.setDate("2017/06/" + i);
				item.setEnterSchool("06:" + i);
				item.setLeaveSchool("16:" + i);
				item.setEmergency("18:" + i);
				item.setEmergencyRelease("18:" + i);
				myDataset.add(item);
			}
		}
	}

	private void findView(View rootView) {
		mMapView = (MapView) rootView.findViewById(R.id.map_view);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.daily_event_view);
		mLocationOnMap = (FloatingActionButton) rootView.findViewById(R.id.map_location);
		mUpdateText = (TextView) rootView.findViewById(R.id.gps_update_time);
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		if (mCurrentItem != null) {
			if (myDataset.indexOf(mCurrentItem) == -1) {
				myDataset.add(0, mCurrentItem);
			}
			mAdapter.notifyDataSetChanged();
			new getCurrentLocation().execute("");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {

		@Override
		public void onMapReady(GoogleMap map) {
			mGoogleMap = map;
			mGoogleMap.setMaxZoomPreference(18);
			mGoogleMap.setMinZoomPreference(12);
			new getCurrentLocation().execute("");
		}
	};
	
	public void setAlert(LatLng position, String updateTime) {
		mLastPosition = position;
		mGoogleMap.clear();
		if (isDetached()) {
			return;
		}
		showRipples(mLastPosition);
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastPosition, 16);
		mGoogleMap.moveCamera(cameraUpdate);
		mGoogleMap.addMarker(new MarkerOptions()
                .position(mLastPosition)
                .title("最後位置"));
		mLocationOnMap.setVisibility(View.VISIBLE);
	}
	
	class getCurrentLocation extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... params) {
			SharedPreferences sp = mContext.getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			String token = sp.getString(Def.SP_LOGIN_TOKEN, "");
			GuardianApiClient apiClient = new GuardianApiClient(getActivity());
			apiClient.setToken(token);
			JSONResponse response = apiClient.getStudentLocation(mStudents.get(mCurrnetStudentIdx));
			if (response == null) {
				return null;
			}
			if (TextUtils.equals(Def.RET_SUCCESS_1, response.getReturn().getResponseSummary().getStatusCode())) {
				String lat = response.getReturn().getResults().getLatitude();
				String lnt = response.getReturn().getResults().getLongitude();
				mLastPosition = new LatLng(Double.parseDouble(lat), Double.parseDouble(lnt));
			} else if (TextUtils.equals(Def.RET_ERR_02, response.getReturn().getResponseSummary().getStatusCode())) {
				return Def.RET_ERR_02;
			}
			return "";
		}

		protected void onPostExecute(String result) {
			if (TextUtils.equals(Def.RET_ERR_02, result)) {
				Toast.makeText(mContext, "Token provided is expired, need to re-login", Toast.LENGTH_LONG).show();
				return;
			}
			mGoogleMap.addMarker(new MarkerOptions()
	                .position(mLastPosition)
	                .title("最後位置"));
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastPosition, 16);
			mGoogleMap.moveCamera(cameraUpdate);
			if (isAlerted) {
				setAlert(mLastPosition, "2017-07-10 週一 07:50");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EE HH:mm", Locale.TAIWAN);
			String updateTime = sdf.format(Calendar.getInstance().getTime());
			mUpdateText.setText(updateTime);
		};
	}
}
