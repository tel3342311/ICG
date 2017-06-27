package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmItemAdapter;
import com.liteon.icampusguardian.util.AlarmItemAdapter.ViewHolder.IAlarmViewHolderClicks;
import com.liteon.icampusguardian.util.HealthyItem;

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

public class AlarmFragment extends Fragment  implements IAlarmViewHolderClicks {

	private static ArrayList<AlarmItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private IAddAlarmClicks mAddAlarmClicks;
	private boolean isEditMode;
	private Toolbar mToolbar;
	public AlarmFragment(IAddAlarmClicks listener) {
		mAddAlarmClicks = listener;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
		findView(rootView);
		initRecycleView();
		setupListener();
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.alarm_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (isEditMode) {
			menu.findItem(R.id.action_add).setVisible(true);
			menu.findItem(R.id.action_complete).setVisible(true);
			menu.findItem(R.id.action_edit).setVisible(false);
		} else {
			menu.findItem(R.id.action_add).setVisible(false);
			menu.findItem(R.id.action_complete).setVisible(false);
			menu.findItem(R.id.action_edit).setVisible(true);	
		}
	    super.onPrepareOptionsMenu(menu);
	}
	
	public boolean isEditMode() {
		return isEditMode;
	}
	
	public void exitEditMode() {
		isEditMode = false;
		getActivity().invalidateOptionsMenu();
		mAddAlarm.setVisibility(View.VISIBLE);
		((AlarmItemAdapter)mAdapter).setEditMode(false);
		mToolbar.setTitle("鬧鈴");
	}
	
	public void enterEditMode() {
		isEditMode = true;
		getActivity().invalidateOptionsMenu();
		mAddAlarm.setVisibility(View.GONE);
		((AlarmItemAdapter)mAdapter).setEditMode(true);
		mToolbar.setTitle("編輯鬧鈴");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			enterEditMode();
			break;
		case R.id.action_add:
			exitEditMode();
			mAddAlarmClicks.onAddAlarmClick();
			break;
		case R.id.action_complete:
			exitEditMode();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void findView(View rootView) {

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_view);
		mAddAlarm = (AppCompatButton) rootView.findViewById(R.id.add_alarm);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
	}
	
	public void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new AlarmItemAdapter(myDataset, this);
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

	@Override
	public void onEditAlarm(int position) {
		mAddAlarmClicks.onEditAlarm(position);
	}
	
	@Override
	public void onDeleteAlarm(int delete) {
		myDataset.remove(delete);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onEnableAlarm(int position, boolean enable) {
		myDataset.get(position).Enabled = enable;		
		
	}
}
