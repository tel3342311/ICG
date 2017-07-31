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
	
	private List<Integer> mDataList;
	private List<String> mDateList;
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
		testData();
		setupPieChart();
		setupHistogram();
		return rootView;
	}

	private void setupHistogram() {
		mHistogramView.setType(mType);
		mHistogramView.setOnHistogramClickListener(mPiechartView);
		mHistogramView.setDates(mDateList);
		mHistogramView.setValuesByDay(mDataList);
		mHistogramView.setTargetNumber(mDataList.get(0));
	}
	private void setupPieChart() {
		mPiechartView.setType(mType);
		mPiechartView.setTargetValue(mDataList.get(0));
		mPiechartView.setValue(mDataList.get(mDataList.size() - 1));
	}
	private void findView(View rootView) {
		mHistogramView = (HealthHistogramView) rootView.findViewById(R.id.healthy_histogram_view);
		mPiechartView = (HealthPieChartView) rootView.findViewById(R.id.pie_chart_view);
	}
	private void testData() {
		int target = (int) (Math.random() * 1000);
		
		mDataList = new ArrayList<>(7);
		mDataList.add(target);
		for (int i = 1; i < 7; i++) {
			mDataList.add((int) (Math.random() * target));
		}

		mDateList = new ArrayList<>(7);

		for (int i = 0; i < 7; i++) {
			mDateList.add( "2017/07/1"+ i);
		}
		
	}
}
