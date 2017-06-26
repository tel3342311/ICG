package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;
import java.util.List;

import com.aigestudio.wheelpicker.WheelPicker;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter.ViewHolder.IAlarmPeriodViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmPeriodItem;
import com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlarmEditingFragment extends Fragment {

	private int mEditIdx = -1;
	private AlarmItem mCurrentAlarmItem;
	private WheelPicker mHourPicker;
	private WheelPicker mMinutePicker;
	private List<String> mHourList = new ArrayList<String>();
	private List<String> mMinuteList = new ArrayList<String>();
	private static ArrayList<AlarmPeriodItem> alarmPeriodDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private IAlarmPeriodViewHolderClicks mOnItemClickListener;
	
	public AlarmEditingFragment(IAlarmPeriodViewHolderClicks clicks) {
		mOnItemClickListener = clicks;
	}

	public AlarmEditingFragment(int idx, IAlarmPeriodViewHolderClicks clicks) {
		mEditIdx = idx;
		mOnItemClickListener = clicks;

	}

	private void testData() {
		mCurrentAlarmItem = new AlarmItem();
		mCurrentAlarmItem.setDate("2017/06/26");
		mCurrentAlarmItem.setPeriod("週一至週五");
		mCurrentAlarmItem.setEnabled(true);
		AlarmPeriodItem item = new AlarmPeriodItem();
		item.setItemType(TYPE.CUSTOMIZE);
		mCurrentAlarmItem.setPeriodItem(item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_alarm_editing, container, false);
		findView(rootView);
		initWheelView();
		initRecycleView();
		return rootView;
	}

	private void findView(View rootView) {
		mHourPicker = (WheelPicker) rootView.findViewById(R.id.main_wheel_left);
		mMinutePicker = (WheelPicker) rootView.findViewById(R.id.main_wheel_right);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_period_view);
	}
	
	private void initRecycleView() {
		for (AlarmPeriodItem.TYPE type : AlarmPeriodItem.TYPE.values()) {
			AlarmPeriodItem item = new AlarmPeriodItem();
			item.setItemType(type);
			alarmPeriodDataset.add(item);
		}
		
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new AlarmPeriodAdapter(alarmPeriodDataset, mOnItemClickListener, mCurrentAlarmItem);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void initWheelView() {
		for (int i = 0; i < 24; i++) {
			mHourList.add(Integer.toString(i));
		}
		mHourPicker.setData(mHourList);

		for (int i = 0; i < 60; i++) {
			mMinuteList.add(Integer.toString(i));
		}
		mMinutePicker.setData(mMinuteList);
	}
}
