package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;
import java.util.List;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.Def;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

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
	private EditText mRunning;
	private EditText mCycling;
	private EditText mSleeping;
	
	private TextView mTitleCarlosUnit;
	private TextView mTitleStepsUnit;
	private TextView mTitleWalkingUnit;
	private TextView mTitleRunningUnit;
	private TextView mTitleCyclingUnit;
	private TextView mTitleSleepingUnit;
	private List<TextView> mTitleList = new ArrayList();
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
		carlos.setOnClickListener(mClickListener);
		View steps = rootView.findViewById(R.id.steps);
		steps.setOnClickListener(mClickListener);
		View walks = rootView.findViewById(R.id.walking);
		walks.setOnClickListener(mClickListener);
		View running = rootView.findViewById(R.id.running);
		running.setOnClickListener(mClickListener);
		View cycling = rootView.findViewById(R.id.cycling);
		cycling.setOnClickListener(mClickListener);
		View sleeping = rootView.findViewById(R.id.sleeping);
		sleeping.setOnClickListener(mClickListener);
		
		mTitleCarlos = (TextView) carlos.findViewById(R.id.title_text);
		mTitleList.add(mTitleCarlos);
		mCarlos = (EditText) carlos.findViewById(R.id.value_text);
		mTitleCarlosUnit = (TextView) carlos.findViewById(R.id.unit_text);
		
		mTitleSteps = (TextView) steps.findViewById(R.id.title_text);
		mTitleList.add(mTitleSteps);
		mStep = (EditText) steps.findViewById(R.id.value_text);
		mTitleStepsUnit = (TextView) steps.findViewById(R.id.unit_text);
		
		mTitleWalking = (TextView) walks.findViewById(R.id.title_text);
		mTitleList.add(mTitleWalking);
		mWalking = (EditText) walks.findViewById(R.id.value_text);
		mTitleWalkingUnit = (TextView) walks.findViewById(R.id.unit_text);
		
		mTitleRunning = (TextView) running.findViewById(R.id.title_text);
		mTitleList.add(mTitleRunning);
		mRunning = (EditText) running.findViewById(R.id.value_text);
		mTitleRunningUnit = (TextView) running.findViewById(R.id.unit_text);
		
		mTitleCycling = (TextView) cycling.findViewById(R.id.title_text);
		mTitleList.add(mTitleCycling);
		mCycling = (EditText) cycling.findViewById(R.id.value_text);
		mTitleCyclingUnit = (TextView) cycling.findViewById(R.id.unit_text);
		
		mTitleSleeping = (TextView) sleeping.findViewById(R.id.title_text);
		mTitleList.add(mTitleSleeping);
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
		
	}
	
	private void setupListener() {
		mCarlos.addTextChangedListener(mCarlosTextWatcher);
		mStep.addTextChangedListener(mStepTextWatcher);
		mWalking.addTextChangedListener(mWalkTextWatcher);
		mRunning.addTextChangedListener(mRunTextWatcher);
		mCycling.addTextChangedListener(mCycleingTextWatcher);
		mSleeping.addTextChangedListener(mSleepingTextWatcher);
	}
	
	@Override
	public void onResume() {
		super.onResume();
    	SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
    	String carlos = sp.getString(Def.SP_TARGET_CARLOS, "");
    	String step = sp.getString(Def.SP_TARGET_CARLOS, "");
    	String walking = sp.getString(Def.SP_TARGET_WALKING, "");
    	String running = sp.getString(Def.SP_TARGET_RUNNING, "");
    	String cycling = sp.getString(Def.SP_TARGET_CYCLING, "");
    	String sleep = sp.getString(Def.SP_TARGET_SLEEPING, "");
    	
		mCarlos.setText(carlos);
		mStep.setText(step);
		mWalking.setText(walking);
		mRunning.setText(running);
		mCycling.setText(cycling);
		mSleeping.setText(sleep);
	}
	
	private TextWatcher mCarlosTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Def.SP_TARGET_CARLOS, s.toString());
			editor.commit();
		}
	};
	
	private TextWatcher mStepTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Def.SP_TARGET_STEPS, s.toString());
			editor.commit();
		}
	};
	
	private TextWatcher mWalkTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Def.SP_TARGET_WALKING, s.toString());
			editor.commit();
		}
	};
	
	private TextWatcher mRunTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Def.SP_TARGET_RUNNING, s.toString());
			editor.commit();
		}
	};
	
	private TextWatcher mCycleingTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Def.SP_TARGET_CYCLING, s.toString());
			editor.commit();
		}
	};
	
	private TextWatcher mSleepingTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Def.SP_TARGET_SLEEPING, s.toString());
			editor.commit();
		}
		

	};
	
	private void requestFocus(View v) {
		TextView title = (TextView) v.findViewById(R.id.title_text);
		title.setTextColor(getResources().getColor(R.color.color_accent));
		EditText editText = (EditText) v.findViewById(R.id.value_text);
		editText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			for (TextView title : mTitleList) {
				title.setTextColor(getResources().getColor(R.color.md_black_1000));
			}
			requestFocus(v);
		}
	};
}
