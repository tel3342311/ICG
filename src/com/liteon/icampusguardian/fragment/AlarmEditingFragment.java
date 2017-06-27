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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private Toolbar mToolbar;
	
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
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_alarm_editing, container, false);
		findView(rootView);
		initWheelView();
		initRecycleView();
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.alarm_edit_menu, menu);
		mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	getActivity().onBackPressed();
            }
        });
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_confirm) {
			//TODO Save alarm to db
			getActivity().onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	private void findView(View rootView) {
		mHourPicker = (WheelPicker) rootView.findViewById(R.id.main_wheel_left);
		mMinutePicker = (WheelPicker) rootView.findViewById(R.id.main_wheel_right);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_period_view);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
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
			String hours;
			hours = i < 10 ? ("0" + Integer.toString(i)) : Integer.toString(i);
			mHourList.add(hours);
		}
		mHourPicker.setData(mHourList);

		for (int i = 0; i < 60; i++) {
			String mins;
			mins = i < 10 ? ("0" + Integer.toString(i)) : Integer.toString(i);
			mMinuteList.add(mins);
		}
		mMinutePicker.setData(mMinuteList);
	}
}
