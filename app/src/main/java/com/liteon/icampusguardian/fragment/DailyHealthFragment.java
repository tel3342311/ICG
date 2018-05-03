package com.liteon.icampusguardian.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.db.HealthDataTable;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.HealthHistogramView;
import com.liteon.icampusguardian.util.HealthPieChartView;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.JSONResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyHealthFragment extends Fragment {
	
	private List<Integer> mDataList;
	private List<String> mDateList;
	private HealthHistogramView mHistogramView;
	private HealthPieChartView mPiechartView;
	private TYPE mType;
	private int mCurrentStudentIdx;
	private DBHelper mDbHelper;
	private List<JSONResponse.Student> mStudents;

	public DailyHealthFragment() { mType = TYPE.ACTIVITY; }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_daily_healthy, container, false);
		findView(rootView);
		return rootView;
	}

    public void setType(TYPE type) {
	    mType = type;
    }

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		getDataFromDB();
		setupPieChart();
		setupHistogram();
	}

	private void setupHistogram() {
		mHistogramView.setType(mType);
		mHistogramView.setOnHistogramClickListener(mPiechartView);
		mHistogramView.setDates(mDateList);
		mHistogramView.setValuesByDay(mDataList);
	}
	private void setupPieChart() {
		mPiechartView.setType(mType);
		mPiechartView.setValue(mDataList.get(mDataList.size() - 1));
	}
	private void findView(View rootView) {
		mHistogramView = rootView.findViewById(R.id.healthy_histogram_view);
		mPiechartView = rootView.findViewById(R.id.pie_chart_view);
	}
	private void getDataFromDB() {
		mDateList = new ArrayList<>(7);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		for (int i = 0; i < 7; i++) {

			String format = simpleDateFormat.format(calendar.getTime());
			mDateList.add(0, format);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		}

		mDataList = getHealthyValue(mType, mCurrentStudentIdx);
		
	}

	private List<Integer> getHealthyValue(HealthyItem.TYPE type, int idx) {
		List<Integer> list = new ArrayList<>(Arrays.asList(new Integer[] { 0,0,0,0,0,0,0}));
		List<JSONResponse.HealthyData> tmp;
		String student_id = mStudents.get(idx).getStudent_id();
		switch(type) {
			case ACTIVITY:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_FITNESS));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getValue());
						}
					}
				}
				break;
			case CALORIES_BURNED:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_CALOS));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getValue());
						}
					}
				}
				break;
			case CYCLING_TIME:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_CYCLING));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getDuration() / 60);
						}
					}
				}
				break;
			case HEART_RATE:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_HEART));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getValue());
						}
					}
				}
				break;
			case RUNNING_TIME:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_RUNNING));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getDuration() / 60);
						}
					}
				}
				break;
			case SLEEP_TIME:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_SLEEP));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getValue() / 60);
						}
					}
				}
				break;
			case TOTAL_STEPS:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_STEPS));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getValue());
						}
					}
				}
				break;
			case WALKING_TIME:
				tmp = mDbHelper.getHealthyDataByDuration(mDbHelper.getReadableDatabase(), student_id, Integer.toString(HealthDataTable.HealthDataEntry.SITUATION_WALKING));
				for (String date : mDateList) {
					list.set(mDateList.indexOf(date), 0);
					for (JSONResponse.HealthyData data : tmp) {
						String tmpDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(data.getDate() * 1000L));
						if (TextUtils.equals(date, tmpDate)) {
							list.set(mDateList.indexOf(date), data.getDuration() / 60);
						}
					}
				}
				break;
			default:
				break;

		}
		return list;
    }
}
