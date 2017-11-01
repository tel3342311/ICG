package com.liteon.icampusguardian.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.AlarmItem;
import com.liteon.icampusguardian.util.Def;
import com.liteon.icampusguardian.util.TargetItem;
import com.liteon.icampusguardian.util.JSONResponse.Student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

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
	
	private Map<String, TargetItem> mTargetMap;
	private List<Student> mStudents;
	private int mCurrentStudentIdx;
	
	private TargetItem mCurrentTargetItem;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_setting_target, container, false);
		findView(rootView);
		setupListener();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.setting_profile_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_confirm).setVisible(true);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_confirm:
				saveTarget();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
				break;
		}
		return super.onOptionsItemSelected(item);
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
		
		mTitleCarlos.setText(R.string.healthy_carlories);
		mTitleSteps.setText(R.string.healthy_steps);
		mTitleWalking.setText(R.string.healthy_walk);
		mTitleRunning.setText(R.string.healthy_running);
		mTitleCycling.setText(R.string.healthy_cycling);
		mTitleSleeping.setText(R.string.healthy_sleep);
		
		mTitleCarlosUnit.setText(R.string.healthy_cal);
		mTitleStepsUnit.setText(R.string.healthy_step);
		mTitleWalkingUnit.setText(R.string.healthy_minutes);
		mTitleRunningUnit.setText(R.string.healthy_minutes);
		mTitleCyclingUnit.setText(R.string.healthy_minutes);
		mTitleSleepingUnit.setText(R.string.sleep_hours);
		
	}
	
	private void setupListener() {
		mCarlos.addTextChangedListener(mCarlosTextWatcher);
		mStep.addTextChangedListener(mStepTextWatcher);
		mWalking.addTextChangedListener(mWalkTextWatcher);
		mRunning.addTextChangedListener(mRunTextWatcher);
		mCycling.addTextChangedListener(mCycleingTextWatcher);
		mSleeping.addTextChangedListener(mSleepingTextWatcher);

        mCarlos.setOnTouchListener(mOnEditTextTouchListener);
		mStep.setOnTouchListener(mOnEditTextTouchListener);
		mWalking.setOnTouchListener(mOnEditTextTouchListener);
		mRunning.setOnTouchListener(mOnEditTextTouchListener);
		mCycling.setOnTouchListener(mOnEditTextTouchListener);
		mSleeping.setOnTouchListener(mOnEditTextTouchListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
    	SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		
		mCurrentStudentIdx = sp.getInt(Def.SP_CURRENT_STUDENT, 0);
    	restoreTarget();
    	
		mCarlos.setText(mCurrentTargetItem.getCarlos());
		mStep.setText(mCurrentTargetItem.getStep());
		mWalking.setText(mCurrentTargetItem.getWalking());
		mRunning.setText(mCurrentTargetItem.getRunning());
		mCycling.setText(mCurrentTargetItem.getCycling());
		mSleeping.setText(mCurrentTargetItem.getSleep());
	}
	
	private void restoreTarget() {
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		String targetMap = sp.getString(Def.SP_TARGET_MAP, "");
		Type typeOfHashMap = new TypeToken<Map<String, TargetItem >>() { }.getType();
        Gson gson = new GsonBuilder().create();
        mTargetMap = gson.fromJson(targetMap, typeOfHashMap);
		if (TextUtils.isEmpty(targetMap)) {
			mTargetMap = new HashMap<String, TargetItem>();
			for (Student student : mStudents) {
				String studentId = student.getStudent_id();
				TargetItem item = new TargetItem();
				item.setCarlos("2000");
				item.setStep("10000");
				item.setWalking("30");
				item.setRunning("30");
				item.setCycling("30");
				item.setSleep("9");
				mTargetMap.put(studentId, item);
			}
		}
		if (mTargetMap.get(mStudents.get(mCurrentStudentIdx).getStudent_id()) == null) {
			TargetItem item = new TargetItem();
			item.setCarlos("2000");
			item.setStep("10000");
			item.setWalking("30");
			item.setRunning("30");
			item.setCycling("30");
			item.setSleep("9");
			mTargetMap.put(mStudents.get(mCurrentStudentIdx).getStudent_id(), new TargetItem());
		}
		mCurrentTargetItem = mTargetMap.get(mStudents.get(mCurrentStudentIdx).getStudent_id());
	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	
	private void saveTarget() {
		Gson gson = new Gson();
		String input = gson.toJson(mTargetMap);
		SharedPreferences sp = getActivity().getSharedPreferences(Def.SHARE_PREFERENCE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Def.SP_TARGET_MAP, input);
		editor.commit();
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

			if (TextUtils.isEmpty(s.toString()) || !TextUtils.isDigitsOnly(s.toString())) {
				mCurrentTargetItem.setCarlos("0");
			} else {
				mCurrentTargetItem.setCarlos(s.toString());
			}
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
			if (TextUtils.isEmpty(s.toString())) {
				mCurrentTargetItem.setStep("0");
			} else {
				mCurrentTargetItem.setStep(s.toString());
			}
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
			if (TextUtils.isEmpty(s.toString())) {
				mCurrentTargetItem.setWalking("0");
			} else {
				mCurrentTargetItem.setWalking(s.toString());
			}
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
			if (TextUtils.isEmpty(s.toString())) {
				mCurrentTargetItem.setRunning("0");
			} else {
				mCurrentTargetItem.setRunning(s.toString());
			}
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
			if (TextUtils.isEmpty(s.toString())) {
				mCurrentTargetItem.setCycling("0");
			} else {
				mCurrentTargetItem.setCycling(s.toString());
			}
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
			if (TextUtils.isEmpty(s.toString())) {
				mCurrentTargetItem.setSleep("0");
			} else {
				mCurrentTargetItem.setSleep(s.toString());
			}
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

    private View.OnTouchListener mOnEditTextTouchListener = new View.OnTouchListener() {


        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (MotionEvent.ACTION_UP == motionEvent.getAction()){
                for (TextView title : mTitleList) {
                    title.setTextColor(getResources().getColor(R.color.md_black_1000));
                }
                requestFocus((ViewGroup)view.getParent());
            }
            return false;
        }
    };
}
