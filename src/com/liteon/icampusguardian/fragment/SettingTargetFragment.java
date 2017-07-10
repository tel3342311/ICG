package com.liteon.icampusguardian.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;

public class SettingTargetFragment extends Fragment {
	
	private DBHelper mDbHelper;
	private TextView mTitleCarlos;
	private TextView mTitleSteps;
	private TextView mTitleWalking;
	private TextView mTitleRunning;
	private TextView mTitleCycling;
	private TextView mTitleSleeping;
	
	private EditText mCarlos;
	private EditText mStep;
	private EditText mWalking;
	private EditText mRunnings;
	private EditText mCycling;
	private EditText mSleeping;
	
	private TextView mTitleCarlosUnit;
	private TextView mTitleStepsUnit;
	private TextView mTitleWalkingUnit;
	private TextView mTitleRunningUnit;
	private TextView mTitleCyclingUnit;
	private TextView mTitleSleepingUnit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_setting_target, container, false);
		findView(rootView);
		setupListener();
		mDbHelper = DBHelper.getInstance(getActivity());
		return rootView;
	}
	
	private void findView(View rootView) {
		View carlos = rootView.findViewById(R.id.carlos);
		View steps = rootView.findViewById(R.id.steps);
		View walks = rootView.findViewById(R.id.walking);
		View running = rootView.findViewById(R.id.running);
		View cycling = rootView.findViewById(R.id.cycling);
		View sleeping = rootView.findViewById(R.id.sleeping);
		
		mTitleCarlos = (TextView) carlos.findViewById(R.id.title_text);
		mCarlos = (EditText) carlos.findViewById(R.id.value_text);
		mTitleCarlosUnit = (TextView) carlos.findViewById(R.id.unit_text);
		
		mTitleSteps = (TextView) steps.findViewById(R.id.title_text);
		mStep = (EditText) steps.findViewById(R.id.value_text);
		mTitleStepsUnit = (TextView) steps.findViewById(R.id.unit_text);
		
		mTitleWalking = (TextView) walks.findViewById(R.id.title_text);
		mWalking = (EditText) walks.findViewById(R.id.value_text);
		mTitleWalkingUnit = (TextView) walks.findViewById(R.id.unit_text);
		
		mTitleRunning = (TextView) running.findViewById(R.id.title_text);
		mRunnings = (EditText) running.findViewById(R.id.value_text);
		mTitleRunningUnit = (TextView) running.findViewById(R.id.unit_text);
		
		mTitleCycling = (TextView) cycling.findViewById(R.id.title_text);
		mCycling = (EditText) cycling.findViewById(R.id.value_text);
		mTitleCyclingUnit = (TextView) cycling.findViewById(R.id.unit_text);
		
		mTitleSleeping = (TextView) sleeping.findViewById(R.id.title_text);
		mSleeping = (EditText) sleeping.findViewById(R.id.value_text);
		mTitleSleepingUnit = (TextView) sleeping.findViewById(R.id.unit_text);
		
		mTitleCarlos.setText("每日燃燒卡路里");
		mTitleSteps.setText("步數");
		mTitleWalking.setText("走路");
		mTitleRunning.setText("跑步");
		mTitleCycling.setText("騎腳踏車");
		mTitleSleeping.setText("總睡眠時數");
		
		mTitleCarlosUnit.setText("卡");
		mTitleStepsUnit.setText("步");
		mTitleWalkingUnit.setText("分");
		mTitleRunningUnit.setText("分");
		mTitleCyclingUnit.setText("分");
		mTitleSleepingUnit.setText("小時");
		
		mCarlos.setText("60");
		mStep.setText("60");
		mWalking.setText("60");
		mRunnings.setText("60");
		mCycling.setText("60");
		mSleeping.setText("60");
		
		
	}
	
	private void setupItem(View v, View ary[]) {

	}
	
	private void setupListener() {

	}
}
