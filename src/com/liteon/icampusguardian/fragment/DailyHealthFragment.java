package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;
import com.liteon.icampusguardian.util.HealthHistogramView;
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

public class DailyHealthFragment extends Fragment {
	
	HealthHistogramView mHistogramView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_daily_healthy, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {
		mHistogramView = (HealthHistogramView) rootView.findViewById(R.id.healthy_histogram_view);
	}
}
