package com.liteon.icampusguardian.fragment;

import com.liteon.icampusguardian.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppInfoPrivacyFragment extends Fragment {

	private View mRootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 mRootView = inflater.inflate(R.layout.fragment_alarm, container, false);
		 findView(mRootView);
		 return mRootView;
	}
	
	
}
