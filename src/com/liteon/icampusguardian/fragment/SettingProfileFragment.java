package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;
import java.util.List;

import com.aigestudio.wheelpicker.WheelPicker;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.db.DBHelper;
import com.liteon.icampusguardian.util.ProfileItem;
import com.liteon.icampusguardian.util.ProfileItem.TYPE;
import com.liteon.icampusguardian.util.ProfileItemAdapter;
import com.liteon.icampusguardian.util.JSONResponse.Student;
import com.liteon.icampusguardian.util.ProfileItemAdapter.ViewHolder.IProfileItemClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SettingProfileFragment extends Fragment implements IProfileItemClickListener {


	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private CardView mCardView;
	private List<ProfileItem> mDataSet;
	private WheelPicker mWheel_left;
	private WheelPicker mWheel_center;
	private WheelPicker mWheel_right;
	private WheelPicker mWheel_single;
	private TextView mWheelTitle;
	private View three_wheel;
	private View one_wheel;
	private DBHelper mDbHelper;
	private List<Student> mStudents;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_setting_profile, container, false);
		findView(rootView);
		setupListener();
		mDbHelper = DBHelper.getInstance(getActivity());
		//get child list
		mStudents = mDbHelper.queryChildList(mDbHelper.getReadableDatabase());
		initRecycleView();
		return rootView;
	}
	
	private void findView(View rootView) {
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.profile_view);
		mCardView = (CardView) rootView.findViewById(R.id.option_wheel);
		three_wheel = rootView.findViewById(R.id.three_wheel);
		mWheel_left = (WheelPicker) three_wheel.findViewById(R.id.main_wheel_left);
		mWheel_center = (WheelPicker) three_wheel.findViewById(R.id.main_wheel_center);
		mWheel_right = (WheelPicker) three_wheel.findViewById(R.id.main_wheel_right);
		one_wheel = rootView.findViewById(R.id.one_wheel);
		mWheel_single = (WheelPicker) one_wheel.findViewById(R.id.main_wheel_left);
		mWheelTitle = (TextView) one_wheel.findViewById(R.id.year_title);
		
	}
	
	private void setupListener() {
		mWheel_left.setOnItemSelectedListener(mWheelClickListener);
		mWheel_center.setOnItemSelectedListener(mWheelClickListener);
		mWheel_right.setOnItemSelectedListener(mWheelClickListener);
		mWheel_single.setOnItemSelectedListener(mWheelClickListener);
	}
	
	private WheelPicker.OnItemSelectedListener mWheelClickListener = new WheelPicker.OnItemSelectedListener() {

		@Override
		public void onItemSelected(WheelPicker wheel, Object data, int position) {
			if (three_wheel.getVisibility() == View.VISIBLE) {
				if (R.id.main_wheel_left == wheel.getId()) {
					
				} else if (R.id.main_wheel_center == wheel.getId()) {
					
				} else if (R.id.main_wheel_right == wheel.getId()) {
					
				}
			} else if (one_wheel.getVisibility() == View.VISIBLE) {
				
			}
		}		
	};
	
	private void testData(){
		mDataSet = new ArrayList<>();
		Student student = mStudents.get(0);
        for (ProfileItem.TYPE type : ProfileItem.TYPE.values()) {
        	ProfileItem item = new ProfileItem();
        	item.setItemType(type);
        	switch(type) {
    		case BIRTHDAY:
    			item.setValue(student.getDob());
    			break;
    		case GENDER:
    			if (TextUtils.equals(student.getGender(), "MALE")) {
    				item.setValue("男性");
    			} else {
    				item.setValue("女性");
        		}
    			break;
    		case HEIGHT:
    			item.setValue(Integer.toString(student.getHeight()));
    			break;
    		case WEIGHT:
    			item.setValue(Integer.toString(student.getWeight()));
    			break;
    		default:
    			break;
    		}
        	mDataSet.add(item);
        }
	}
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new ProfileItemAdapter(mDataSet, this);
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onProfileItemClick(TYPE type) {
		setupWheel(type);
	}
	private void setupWheel(TYPE type) {
		mCardView.setVisibility(View.VISIBLE);
		three_wheel.setVisibility(View.INVISIBLE);
		one_wheel.setVisibility(View.INVISIBLE);
		switch(type) {
		case BIRTHDAY:
			three_wheel.setVisibility(View.VISIBLE);
			List<String> years = new ArrayList<>();
			for (int i = 1990; i < 2017 ; i++) {
				years.add(Integer.toString(i));
			}
			mWheel_left.setData(years);
			List<String> months = new ArrayList<>();
			for (int i = 1; i < 13 ; i++) {
				months.add(Integer.toString(i));
			}
			mWheel_center.setData(months);
			List<String> days = new ArrayList<>();
			for (int i = 1; i < 30; i++) {
				days.add(Integer.toString(i));
			}
			mWheel_right.setData(days);
			break;
		case GENDER:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText("性別");
			List<String> gender = new ArrayList<>();
			gender.add("男生");
			gender.add("女生");
			mWheel_single.setData(gender);
			mWheel_single.setCyclic(false);
			break;
		case HEIGHT:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText("身高");
			List<String> height = new ArrayList<>();
			for (int i = 130; i < 200; i++) {
				height.add(Integer.toString(i));
			}
			mWheel_single.setData(height);
			break;
		case WEIGHT:
			one_wheel.setVisibility(View.VISIBLE);
			mWheelTitle.setText("體重");
			List<String> weight = new ArrayList<>();
			for (int i = 30; i < 100; i++) {
				weight.add(Integer.toString(i));
			}
			mWheel_single.setData(weight);
			break;
		default:
			break;
		}
	}
}
