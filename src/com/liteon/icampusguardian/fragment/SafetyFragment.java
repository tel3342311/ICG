package com.liteon.icampusguardian.fragment;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.LoginActivity;
import com.liteon.icampusguardian.MainActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.JSONResponse;
import com.liteon.icampusguardian.util.JSONResponse.Device;
import com.liteon.icampusguardian.util.JSONResponse.DeviceEvent;
import com.liteon.icampusguardian.util.JSONResponse.Results;
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
	private String mLastPositionUpdateTime;
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
	private Map<String, GeoEventItem> mEventReport = new HashMap<>();
	private Map<String, Map<String, GeoEventItem>> mAllGeoEventItem;
	
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
		if (mStudents.size() == 0) {
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(Def.SP_LOGIN_TOKEN);
			editor.commit();
			DBHelper helper = DBHelper.getInstance(getActivity());
			helper.deleteAccount(helper.getWritableDatabase());
			getActivity().finish();
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			startActivity(intent);
		} else if (mStudents.size() >0 && mCurrnetStudentIdx >= mStudents.size()) {
			mCurrnetStudentIdx = 0;
		}
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
		//testData();
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
		restoreAlarm();
		String id = mStudents.get(mCurrnetStudentIdx).getStudent_id();
		new getEventReportTask().execute(id);
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
		saveAlarm();
	}

	private void restoreAlarm() {
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String geoItemMap = sp.getString(Def.SP_GEO_ITEM_MAP, "");
		Type typeOfHashMap = new TypeToken<Map<String, Map<String, GeoEventItem>>>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mAllGeoEventItem = gson.fromJson(geoItemMap, typeOfHashMap);
		if (TextUtils.isEmpty(geoItemMap)) {
			mAllGeoEventItem = new HashMap<String, Map<String, GeoEventItem>>();
			for (Student student : mStudents) {
				String id = student.getStudent_id();
				mAllGeoEventItem.put(id, new HashMap<String, GeoEventItem>());
			}
		}
		if (mAllGeoEventItem.get(mStudents.get(mCurrnetStudentIdx).getStudent_id()) == null) {
			mAllGeoEventItem.put(mStudents.get(mCurrnetStudentIdx).getStudent_id(), new HashMap<String, GeoEventItem>());
		}
		mEventReport = mAllGeoEventItem.get(mStudents.get(mCurrnetStudentIdx).getStudent_id());
		myDataset.clear();
		myDataset.addAll(mEventReport.values());
		Collections.sort(myDataset, new Comparator<GeoEventItem>() {
	        @Override
	        public int compare(GeoEventItem item1, GeoEventItem item2)
	        {

	            return  item2.getDate().compareTo(item1.getDate());
	        }
	    });
		mAdapter.notifyDataSetChanged();
	}
	
	private void saveAlarm() {
		mAllGeoEventItem.put(mStudents.get(mCurrnetStudentIdx).getStudent_id(), mEventReport);
		Gson gson = new Gson();
		String input = gson.toJson(mAllGeoEventItem);
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_GEO_ITEM_MAP, input);
		editor.commit();
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
				if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lnt)) {
					return "";
				}
				mLastPositionUpdateTime = response.getReturn().getResults().getEvent_occured_date();
				
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
			if (mGoogleMap != null) {
				mGoogleMap.addMarker(new MarkerOptions().position(mLastPosition).title("最後位置"));
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastPosition, 16);
				mGoogleMap.moveCamera(cameraUpdate);
				if (isAlerted) {
					setAlert(mLastPosition, "2017-07-10 週一 07:50");
				}
				SimpleDateFormat sdFormat = new SimpleDateFormat();
				String format = "yyyy-MM-dd HH:mm:ss.S";
				sdFormat.applyPattern(format);
				Date date = Calendar.getInstance().getTime();
				if (!TextUtils.isEmpty(mLastPositionUpdateTime)) {
					try {
						date = sdFormat.parse(mLastPositionUpdateTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EE HH:mm", Locale.TAIWAN);
				String updateTime = sdf.format(date);
				mUpdateText.setText(updateTime);
			}
		};
	}
	
	class getEventReportTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... args) {
			GuardianApiClient apiClient = new GuardianApiClient(getActivity());
			JSONResponse responseEnter = apiClient.getDeviceEventReport(args[0], Def.EVENT_ID_ENTER_SCHOOL, "30");
			parseEvent(responseEnter);
			JSONResponse responseSOS = apiClient.getDeviceEventReport(args[0], Def.EVENT_ID_SOS_ALERT, "30");
			parseEvent(responseSOS);
			if (mCurrentItem != null) {
				GeoEventItem item = mEventReport.get(mCurrentItem.getDate());
				if (item == null) {
					mEventReport.put(mCurrentItem.getDate(), mCurrentItem);
				} else {
					item.setEmergency(mCurrentItem.getEmergency());
				}
			}
			myDataset.clear();
			myDataset.addAll(mEventReport.values());
			Collections.sort(myDataset, new Comparator<GeoEventItem>() {
		        @Override
		        public int compare(GeoEventItem item1, GeoEventItem item2)
		        {

		            return  item2.getDate().compareTo(item1.getDate());
		        }
		    });
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	private void parseEvent(JSONResponse response) {
		if (response != null) {
			Results results = response.getReturn().getResults();
			if (results != null) {
				String event_id = results.getEvent_id();
				Device[] devices = results.getDevices();
				if (devices[0] != null) {
					DeviceEvent[] events = devices[0].getDevice_events();
					for (DeviceEvent event : events) {
						String event_occured_date = event.getEvent_occured_date();
						Date date = getDateByStringFormatted(event_occured_date, "yyyy-MM-dd HH:mm:ss.S");
						String key = getStringByDate(date, "yyyy/MM/dd");
						GeoEventItem item;
						if (mEventReport.containsKey(key)) {
							item = mEventReport.get(key);
						} else {
							item = new GeoEventItem();
							item.setDate(key);
							mEventReport.put(key, item);
						}
						setGeoItemById(event_id, date, item);
					}
				}
			}
		}
	}
	
	private Date getDateByStringFormatted(String time, String pattern) {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			date = simpleDateFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	private String getStringByDate(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}
	
	private void setGeoItemById(String eventId, Date date, GeoEventItem item) {
		String time = getStringByDate(date, "MM:dd");
		if (TextUtils.equals(eventId, Def.EVENT_ID_ENTER_SCHOOL)) {
			String tmp = item.getEnterSchool();
			if (TextUtils.isEmpty(tmp)) {
				item.setEnterSchool(time);
			} else {
				if (time.compareTo(tmp) > 0) {
					item.setEnterSchool(time);
				}
			}
		} else if (TextUtils.equals(eventId, Def.EVENT_ID_LEAVE_SCHOOL)) {
			String tmp = item.getLeavelSchool();
			if (TextUtils.isEmpty(tmp)) {
				item.setLeaveSchool(time);
			} else {
				if (time.compareTo(tmp) > 0) {
					item.setLeaveSchool(time);
				}
			}
		} else if (TextUtils.equals(eventId, Def.EVENT_ID_SOS_ALERT)) {
			String tmp = item.getEmergency();
			if (TextUtils.isEmpty(tmp)) {
				item.setEmergency(time);
			} else {
				if (time.compareTo(tmp) > 0) {
					item.setEmergency(time);
				}
			}
		} else if (TextUtils.equals(eventId, Def.EVENT_ID_SOS_REMOVE)) {
			String tmp = item.getEmergencyRelease();
			if (TextUtils.isEmpty(tmp)) {
				item.setEmergencyRelease(time);
			} else {
				if (time.compareTo(tmp) > 0) {
					item.setEmergencyRelease(time);
				}
			}
		}
	}
	
}
