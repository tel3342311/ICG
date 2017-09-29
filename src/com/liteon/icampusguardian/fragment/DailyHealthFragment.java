package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GeoEventAdapter;
import com.liteon.icampusguardian.util.GeoEventItem;
import com.liteon.icampusguardian.util.HealthHistogramView;
import com.liteon.icampusguardian.util.HealthPieChartView;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.HealthyItemAdapter;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
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
		int target = getTargetByType(mType);
		
		mDataList = new ArrayList<>(7);
		mDataList.add(target);
		for (int i = 1; i < 7; i++) {
			mDataList.add((int) (Math.random() * target));
		}

		mDateList = new ArrayList<>(7);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		for (int i = 0; i < 7; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
			String format = simpleDateFormat.format(calendar.getTime());
			mDateList.add(0, format);
			
		}
		
	}
	
	private int getTargetByType(HealthyItem.TYPE type) {
		int target = 0;
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
    	String carlos = sp.getString(Def.SP_TARGET_CARLOS, "2000");
    	String step = sp.getString(Def.SP_TARGET_STEPS, "10000");
    	String walking = sp.getString(Def.SP_TARGET_WALKING, "30");
    	String running = sp.getString(Def.SP_TARGET_RUNNING, "30");
    	String cycling = sp.getString(Def.SP_TARGET_CYCLING, "30");
    	String sleep = sp.getString(Def.SP_TARGET_SLEEPING, "9");
		switch(type) {
		case ACTIVITY:
			target = 99;
			break;
		case CALORIES_BURNED:
			target = Integer.parseInt(carlos);
			break;
		case CYCLING_TIME:
			target = Integer.parseInt(cycling);
			break;
		case HEART_RATE:
			target = 80;
			break;
		case RUNNING_TIME:
			target = Integer.parseInt(running);
			break;
		case SLEEP_TIME:
			target = Integer.parseInt(sleep);
			target *= 60;
			break;
		case TOTAL_STEPS:
			target = Integer.parseInt(step);
			break;
		case WALKING_TIME:
			target = Integer.parseInt(walking);
			break;
		default:
			break;
		
		}
		return target;
	}
}
