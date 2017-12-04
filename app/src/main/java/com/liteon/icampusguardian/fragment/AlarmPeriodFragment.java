package com.liteon.icampusguardian.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmManager;
import com.liteon.icampusguardian.util.WeekAdapter;
import com.liteon.icampusguardian.util.WeekAdapter.ViewHolder.IWeekViewHolderClicks;
import com.liteon.icampusguardian.util.WeekPeriodItem;

import java.util.ArrayList;

public class AlarmPeriodFragment extends Fragment implements IWeekViewHolderClicks {

	private static ArrayList<WeekPeriodItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AlarmItem mItem;
	
	public AlarmPeriodFragment() {
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
		if (AlarmManager.ACTION_ADDING == AlarmManager.getCurrentAction()) {
			mItem = AlarmManager.mNewItem;
		} else {
			mItem = AlarmManager.mCurrentItem;
		}
		initRecycleView();
	}
	
	public void findView(View rootView) {
		mRecyclerView = rootView.findViewById(R.id.alarm_view);
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
	
	public long getCurrentPeriod() {
		return mItem.getPeriodItem().getCustomValue();
	}
}
