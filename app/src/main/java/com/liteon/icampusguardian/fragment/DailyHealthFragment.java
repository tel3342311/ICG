package com.liteon.icampusguardian.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.HealthHistogramView;
import com.liteon.icampusguardian.util.HealthPieChartView;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DailyHealthFragment extends Fragment {
	
	private List<Integer> mDataList;
	private List<String> mDateList;
	private HealthHistogramView mHistogramView;
	private HealthPieChartView mPiechartView;
	private TYPE mType;
	private int mCurrnetStudentIdx;

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

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		testData();
		setupPieChart();
		setupHistogram();
	}

	private void setupHistogram() {
		mHistogramView.setType(mType);
		mHistogramView.setOnHistogramClickListener(mPiechartView);
		mHistogramView.setDates(mDateList);
		mHistogramView.setValuesByDay(mDataList);
		//mHistogramView.setTargetNumber(getTargetByType(mType));
	}
	private void setupPieChart() {
		mPiechartView.setType(mType);
		mPiechartView.setValue(mDataList.get(mDataList.size() - 1));
	}
	private void findView(View rootView) {
		mHistogramView = rootView.findViewById(R.id.healthy_histogram_view);
		mPiechartView = rootView.findViewById(R.id.pie_chart_view);
	}
	private void testData() {

		mDataList = getTestValue(mType, mCurrnetStudentIdx);

		mDateList = new ArrayList<>(7);
		String dateStr = "2018/01/19";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date date = Calendar.getInstance().getTime();
		try {
			 date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
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

	private List<Integer> getTestValue(HealthyItem.TYPE type, int idx) {
		List<Integer> list = new ArrayList<>();
		switch(type) {
			case ACTIVITY:
				if (idx == 0) {
					Integer[] data = {85,83,80,82,88,85,86};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {76,78,80,76,72,70,76};
					list.addAll(Arrays.asList(data));
				}
				break;
			case CALORIES_BURNED:
				if (idx == 0) {
					Integer[] data = {1060,1020,988,1005,1250,1190,1200};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {830,980,1100,920,940,932,860};
					list.addAll(Arrays.asList(data));
				}
				break;
			case CYCLING_TIME:
				if (idx == 0) {
					Integer[] data = {15,10,20,30,15,25,20};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {15,20,10,10,25,10,15};
					list.addAll(Arrays.asList(data));
				}
				break;
			case HEART_RATE:
				if (idx == 0) {
					Integer[] data = {81,80,82,81,83,79,80};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {85,87,86,85,83,84,86};
					list.addAll(Arrays.asList(data));
				}
				break;
			case RUNNING_TIME:
				if (idx == 0) {
					Integer[] data = {40,15,15,32,30,18,20};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {25,12,30,15,18,22,23};
					list.addAll(Arrays.asList(data));
				}
				break;
			case SLEEP_TIME:
				if (idx == 0) {
					Integer[] data = {560,575,560,573,590,610,535};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {530,523,515,520,560,592,490};
					list.addAll(Arrays.asList(data));
				}
				break;
			case TOTAL_STEPS:
				if (idx == 0) {
					Integer[] data = {7600,8200,7500,6691,5682,5498,6687};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {6400,5250,7123,4451,5338,5409,6127};
					list.addAll(Arrays.asList(data));
				}
				break;
			case WALKING_TIME:
				if (idx == 0) {
					Integer[] data = {25,42,37,27,22,20,27};
					list.addAll(Arrays.asList(data));
				} else {
					Integer[] data = {23,18,35,18,22,23,20};
					list.addAll(Arrays.asList(data));
				}
				break;
			default:
				break;

		}
        Collections.reverse(list);
		return list;
    }
}
