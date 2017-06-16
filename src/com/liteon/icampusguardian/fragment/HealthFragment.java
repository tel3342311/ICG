package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItemAdapter;

import android.icu.text.DisplayContext.Type;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HealthFragment extends Fragment {

	private static ArrayList<HealthyItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_healthy, container, false);
		findView(rootView);
		initRecycleView();
		return rootView;
	}

	private void initRecycleView() {

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new HealthyItemAdapter(myDataset);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void testData() {
		for (HealthyItem.TYPE type : HealthyItem.TYPE.values()) {
			HealthyItem item = new HealthyItem();
			item.setItemType(type);
			item.setValue((int) System.currentTimeMillis() % 5000); 
			myDataset.add(item);
		}
	}

	private void findView(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.healthy_event_view);
	}
}
