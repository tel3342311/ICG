package com.liteon.icampusguardian.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liteon.icampusguardian.App;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.db.HealthDataTable;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.GuardianApiClient;
import com.liteon.icampusguardian.util.HealthHistogramView;
import com.liteon.icampusguardian.util.HealthPieChartView;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.HealthyItem.TYPE;
import com.liteon.icampusguardian.util.JSONResponse;

import org.w3c.dom.Text;

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
	private int mCurrentStudentIdx;
	private DBHelper mDbHelper;
	private List<JSONResponse.Student> mStudents;
	public DailyHealthFragment(HealthyItem.TYPE type) {
		mType = type;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_daily_healthy, container, false);
		findView(rootView);
		return rootView;
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
		//new SyncHealthyData().execute();
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
        //Collections.reverse(list);
		return list;
    }

	class SyncHealthyData extends AsyncTask<Void, Void, Void> {

		JSONResponse.HealthyData[] fitness;
		JSONResponse.HealthyData[] activity;
		JSONResponse.HealthyData[] calories;
		JSONResponse.HealthyData[] heartrate;
		JSONResponse.HealthyData[] sleep;
		JSONResponse.HealthyData[] steps;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... voids) {

			Date end = Calendar.getInstance().getTime();
			Calendar c = Calendar.getInstance();
			c.setTime(end);
			c.add(Calendar.DAY_OF_YEAR, -7);
			Date start = c.getTime();
			SimpleDateFormat sdfQurey = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdfQurey.format(start);
			String endDate = sdfQurey.format(end);

			GuardianApiClient apiClient = GuardianApiClient.getInstance(App.getContext());
			JSONResponse jsonResponse = apiClient.getHealthyData(mStudents.get(mCurrentStudentIdx).getStudent_id(), startDate, endDate);
			if (jsonResponse != null) {
				if (jsonResponse.getReturn() != null && jsonResponse.getReturn().getResults() != null) {
					fitness = jsonResponse.getReturn().getResults().getFitness();
					activity = jsonResponse.getReturn().getResults().getActivity();
					calories = jsonResponse.getReturn().getResults().getCalories();
					heartrate = jsonResponse.getReturn().getResults().getHeartrate();
					sleep = jsonResponse.getReturn().getResults().getSleep();
					steps = jsonResponse.getReturn().getResults().getSteps();

				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			mDataList = Arrays.asList(0,0,0,0,0,0,0);
			int startIdx = 0;
			switch (mType) {

				case ACTIVITY:
					for (int i = 0; i < fitness.length; i++) {
						mDataList.set(i >= 7 ? 6 : i , fitness[i].getValue());
					}
					break;
				case CALORIES_BURNED:
					for (int i = 0; i < calories.length; i++) {
						mDataList.set(i >= 7 ? 6 : i,calories[i].getValue());
					}
					break;
				case TOTAL_STEPS:
					for (int i = 0; i < steps.length; i++) {
						mDataList.set(i >= 7 ? 6 : i,steps[i].getValue());
					}
					break;
				case WALKING_TIME:
					for (int i = 0; i < activity.length; i++) {
						if (activity[i].getSituation() == 2) {
							mDataList.set(startIdx, activity[i].getValue());
						}
					}
					break;
				case RUNNING_TIME:
					for (int i = 0; i < activity.length; i++) {
						if (activity[i].getSituation() == 3) {
							mDataList.set(startIdx, activity[i].getValue());
							startIdx++;
						}
					}
					break;
				case CYCLING_TIME:
					for (int i = 0; i < activity.length; i++) {
						if (activity[i].getSituation() == 4) {
							mDataList.set(startIdx,activity[i].getValue());
							startIdx++;
						}
					}
					break;
				case HEART_RATE:
					for (int i = 0; i < heartrate.length; i++) {
						mDataList.set(i >= 7 ? 6 : i, heartrate[i].getValue());
					}
					break;
				case SLEEP_TIME:
					for (int i = 0; i < sleep.length; i++) {
						mDataList.set(i >= 7 ? 6 : i, sleep[i].getValue());
					}
					break;
			}
			setupPieChart();
			setupHistogram();
		}
	}
}
