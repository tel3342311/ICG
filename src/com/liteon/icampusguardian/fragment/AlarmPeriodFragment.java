package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks;
import com.liteon.icampusguardian.util.HealthyItem;
import com.liteon.icampusguardian.util.WeekAdapter;
import com.liteon.icampusguardian.util.WeekAdapter.ViewHolder.IWeekViewHolderClicks;
import com.liteon.icampusguardian.util.WeekPeriodItem;

import android.R.integer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
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
import android.view.View.OnClickListener;

public class AlarmPeriodFragment extends Fragment implements IWeekViewHolderClicks{

	private static ArrayList<WeekPeriodItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AlarmItem mItem;
	
	public AlarmPeriodFragment(AlarmItem item) {
		mItem = item;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_alarm_period, container, false);
		findView(rootView);
		initRecycleView();
		setupListener();
		return rootView;
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
		mItem.PeriodItem.setValue(value);
	}
}
