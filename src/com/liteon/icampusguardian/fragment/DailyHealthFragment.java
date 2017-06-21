package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;
import com.liteon.icampusguardian.util.HealthHistogramView;
import com.liteon.icampusguardian.util.HealthPieChartView;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter;

import android.R.integer;
import android.icu.text.DisplayContext.Type;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DailyHealthFragment extends Fragment {
	
	private HealthHistogramView mHistogramView;
	private HealthPieChartView mPiechartView;
	private TYPE mType;
	
	public DailyHealthFragment(HealthyItem.TYPE type) {
		mType = type;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_daily_healthy, container, false);
		findView(rootView);
		setupPieChart();
		setupHistogram();
		return rootView;
	}

	private void setupHistogram() {
		List<Integer> list = testData();
		mHistogramView.setValuesByDay(list);
		mHistogramView.setTargetNumber(list.get(0));
		mHistogramView.setOnHistogramClickListener(mPiechartView);
	}
	private void setupPieChart() {
		mPiechartView.setType(mType);
	}
	private void findView(View rootView) {
		mHistogramView = (HealthHistogramView) rootView.findViewById(R.id.healthy_histogram_view);
		mPiechartView = (HealthPieChartView) rootView.findViewById(R.id.pie_chart_view);
	}
	private List<Integer> testData() {
		int target = (int) (System.currentTimeMillis() % 1000);
		
		List<Integer> list = new ArrayList<>(7);
		list.add(target);
		for (int i = 1; i < 7; i++) {
			list.add((int) (Math.random() * target));
		}
		return list;
	}
}
