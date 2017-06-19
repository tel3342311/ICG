package com.liteon.icampusguardian.fragment;

import java.util.ArrayList;

import com.liteon.icampusguardian.R;
import com.liteon.icampusguardian.util.CircularImageView;
import com.liteon.icampusguardian.util.SettingItem;
import com.liteon.icampusguardian.util.SettingItemAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	
	private static ArrayList<SettingItem> myDataset = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private AppCompatButton mAddAlarm;
	private CircularImageView mChildIcon;
	private TextView mChildName;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
		findView(rootView);
		initRecycleView();
		initChildInfo();
		return rootView;
	}
	
	private void findView(View rootView) {
		mChildIcon = (CircularImageView) rootView.findViewById(R.id.child_icon);
		mChildName = (TextView) rootView.findViewById(R.id.child_name);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.setting_view);
	}
	
	private void initRecycleView() {
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(mLayoutManager);
		testData();
		mAdapter = new SettingItemAdapter(myDataset);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void initChildInfo() {
		mChildIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher, null));
		mChildIcon.setBorderColor(getResources().getColor(R.color.md_white_1000));
		mChildIcon.setBorderWidth(10);
		mChildIcon.setSelectorColor(getResources().getColor(R.color.md_blue_400));
		mChildIcon.setSelectorStrokeColor(getResources().getColor(R.color.md_blue_800));
		mChildIcon.setSelectorStrokeWidth(10);
		mChildIcon.addShadow();
		mChildName.setText("王小明");
	}
	
	private void testData() {
		for (SettingItem.TYPE type : SettingItem.TYPE.values()) {
			SettingItem item = new SettingItem();
			item.setItemType(type);
			myDataset.add(item);
		}
	}
}
