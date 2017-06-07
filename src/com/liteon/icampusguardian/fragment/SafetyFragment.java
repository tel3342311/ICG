package com.liteon.icampusguardian.fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.liteon.icampusguardian.R;

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
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
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
	
	private void initRecycleView(){

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
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
		}
	};
}
