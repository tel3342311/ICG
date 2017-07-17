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
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItem;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
	private Map<String, List<AlarmItem>> mAlarmMap;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	private int mCurrnetStudentIdx;
	private static ArrayList<AlarmItem> myDataset = new ArrayList<>();
	private EditText mAlarmName;
	
	public AlarmEditingFragment(IAlarmPeriodViewHolderClicks clicks) {
		mOnItemClickListener = clicks;
	}

	public AlarmEditingFragment(int idx, IAlarmPeriodViewHolderClicks clicks) {
		mEditIdx = idx;
		mOnItemClickListener = clicks;

	}

	private void testData() {
		mCurrentAlarmItem = new AlarmItem();
		mCurrentAlarmItem.setTitle("上學");
		mCurrentAlarmItem.setDate("00:00");
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
		setListener();
		initWheelView();
		initRecycleView();
		mDbHelper = DBHelper.getInstance(getActivity());
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
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
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			mCurrentAlarmItem.setTitle(s.toString());
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
		mAlarmName = (EditText) rootView.findViewById(R.id.alarm_name);
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
				String uuid = student.getUuid();
				mAlarmMap.put(uuid, new ArrayList<AlarmItem>());
			}
		}
		myDataset.clear();
		myDataset.addAll((ArrayList) mAlarmMap.get(mStudents.get(mCurrnetStudentIdx).getUuid()));
		if (mEditIdx == -1) {
			testData();
			myDataset.add(mCurrentAlarmItem);
		} else {
			mCurrentAlarmItem = myDataset.get(mEditIdx);
			mAlarmName.setText(mCurrentAlarmItem.getTitle());
		}
		mHourPicker.setSelectedItemPosition(mHourList.indexOf(mCurrentAlarmItem.getDate().substring(0, 2)));
		mMinutePicker.setSelectedItemPosition(mMinuteList.indexOf(mCurrentAlarmItem.getDate().substring(3)));

	}
	
	private void saveAlarm() {
		mAlarmMap.put(mStudents.get(mCurrnetStudentIdx).getUuid(), myDataset);
		Gson gson = new Gson();
		String input = gson.toJson(mAlarmMap);
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_ALARM_MAP, input);
		editor.commit();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		mCurrnetStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
		restoreAlarm();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveAlarm();
	}

	@Override
	public void onClick(AlarmPeriodItem item) {
		mCurrentAlarmItem.setPeriodItem(item);
		mCurrentAlarmItem.setPeriod(item.getTitle());
		mOnItemClickListener.onClick(item);
	}
}
