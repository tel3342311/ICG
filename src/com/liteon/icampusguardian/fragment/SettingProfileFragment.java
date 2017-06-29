package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;
import java.util.List;

import com.aigestudio.wheelpicker.WheelPicker;
import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.ProfileItem;
import com.liteon.icampusguardian.util.ProfileItem.TYPE;
import com.liteon.icampusguardian.util.ProfileItemAdapter;
import com.liteon.icampusguardian.util.ProfileItemAdapter.ViewHolder.IProfileItemClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_setting_profile, container, false);
		findView(rootView);
		setupListener();
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

	}
	
	private void testData(){
		mDataSet = new ArrayList<>();
        for (ProfileItem.TYPE type : ProfileItem.TYPE.values()) {
        	ProfileItem item = new ProfileItem();
        	item.setItemType(type);
        	switch(type) {
    		case BIRTHDAY:
    			item.setValue("1990/01/01");
    			break;
    		case GENDER:
    			item.setValue("男性");
    			break;
    		case HEIGHT:
    			item.setValue("150");
    			break;
    		case WEIGHT:
    			item.setValue("50");
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
