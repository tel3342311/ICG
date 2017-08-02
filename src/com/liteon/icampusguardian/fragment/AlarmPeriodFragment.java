package com.liteon.icampusguardian.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmPeriodItem;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.WeekAdapter;
import com.liteon.icampusguardian.util.WeekAdapter.ViewHolder.IWeekViewHolderClicks;
import com.liteon.icampusguardian.util.WeekPeriodItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlarmPeriodFragment extends Fragment implements IWeekViewHolderClicks {

	private static ArrayList<WeekPeriodItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AlarmItem mItem;
	private int mEditIdx = -1;
	private Map<String, List<AlarmItem>> mAlarmMap;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private static ArrayList<AlarmItem> alarmDataset = new ArrayList<>();
	
	public AlarmPeriodFragment(int editIdx) {
		mEditIdx = editIdx;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_alarm_period, container, false);
		findView(rootView);

		setupListener();
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		mDbHelper = DBHelper.getInstance(getActivity());
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		restoreAlarm();
		initRecycleView();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveAlarm();
	}
	
	private void restoreAlarm() {
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String alarmMap = sp.getString(Def.SP_ALARM_MAP, "");
		Type typeOfHashMap = new TypeToken<Map<String, List<AlarmItem>>>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mAlarmMap = gson.fromJson(alarmMap, typeOfHashMap);
		if (TextUtils.isEmpty(alarmMap)) {
			mAlarmMap = new HashMap<String, List<AlarmItem>>();
			for (Student student : mStudents) {
				String studentId = student.getStudent_id();
				mAlarmMap.put(studentId, new ArrayList<AlarmItem>());
			}
		}
		alarmDataset = (ArrayList)mAlarmMap.get(mStudents.get(mCurrnetStudentIdx).getStudent_id());
		if (mEditIdx == -1) {
			mItem = alarmDataset.get(alarmDataset.size() - 1);
		} else {
			mItem = alarmDataset.get(mEditIdx);
		}
		if (mItem.getPeriodItem() == null) {
			AlarmPeriodItem item = new AlarmPeriodItem();
			mItem.setPeriodItem(item);
		}
		
	}
	
	private void saveAlarm() {
		mAlarmMap.put(mStudents.get(mCurrnetStudentIdx).getStudent_id(), alarmDataset);
		Gson gson = new Gson();
		String input = gson.toJson(mAlarmMap);
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_ALARM_MAP, input);
		editor.commit();
	}
	
	public void findView(View rootView) {

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_view);

	}
	
	public void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new WeekAdapter(mItem, this);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupListener() {
	}

	@Override
	public void onWeekItemClick(long value) {
		mItem.getPeriodItem().setCustomValue(value);
		StringBuilder sb = new StringBuilder();
		for (WeekPeriodItem.TYPE type : WeekPeriodItem.TYPE.values()) {
			if ((value & type.getValue()) == type.getValue()) {
				sb.append(type.getName() + " ");
			}
		}
		mItem.setPeriod(sb.toString());
	}
}
