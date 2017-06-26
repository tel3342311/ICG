package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.HealthyItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class AlarmFragment extends Fragment {

	private static ArrayList<AlarmItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private IAddAlarmClicks mAddAlarmClicks;
	public AlarmFragment(IAddAlarmClicks listener) {
		mAddAlarmClicks = listener;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
		findView(rootView);
		initRecycleView();
		setupListener();
		return rootView;
	}
	
	public void findView(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_view);
		mAddAlarm = (AppCompatButton) rootView.findViewById(R.id.add_alarm);
	}
	
	public void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new AlarmItemAdapter(myDataset);
		mRecyclerView.setAdapter(mAdapter);
	}
	
	private void setupListener() {
		mAddAlarm.setOnClickListener(mAddAlarmClickListener);
	}
	
	private OnClickListener mAddAlarmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mAddAlarmClicks.onAddAlarmClick();
		}
	};
	
	public void testData() {
		AlarmItem item = new AlarmItem();
		item.setTitle("上學");
		item.setDate("06:10");
		item.setPeriod("週一");
		item.setEnabled(true);
		myDataset.add(item);
	}
	
	public static interface IAddAlarmClicks {
        public void onAddAlarmClick();
        public void onEditAlarm(int idx);
    }
}
