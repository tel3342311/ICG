package com.liteon.icampusguardian.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.WheelPicker.OnItemSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.MainActivity;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.AlarmManager;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter;
import com.liteon.icampusguardian.util.AlarmPeriodAdapter.ViewHolder.IAlarmPeriodViewHolderClicks;
import com.liteon.icampusguardian.util.AlarmPeriodItem;
import com.liteon.icampusguardian.util.AlarmPeriodItem.TYPE;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.TintableBackgroundView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class AlarmEditingFragment extends Fragment implements IAlarmPeriodViewHolderClicks{

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
	private TextView mTitleView;
	private EditText mAlarmName;
	public AlarmEditingFragment(IAlarmPeriodViewHolderClicks clicks) {
		mOnItemClickListener = clicks;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_alarm_editing, container, false);
		findView(rootView);
		setListener();

		return rootView;
	}

	private void setListener() {
		mAlarmName.addTextChangedListener(mOnTitleChange);
		mHourPicker.setOnItemSelectedListener(mOnHourWheelSeleted);
		mMinutePicker.setOnItemSelectedListener(mOnMinuteWheelSeleted);
	}
	
	private OnItemSelectedListener mOnHourWheelSeleted = new OnItemSelectedListener() {
		
		@Override
		public void onItemSelected(WheelPicker arg0, Object arg1, int arg2) {
			mCurrentAlarmItem.setDate(mHourList.get(arg2) + ":" + mMinuteList.get(mMinutePicker.getCurrentItemPosition()));
		}
	};
	
	private OnItemSelectedListener mOnMinuteWheelSeleted = new OnItemSelectedListener() {
		
		@Override
		public void onItemSelected(WheelPicker arg0, Object arg1, int arg2) {
			mCurrentAlarmItem.setDate(mHourList.get(mHourPicker.getCurrentItemPosition()) + ":" + mMinuteList.get(arg2));
		}
	};
	private TextWatcher mOnTitleChange = new TextWatcher() {
		private int editStart;  
	    private int editEnd;
	    private int maxLen = 8; // the max byte  
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			editStart = mAlarmName.getSelectionStart();  
	        editEnd = mAlarmName.getSelectionEnd();  
	        mAlarmName.removeTextChangedListener(mOnTitleChange);  
	        if (!TextUtils.isEmpty(mAlarmName.getText())) {  
	            String etstring = mAlarmName.getText().toString().trim();  
	            while (calculateLength(s.toString()) > maxLen) {  
	                s.delete(editStart - 1, editEnd);  
	                editStart--;  
	                editEnd--;  
	                Log.d("TextChanged", "editStart = " + editStart + " editEnd = " + editEnd);  
	            }  
	        }  
	  
	        mAlarmName.setText(s);  
	        mAlarmName.setSelection(editStart);  
	  
 
	        mAlarmName.addTextChangedListener(mOnTitleChange);  
			mCurrentAlarmItem.setTitle(s.toString());
		}
		
		private int calculateLength(String etstring) {  
	        char[] ch = etstring.toCharArray();  
	  
	        int varlength = 0;  
	        for (int i = 0; i < ch.length; i++) {  
	            if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) { // 中文字符範圍0x4e00 0x9fbb  
	                varlength = varlength + 2;  
	            } else {  
	                varlength++;  
	            }  
	        }  
	        Log.d("TextChanged", "varlength = " + varlength);  
	        return varlength;  
	    }  
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.one_confirm_menu, menu);
		mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	getActivity().onBackPressed();
            	if (AlarmManager.getCurrentAction() == AlarmManager.ACTION_EDITING) {
            		AlarmManager.restoreCurrentItem();
            	}
            	AlarmManager.mNewItem = null;
            	AlarmManager.setCurrentAction(-1);
            	AlarmManager.saveAlarm();
            }
        });
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_confirm) {
			getActivity().onBackPressed();
			if (AlarmManager.ACTION_ADDING == AlarmManager.getCurrentAction()) {
				AlarmManager.getDataSet().add(mCurrentAlarmItem);
				AlarmManager.mNewItem = null;
			}
			AlarmManager.saveAlarm();
		}
		return super.onOptionsItemSelected(item);
	}
	private void findView(View rootView) {
		mAlarmName = (EditText) rootView.findViewById(R.id.alarm_name);
		mHourPicker = (WheelPicker) rootView.findViewById(R.id.main_wheel_left);
		mMinutePicker = (WheelPicker) rootView.findViewById(R.id.main_wheel_right);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.alarm_period_view);
		mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		mTitleView = (TextView) getActivity().findViewById(R.id.toolbar_title);
	}
	
	private void initRecycleView() {
		if (alarmPeriodDataset.size() == 0) {
			
			for (AlarmPeriodItem.TYPE type : AlarmPeriodItem.TYPE.values()) {
				AlarmPeriodItem item = new AlarmPeriodItem();
				item.setItemType(type);
				alarmPeriodDataset.add(item);
				
			}
		}
		for (AlarmPeriodItem item : alarmPeriodDataset) {
			if (item.getItemType() == mCurrentAlarmItem.getPeriodItem().getItemType()) {
				item.setSelected(true);
			} else {
				item.setSelected(false);
			}
		}
		
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new AlarmPeriodAdapter(alarmPeriodDataset, this, mCurrentAlarmItem);
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
		mHourPicker.setSelectedItemPosition(mHourList.indexOf(mCurrentAlarmItem.getDate().substring(0, 2)));
		mMinutePicker.setSelectedItemPosition(mMinuteList.indexOf(mCurrentAlarmItem.getDate().substring(3)));

	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (AlarmManager.getCurrentAction() == AlarmManager.ACTION_ADDING) {
			if (AlarmManager.mNewItem == null) {
				mCurrentAlarmItem = new AlarmItem();
				mCurrentAlarmItem.setTitle(getString(R.string.alarm));
				mCurrentAlarmItem.setDate("00:00");
				mCurrentAlarmItem.setPeriod(getString(R.string.alarm_week_day));
				mCurrentAlarmItem.setEnabled(true);
				AlarmPeriodItem item = new AlarmPeriodItem();
				item.setItemType(TYPE.WEEK_DAY);
				mCurrentAlarmItem.setPeriodItem(item);
				AlarmManager.mNewItem = mCurrentAlarmItem;
			} else {
				mCurrentAlarmItem = AlarmManager.mNewItem;
			}
			
		} else {
			mCurrentAlarmItem = AlarmManager.mCurrentItem;
		}
		initWheelView();
		initRecycleView();
		mAlarmName.setText(mCurrentAlarmItem.getTitle());
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(AlarmPeriodItem item, AlarmItem alarmItem) {
		mCurrentAlarmItem.setPeriodItem(item);
		if (item.getItemType() != TYPE.CUSTOMIZE) {
			mCurrentAlarmItem.setPeriod(item.getTitle());

		}
		mOnItemClickListener.onClick(item, mCurrentAlarmItem);

		for (AlarmPeriodItem periodItem : alarmPeriodDataset) {
			periodItem.setSelected(false);
		}
		item.setSelected(true);
		mAdapter.notifyDataSetChanged();
	}
}
