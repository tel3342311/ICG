package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SafetyFragment extends Fragment {

	private MapView mMapView;
	private GoogleMap mGoogleMap;
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private static final LatLng mLastPosition = new LatLng(25.077877, 121.571141);
	private static ArrayList<GeoEventItem> myDataset = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_safty, container, false);
		findView(rootView);
		mMapView.onCreate(savedInstanceState);

		initMapComponent();
		initRecycleView();
		return rootView;
	}

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
			for (int i = 11; i < 30; i++) {
				GeoEventItem item = new GeoEventItem();
				item.setDate("2017/06/" + i);
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
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
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
			mGoogleMap.addMarker(new MarkerOptions()
	                .position(mLastPosition)
	                .title("最後位置"));
			mGoogleMap.setMaxZoomPreference(18);
			mGoogleMap.setMinZoomPreference(12);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastPosition, 16);
			mGoogleMap.moveCamera(cameraUpdate);
		}
	};
}
